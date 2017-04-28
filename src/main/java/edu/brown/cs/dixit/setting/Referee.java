package edu.brown.cs.dixit.setting;

import java.util.HashMap;
import java.util.Map;

public class Referee {
  
  private int winnerPoint;
  private int answer;
  private String storyTeller;
  private int numPlayers;
  private int victoryPoint;
  private boolean gameWon;
  private String currPrompt;
  private Map<String, Integer> chosen; 
  private Map<String, Integer> pickRecord; 
  private Map<String, Integer> result;
  private Map<String, Integer> scoreBoard;
  
  public Referee() {
    winnerPoint = -1;
    answer = -1;
    storyTeller = "";
    currPrompt = "";
    numPlayers = Setting.NUM_DEFAULT_PLAYERS;
    victoryPoint = Setting.NUM_DEFAULT_VICTORY_POINT;
    gameWon = false;
    chosen = new HashMap<String, Integer>();
    pickRecord = new HashMap<String, Integer>();
    scoreBoard = new HashMap<String, Integer>();
  }
  
  public Referee(int cap) {
    winnerPoint = -1;
    answer = -1;
    storyTeller = "";
    currPrompt = "";
    numPlayers = cap;
    victoryPoint = Setting.NUM_DEFAULT_VICTORY_POINT;
    gameWon = false;
    chosen = new HashMap<String, Integer>();
    pickRecord = new HashMap<String, Integer>();
    scoreBoard = new HashMap<String, Integer>();
  }
  
  // receive submissions
  public void receiveStory(String prompt, String playerId, int cardId) {
    setPrompt(prompt);
    setStoryTeller(playerId);
    setAnswer(cardId);
  }
  
  // receive votes
  public void receiveVotes(String id, int pick) {
     pickRecord.put(id, pick);
  }
  
  // tally scores
  public Map<String, Integer> tallyScores() {
    //reset result
    result = new HashMap<String, Integer>();
    
    int count_answer = 0;
    //Points for other players
    for (String key: pickRecord.keySet()) {    
      int pickedCard = pickRecord.get(key);
      if (pickedCard == getAnswer()) {
        
        for (String keyTwo: chosen.keySet()) {
          if (chosen.get(keyTwo) == pickedCard) {
            if (result.containsKey(keyTwo)) {
              result.put(keyTwo, result.get(keyTwo) + 3);
            } else {
              result.put(keyTwo, 3);
            }
          } 
        }
        count_answer += 1;
        
      } else {
        for (String keyTwo: chosen.keySet()) {
          if (chosen.get(keyTwo) == pickedCard) {
            if (result.containsKey(keyTwo)) {
              result.put(keyTwo, result.get(keyTwo) + 1);
            } else {
              result.put(keyTwo, 1);
            }
          } 
        }
      }
    }
    
    //Point for Story-teller
    if ((count_answer == 0) || (count_answer == pickRecord.size())) {
      result.put(storyTeller, 0);
      for (String key: pickRecord.keySet()) {
        result.put(key, 2);
      }
    } else {
      result.put(storyTeller, 3);
    }
    
    //reset 
    pickRecord = new HashMap<String, Integer>();
    
    //messages can be added later in the backend?
    //need to check if the game ended
    
    for (String key: result.keySet()) {
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
  
  public String getStoryTeller() {
    return storyTeller;
  }
  
  public void setStoryTeller(String id) {
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

  public Map<String, Integer> getPickRecord() {
    return pickRecord;
  }

  public void setPickRecord(String id, int selectedCard) {
    pickRecord.put(id, selectedCard);
  }

  public void setChosen(String id, int chosenCard) {
    chosen.put(id, chosenCard);
  }
  
  public int getChosenSize() {
    return chosen.size();
  }
  
  public Integer getChosen(String id) {
    return chosen.get(id);
  }
}
