/*
 * Copyright (C) 2018-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package org.sonarsource.minion;

import com.google.gson.Gson;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analyzer {
  private static final Pattern VERSION_REGEX = Pattern.compile("\\d+\\.\\d+(\\.\\d+)*");

  public static class Message {
    String description;
    String component;
    String component_version;

  }

  public String analyze(String json) {
    Message message = new Gson().fromJson(json, Message.class);
    return "message analyzed : "+message.description;
  }

  Set<String> getVersions(String message) {
    Set<String> res = new HashSet<>();
    Matcher matcher = VERSION_REGEX.matcher(message);
    while(matcher.find()) {
      res.add(matcher.group());
    }
    return res;
  }
}
