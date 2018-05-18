package org.sonarsource.minion;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.Version;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SonarJavaIntegrationTest {


//  @Test
  public void read_java_tickets() throws IOException {
    Analyzer analyzer = new Analyzer(new Qualifier(), new CachedJiraInputConnector());
    StringBuilder sb = new StringBuilder();
    for (File javaFile : new File("src/test/resources/javaTickets").listFiles()) {
      String content = new String(Files.readAllBytes(javaFile.toPath()));
      sb.append("Ticket :").append(javaFile.getName()).append("\n");
      sb.append(analyzer.analyze(content)).append("\n");
      sb.append("=================================================\n");
      sb.append("=================================================\n");


    }

    Files.write(new File("src/test/resources/javaTickets/report.txt").toPath(), sb.toString().getBytes());
  }

  private static final String JIRA_URL = "https://jira.sonarsource.com";
  private static final String SONARJAVA = "SONARJAVA";

  //@Test
  public void generate_sonarjava_files() throws URISyntaxException, IOException {
    Gson gson = new Gson();

    JiraRestClient jiraRestClient = new AsynchronousJiraRestClientFactory().create(new URI(JIRA_URL), new AnonymousAuthenticationHandler());
    SearchResult claim = jiraRestClient.getSearchClient().searchJql("project = " + SONARJAVA + " and type = BUG", 1000, 0).claim();
    for (BasicIssue basicIssue : claim.getIssues()) {
      Issue issue = jiraRestClient.getIssueClient().getIssue(basicIssue.getKey()).claim();
      String desc = issue.getDescription();
      if(desc == null || desc.isEmpty()) {
        desc = "PLACEHOLDER";
      }
      Set<String> versions = new HashSet<>();
      for (Version v  : issue.getFixVersions()) {
        versions.add(v.getName());
      }
      Analyzer.Message message = new Analyzer.Message(desc, "SonarJava", versions.stream().collect(Collectors.joining(",")));

      Files.write(new File("src/test/resources/javaTickets/"+issue.getKey()).toPath(), gson.toJson(message).getBytes());
    }
  }
}
