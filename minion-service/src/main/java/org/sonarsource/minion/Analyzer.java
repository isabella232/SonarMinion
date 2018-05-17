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
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.CheckForNull;

public class Analyzer {
  private static final Pattern VERSION_REGEX = Pattern.compile("\\d+\\.\\d+(\\.\\d+)*");
  private final Qualifier qualifier;

  private UpdateCenter updateCenter = new UpdateCenter();

  Analyzer() {
    this.qualifier = new Qualifier();
  }

  Analyzer(Qualifier qualifier) {
    this.qualifier = qualifier;
  }

  public static class Message {
    String description;
    String component;
    String component_version;
  }

  public String analyze(String json) {
    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    Message message = gson.fromJson(json, Message.class);
    if (message == null) {
      throw new IllegalArgumentException("Invalid json message");
    }
    Set<String> versions = new HashSet<>();
    Set<String> products = new HashSet<>();
    if (message.component_version == null || message.component_version.isEmpty()) {
      versions = getVersions(message.description);
      if (versions.isEmpty()) {
        return "There seems to be no product nor version in your question, could you precise those information ?";
      }
    } else {
      versions.add(message.component_version);
    }
    if (message.component == null || message.component.isEmpty()) {
      products = getProducts(message.description);
      if (products.isEmpty()) {
        return "Could you precise which component of the SonarQube ecosystem your question is about ?";
      }
    } else {
      products = getProducts(message.component);
    }
    Map<String, String> productsVersions = getVersionsByProduct(products, versions);
    return qualifier.qualify(getErrorMessages(message.description), productsVersions);
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

  Set<String> getErrorMessages(String message) {
    Set<String> res = new HashSet<>();
    int index = message.indexOf("Caused by:");

    while (index > 0) {
      int at = message.indexOf("at ", index);
      if (at < 0) {
        break;
      }
      res.add(message.substring(index, at + 3));
      index = message.indexOf("Caused by:", at);
    }

    return res;
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
