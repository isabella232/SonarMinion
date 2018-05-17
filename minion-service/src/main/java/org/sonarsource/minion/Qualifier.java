/*
 * Copyright (C) 2018-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package org.sonarsource.minion;

import java.util.Map;
import java.util.Set;

public class Qualifier {

  public String qualify(Set<String> errorMessage, Map<String, String> productsVersions) {
    return "Your question seems related to SONAR-42";
  }
}
