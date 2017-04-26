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
import edu.brown.cs.dixit.setting.Referee;

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
    STATUS,
    MULTI_TAB,
    STORY
  }
  
  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
	allSessions.add(session);
  	List<HttpCookie> cookies = session.getUpgradeRequest().getCookies();
  	int gameId  = 0;
  	//String userId = "";
  	if (cookies != null) {
  	    System.out.println("cookies: " + cookies.toString());
    	for (HttpCookie crumb: cookies) {
    	  if (crumb.getName().equals("gameid")) {
    	      gameId = Integer.parseInt(crumb.getValue());
    	  }
    	  
          if (crumb.getName().equals("userid")) {
              //userId = crumb.getValue();
              gt.addSession(crumb.getValue(), session);
          }
    	}
  	}
    if (gt.getAllGame().size() != 0) {
      System.out.println(gameId);
      System.out.println(gt.getGame(gameId));
      if (gt.getGame(gameId) != null && gt.getGame(gameId).getPlayers() != null) {
        List<GamePlayer> users = gt.getGame(gameId).getPlayers();
        for (GamePlayer user: users) {
          //Session s = gt.getSession(user.playerId());
          JsonObject allJoinedMessage = new JsonObject();
          JsonObject playerInfo = new JsonObject();
          allJoinedMessage.addProperty("type", MESSAGE_TYPE.ALL_JOINED.ordinal());
          List<Card> personalDeck = user.getHand();
          JsonObject hand = new JsonObject();
          for (int i = 0; i < personalDeck.size(); i++){
            hand.addProperty(String.valueOf(i), personalDeck.get(i).toString());
          }    
          playerInfo.add("hand", hand);
          playerInfo.addProperty("storyteller", this.getSTName(this.getGameFromSession(session)));
          try {
            allJoinedMessage.add("payload", playerInfo);
            gt.getSession(user.playerId()).getRemote().sendString(allJoinedMessage.toString());
          } catch (IOException e) {
            System.out.println(e);
          }
        }
      }
    }
  	System.out.println("no. of sessions " + allSessions.size());
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
		    DixitGame currGame = getGameFromSession(session);
		    Referee bestRef = currGame.getRefree();
			bestRef.setAnswer(cardId);
			String stId = getSTId(getGameFromSession(session));
			bestRef.setStoryTeller(stId);
			bestRef.setChosen(stId, cardId);
			
  			for (Session indivSession : allSessions) {
  				indivSession.getRemote().sendString(stMessage.toString());
  			}	
  			this.updateStatus(this.getGameFromSession(session));
  		  
  			break;
  		case GS_SUBMIT:
		    System.out.println("Guess received");
		    List<HttpCookie> cookies = session.getUpgradeRequest().getCookies();
		    String userId = "";
		    if (cookies != null) {
		        for (HttpCookie crumb: cookies) {
		          if (crumb.getName().equals("userid")) {
		              userId = crumb.getValue();
		          }
		        }
		    }
		    int guessedCard = payload.get("card_id").getAsInt();
		    DixitGame curGame = getGameFromSession(session);
            Referee besRef = curGame.getRefree();
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
	            for (GamePlayer player: curGame.getPlayers()) {
	              gt.getSession(player.playerId()).getRemote().sendString(votingMessage.toString());
	            }   
		    }
		    
  			break;
  			
  		case VOTING:
  			System.out.println("Vote received");
  			break;
  	
  		case STORY:
  			JsonObject storyMessage = new JsonObject();
  			JsonObject storyPayload = new JsonObject();
  			storyMessage.addProperty("type", MESSAGE_TYPE.STORY.ordinal());
  			storyPayload.addProperty("storyteller", this.getSTName(this.getGameFromSession(session)));
  			for (Session indivSession : allSessions) {
  				indivSession.getRemote().sendString(storyMessage.toString());
  			}		
  			break;
  	}
  		
  }
 
  private DixitGame getGameFromSession(Session s) {
	  	List<HttpCookie> cookies = s.getUpgradeRequest().getCookies();
	  	String gameId = "";
	  	if (cookies != null) {
	  		for(HttpCookie singcook : cookies){
	  			if(singcook.getName().equals("gameid")){
	  				gameId = singcook.getValue();
		  		}
		  	}
		}
	  	
	  	return gt.getGame(Integer.parseInt(gameId));
  }
  
  private String randomId(){
	  return UUID.randomUUID().toString();
  }
  
  private GamePlayer createNewUser(Session s, DixitGame game, String user_name) {
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
		}
	  	cookies.add(new HttpCookie(Network.GAME_IDENTIFIER, Integer.toString(game.getId())));
			
	  	//add or override session
	  	gt.addSession(id, s);
	  	
	    if (game.getCapacity() > game.getNumPlayers()) {
				GamePlayer newPlayer = game.addPlayer(id, user_name);
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

	  			  System.out.println(gt.getSession().size());
	  			  
	  			  // construct JSON object for first hand of cards
	  			  List<Card> firstHand = user.getFirstHand();
	  			  JsonObject hand = new JsonObject();
	  			  for (int i = 0; i < firstHand.size(); i++){
	  			  	hand.addProperty(String.valueOf(i), firstHand.get(i).toString());
	  			  }
	  			  	
            playerInfo.add("hand", hand);
            playerInfo.addProperty("storyteller", getSTId(getGameFromSession(s)));
	  			  try {
	  				System.out.println("all player message sent");
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
		  System.out.print(cookies);
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
}
