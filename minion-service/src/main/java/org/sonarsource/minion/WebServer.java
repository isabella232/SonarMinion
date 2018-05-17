/*
 * Copyright (C) 2018-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */

package org.sonarsource.minion;

import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.exception;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.redirect;
import static spark.Spark.staticFiles;

public class WebServer {

  private static final int DEFAULT_PORT = 9001;

  private static final Logger LOGGER = LoggerFactory.getLogger(WebServer.class);

  private final Analyzer analyzer = new Analyzer();

  public void start(int port) {
    port(port);
    LOGGER.info("Listening on port {}", port);

    staticFiles.location("img");

    redirect.get("*", "/minion.png");

    post("/analyze", (request, response) -> {
      String message = request.body();
      if (message == null || message.trim().isEmpty()) {
        throw new IllegalArgumentException("Body should not be empty");
      }
      return analyzer.analyze(message);
    });

    exception(IllegalArgumentException.class, (e, req, res) -> {
      res.status(400);
      res.body(e.getMessage());
    });

    exception(JsonSyntaxException.class, (e, req, res) -> {
      res.status(400);
      res.body(e.getMessage());
    });
  }

  public static void main(String[] args) {
    if (args.length > 0) {
      String port = args[0];
      new WebServer().start(Integer.parseInt(port));
      return;
    }
    new WebServer().start(DEFAULT_PORT);
  }
}
