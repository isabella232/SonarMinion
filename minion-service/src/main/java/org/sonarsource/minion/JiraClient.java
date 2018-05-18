/*
 * Copyright (C) 2017-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */

package org.sonarsource.minion;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import java.net.URI;
import java.net.URISyntaxException;

public class JiraClient {

  private static final String JIRA_URL = "https://jira.sonarsource.com";

  public JiraRestClient getClient() {
    JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
    URI uri = null;
    try {
      uri = new URI(JIRA_URL);
    } catch (URISyntaxException e) {
      throw new IllegalStateException("Jira from sonarsource not found !?");
    }
    return factory.create(uri, new AnonymousAuthenticationHandler());
  }
}
