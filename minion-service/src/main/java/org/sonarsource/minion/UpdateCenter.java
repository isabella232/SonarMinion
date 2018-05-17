/*
 * Copyright (C) 2018-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package org.sonarsource.minion;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class UpdateCenter {

  private static final String SONARQUBE_PRODUCT_KEY = "sonar";

  private final Properties updateCenterProperties;
  private final Map<String, String> productKeysByName;

  public UpdateCenter() {
    this.updateCenterProperties = loadFile();
    Set<String> pluginKeys = stream(this.updateCenterProperties.getProperty("plugins").split(",")).collect(Collectors.toSet());
    this.productKeysByName = pluginKeys.stream()
      .collect(Collectors.toMap(pluginKey -> updateCenterProperties.getProperty(pluginKey + ".name"), Function.identity()));
    this.productKeysByName.put("SonarQube", SONARQUBE_PRODUCT_KEY);
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
    return productKeysByName.keySet();
  }

  public Collection<String> findSortedVersions(String productName) {
    String productKey = productKeysByName.get(productName);
    if (productKey == null) {
      return Collections.emptyList();
    }
    return stream(updateCenterProperties.getProperty(productKey + ".versions")
      .split(","))
        .collect(Collectors.toCollection(TreeSet::new));
  }
}
