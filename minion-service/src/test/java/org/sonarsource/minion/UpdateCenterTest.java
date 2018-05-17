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
    assertThat(underTest.findProducts()).contains("SonarCOBOL");
  }
}
