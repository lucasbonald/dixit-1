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
  
  private InetSocketAddress ipaddress;
  private static enum MESSAGE_TYPE {
    CONNECT,
    CREATE,
    JOIN,
    NEW_GAME,
    ALL_JOINED,
    ST_SUBMIT,
    GS_SUBMIT,
    VOTING,
    STATUS
  }
  
  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
    // TODO Add the session to the queue
  	allSessions.add(session);
  	System.out.println("no. of sessions " + allSessions.size());
  	System.out.println("session size on connect");
  	System.out.printf("%d \n", allSessions.size());
  	List<HttpCookie> cookies = session.getUpgradeRequest().getCookies();
  	if (cookies != null) {
  	    System.out.println("cookies: " + cookies.toString());
    	for (HttpCookie crumb: cookies) {
          if (crumb.getName().equals("userid")) {
            System.out.print("session added on connect");
            gt.addSession(crumb.getValue(), session);
          }
    	}
  	}
  }

  @OnWebSocketClose
  public void closed(Session session, int statusCode, String reason) {
    // TODO Remove the session from the queue
	  System.out.println("session closed");
	allSessions.remove(session);  
	
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
  			System.out.println("new game created!");
  			int newGameId = gt.createGameID();
  			DixitGame newGame = new DixitGame(newGameId, payload.get("num_players").getAsInt());
  			//need to initialize the game with all information like victory points
  			gt.addGame(newGame);
  			newGame.getDeck().initializeDeck("../img/img");
  			
  			//now user should be created
  			System.out.println("user_name: "+ payload.get("user_name").getAsString());
  			createNewUser(session, newGame, payload.get("user_name").getAsString());
  			
  			JsonObject newGameMessage = new JsonObject();
  			newGameMessage.addProperty("type", MESSAGE_TYPE.NEW_GAME.ordinal());
  			JsonObject newGamePayload = new JsonObject();
  			newGamePayload.addProperty("game_id", newGameId);
  			newGamePayload.addProperty("lobby_name", payload.get("lobby_name").getAsString());
  			newGamePayload.addProperty("num_players", newGame.getNumPlayers());
  			newGamePayload.addProperty("capacity", newGame.getCapacity());
  			newGameMessage.add("payload", newGamePayload);
  			
  			//need db to keep track of all the lobbies
  			for (Session indivSession : allSessions) {
  				indivSession.getRemote().sendString(newGameMessage.toString());
  			}		
  			break;
  			
  		case JOIN:
  		    
  		    System.out.println("joined!");
  			int gameId = payload.get("game_id").getAsInt();
  			String user = payload.get("user_name").getAsString();
  			DixitGame join = gt.getGame(gameId);
  			createNewUser(session, join, user);
  			break;			
  		
  		case ST_SUBMIT:
  			//get the variables
  			System.out.println("got here baby");
  			String prompt = payload.get("prompt").getAsString();
  			int answer = payload.get("answer").getAsInt();
  			System.out.println(prompt);
  			System.out.println("answer is " + answer);

  			
			JsonObject stMessage = new JsonObject();
			stMessage.addProperty("type", MESSAGE_TYPE.ST_SUBMIT.ordinal());
			
			JsonObject stsubmitPayload = new JsonObject();
			stsubmitPayload.addProperty("prompt", prompt);
			stsubmitPayload.addProperty("answer", answer);
			stMessage.add("payload", stsubmitPayload);
			
  			for (Session indivSession : allSessions) {
  				indivSession.getRemote().sendString(stMessage.toString());
  			}	
			  
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
	  	boolean hasUserId=false;
	  	String id = "";
	  	if (cookies != null) {
	  		for(HttpCookie singcook : cookies){
	  			if(singcook.getName().equals("userid")){
		  			System.out.println("already has user id");
		  			id = singcook.getValue();
		  			hasUserId=true;
		  		}
		  	}
		}
	  	if (!hasUserId) {
	  		cookies = new ArrayList<HttpCookie>();
	  		id = randomId();
	  		System.out.println("new user id created");
	  		cookies.add(new HttpCookie(Network.USER_IDENTIFER, id));
		    cookies.add(new HttpCookie(Network.GAME_IDENTIFIER, Integer.toString(game.getId())));
		}
	  	
	  	//add or override session
	  	gt.addSession(id, s);
	  	System.out.println(gt.getSession().values().toString());
	  	
	    if (game.getCapacity() > game.getNumPlayers()) {
				Player newPlayer = game.addPlayer(id, user_name);
			if (game.getCapacity() == game.getNumPlayers()) {
				for (GamePlayer player : game.getPlayers()) {
					player.getFirstHand();
				}
				JsonObject allJoinedMessage = new JsonObject();
				JsonObject playerInfo = new JsonObject();
	  			allJoinedMessage.addProperty("type", MESSAGE_TYPE.ALL_JOINED.ordinal());
	  			//should be sending the information about cards	
	  			for (GamePlayer user : game.getPlayers()) {
	  			  // need toString override method
	              playerInfo.addProperty("deck", user.getFirstHand().toString()) ;
	  			  try {
	  			    allJoinedMessage.add("payload", playerInfo);
	  			    gt.getSession(user.playerId()).getRemote().sendString(allJoinedMessage.toString());
	  			  } catch (IOException e) {
	  			    System.out.println(e);
	  			  }
	  			}
	  		}
			sendCookie(s, cookies);
			return newPlayer;
		    
		}
		return null;
	}
  
  private void sendCookie(Session s, List<HttpCookie> cookies){
	  JsonObject json = new JsonObject();
	  JsonObject jsonCookie = new JsonObject();
	  json.addProperty("type", "set_uid");
	  jsonCookie.add("cookies", GSON.toJsonTree(cookies));
	  json.add("payload", jsonCookie);
	  try {
		  s.getRemote().sendString(json.toString());
		} catch (IOException e) {
			System.out.println("Found IOException while sending cookie");
		}
	  
  }
  
  private void updateStatus() {
	  JsonObject statusMessage = new JsonObject();
  }
}
