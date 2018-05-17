/*
 * Copyright (C) 2018-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package org.sonarsource.minion;

import java.util.Collections;
import java.util.HashMap;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QualifierTest {


  @Test
  public void request_jira() {
    String response = new Qualifier().qualify(Collections.singleton("Caused by: java.lang.UnsupportedOperationException: a measure can be set only once for a specific Component"), new HashMap<>());
    assertThat(response).contains("SONAR-9384");
  }
}
