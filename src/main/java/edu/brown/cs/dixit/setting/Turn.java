package edu.brown.cs.dixit.setting;

import java.util.ArrayList;
import java.util.List;

public class Turn {
  
  private final int numPlayer;
  private int currTurn;
  private List<GamePlayer> players;
  private String prompt;
  private int answer;
  
  public Turn(int number) {
    numPlayer = number;
    currTurn = 0;
    players = new ArrayList<>();
    prompt = "";
  }
  
  public void addPlayers(GamePlayer user) {
    players.add(user);
  }
  
  public void removePlayers(GamePlayer player){
	  players.remove(player);
  }
  
  public String getCurrTeller() {
    return players.get(currTurn).playerId();
  }
 
  public void incrementTurn() {
    if (currTurn == numPlayer - 1) {
      currTurn = 0;
    } else {
      currTurn += 1;
    }
  }
  
  public String getPrompt() {
    return prompt;
  }
  
  public void setPrompt(String newPrompt) {
    prompt = newPrompt;
  }
  
  public int getAnswer() {
    return answer;
  }
  
  public void setAnswer(int newAnswer) {
    answer = newAnswer;
  }
  
  public void setTurn(int zero) {
    currTurn = zero;
  }
}
