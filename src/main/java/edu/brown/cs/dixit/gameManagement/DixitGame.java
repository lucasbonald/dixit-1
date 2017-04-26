package edu.brown.cs.dixit.gameManagement;

import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.dixit.setting.Deck;
import edu.brown.cs.dixit.setting.GamePlayer;
import edu.brown.cs.dixit.setting.Referee;
import edu.brown.cs.dixit.setting.Turn;

public class DixitGame {

	private final int id;
	private final int capacity;
	private final Deck deck;
	private List<GamePlayer> players;
	private Referee referee;
	private Turn turn;
	
	//wrapper for all the information 
	//needs to contain the players & deck & referee & turn
	
	public DixitGame(int gameID, int cap) {
		id = gameID;
		capacity = cap;
		deck = new Deck();
		players = new ArrayList<>();
		referee = new Referee(cap);
		turn = new Turn(cap);
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
	  players.add(new_player);
	  return new_player;
	}
	
	public List<GamePlayer> getPlayers() {
		return players;
	}
	
	public Referee getRefree() {
	  return referee;
	}
}
