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

  private static final int DEFAULT_PORT = 9001;

  private static final Logger LOGGER = LoggerFactory.getLogger(WebServer.class);

  private final Analyzer analyzer = new Analyzer();

  public void start(int port) {
    port(port);
    LOGGER.info("Listening on port {}", port);

    post("/analyze", (request, response) -> {
      String message = request.body();
      return analyzer.analyze(message);
    });
  }

  public static void main(String[] args) {
    if (args.length > 0){
      String port = args[0];
      new WebServer().start(Integer.parseInt(port));
      return;
    }
    new WebServer().start(DEFAULT_PORT);
  }
}
