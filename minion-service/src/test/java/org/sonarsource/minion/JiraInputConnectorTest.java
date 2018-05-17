/*
 * Copyright (C) 2017-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */

package org.sonarsource.minion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JiraInputConnectorTest {

  private JiraInputConnector underTest = new JiraInputConnector();

  @Test
  public void test() {
    assertThat(underTest.findProducts()).contains("SonarQube", "SonarJava");

    assertThat(underTest.findSortedVersions("SonarQube")).contains("6.7", "7.0");
  }

  @Test
  @Ignore
  public void export_to_file() {
    StringBuilder file = new StringBuilder();
    List<String> projects = new ArrayList<>();
    underTest.findProjects().stream()
      .forEach(p -> {
        Collection<String> versions = underTest.findSortedVersions(p.getName());
        if (versions.isEmpty()) {
          return;
        }
        String key = p.getKey();
        file.append(key).append(".name").append("=").append(p.getName()).append("\n");
        file.append(key).append(".versions").append("=").append(versions.stream().collect(Collectors.joining(","))).append("\n");
        projects.add(key);
      });
    file.append("products").append("=").append(projects.stream().collect(Collectors.joining(",")));
    System.out.println("***");
  }
}
