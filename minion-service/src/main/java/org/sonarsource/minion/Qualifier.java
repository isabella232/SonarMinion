/*
 * Copyright (C) 2018-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package org.sonarsource.minion;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Qualifier {

  private static final String JIRA_URL = "https://jira.sonarsource.com";

  private final JiraRestClient client;

  public Qualifier() {
    JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
    URI uri = null;
    try {
      uri = new URI(JIRA_URL);
    } catch (URISyntaxException e) {
      throw new IllegalStateException("Jira from sonarsource not found !?");
    }
    this.client = factory.create(uri, new AnonymousAuthenticationHandler());
  }

  public Set<String> qualify(Set<String> errorMessage, Map<String, String> productsVersions) {
    String jql = errorMessage.stream().map(e -> "text ~\"" + escapeForJQL(e) + "\"").collect(Collectors.joining(" OR "));
    Promise<SearchResult> searchResultPromise = client.getSearchClient().searchJql(jql);
    SearchResult sr = searchResultPromise.claim();
    Set<String> jiraTickets = new HashSet<>();
    for (BasicIssue basicIssue : sr.getIssues()) {
      jiraTickets.add(basicIssue.getKey());
    }
    return jiraTickets;
  }

  private String escapeForJQL(String e) {
    return e.replace("\t", "\\t")
      .replace(":", "\\\\\\\\:")
      .replace("[", "\\\\\\\\[")
      .replace("]", "\\\\\\\\]")
      .replace("~", "")
      ;
  }
}
