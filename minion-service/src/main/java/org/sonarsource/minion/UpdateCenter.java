/*
 * Copyright (C) 2018-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package org.sonarsource.minion;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class UpdateCenter {

  private final Properties updateCenterProperties;

  private final Set<String> pluginKeys;
  private final Set<String> pluginNames;

  public UpdateCenter() {
    this.updateCenterProperties = loadFile();
    this.pluginKeys =
      stream(this.updateCenterProperties.getProperty("plugins").split(","))
      .collect(Collectors.toSet());
    this.pluginNames = pluginKeys.stream().map(pluginKey -> updateCenterProperties.getProperty(pluginKey + ".name"))
      .collect(Collectors.toSet());
    this.pluginNames.add("SonarQube");
  }

  private Properties loadFile() {
    try {
      Properties updateCenterProperties = new Properties();
      updateCenterProperties.load(getClass().getResourceAsStream("/update-center.properties"));
      return updateCenterProperties;
    } catch (IOException e) {
      throw new IllegalStateException("Can't read update center file");
    }
  }

  public Collection<String> findProducts() {
    return pluginNames;
  }
}
