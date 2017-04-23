package edu.brown.cs.dixit.gameManagement;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jetty.websocket.api.Session;

public class GameTracker {

  private static final Map<Integer, DixitGame> gameInfo = new ConcurrentHashMap<>();
  private static final Map<Integer, Queue<Session>> games = new ConcurrentHashMap<>();
  private static final Map<Session, Integer> playerIds = new ConcurrentHashMap<>();
	
  
  public void addGame(Session session, DixitGame game, int playerId) {
  	Queue<Session> newPlayers = new ConcurrentLinkedQueue<>();
		newPlayers.add(session);
		gameInfo.put(game.getId(), game);
		games.put(game.getId(), newPlayers);
		playerIds.put(session, playerId);
  }
  /*
  public void addPlayer(Session session, int gameId) {
		Queue<Session> players = games.get(gameId); 
		players.add(session);
  }
  
  public void removePlayer(Session session) {
  	games.get(playerIds.get(session)).remove(session);
  }
  */
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
