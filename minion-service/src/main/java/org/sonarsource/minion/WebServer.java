/*
 * Copyright (C) 2018-2018 SonarSource SA
 * All rights reserved
 * mailto:info AT sonarsource DOT com
 */

package org.sonarsource.minion;

import com.google.gson.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.redirect;
import static spark.Spark.staticFiles;

public class WebServer {

  private static final int DEFAULT_PORT = 9001;

  private static final Logger LOGGER = LoggerFactory.getLogger(WebServer.class);

  private final InputConnector jiraInputConnector = new CachedJiraInputConnector();
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

      if (description != null && !description.isEmpty()) {
        return resultToString(analyzer.analyze(description, component, component_version));
      }

      String message = request.body();
      if (message == null || message.trim().isEmpty()) {
        throw new IllegalArgumentException("Body should not be empty");
      } else {
        return resultToString(analyzer.analyze(message));
      }
    });

    post("/process_message", (request, response) -> {
      String payload = request.body();
      if (payload == null || payload .trim().isEmpty()) {
        throw new IllegalArgumentException("Body should not be empty");
      }

      JsonObject post = new JsonParser().parse(payload).getAsJsonObject().get("post").getAsJsonObject();
      String raw_post = post.get("cooked").getAsString();
      return resultToString(analyzer.analyze(raw_post, "", ""));
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

  public String resultToString(Analyzer.Result result) {
    String message = result.getMessage();
    if (message != null) {
      return result.getMessage();
    } else {
      StringBuilder s = new StringBuilder();
      s.append("JIRA tickets found : " + result.getJiraTickets().stream().map(t -> "<a href=\"https://jira.sonarsource.com/browse/"+t+"\">"+t+"</a>").collect(Collectors.joining("<br/>")));
      s.append("<br/>");
      s.append("Products found : " + result.getProductsVersions().entrySet().stream().map(entry -> entry.getKey() + " - " + entry.getValue()).collect(Collectors.joining("<br/>")));
      s.append("<br/>");
      s.append("Errors found : " + result.getErrorMessages().stream().collect(Collectors.joining("<br/>")));
      return s.toString();
    }
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
