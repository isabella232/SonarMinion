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

  private static final Gson GSON = new Gson();

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
  public void test_gg_versions() throws IOException {
    String[][] expected = new String[][]{
      {"7.0","7.1","0.0.0.0", "2018.05.03"},
      {"47.4448709", "51.6451219"},
    };

    Analyzer analyzer = new Analyzer();
      for (int i = 1; i <= 2; i++) {
        Analyzer.Message message = getMessage("src/test/resources/google_groups/input" + i + ".json");
        assertThat(analyzer.getVersions(message.description)).containsExactlyInAnyOrder(expected[i-1]);
      }
  }

  @Test
  public void test_sd_versions() throws IOException {
    String[][] expected = new String[][]{
            {"6.7.1", "5.6"},
            {},
            {"2.0.0", "41.4657263", "5.9.0.1001", "41.3511383", "5.6"},
            {"300.000", "2.264.000"}
    };

    Analyzer analyzer = new Analyzer();
      for (int i = 1; i <= 4; i++) {
        Analyzer.Message message = getMessage("src/test/resources/servicedesk/input" + i + ".json");
        assertThat(analyzer.getVersions(message.description)).containsExactlyInAnyOrder(expected[i-1]);
      }
  }


  @Test
  public void test_gg_products() throws IOException {
    String[][] expected = new String[][]{
      {},
      {"SonarQube"},
      {},
      {}
    };

    for (int i = 1; i <= 2; i++) {
        Analyzer.Message message = getMessage("src/test/resources/google_groups/input" + i + ".json");
        assertThat(new Analyzer().getProducts(message.description)).containsExactlyInAnyOrder(expected[i - 1]);
      }
  }

  @Test
  public void test_sd_products() throws IOException {
    String[][] expected = new String[][]{
            {"SonarQube"},
            {},
            {"SonarQube"},
            {}
    };

    for (int i = 1; i <= 4; i++) {
      Analyzer.Message message = getMessage("src/test/resources/servicedesk/input" + i + ".json");
      assertThat(new Analyzer().getProducts(message.description)).containsExactlyInAnyOrder(expected[i - 1]);
    }
  }

  private static Analyzer.Message getMessage(String pathname) throws IOException {
    File file = new File(pathname);
    String content = Files.readAllLines(file.toPath()).stream().collect(Collectors.joining("\n"));
    return GSON.fromJson(content, Analyzer.Message.class);
  }

  @Test
  public void errorMessage() throws IOException {
    String[][] expected = new String[][]{
      {"Caused by: java.lang.UnsupportedOperationException: a measure can be set only once for a specific Component (key=net.lidl:imagereader:BRANCH:feature-12345), Metric (key=skipped_tests). Use update method \tat "},
      {},
      {},
      {},
      {},
      {},
      {},
      {}
    };
    for (int i = 1; i <= 8; i++) {
      Analyzer.Message message = getMessage("src/test/resources/servicedesk/input" + i + ".json");
      assertThat(new Analyzer().getErrorMessages(message.description)).containsExactlyInAnyOrder(expected[i-1]);
    }
  }
}
