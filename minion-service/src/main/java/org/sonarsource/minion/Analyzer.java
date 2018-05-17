/*
 * Copyright (C) 2018-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package org.sonarsource.minion;

import com.google.gson.Gson;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Analyzer {
  private static final Pattern VERSION_REGEX = Pattern.compile("\\d+\\.\\d+(\\.\\d+)*");

  public static class Message {
    String description;
    String component;
    String component_version;
  }

  public String analyze(String json) {
    Message message = new Gson().fromJson(json, Message.class);
    if(message.component_version == null || message.component_version.isEmpty()) {
      Set<String> versions = getVersions(message.description);
      if(versions.isEmpty()) {
        return "There seems to be no product nor version in your question, could you precise those information ?";
      }
      message.component_version = versions.stream().collect(Collectors.joining(","));
    }
    if(message.component == null || message.component.isEmpty()) {
      return "Could you precise which component of the SonarQube ecosystem your question is about ?";
    }
    return "Your question seems related to SONAR-42";
  }

  Set<String> getVersions(String message) {
    Set<String> res = new HashSet<>();
    Matcher matcher = VERSION_REGEX.matcher(message);
    while(matcher.find()) {
      res.add(matcher.group());
    }
    return res;
  }

  Set<String> getProducts(String message) {
    UpdateCenter updateCenter = new UpdateCenter();
    Collection<String> knownProducts = updateCenter.findProducts();
    return knownProducts.stream()
      .filter(message::contains)
      .collect(Collectors.toSet());
  }
}
