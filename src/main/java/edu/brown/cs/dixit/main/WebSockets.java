package edu.brown.cs.dixit.main;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.brown.cs.dixit.gameManagement.DixitGame;
import edu.brown.cs.dixit.gameManagement.GameTracker;
import edu.brown.cs.dixit.setting.GamePlayer;
import edu.brown.cs.dixit.setting.Player;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

@WebSocket
public class WebSockets {
  private static final Gson GSON = new Gson();
  public static final GameTracker gt = new GameTracker();
  private static int nextId = 1;
  private final Map<String, GamePlayer> uuidToUser;

  private InetSocketAddress ipaddress;
  private static enum MESSAGE_TYPE {
    CONNECT,
    CREATE,
    JOIN,
    ALL_JOINED,
    ST_SUBMIT,
    GS_SUBMIT,
    VOTING
  }

  public WebSockets(){
	  uuidToUser= new ConcurrentHashMap<>();
  }
  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
     //TODO Add the session to the queue
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
	  // TODO Send the CONNECT message
	  session.getRemote().sendString(connectMessage.toString());
	  nextId++;
	  //createNewUser(session);
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
  			DixitGame newGame = new DixitGame(payload.get("game_id").getAsInt(), payload.get("num_players").getAsInt());
  			gt.addGame(session, newGame, payload.get("user_id").getAsInt());
  			newGame.getDeck().initializeDeck("../img/img");
  			//newGame.addPlayer(payload.get("user_id"), payload.get("user_name"), newGame.getDeck());
  			
  			// Java function for making GET request to user's page
  			
  			break;
  		case JOIN:
  			//Dixitgame game = gt.
  			// distribute cards that have not yet been distributed to new player
  			// GET request to user's interface page
  			
  			//int gameId = payload.get("game_id").getAsInt();
  			//gt.addPlayer(session, gameId);
  			
  			// inform all players that all players have joined
  			/*if (gt.getNumPlayers(gameId) == gt.getCapacity(gameId)) {
  				JsonObject allJoinedMessage = new JsonObject();
  				allJoinedMessage.addProperty("type", MESSAGE_TYPE.ALL_JOINED.ordinal());
  				for (Session player : gt.getPlayers(gameId)) {
  					player.getRemote().sendString(allJoinedMessage.toString());
  				}
  			}*/
  			break;
  		case ST_SUBMIT:
  			break;
  		case GS_SUBMIT:
  			break;
  		case VOTING:
  			break;
  	}
  		
  	
  	
  	
	 
	 
	 
	 
	 /*
	
    	}
    }
    
    System.out.printf("score %d \n", score);
    int id = received.get("payload").getAsJsonObject().get("id").getAsInt();
    // TODO Compute the player's score
    
    // TODO Send an UPDATE message to all users
[          
    }*/
  }
  
  private void setCookie(Session s, GamePlayer u, List<HttpCookie> cookies) {
	    JsonObject j = new JsonObject();
	    j.addProperty("json", "setCookie");
	    j.add("cookies", GSON.toJsonTree(cookies));
	    try {
			s.getRemote().sendString(j.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
  private GamePlayer createNewUser(Session s) {

    List<HttpCookie> cookies = s.getUpgradeRequest().getCookies();
    Random rand = new Random();
    int num = rand.nextInt(10);
    System.out.printf("%d id", num);
    String id = rand.toString();
    cookies.add(new HttpCookie("test", id));
    GamePlayer p = new GamePlayer(0, "", null);
    //User u = new User(s);
    uuidToUser.put(id, p);
    setCookie(s, p, cookies);
	return p;
    
  }
}
