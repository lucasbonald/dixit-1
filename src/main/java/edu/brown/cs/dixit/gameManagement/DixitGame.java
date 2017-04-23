package edu.brown.cs.dixit.gameManagement;

import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.dixit.setting.Deck;
import edu.brown.cs.dixit.setting.GamePlayer;

public class DixitGame {

	private final int id;
	private final int num_players;
	private final Deck deck;
	private final List<GamePlayer> players;
	
	//wrapper for all the information 
	//needs to contain the players & deck & referee & turn
	
	public DixitGame(int ID, int player_number) {
		id = ID;
		num_players = player_number;
		deck = new Deck();
		players = new ArrayList<>();
	}
	
	public int getId() {
		return id;
	}
	
	public int getNumPlayers() {
		return num_players;
	}
	
	public Deck getDeck() {
	    return deck;
	}
	
	public void addPlayer(int id, String name, Deck deck) {
	  GamePlayer new_player = new GamePlayer(id, name, deck);
	  players.add(new_player);
	}
}
