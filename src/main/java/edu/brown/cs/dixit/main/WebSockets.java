package edu.brown.cs.dixit.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

@WebSocket
public class ScoringWebSocket {
  private static final Gson GSON = new Gson();
  private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
  private static int nextId = 0;

  private static enum MESSAGE_TYPE {
    CONNECT,
    SCORE,
    UPDATE
  }

  @OnWebSocketConnect                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
  public void connected(Session session) throws IOException {
    // TODO Add the session to the queue
    sessions.add(session);
    // TODO Build the CONNECT message
    JsonObject message = new JsonObject();
    JsonObject obj = new JsonObject();
    obj.addProperty("id", nextId);
    message.addProperty("type", MESSAGE_TYPE.CONNECT.ordinal());
    message.add("payload", obj);
    // TODO Send the CONNECT message
    session.getRemote().sendString(message.toString());
    //session.getRemote().sendString("TODO");
    nextId++;
  }

  @OnWebSocketClose
  public void closed(Session session, int statusCode, String reason) {
    // TODO Remove the session from the queue
    sessions.remove(session);
  }

  @SuppressWarnings("unchecked")
  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    JsonObject received = GSON.fromJson(message, JsonObject.class);
    assert received.get("type").getAsInt() == MESSAGE_TYPE.SCORE.ordinal();
    // TODO Compute the player's score
    
    JsonObject payload = received.get("payload").getAsJsonObject();
    System.out.println(payload);
    Board board = new Board(payload.get("board").getAsString().replace("\n","")); 
    List<String> guessed = GSON.fromJson(payload.get("text"), ArrayList.class);
    Set<String> wordList = board.play();
    int score = 0;
    for (String guess: guessed) {
      if (wordList.contains(guess)) {
        int s = Board.score(guess);
        score += s;
      }
    }
    // TODO Send an UPDATE message to all users
    JsonObject update = new JsonObject();
    JsonObject obj = new JsonObject();
    obj.addProperty("id", payload.get("id").getAsInt());
    obj.addProperty("score", score);
    update.addProperty("type", MESSAGE_TYPE.UPDATE.ordinal());
    update.add("payload", obj);
    for (Session s: sessions) {
      s.getRemote().sendString(update.toString());
    }
  }
  
}
