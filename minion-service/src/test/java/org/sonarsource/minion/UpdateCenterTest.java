/*
 * Copyright (C) 2018-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package org.sonarsource.minion;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateCenterTest {

  private UpdateCenter underTest = new UpdateCenter();

  @Test
  public void find_products() {
    assertThat(underTest.findProducts())
      .contains("SonarCOBOL", "SonarQube");
  }

  @Test
  public void find_version_for_sonarqube() {
    assertThat(underTest.findSortedVersions("SonarQube"))
      .containsExactly(
        "5.6",
        "5.6.1",
        "5.6.2",
        "5.6.3",
        "5.6.4",
        "5.6.5",
        "5.6.6",
        "5.6.7",
        "6.0",
        "6.1",
        "6.2",
        "6.3",
        "6.3.1",
        "6.4",
        "6.5",
        "6.6",
        "6.7",
        "6.7.1",
        "6.7.2",
        "6.7.3",
        "7.0",
        "7.1");
  }

  @Test
  public void find_version_for_sonar_cobol() {
    assertThat(underTest.findSortedVersions("SonarCOBOL"))
      .containsExactlyInAnyOrder("4.0.2", "4.2");
  }

  @Test
  public void find_version_for_unknown_product() {
    assertThat(underTest.findSortedVersions("unknown"))
      .isEmpty();
  }

}
