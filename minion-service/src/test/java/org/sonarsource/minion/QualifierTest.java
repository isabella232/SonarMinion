/*
 * Copyright (C) 2018-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package org.sonarsource.minion;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QualifierTest {

  @Test
  public void request_jira() {
    Set<String> response = new Qualifier().qualify(Collections.singleton("Caused by: java.lang.UnsupportedOperationException: a measure can be set only once for a specific Component"));
    assertThat(response).contains("SONAR-9384");

    List<String> strings = Arrays.asList("	at org.sonar.server.computation.task.projectanalysis.component.VisitException.rethrowOrWrap(VisitException.java:44)",
      "	at org.sonar.server.computation.task.projectanalysis.measure.MapBasedRawMeasureRepository.add(MapBasedRawMeasureRepository.java:85)");
    response = new Qualifier().qualify(new HashSet<>(strings));
    assertThat(response).contains("SONAR-9384");
  }
}
