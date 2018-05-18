/*
 * Copyright (C) 2018-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package org.sonarsource.minion;

import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.util.concurrent.Promise;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Qualifier {

  private final JiraClient jiraClient;

  public Qualifier(JiraClient jiraClient) {
    this.jiraClient = jiraClient;
  }

  public Qualifier() {
    this(new JiraClient());
  }

  public Set<String> qualify(Set<String> errorMessage) {
    String jql = errorMessage.stream().map(e -> "text ~\"" + escapeForJQL(e) + "\"").collect(Collectors.joining(" OR "));
    Promise<SearchResult> searchResultPromise = jiraClient.getClient().getSearchClient().searchJql(jql);
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
      .replace("~", "");
  }
}
