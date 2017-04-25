package edu.brown.cs.dixit.setting;

import java.util.ArrayList;
import java.util.List;

public class Turn {
  
  private final int numPlayer;
  private int currTurn;
  private List<Player> players;
  
  public Turn(int number) {
    numPlayer = number;
    currTurn = 0;
    players = new ArrayList<>();
  }
  
  public void setPlayers(List<Player> users) {
    players = users;
  }
  
  public String getCurrentTeller() {
    String storyTeller =  players.get(currTurn).playerId();
    if (currTurn == numPlayer - 1) {
      currTurn = 0;
    } else {
      currTurn += 1;
    }
    return storyTeller;
  }
 
}
