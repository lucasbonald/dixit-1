package edu.brown.cs.dixit.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

@WebSocket
public class WebSockets {
  private static final Gson GSON = new Gson();
  private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
  private static int nextId = 0;
  private InetSocketAddress ipaddress;
  private static enum MESSAGE_TYPE {
    CONNECT,
    UPDATE
  }

  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
    // TODO Add the session to the queued
	  JsonObject json = new JsonObject();
	  JsonObject payload = new JsonObject();
	  if(ipaddress==null){
		  ipaddress = session.getLocalAddress();
		  payload.addProperty("num", 0);
	  }else if(session.getLocalAddress().equals(ipaddress)){
		  payload.addProperty("num", 1);
	  }else{
		  payload.addProperty("num", 0);
	  }
	  payload.addProperty("id", nextId);
	  json.addProperty("type", MESSAGE_TYPE.CONNECT.ordinal());
	  json.add("payload", payload);
	// TODO Send the CONNECT message
	session.getRemote().sendString(json.toString());
    nextId++;
  }

  @OnWebSocketClose
  public void closed(Session session, int statusCode, String reason) {
    // TODO Remove the session from the queue
	  sessions.remove(session);
	  if(sessions.size()==0){
		  ipaddress = null;
	  }
  }

  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
	 int score = 0;
	 /*
	
    	}
    }
    
    System.out.printf("score %d \n", score);
    int id = received.get("payload").getAsJsonObject().get("id").getAsInt();
    // TODO Compute the player's score
    
    // TODO Send an UPDATE message to all users
    JsonObject json = new JsonObject();
    JsonObject payload = new JsonObject();
	payload.addProperty("id", id);
	payload.addProperty("score", score);
	json.addProperty("type", MESSAGE_TYPE.UPDATE.ordinal());
	json.add("payload", payload);
    for(Session single : sessions){
    	single.getRemote().sendString(json.toString());
    }*/
  }
}
