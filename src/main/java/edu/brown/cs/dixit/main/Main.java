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

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
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

import edu.brown.cs.dixit.setting.*;

public class Main {

  private static final int PORT_NUM = 4567;
  private static final Gson GSON = new Gson();

  
  public static void main(String[] args) {
    new Main(args).run();
  }
  
  private String[] args;
  private Deck deck; //need db to save deck for each game
  
  private Main(String[] args) {
    this.args = args;
    this.deck = null;
  }
  
  private void run() {
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    OptionSet options = parser.parse(args);
    if (options.has("gui")) { 
      //deck initialized
      deck.initializeDeck("../img/img");
      runSparkServer();
    }
  }
  
  private static void runSparkServer() {
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());
    FreeMarkerEngine freeMarker = createEngine();
    //Spark.webSocket("/scores", WebSockets.class);
    Spark.get("/",new LogInHandler(), freeMarker);   
  }
  
  // TODO: create a PlayHandler
  private static class LogInHandler implements TemplateViewRoute {
        @Override
        public ModelAndView handle(Request req, Response res) {
            Map<String, Object> variables = ImmutableMap.of("title", "Hello");
            return new ModelAndView(variables, "login.ftl");
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
