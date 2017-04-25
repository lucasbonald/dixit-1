package edu.brown.cs.dixit.main;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.brown.cs.dixit.gameManagement.DixitGame;
import edu.brown.cs.dixit.gameManagement.GameTracker;
import edu.brown.cs.dixit.setting.Card;
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
  private static final GameTracker gt = new GameTracker();
  private static final Queue<Session> allSessions = new ConcurrentLinkedQueue<>();
  private static int nextId = 1;

  private InetSocketAddress ipaddress;
  private static enum MESSAGE_TYPE {
    CONNECT,
    CREATE,
    JOIN,
    NEW_GAME,
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
  	
  	//this should check current status -- cookies    
    
    
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
  			//game created
  			int newGameId = gt.createID();
  			DixitGame newGame = new DixitGame(newGameId, payload.get("num_players").getAsInt());
  			//need to initialize the game with all information like victory points
  			//gt.addGame(session, newGame, payload.get("user_id").getAsInt());
  			gt.addGame(session, newGame);
  			newGame.getDeck().initializeDeck("../img/img");
  			
  			//now user should be created
  			createNewUser(session, newGame, payload.get("user_name").getAsString());
  			
  			JsonObject newGameMessage = new JsonObject();
  			newGameMessage.addProperty("type", MESSAGE_TYPE.NEW_GAME.ordinal());
  			JsonObject newGamePayload = new JsonObject();
  			newGamePayload.addProperty("game_id", newGameId);
  			newGamePayload.addProperty("lobby_name", payload.get("lobby_name").getAsString());
  			newGamePayload.addProperty("num_players", newGame.getNumPlayers());
  			newGamePayload.addProperty("capacity", newGame.getCapacity());
  			newGameMessage.add("payload", newGamePayload);
  			
  			for (Session indivSession : allSessions) {
  				System.out.println(allSessions.size());
  				indivSession.getRemote().sendString(newGameMessage.toString());
  			}
  			
  			break;
  			
  		case JOIN:
  		  System.out.println("joined!");
  			int gameId = payload.get("game_id").getAsInt();
  			String user = payload.get("user_name").getAsString();
  			DixitGame join = gt.getGame(gameId);
  			if (join.getCapacity() != join.getNumPlayers()) {
  				join.addPlayer(payload.get("user_id").getAsInt(), payload.get("user_name").getAsString(), join.getDeck());
    			if (join.getCapacity() == join.getNumPlayers()) {
    				for (GamePlayer player : join.getPlayers()) {
    					List<Card> firstHand = player.getFirstHand();
    				}
    			}
  			}
  			createNewUser(session, join, user);
  			
 
  			// distribute cards that have not yet been distributed to new player
  			// GET request to user's interface pages
  			
  			
  			// inform all players that all players have joined
  			
  			break;
  		case ST_SUBMIT:
  			//get the variables
  			String prompt = payload.get("prompt").getAsString();
  			int answer = payload.get("answer").getAsInt();
  			System.out.println(prompt);
  			
  			
			JsonObject stMessage = new JsonObject();
			stMessage.addProperty("type", MESSAGE_TYPE.ST_SUBMIT.ordinal());
			
			JsonObject returnPayload = new JsonObject();
			returnPayload.addProperty("prompt", prompt);
			returnPayload.addProperty("answer", answer);
			stMessage.add("payload", returnPayload);
			  
  			// build object
  			//send the prompt and answer cardid to all players
//			for (Session player : gt.getPlayers(gameId)) {
//				player.getRemote().sendString(stMessage.toString());
//			}
  			break;
  		case GS_SUBMIT:
  			break;
  		case VOTING:
  			break;
  	}
  		
  }
  
  private String randomId(){
	  return UUID.randomUUID().toString();
  }
  
  private Player createNewUser(Session s, DixitGame game, String user_name) {
	  	List<HttpCookie> cookies = s.getUpgradeRequest().getCookies();
	  	String id = randomId();
	    cookies.add(new HttpCookie(Network.USER_IDENTIFER, id));
	    cookies.add(new HttpCookie(Network.GAME_IDENTIFIER, Integer.toString(game.getId())));
	    if (game.getCapacity() > game.getNumPlayers()) {
				Player newplayer = game.addPlayer(id, user_name);
			if (game.getCapacity() == game.getNumPlayers()) {
				for (GamePlayer player : game.getPlayers()) {
					List<Card> firstHand = player.getFirstHand();
				}
				JsonObject allJoinedMessage = new JsonObject();
	  			allJoinedMessage.addProperty("type", MESSAGE_TYPE.ALL_JOINED.ordinal());
	  				
	  			for (Session player : gt.getPlayers(game.getId())) {
	  				try {
						player.getRemote().sendString(allJoinedMessage.toString());
					} catch (IOException e) {
						System.out.println("Can't inform the game is full");
					}
	  			}
	  		}
			sendCookie(s, cookies);
			return newplayer;
		    
		}
		//uuidToUser.put(id, p);
	    return null;
	}
  
  private void sendCookie(Session s, List<HttpCookie> cookies){
	  JsonObject json = new JsonObject();
	  JsonObject jsonCookie = new JsonObject();
	  json.addProperty("type", "set_uid");
	  jsonCookie.add("cookies", GSON.toJsonTree(cookies));
	  json.add("payload", jsonCookie);
	  try {
		  System.out.println("cookies");
		  System.out.printf("%s, \n", json.toString());
		s.getRemote().sendString(json.toString());
	} catch (IOException e) {
		System.out.println("Found IOException while sending cookie");
	}
	  
  }
}
