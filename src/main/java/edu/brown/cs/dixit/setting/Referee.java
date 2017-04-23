package edu.brown.cs.dixit.setting;

public class Referee {
  // tally up points & 
  private int winnerPoint;
  private int answer;
  private String storyTeller;
  private int numPlayers;
  
  public Referee() {
    winnerPoint = -1;
    answer = 0;
    storyTeller = null;
    numPlayers = Setting.NUM_DEFAULT_PLAYERS;
  }

}
