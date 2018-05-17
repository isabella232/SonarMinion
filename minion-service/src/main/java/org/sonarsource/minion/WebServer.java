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
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.redirect;
import static spark.Spark.staticFiles;

import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;
import java.util.Map;


public class WebServer {

  private static final int DEFAULT_PORT = 9001;

  private static final Logger LOGGER = LoggerFactory.getLogger(WebServer.class);

  private final JiraInputConnector jiraInputConnector = new JiraInputConnector();
  private final Qualifier qualifier = new Qualifier();
  private final Analyzer analyzer = new Analyzer(qualifier, jiraInputConnector);


  public void start(int port) {
    port(port);
    LOGGER.info("Listening on port {}", port);

    staticFiles.location("img");

      get("/", (request, response) -> {
          Map<String, Object> model = new HashMap<>();
          model.put("message", "Velocity World");

          // The vm files are located under the resources directory
          return new ModelAndView(model, "hello.vm");
      }, new VelocityTemplateEngine());


    redirect.get("*", "/minion.png");


    post("/analyze", (request, response) -> {
      String description = request.queryParams("description");
      String component = request.queryParams("component");
      String component_version = request.queryParams("component_version");

      if (description !=null && !description.isEmpty()){
        return analyzer.analyze(description, component, component_version);
      }

      String message = request.body();
      if (message == null || message.trim().isEmpty()) {
        throw new IllegalArgumentException("Body should not be empty");
      }
      else {
        return analyzer.analyze(message);
      }
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
