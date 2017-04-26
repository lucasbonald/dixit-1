package edu.brown.cs.dixit.gameManagement;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;

public class GameTracker {

  private static final Map<Integer, DixitGame> gameInfo = new ConcurrentHashMap<>();
  private static final Map<String, Session> playerSession = new ConcurrentHashMap<>();
  private Set<Integer> idset = new HashSet<Integer>();
	
  public int createGameID(){
	  for(int i = 0; i < 100; i++){
		  if(!idset.contains(i)){
			  idset.add(i);
			  return i;
		  }
	  }
	  return -1;
  }

  public void addGame(DixitGame game) {
  	gameInfo.put(game.getId(), game);
  }
  
  public DixitGame getGame(int gameId) {
  	return gameInfo.get(gameId);
  }
  
  public int getCapacity(int gameId) {
    return gameInfo.get(gameId).getNumPlayers();
  }
  
  public void addSession(String playerId, Session session) {
	playerSession.put(playerId, session);
  }
  
  public boolean checkOpenSession(String playerId){
	  Session sess = playerSession.get(playerId);
	  if(sess == null || !sess.isOpen()){
		  return false;
	  }else{
		  return true;
	  }
	}
  
  public Session getSession(String playerId) {
	return playerSession.get(playerId);
  }
  
  public Map<String, Session> getSession() {
    return playerSession;
  }
}
