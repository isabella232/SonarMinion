/*
 * Copyright (C) 2018-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package org.sonarsource.minion;


import org.assertj.core.api.Assertions;
import org.junit.Test;

public class AnalyzerTest {

  @Test
  public void test_returned_message() {
    String result = new Analyzer().analyze("foo");
    Assertions.assertThat(result).isEqualTo("message analyzed : foo");
  }
}
