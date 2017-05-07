package edu.brown.cs.dixit.main;

import java.io.IOException;
import java.net.HttpCookie;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
  private Referee currRef;
  private String userId;
  private int round = 1;
  private static Connection conn = null;
  private static enum MESSAGE_TYPE {
    CONNECT,
    CREATE,
    JOIN,
    NEW_GAME,
    ALL_JOINED,
    ST_SUBMIT,
    GS_SUBMIT,
    ALL_GUESSES,
    VOTE,
    STATUS,
    MULTI_TAB,
    RESULTS,
    CHAT_UPDATE,
    CHAT_MSG,
    END_OF_ROUND,
    LOAD
  }

  public static void connectDB() throws ClassNotFoundException, SQLException {
      Class.forName("org.sqlite.JDBC");
      String urlToDB = "jdbc:sqlite:" + "chatroom.sqlite3";
      conn = DriverManager.getConnection(urlToDB);
      Statement stat = conn.createStatement();
      stat.executeUpdate("PRAGMA foreign_keys = ON;");
      

      PreparedStatement prep;
      prep = conn.prepareStatement("CREATE TABLE IF NOT EXISTS messages("
    		  + "id INTEGER PRIMARY KEY AUTOINCREMENT, game TEXT," +
    				  "username TEXT, body TEXT, time INTEGER)");
      prep.executeUpdate();
      prep.close();
      
  }
  
  private void saveMessage(String game, String username, String body, Integer time) throws SQLException {
	  PreparedStatement prep;
      prep = conn.prepareStatement("INSERT INTO messages (game, username, body, time) VALUES (?, ?, ?, ?);");
      prep.setString(1, game);
      prep.setString(2, username);
      prep.setString(3, body);
      prep.setInt(4, time);
      prep.executeUpdate();
      prep.close();
  }
  
  
  private void getMessage(String game) throws SQLException {
	  System.out.println("getmessage in java called with gameid " + game);
	  ChatMessage message = new ChatMessage();
	  PreparedStatement prep;
      prep = conn.prepareStatement("SELECT game, username, body, time FROM messages WHERE game = ? ORDER BY time;");
      prep.setString(1, game);
      ResultSet rs = prep.executeQuery(); 
      while (rs.next()) {
    	  message.game.add(rs.getString(1));
    	  message.username.add(rs.getString(2));
    	  message.body.add(rs.getString(3));
    	  message.time.add(rs.getInt(4));
      }
      prep.close();
      
	  JsonObject chatMessage = new JsonObject();
	  JsonObject chatPayload = new JsonObject();
	  System.out.println("message is" + message);
	  chatMessage.addProperty("type", MESSAGE_TYPE.CHAT_UPDATE.ordinal());
	  chatPayload.addProperty("messages", GSON.toJson(message));
	  chatMessage.add("payload", chatPayload);
	  
		for (Session indivSession : allSessions) {
			try {
				indivSession.getRemote().sendString(chatMessage.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	  
  }

  @OnWebSocketConnect
  public void connected(Session session) throws IOException, ClassNotFoundException, SQLException {
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
	//don't we have to remove session in the hashmap as well?
    System.out.println("session closed");
	allSessions.remove(session);  
  }

  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException, SQLException {
  	JsonObject received = GSON.fromJson(message, JsonObject.class);
  	JsonObject payload = received.getAsJsonObject("payload");
  	MESSAGE_TYPE messageType = MESSAGE_TYPE.values()[received.get("type").getAsInt()];
  	GamePlayer teller;
  	switch (messageType) {
  		default:
  			System.out.println(messageType.toString() + ": Unknown message type!");
  			break;
  		case LOAD:
  			JsonObject loadGameMessage = new JsonObject();
  			loadGameMessage.addProperty("type", MESSAGE_TYPE.LOAD.ordinal());
  			JsonObject loadGamePayload = new JsonObject();
  			if(!gt.getAllGame().keySet().isEmpty()){
  				JsonArray gameArray = new JsonArray();
  				JsonObject gameJson = new JsonObject();
  				for(int gameKey:gt.getAllGame().keySet()){
  					DixitGame loadedGame = gt.getAllGame().get(gameKey);
  					gameJson.addProperty("id", loadedGame.getId());
  	  				gameJson.addProperty("name", loadedGame.getName());
  	  				gameJson.addProperty("capacity", loadedGame.getCapacity());
  	  				gameJson.addProperty("player", loadedGame.getNumPlayers());
  	  			}
  				gameArray.add(gameJson);
  				loadGamePayload.add("gamearray",gameArray);
  				loadGameMessage.add("payload", loadGamePayload);
  	  			session.getRemote().sendString(loadGameMessage.toString());
  			}
  			
  			
  			break;
  		case CREATE:
  			//game created
  			System.out.println("new game created!");
  			int newGameId = gt.createGameID();
  			DixitGame newGame = new DixitGame(newGameId, payload.get("num_players").getAsInt(), payload.get("victory_pts").getAsInt(), payload.get("lobby_name").getAsString());
  			//need to initialize the game with additional features like more cards / input types
  			gt.addGame(newGame);
  			newGame.getDeck().initializeDeck("../img/img");
  			//set Storyteller
  			teller = createNewUser(session, newGame, payload.get("user_name").getAsString());
  			newGame.setST(teller.playerId());
  			newGame.addStatus(teller.playerId(), "Storytelling");
  			//send message
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
  			
  			//fix needed
  			assignRole(session, 1);
  			break;

  		case JOIN:
  			
		    System.out.println("joined!");
  			int gameId = payload.get("game_id").getAsInt();
  			String user = payload.get("user_name").getAsString();
  			DixitGame join = gt.getGame(gameId);
  			GamePlayer joiner = createNewUser(session, join, user);
  			join.addStatus(joiner.playerId(), "Waiting");
  			
  			
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
		    currRef = currGame.getRefree();
			currRef.receiveStory(prompt, currGame.getST(), cardId);
			currRef.setChosen(currGame.getST(), cardId);	
			
			teller  = currGame.getPlayer(currGame.getST());
			teller.removeCard(cardId);
			
			for (GamePlayer player : currGame.getPlayers()){
				currGame.addStatus(player.playerId(), "Guessing");
			}
			currGame.addStatus(currGame.getST(), "Waiting");
			sendMsgToGame(stMessage.toString());
			updateStatus(currGame);
  		  
  			break;
  			
  		case GS_SUBMIT:
  		  
		    System.out.println("Guess received");
		    int guessedCard = payload.get("card_id").getAsInt();
		    GamePlayer guesser = currGame.getPlayer(payload.get("user_id").getAsString());
		    guesser.removeCard(guessedCard);
		    currRef = currGame.getRefree();
		    currRef.setChosen(userId, guessedCard);
		    
		    System.out.println("num guesses: " + currRef.getChosenSize());
		    System.out.println("capacity: " + currGame.getCapacity());
		    
		    if (currRef.getChosenSize() == currGame.getCapacity()) {
	    		System.out.println("all guesses received");
    	        JsonObject allGuessesMessage = new JsonObject();
    	        allGuessesMessage.addProperty("type", MESSAGE_TYPE.ALL_GUESSES.ordinal());        
                JsonObject guessesPayload = new JsonObject();
                guessesPayload.addProperty("answer", currRef.getAnswer());
                guessesPayload.addProperty("guessed", currRef.getChosen(userId));
                allGuessesMessage.add("payload", guessesPayload);
                sendMsgToGame(allGuessesMessage.toString());
          
                for (GamePlayer player : currGame.getPlayers()){
                  currGame.addStatus(player.playerId(), "Voting");
                }
                currGame.addStatus(currGame.getST(), "Waiting");
                updateStatus(currGame);
		    }
  			break;
  			
  		case VOTE:
  			System.out.println("Vote received");
  			int vote = payload.get("card_id").getAsInt();
  			String voterId = payload.get("user_id").getAsString();
  			GamePlayer voter = currGame.getPlayer(voterId);
  			currGame.addStatus(voterId, "Voted");
  			updateStatus(currGame);
  			
  			currRef = currGame.getRefree();
  			currRef.receiveVotes(voterId, vote);
  			
  			JsonObject voteUpdate = new JsonObject();
  			voteUpdate.addProperty("type", MESSAGE_TYPE.VOTE.ordinal());
  			JsonObject voteInfo = new JsonObject();
  			voteInfo.addProperty("card_id", vote);
  			voteInfo.addProperty("user_name", voter.playerName());
  			voteUpdate.add("payload", voteInfo);
  			sendMsgToGame(voteUpdate.toString());
  			
  			if (currRef.getPickedSize() == currGame.getCapacity() - 1) {
  				System.out.println("all voting done!");
  				Map<String, Integer> result = currRef.tallyScores();
                JsonObject voteResult = new JsonObject();
                voteResult.addProperty("type", MESSAGE_TYPE.RESULTS.ordinal());
                JsonObject resultInfo = new JsonObject();
  				// new Storyteller
                currGame.nextTurn();
  				JsonObject stInfo = getSTdetails();
                
  				JsonObject points = new JsonObject();
                for (String key: result.keySet()) {
  				  points.addProperty(key, result.get(key));
  				}
                resultInfo.add("points", points);
                
  				for (String key: result.keySet()) {
  				  resultInfo.add("storyteller", stInfo);
  				  GamePlayer currPlayer = currGame.getPlayer(key);
  				  
  				  List<Card> personalDeck = currPlayer.refillCard();
  				  JsonObject hand = new JsonObject();
                  for (int i = 0; i < personalDeck.size(); i++) {
                    hand.addProperty(String.valueOf(i), personalDeck.get(i).toString());
                  }
                  resultInfo.add("hand", hand);
                  voteResult.add("payload", resultInfo);
                  gt.getSession(key).getRemote().sendString(voteResult.toString());
                  
                  // update player statuses
  				  String currPlayerId = currPlayer.playerId();
  				  if (currPlayerId == stInfo.get("user_id").getAsString()) {
  					  System.out.println(String.valueOf(currPlayer) + " now storytelling");
  					  currGame.addStatus(currPlayerId, "Storytelling");
  				  } else {
  					System.out.println(String.valueOf(currPlayer) + " now waiting");
  					  currGame.addStatus(currPlayerId, "Waiting");
  				  }
  				}
  				
  				updateStatus(currGame);
  			}
  			break;
  			
  		case CHAT_MSG:
  			String body = payload.get("body").getAsString();
  			Integer time = payload.get("time").getAsInt();
  			String game = this.getRoomId(session);
  			String username = this.getUsername(session);
  			this.saveMessage(game, username, body, time);
  			this.getMessage(game);  		    
  	}
  }
  
  
 private String getRoomId(Session s) {
	 List<HttpCookie> cookies = s.getUpgradeRequest().getCookies();
	  for (HttpCookie crumb: cookies) {
	  	  if (crumb.getName().equals("gameid")) {
	  		  return crumb.getValue();
	  	  } 
	  }
	  return null;
 }
 
 private String getUsername(Session s) {
	 List<HttpCookie> cookies = s.getUpgradeRequest().getCookies(); 
	 String username = "no player found";
	  for (HttpCookie crumb: cookies) {
	  	  if (crumb.getName().equals("gameid")) {
	  	      currGame = gt.getGame(Integer.parseInt(crumb.getValue()));
	  	  }
	  	  if (crumb.getName().equals("userid")) {
	            userId = crumb.getValue();
	            username = currGame.getPlayer(userId).playerName();
	      }
	  	}
	  return username;
	 

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
			gt.addSession(id, s);	  	
			GamePlayer newPlayer = game.addPlayer(id, user_name);
			sendCookie(s, cookies);
			return newPlayer;		    
		}
		return null;
	}
  
  private void sendMsgToGame(String message) {
	  for (GamePlayer player: currGame.getPlayers()) {
          try {
			gt.getSession(player.playerId()).getRemote().sendString(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }   
  }
  
  private void sendCookie(Session s, List<HttpCookie> cookies){
	  JsonObject json = new JsonObject();
	  JsonObject jsonCookie = new JsonObject();
	  json.addProperty("type", "set_uid");
	  jsonCookie.add("cookies", GSON.toJsonTree(cookies));
	  json.add("payload", jsonCookie);
	  try {
		  //System.out.println("cookies?");
		  //System.out.print(json);
		  s.getRemote().sendString(json.toString());
		} catch (IOException e) {
			System.out.println("Found IOException while sending cookie");
		}
	  
  }
  
  private void updateStatus(DixitGame game) {
	  JsonObject statusMessage = new JsonObject();
	  JsonObject statusPayload = new JsonObject();
	  statusMessage.addProperty("type", MESSAGE_TYPE.STATUS.ordinal());
		List<String> playerIds = new ArrayList<>();
		List<String> statuses = new ArrayList<>();

		for (GamePlayer user : game.getPlayers()) {
			  // need toString override method
			playerIds.add(user.playerId());
			statuses.add(game.getStatus(user.playerId()));
		}

		statusPayload.addProperty("player_ids", GSON.toJson(playerIds));
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
		            //System.out.printf("session added to %s %d \n", userId, session.hashCode());
		            gt.addSession(userId, session);
		      }
		  	}
		  
		  	System.out.println("num players currently: " + currGame.getPlayers().size());
		  	if(currGame.getPlayers().size() == currGame.getCapacity()){
		  		
		  		System.out.println("all joined");
		  		
		  		Collection<GamePlayer> users = currGame.getPlayers();
		  		
		  		JsonObject players = new JsonObject();
                int playerCount = 0;
		  		for (GamePlayer user_temp : users) {
		  			playerCount++;
		  			JsonObject player = new JsonObject();
		  			player.addProperty("user_name", user_temp.playerName());
		  			player.addProperty("user_id", user_temp.playerId());
		  			players.add(String.valueOf(playerCount), player);
		  		}
		  		
		  		for(GamePlayer user:users) {
		  			
		  			// define new ALL_JOINED message object
		  			JsonObject allJoinedMessage = new JsonObject();
	                JsonObject playerInfo = new JsonObject();
	                allJoinedMessage.addProperty("type", MESSAGE_TYPE.ALL_JOINED.ordinal());
	                
	                // add information about all players
	                playerInfo.add("players", players);
                  	
	                // add hand information
                    List<Card> personalDeck = user.getFirstHand();
                    JsonObject hand = new JsonObject();
                    for (int i = 0; i < personalDeck.size(); i++){
                      hand.addProperty(String.valueOf(i), personalDeck.get(i).toString());
                    }
                    playerInfo.add("hand", hand);
                    
                    // add information about storyteller
                    JsonObject stInfo = getSTdetails();
                    playerInfo.add("storyteller", stInfo);
                    
                    // send message to all players
                    try {
                      allJoinedMessage.add("payload", playerInfo);
                      System.out.println("all messages sent");
                      gt.getSession(user.playerId()).getRemote().sendString(allJoinedMessage.toString());
                    } catch (IOException e) {
                      System.out.println(e);
                    }   
                  }
		  		updateStatus(currGame);
		  		round++;
		  	}
	  } catch (NullPointerException e) {
			// TODO Auto-generated catch block
		  System.out.println("Find how to refresh Browser");
		  
	  }
  }
  
  private JsonObject getSTdetails() {
    JsonObject stInfo = new JsonObject();
    GamePlayer st = currGame.getPlayer(currGame.getST());
    stInfo.addProperty("user_name", st.playerName());
    stInfo.addProperty("user_id", st.playerId());
    return stInfo;
  }

  private void assignRole(Session s, int num){
	  String role="";
		if (num==1) {
			role = "teller";
		} else {
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
