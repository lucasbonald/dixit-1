package edu.brown.cs.dixit.gameManagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import edu.brown.cs.dixit.setting.Deck;
import edu.brown.cs.dixit.setting.GamePlayer;
import edu.brown.cs.dixit.setting.Referee;
import edu.brown.cs.dixit.setting.Turn;

public class DixitGame {

	private final int id;
	private final int capacity;
	private final Deck deck;
	private final String name;
	private Map<String, GamePlayer> players;
	private Referee referee;
	private Map<String, String> playerStatus;
	
	//wrapper for all the information 
	//needs to contain the players & deck & referee & turn
	
	public DixitGame(int gameID, int cap, int victPoint, String gameName) {
		id = gameID;
		name = gameName;
		capacity = cap;
		deck = new Deck();
		players = new ConcurrentHashMap<>();
		referee = new Referee(cap, victPoint, new Turn(cap));
		playerStatus = new ConcurrentHashMap<>();
	}
	
	public int getId() {
		return id;
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	public int getNumPlayers() {
		return players.size();
	}
	
	public Deck getDeck() {
	    return deck;
	}
	
	public GamePlayer addPlayer(String id, String name) {
	    GamePlayer new_player = new GamePlayer(id, name, deck);
	    players.put(id, new_player);
	    referee.getTurn().addPlayers(new_player);
	    referee.addBoard(id, 0);
	    return new_player;
	}
	
	public void removePlayer(GamePlayer player){
		players.remove(player.getId());
		referee.getTurn().removePlayers(player);
		referee.removeBoard(player.getId());
		playerStatus.remove(player.getId());
	}
	
	public GamePlayer getPlayer(String id) {
		return players.get(id);
	}
	
	public Collection<GamePlayer> getPlayers() {
		return players.values();
	}
	
	public Set<String> getPlayerNames() {
	  return players.keySet();
	}
	
	public Referee getRefree() {
	    return referee;
	}
	
	public void setST(String id) {
	  referee.setTeller(id);
	}
	
	public String getST() {
	  return referee.getStoryTeller();
	}
	
	public void nextTurn() {
	  referee.incrementTurn();
	}
	
	public void addStatus(String id, String status) {
	  playerStatus.put(id, status);
	}
	
	
	public String getStatus(String id) {
	  return playerStatus.get(id);
	}
	public String getName(){
		return name;
	}
	
}
