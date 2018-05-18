/*
 * Copyright (C) 2017-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */

package org.sonarsource.minion;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.Version;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JiraInputConnector implements InputConnector {

  private static final String JIRA_URL = "https://jira.sonarsource.com";

  private final JiraClient jiraClient;

  private Map<String, Project> projectsByName;

  public JiraInputConnector(JiraClient jiraClient) {
    this.jiraClient = jiraClient;
  }

  public JiraInputConnector() {
    this(new JiraClient());
    JiraRestClient client = jiraClient.getClient();
    this.projectsByName = StreamSupport.stream(client.getProjectClient().getAllProjects().claim().spliterator(), false)
      .map(p -> client.getProjectClient().getProject(p.getKey()).claim())
      .map(p -> new Project(p.getKey(), p.getName(),
        StreamSupport.stream(p.getVersions().spliterator(), false)
          .map(Version::getName)
          .collect(Collectors.toSet())))
      .collect(Collectors.toMap(Project::getName, Function.identity()));
  }

  @Override
  public Collection<String> findProducts() {
    return projectsByName.keySet();
  }

  public Collection<Project> findProjects() {
    return projectsByName.values();
  }

  @Override
  public Collection<String> findSortedVersions(String productName) {
    Project project = projectsByName.get(productName);
    if (project == null) {
      return Collections.emptyList();
    }
    return project.getSortedVersions();
  }

  static class Project {
    private final String key;
    private final String name;
    private final Set<String> versions;

    Project(String key, String name, Set<String> versions) {
      this.key = key;
      this.name = name;
      this.versions = new TreeSet<>(versions);
    }

    String getKey() {
      return key;
    }

    String getName() {
      return name;
    }

    Set<String> getSortedVersions() {
      return versions;
    }
  }

}
