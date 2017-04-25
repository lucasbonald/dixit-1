package edu.brown.cs.dixit.gameManagement;

import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jetty.websocket.api.Session;

public class GameTracker {

  private static final Map<Integer, DixitGame> gameInfo = new ConcurrentHashMap<>();
  private static final Map<Integer, Queue<Session>> games = new ConcurrentHashMap<>();
  private static final Map<Session, Integer> playerIds = new ConcurrentHashMap<>();
  private Set<Integer> idset = new HashSet<Integer>();
	
  public int createID(){
	  for(int i = 0;i<100;i++){
		  if(!idset.contains(i)){
			  idset.add(i);
			  return i;
		  }
	  }
	  return -1;
  }
  public void addGame(Session session, DixitGame game) {
  	Queue<Session> newPlayers = new ConcurrentLinkedQueue<>();
		newPlayers.add(session);
		gameInfo.put(game.getId(), game);
		games.put(game.getId(), newPlayers);
		playerIds.put(session, playerId);
  }
  
  public DixitGame getGame(int gameId) {
  	return gameInfo.get(gameId);
  }
  
  public Queue<Session> getPlayers(int gameId) {
  	return games.get(gameId);
  }
  
  public int getNumPlayers(int gameId) {
  	return games.get(gameId).size();
  }
  
	public int getCapacity(int gameId) {
		return gameInfo.get(gameId).getNumPlayers();
	}
	
	
}
