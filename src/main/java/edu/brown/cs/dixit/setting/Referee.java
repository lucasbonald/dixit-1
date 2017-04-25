package edu.brown.cs.dixit.setting;

import java.util.HashMap;
import java.util.Map;

public class Referee {
  
  private int winnerPoint;
  private int answer;
  private int storyTeller;
  private int numPlayers;
  private int victoryPoint;
  private boolean gameWon;
  private String currPrompt;
  private Map<Integer, Integer> chosen; 
  private Map<Integer, Integer> pickRecord; 
  private Map<Integer, Integer> result;
  private Map<Integer, Integer> scoreBoard;
  
  public Referee() {
    winnerPoint = -1;
    answer = -1;
    storyTeller = -1;
    currPrompt = "";
    numPlayers = Setting.NUM_DEFAULT_PLAYERS;
    victoryPoint = Setting.NUM_DEFAULT_VICTORY_POINT;
    gameWon = false;
    chosen = new HashMap<Integer, Integer>();
    pickRecord = new HashMap<Integer, Integer>();
    result = new HashMap<Integer, Integer>();
    scoreBoard = new HashMap<Integer, Integer>();
  }
  
  // need new constructor;
  
  public void receiveStory(String prompt, int playerId, int cardId) {
    setPrompt(prompt);
    setStoryTeller(playerId);
    setAnswer(cardId);
  }
  
  public void receiveCards(int id, int cardId) {
    chosen.put(id, cardId);
  }
  
  public void receiveVotes(int id, int pick) {
     pickRecord.put(id, pick);
  }
  
  public Map<Integer, Integer> tallyScores() {
    //reset result
    result = new HashMap<Integer, Integer>();
    
    int count_answer = 0;
    //Points for other players
    for (Integer key: pickRecord.keySet()) {    
      int pickedCard = pickRecord.get(key);
      if (pickedCard == getAnswer()) {
        
        if (result.containsKey(pickedCard)) {
          result.put(pickedCard, result.get(pickedCard) + 3);
        } else {
          result.put(pickedCard, 3);
        }
        
        count_answer += 1;
        
      } else {
        if (result.containsKey(pickedCard)) {
          result.put(pickedCard, result.get(pickedCard) + 1);
        } else {
          result.put(pickedCard, 1);
        }
      }
    }
    
    //Point for Story-teller
    if ((count_answer == 0) || (count_answer == pickRecord.size())) {
      result.put(storyTeller, 0);
      for (Integer key: pickRecord.keySet()) {
        result.put(key, 2);
      }
    } else {
      result.put(storyTeller, 3);
    }
    
    //reset 
    chosen = new HashMap<Integer, Integer>();
    pickRecord = new HashMap<Integer, Integer>();
    
    //messages can be added later in the backend?
    //need to check if the game ended
    
    for (Integer key: result.keySet()) {
      int newScore = scoreBoard.get(key) + result.get(key);
      scoreBoard.put(key, newScore);
      if (newScore >= victoryPoint) {
        gameWon = true; // need who's winning as well
      }
    }
    
    //need to check if all the card is used and manually finish th game
    
    return result;
  }
 
  public int getWinnerPoint() {
    return winnerPoint;
  }

  public int getAnswer() {
    return answer;
  }

  public void setAnswer(int cardId) {
    this.answer = cardId;
  }
  
  public int getStoryTeller() {
    return storyTeller;
  }
  
  public void setStoryTeller(int id) {
    this.storyTeller = id;
  }

  public int getNumPlayers() {
    return numPlayers;
  }  
  
  public int getVictoryPoint() {
    return victoryPoint;
  }
  
  public String getPrompt() {
    return currPrompt;
  }
  
  public void setPrompt(String prompt) {
    this.currPrompt = prompt;
  }
  
}
