package edu.brown.cs.dixit.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.brown.cs.dixit.gameManagement.DixitGame;
import edu.brown.cs.dixit.gameManagement.GameTracker;
import edu.brown.cs.dixit.setting.Card;
import edu.brown.cs.dixit.setting.GamePlayer;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

@WebSocket
public class WebSockets {
  private static final Gson GSON = new Gson();
  private static final GameTracker gt = new GameTracker();
  private static final Queue<Session> allSessions = new ConcurrentLinkedQueue<>();
  private static int nextId = 1;

  private InetSocketAddress ipaddress;
  private static enum MESSAGE_TYPE {
    CONNECT,
    CREATE,
    JOIN,
    GAME_JOINED,
    ALL_JOINED,
    ST_SUBMIT,
    GS_SUBMIT,
    VOTING
  }

  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
    // TODO Add the session to the queue
  	System.out.println(session);
  	allSessions.add(session);
	  JsonObject connectMessage = new JsonObject();
	  connectMessage.addProperty("type", MESSAGE_TYPE.CONNECT.ordinal());
	  JsonObject payload = new JsonObject();
	  payload.addProperty("user_id", nextId);
	  connectMessage.add("payload", payload);
	  
	  ipaddress = session.getLocalAddress();	  
	  if (ipaddress == null){
		  ipaddress = session.getLocalAddress();
		  payload.addProperty("num", 0);
	  } else if (session.getLocalAddress().equals(ipaddress)){
		  payload.addProperty("num", 1);
	  } else {
		  payload.addProperty("num", 0);	
	  }
	  
	  System.out.println(session.toString());
	  System.out.println(session.getLocalAddress().toString());
	  System.out.println(session.getRemoteAddress().toString());
		// TODO Send the CONNECT message
		session.getRemote().sendString(connectMessage.toString());
    nextId++;
    
    
    
  }

  @OnWebSocketClose
  public void closed(Session session, int statusCode, String reason) {
    // TODO Remove the session from the queue
	  //gt.removePlayer(session);
	  
//	  if(sessions.size()==0){
//		  ipaddress = null;
//	  }
  }

  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
  	JsonObject received = GSON.fromJson(message, JsonObject.class);
  	JsonObject payload = received.getAsJsonObject("payload");
  	MESSAGE_TYPE messageType = MESSAGE_TYPE.values()[received.get("type").getAsInt()];
  	
  	switch (messageType) {
  		default:
  			System.out.println("Unknown message type!");
  			break;
  		case CREATE:
  			int newGameId = payload.get("game_id").getAsInt();
  			DixitGame newGame = new DixitGame(newGameId, payload.get("num_players").getAsInt());
  			gt.addGame(session, newGame, payload.get("user_id").getAsInt());
  			newGame.getDeck().initializeDeck("../img/img");
  			newGame.addPlayer(payload.get("user_id").getAsInt(), payload.get("user_name").getAsString(), newGame.getDeck());
  			
  			JsonObject newGameMessage = new JsonObject();
  			newGameMessage.addProperty("type", MESSAGE_TYPE.GAME_JOINED.ordinal());
  			JsonObject newGamePayload = new JsonObject();
  			newGamePayload.addProperty("game_id", newGameId);
  			newGamePayload.addProperty("lobby_name", payload.get("lobby_name").getAsString());
  			newGamePayload.addProperty("num_players", newGame.getNumPlayers());
  			newGamePayload.addProperty("capacity", newGame.getCapacity());
  			newGameMessage.add("payload", newGamePayload);
  			
  			for (Session indivSession : allSessions) {
  				indivSession.getRemote().sendString(newGameMessage.toString());
  			}
  			
  			break;
  			
  		case JOIN:
  			int gameId = payload.get("game_id").getAsInt();
  			DixitGame join = gt.getGame(gameId);
  			if (join.getCapacity() != join.getNumPlayers()) {
  				join.addPlayer(payload.get("user_id").getAsInt(), payload.get("user_name").getAsString(), join.getDeck());
    			if (join.getCapacity() == join.getNumPlayers()) {
    				for (GamePlayer player : join.getPlayers()) {
    					List<Card> firstHand = player.getFirstHand();
    				}
    			}
  			}
  		
  			
  			
  			
  			// distribute cards that have not yet been distributed to new player
  			// GET request to user's interface pages
  			
  			
  			// inform all players that all players have joined
  			if (gt.getNumPlayers(gameId) == gt.getCapacity(gameId)) {
  				JsonObject allJoinedMessage = new JsonObject();
  				allJoinedMessage.addProperty("type", MESSAGE_TYPE.ALL_JOINED.ordinal());
  				for (Session player : gt.getPlayers(gameId)) {
  					player.getRemote().sendString(allJoinedMessage.toString());
  				}
  			}
  			break;
  		case ST_SUBMIT:
  			String prompt = payload.get("prompt").getAsString();
  			System.out.println(prompt);
  			break;
  		case GS_SUBMIT:
  			break;
  		case VOTING:
  			break;
  	}
  		
  }
}
