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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Qualifier {

  private static final String JIRA_URL = "https://jira.sonarsource.com";

  public String qualify(Set<String> errorMessage, Map<String, String> productsVersions) {
    JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
    URI uri = null;
    try {
      uri = new URI(JIRA_URL);
    } catch (URISyntaxException e) {
      throw new IllegalStateException("Jira from sonarsource not found !?");
    }
    JiraRestClient client = factory.create(uri, new AnonymousAuthenticationHandler());

    String jql = errorMessage.stream().map(e -> "text ~\"" + e.replace("\t","\\t") + "\"").collect(Collectors.joining(" OR "));
    Promise<SearchResult> searchResultPromise = client.getSearchClient().searchJql(jql);
    SearchResult sr = searchResultPromise.claim();
    StringBuilder result = new StringBuilder();
    for (BasicIssue basicIssue : sr.getIssues()) {
      result.append(basicIssue.getKey()).append(", ");
    }
    if(result.length() == 0) {
      return "Your question seems related to SONAR-42";
    }
    return result.toString();

  }
}
