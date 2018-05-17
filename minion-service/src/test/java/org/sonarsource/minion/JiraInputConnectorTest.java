/*
 * Copyright (C) 2017-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */

package org.sonarsource.minion;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JiraInputConnectorTest {

  private InputConnector underTest = new JiraInputConnector();

  @Test
  public void test() {
    assertThat(underTest.findProducts()).contains("SonarQube", "SonarJava");

    assertThat(underTest.findSortedVersions("SonarQube")).contains("6.7", "7.0");
  }
}