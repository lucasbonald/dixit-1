package edu.brown.cs.dixit.main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;


import freemarker.template.Configuration;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

public class Main {

  private static final int PORT_NUM = 4567;
  private static final Gson GSON = new Gson();
  
  
  public static void main(String[] args) {
    runSparkServer();
  }
  
  private static void runSparkServer() {
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());
    FreeMarkerEngine freeMarker = createEngine();
    Spark.webSocket("/scores", WebSockets.class);
    Spark.get("/play",new PlayHandler(), freeMarker);   
  }
  // TODO: create a PlayHandler
  private static class PlayHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) {
            Board board = new Board();
            try { 
              saveNewBoard(board);
            } catch (SQLException e) {
              System.out.println("SQL Error");
            }
            legal = board.play();
            Map<String, Object> variables = ImmutableMap.of("title", "Hello" , "board", board, "guesses", new ArrayList<String>());
            return new ModelAndView(variables, "play.ftl");
        }
  }
  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.\n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private static final int INTERNAL_SERVER_ERROR = 500;
  /** A handler to print an Exception as text into the Response.
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(INTERNAL_SERVER_ERROR);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }
}
