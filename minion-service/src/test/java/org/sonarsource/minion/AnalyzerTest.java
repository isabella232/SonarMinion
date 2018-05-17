/*
 * Copyright (C) 2018-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */
package org.sonarsource.minion;


import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnalyzerTest {

  @Test
  public void test_returned_message() {
    Analyzer analyzer = new Analyzer();
    String answer = analyzer.analyze("{description:\"foo\"}");
    assertThat(answer).isEqualTo("There seems to be no product nor version in your question, could you precise those information ?");

    // version in message
    answer = analyzer.analyze("{description:\"foo 6.2\"}");
    assertThat(answer).isEqualTo("Could you precise which component of the SonarQube ecosystem your question is about ?");

    answer = analyzer.analyze("{description:\"foo 6.2\", component:\"plop\"}");
    assertThat(answer).isEqualTo("Your question seems related to SONAR-42");
  }

  @Test
  public void test_versions() throws IOException {
    String[][] expected = new String[][]{
      {"6.7.1", "5.6"},
      {},
      {"2.0.0", "41.4657263", "5.9.0.1001", "41.3511383", "5.6"},
      {"300.000", "2.264.000"}
    };

    Analyzer analyzer = new Analyzer();
    for (int i = 1; i <= 4; i++) {
      File file = new File("src/test/resources/message-" + i + "-Jira.txt");
      String content = Files.readAllLines(file.toPath()).stream().collect(Collectors.joining("\n"));
      Analyzer.Message message = new Gson().fromJson(content, Analyzer.Message.class);
      assertThat(analyzer.getVersions(message.description)).containsExactlyInAnyOrder(expected[i-1]);
    }
  }
}
