/*
 * Copyright (C) 2017-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */

package org.sonarsource.minion;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.domain.Version;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import java.net.URI;
import java.net.URISyntaxException;
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

  private Map<String, Project> projectsByName;

  public JiraInputConnector() {
    JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
    URI uri = null;
    try {
      uri = new URI(JIRA_URL);
    } catch (URISyntaxException e) {
      throw new IllegalStateException("Jira from sonarsource not found !?");
    }
    JiraRestClient client = factory.create(uri, new AnonymousAuthenticationHandler());

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

  @Override
  public Collection<String> findSortedVersions(String productName) {
    Project project = projectsByName.get(productName);
    if (project == null) {
      return Collections.emptyList();
    }
    return project.getSortedVersions();
  }

  private static class Project {
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
