package edu.brown.cs.dixit.setting;

import java.util.HashMap;
import java.util.Map;

public class Referee {
  
  private int winnerPoint;
  private int answer;
  private int storyTeller;
  private int numPlayers;
  private int victoryPoint;
  private Map<Integer, Integer> chosen; 
  private Map<Integer, Integer> pickRecord; 
  private Map<Integer, Integer> result;
  
  public Referee() {
    winnerPoint = -1;
    answer = -1;
    storyTeller = -1;
    numPlayers = Setting.NUM_DEFAULT_PLAYERS;
    victoryPoint = Setting.NUM_DEFAULT_VICTORY_POINT;
    chosen = new HashMap<Integer, Integer>();
    pickRecord = new HashMap<Integer, Integer>();
    result = new HashMap<Integer, Integer>();
  }
  
  public void receiveSubmissions(int id, int pick) {
     pickRecord.put(id, pick);
  }
  
  // need information of whose card it is
  public void tallyScores() {
    for (Integer key: pickRecord.keySet()) {    
      if (pickRecord.get(key) == getAnswer()) {
        result.put(key, 3);
      } else {
      }
    }
  }
  
  public int getWinnerPoint() {
    return winnerPoint;
  }

  public int getAnswer() {
    return answer;
  }

  public int getStoryTeller() {
    return storyTeller;
  }

  public int getNumPlayers() {
    return numPlayers;
  }  
  
  public int getVictoryPoint() {
    return victoryPoint;
  }
  
}
