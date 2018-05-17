/*
 * Copyright (C) 2018-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package org.sonarsource.minion;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.CheckForNull;

public class Analyzer {
  private static final Pattern VERSION_REGEX = Pattern.compile("\\d+\\.\\d+(\\.\\d+)*");
  private static final Pattern STACK_REGEX= Pattern.compile("(^\\d+\\) .+)|(^.+Exception: .+)|(^\\s+at .+)|(^\\s+... \\d+ more)|(^\\s*Caused by:.+)", Pattern.MULTILINE);
  private final Qualifier qualifier;

  private UpdateCenter updateCenter = new UpdateCenter();

  public Analyzer(Qualifier qualifier, InputConnector inputConnector) {
    this.qualifier = qualifier;
    this.inputConnector = inputConnector;
  }

  public static class Message {
    String description;
    String component;
    String component_version;
  }

  public String analyze(String json) {
    Gson gson = new GsonBuilder().create();
    Message message = gson.fromJson(json, Message.class);
    if (message == null) {
      throw new IllegalArgumentException("Invalid json message");
    }
    Set<String> versions = new HashSet<>();
    if (message.component_version == null || message.component_version.isEmpty()) {
      versions = getVersions(message.description);
      if (versions.isEmpty()) {
        return "There seems to be no product nor version in your question, could you precise those information ?";
      }
    } else {
      versions.add(message.component_version);
    }

    Set<String> products = new HashSet<>();
    if (message.component == null || message.component.isEmpty()) {
      products = getProducts(message.description);
    } else {
      products = getProducts(message.component);
    }
    if (products.isEmpty()) {
      return "Could you precise which component of the SonarQube ecosystem your question is about ?";
    }

    Map<String, String> productsVersions = getVersionsByProduct(products, versions);
    List<String> errorMessages = getErrorMessages(message.description);
    if (errorMessages.isEmpty()) {
      return "We didn't understand the error, could you please describe the error ?";
    }
    return qualifier.qualify(new HashSet<>(errorMessages), productsVersions);
  }

  Set<String> getVersions(String message) {
    Set<String> res = new HashSet<>();
    Matcher matcher = VERSION_REGEX.matcher(message);
    while (matcher.find()) {
      res.add(matcher.group());
    }
    return res;
  }

  Set<String> getProducts(String message) {
    Collection<String> knownProducts = updateCenter.findProducts();
    return knownProducts.stream()
      .filter(message::contains)
      .collect(Collectors.toSet());
  }

  List<String> getErrorMessages(String message) {
    List<String> res = new ArrayList<>();
    Matcher matcher = STACK_REGEX.matcher(message);
    while (matcher.find()) {
      res.add(matcher.group());
    }
    return IntStream.range(0, res.size())
      // keep the first element in case we dd
      .filter(i -> i==0 || res.get(i).matches("(^.+Exception: .+)|(^\\s*Caused by:.+)"))
      .mapToObj(i -> {
        int next = i + 1;
        String line = null;
        while (next < res.size() && (line == null || !line.contains("sonar"))) {
          line = res.get(next);
          next++;
        }
        return line;
      }).filter(Objects::nonNull).collect(Collectors.toList());
  }

  @CheckForNull
  String getVersion(String product, Collection<String> versions) {
    List<String> knownVersions = new ArrayList<>(updateCenter.findSortedVersions(product));
    Collections.reverse(knownVersions);
    return knownVersions.stream()
      .filter(versions::contains)
      .findFirst().orElse(null);
  }

  private Map<String, String> getVersionsByProduct(Set<String> products, Set<String> versions) {
    return products.stream()
      .filter(p -> getVersion(p, versions) != null)
      .collect(Collectors.toMap(Function.identity(), p -> getVersion(p, versions)));
  }

}
