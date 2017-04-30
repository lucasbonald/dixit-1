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

import edu.brown.cs.dixit.gameManagement.*;
import edu.brown.cs.dixit.setting.*;

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
  private DixitGame currGame;
  private String userId;
  private static enum MESSAGE_TYPE {
    CONNECT,
    CREATE,
    JOIN,
    NEW_GAME,
    ALL_JOINED,
    ST_SUBMIT,
    GS_SUBMIT,
    VOTING,
    STATUS,
    MULTI_TAB,
    STORY
  }
  
  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
	System.out.println("curr session hashcode: " + session.hashCode());
	allSessions.add(session);
  	List<HttpCookie> cookies = session.getUpgradeRequest().getCookies();
  	if (cookies == null) {
  		System.out.println("cookie is null, should be redirected to beginning page");
  	} else {
    	this.updateCookies(session, cookies);
    	System.out.println("no. of sessions " + allSessions.size());
  	}
  }

  @OnWebSocketClose
  public void closed(Session session, int statusCode, String reason) {
    // TODO Remove the session from the queue
	System.out.println("session closed");
	System.out.println("before: "+allSessions.size());
	allSessions.remove(session);  
	System.out.println("session removed");
	System.out.println("after: "+allSessions.size());
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
  			
  			//set Storyteller
  			GamePlayer teller = createNewUser(session, newGame, payload.get("user_name").getAsString());
  			teller.setGuesser("False");
  			
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
  			
  			
  			assignRole(session, newGame.getNumPlayers());
  			break;

  		case JOIN:
  			
		    System.out.println("joined!");
  			int gameId = payload.get("game_id").getAsInt();
  			String user = payload.get("user_name").getAsString();
  			DixitGame join = gt.getGame(gameId);
  			createNewUser(session, join, user);
  			assignRole(session, join.getNumPlayers());
  			
  			break;		
  			
  		case ST_SUBMIT:
  			//get the variables
		    System.out.println("Story received");
  			String prompt = payload.get("prompt").getAsString();

  			int cardId = payload.get("card_id").getAsInt();
  			String cardUrl = payload.get("card_url").getAsString();
			JsonObject stMessage = new JsonObject();
			stMessage.addProperty("type", MESSAGE_TYPE.ST_SUBMIT.ordinal());
				
			JsonObject stSubmitPayload = new JsonObject();
			stSubmitPayload.addProperty("prompt", prompt);
			stSubmitPayload.addProperty("card_id", cardId);
			stSubmitPayload.addProperty("card_url", cardUrl);
			stMessage.add("payload", stSubmitPayload);
		    System.out.println("curr:" + currGame);
		    Referee bestRef = currGame.getRefree();
			bestRef.setAnswer(cardId);
			String stId = getSTId(currGame);
			bestRef.setStoryTeller(stId);
			bestRef.setChosen(stId, cardId);
			
			sendMsgtoGame(stMessage.toString());
			
  			this.updateStatus(currGame);
  		  
  			break;
  			
  		case GS_SUBMIT:
		    System.out.println("Guess received");
		    int guessedCard = payload.get("card_id").getAsInt();
		    Referee besRef = currGame.getRefree();
		    besRef.setChosen(userId, guessedCard);
		    if (besRef.getChosenSize() == 2) {
		        JsonObject votingMessage = new JsonObject();
	            votingMessage.addProperty("type", MESSAGE_TYPE.VOTING.ordinal());        
	            JsonObject votePayload = new JsonObject();
	            //JsonObject answer = new JsonObject();
	            //JsonObject guessed = new JsonObject();
	            votePayload.addProperty("answer", besRef.getAnswer());
	            votePayload.addProperty("guessed", besRef.getChosen(userId));
	            votingMessage.add("payload", votePayload);
	            sendMsgtoGame(votingMessage.toString());   
		    }
		    
  			break;
  			
  		case VOTING:
  			System.out.println("Vote received");
  			break;
  	
  		case STORY:
  			JsonObject storyMessage = new JsonObject();
  			JsonObject storyPayload = new JsonObject();
  			storyMessage.addProperty("type", MESSAGE_TYPE.STORY.ordinal());
  			storyPayload.addProperty("storyteller", this.getSTName(currGame));
  			for (Session indivSession : allSessions) {
  				indivSession.getRemote().sendString(storyMessage.toString());
  			}		
  			break;
  	}
  		
  }
 private String randomId(){
	  return UUID.randomUUID().toString();
  }
  
  private GamePlayer createNewUser(Session s, DixitGame game, String user_name) {
	  if (game.getCapacity() > game.getNumPlayers()) {
			List<HttpCookie> cookies = new ArrayList<HttpCookie>();
			String id = randomId();
			cookies.add(new HttpCookie(Network.USER_IDENTIFER, id));
			cookies.add(new HttpCookie(Network.GAME_IDENTIFIER, Integer.toString(game.getId())));					
			//add or override session
			gt.addSession(id, s);	  	
			GamePlayer newPlayer = game.addPlayer(id, user_name);
			sendCookie(s, cookies);
			return newPlayer;
		    
		}
		return null;
	}
  
  private void sendMsgtoGame(String message){
	  for (GamePlayer player: currGame.getPlayers()) {
          try {
			gt.getSession(player.playerId()).getRemote().sendString(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }   
  }
  
  
  private void sendMultiTab(Session s){
	  allSessions.remove(s);
	  JsonObject multi = new JsonObject();
	  multi.addProperty("type", MESSAGE_TYPE.MULTI_TAB.ordinal());
	  try {
		s.getRemote().sendString(multi.toString());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
  
  private void sendCookie(Session s, List<HttpCookie> cookies){
	  JsonObject json = new JsonObject();
	  JsonObject jsonCookie = new JsonObject();
	  json.addProperty("type", "set_uid");
	  jsonCookie.add("cookies", GSON.toJsonTree(cookies));
	  json.add("payload", jsonCookie);
	  try {
		  System.out.println("cookies?");
		  System.out.print(json);
		  s.getRemote().sendString(json.toString());
		} catch (IOException e) {
			System.out.println("Found IOException while sending cookie");
		}
	  
  }
  
  private String getSTName(DixitGame game) {
		for (GamePlayer user : game.getPlayers()) {
			  if (user.getGuesser().equals("False")) {
				  return user.playerName();
			  }
		}
		return "";
  }
  
  private String getSTId(DixitGame game) {
    for (GamePlayer user : game.getPlayers()) {
          if (user.getGuesser().equals("False")) {
              return user.playerId();
          }
    }
    return "";
  }
  
  private void updateStatus(DixitGame game) {
	  JsonObject statusMessage = new JsonObject();
	  JsonObject statusPayload = new JsonObject();
	  statusMessage.addProperty("type", MESSAGE_TYPE.STATUS.ordinal());
		List<String> playernames = new ArrayList<>();
		List<String> statuses = new ArrayList<>();

		for (GamePlayer user : game.getPlayers()) {
			  // need toString override method
			playernames.add(user.playerName());
			statuses.add(user.getStatus());
		}
		System.out.println("playernames and status");
		System.out.println(playernames);
		System.out.println(statuses);

		statusPayload.addProperty("playernames", GSON.toJson(playernames));
		statusPayload.addProperty("statuses", GSON.toJson(statuses));

		statusMessage.add("payload", statusPayload);
		for (Session indivSession : allSessions) {
			try {
				indivSession.getRemote().sendString(statusMessage.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
			  
  }
  private void updateCookies(Session session, List<HttpCookie> cookies){
	  try{
		  for (HttpCookie crumb: cookies) {
		  	  if (crumb.getName().equals("gameid")) {
		  	      currGame = gt.getGame(Integer.parseInt(crumb.getValue()));
		  	  }
		  	  if (crumb.getName().equals("userid")) {
		            userId = crumb.getValue();
		            System.out.printf("session added to %s %d \n", userId, session.hashCode());
		            gt.addSession(userId, session);
		        }
		  	}
		  	if(currGame.getPlayers().size() == currGame.getCapacity()){
		  		List<GamePlayer> users = currGame.getPlayers();
		          for(GamePlayer user:users){
		          	JsonObject allJoinedMessage = new JsonObject();
		              JsonObject playerInfo = new JsonObject();
		              allJoinedMessage.addProperty("type", MESSAGE_TYPE.ALL_JOINED.ordinal());
		              List<Card> personalDeck = user.getFirstHand();
		             // List<Card> personalDeck = user.getHand();
		              JsonObject hand = new JsonObject();
		              for (int i = 0; i < personalDeck.size(); i++){
		                hand.addProperty(String.valueOf(i), personalDeck.get(i).toString());
		              }
		              playerInfo.add("hand", hand);
		              playerInfo.addProperty("storyteller", this.getSTName(currGame));
		              try {
		                  allJoinedMessage.add("payload", playerInfo);
		                  System.out.println("all messages sent");
		                  gt.getSession(user.playerId()).getRemote().sendString(allJoinedMessage.toString());
		                } catch (IOException e) {
		                  System.out.println(e);
		                }   
		          }
		  	}
	  }catch (NullPointerException e) {
			// TODO Auto-generated catch block
		  System.out.println("Find how to refresh Browser");
		  
		}
  }
  
  private void assignRole(Session s, int num){
	  String role="";
		if(num==1){
			role = "teller";
		}else{
			role = "guessor";
		}
		
		JsonObject joinGameMessage = new JsonObject();
		joinGameMessage.addProperty("type", MESSAGE_TYPE.JOIN.ordinal());
		JsonObject joinGamePayload = new JsonObject();
		joinGamePayload.addProperty("role", role);
		joinGameMessage.add("payload",joinGamePayload);
		try {
			s.getRemote().sendString(joinGameMessage.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }
}
