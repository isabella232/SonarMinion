/*
 * Copyright (C) 2018-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */

package org.sonarsource.minion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.port;
import static spark.Spark.post;

public class WebServer {

  private static final int PORT = 9001;

  private static final Logger LOGGER = LoggerFactory.getLogger(WebServer.class);

  private final Analyzer analyzer = new Analyzer();

  public void start() {
    port(PORT);
    LOGGER.info("Listening on port {}", PORT);

    post("/analyze", (request, response) -> {
      String message = request.body();
      return analyzer.analyze(message);
    });
  }

  public static void main(String[] args) {
    new WebServer().start();
  }
}
