/*
 * Copyright (C) 2017-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */

package org.sonarsource.minion;

import java.util.Collection;

public interface InputConnector {

  Collection<String> findProducts();

  Collection<String> findSortedVersions(String productName);
}
