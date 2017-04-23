package edu.brown.cs.dixit.main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import freemarker.template.Configuration;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
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
  //private static final Gson GSON = new Gson();

  public static void main(String[] args) {
    new Main(args).run();
  }
  
  private String[] args;
  private Deck deck; //need db to save deck for each game
  private Map<Integer, Player> playerMap; //keep track of players
  private Referee referee;
  
  private Main(String[] args) {
    this.args = args;
    this.deck = new Deck();
    this.playerMap = new HashMap<Integer, Player>();
    this.referee = new Referee();
  }
  
  private void run() {
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    OptionSet options = parser.parse(args);
    if (options.has("gui")) { 
      //deck initialized
      deck.initializeDeck("../img/img");
      Spark.port(PORT_NUM);
      runSparkServer();
    }
  }
  
  private static void runSparkServer() {
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());
    FreeMarkerEngine freeMarker = createEngine();
    Spark.webSocket("/play", WebSockets.class);
    Spark.get("/", new LogInHandler(), freeMarker);   
  }
  
  private static class LogInHandler implements TemplateViewRoute {
      @Override
      public ModelAndView handle(Request req, Response res) {
          Map<String, Object> variables = ImmutableMap.of("title", "Dixit Online", "imageLink", "../img/img1.png");
          return new ModelAndView(variables, "create_game.ftl");
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
