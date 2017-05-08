package edu.brown.cs.dixit.setting;

import java.util.HashMap;
import java.util.Map;

public class Referee {
  
  private int numPlayers;
  private int victoryPoint;
  private String gameWon;
  private Map<String, Integer> chosen; 
  private Map<String, Integer> pickRecord; 
  private Map<String, Integer> result;
  private Map<String, Integer> scoreBoard;
  private Turn gameTurn;
  
  public Referee() {
    numPlayers = Setting.NUM_DEFAULT_PLAYERS;
    victoryPoint = Setting.NUM_DEFAULT_VICTORY_POINT;
    gameWon = "";
    chosen = new HashMap<String, Integer>();
    pickRecord = new HashMap<String, Integer>();
    scoreBoard = new HashMap<String, Integer>();
  }
  
  public Referee(int cap, int victPoint, Turn turn) {
    numPlayers = cap;
    victoryPoint = victPoint;
    gameWon = "";
    chosen = new HashMap<String, Integer>();
    pickRecord = new HashMap<String, Integer>();
    scoreBoard = new HashMap<String, Integer>();
    gameTurn = turn;
  }
  
  // receive submissions
  public void receiveStory(String prompt, String playerId, int cardId) {
    setPrompt(prompt);
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
    System.out.println("result size: " + result.size());
    int count_answer = 0;
    int pickedCard = 0;
    //Points for other players
    System.out.println("pick record size: " + pickRecord.size());
    for (String key: pickRecord.keySet()) {    
      System.out.println("user id: " + key);
      pickedCard = pickRecord.get(key);
      if (pickedCard == getAnswer()) {
        if (result.containsKey(key)) {
          result.put(key, result.get(key) + 3);
        } else {
          result.put(key, 3);
        }
        count_answer += 1;  
      } else {
        result.put(key, 0);
      }
    }
    
    for (String key: pickRecord.keySet()) {
        for (String keyTwo: chosen.keySet()) {
          if (chosen.get(keyTwo) == pickedCard && !keyTwo.equals(key)) {
            if (result.containsKey(keyTwo)) {
              result.put(keyTwo, result.get(keyTwo) + 1);
            } else {
              result.put(keyTwo, 1);
            }
          }
        }
    }
    
    System.out.println("result size before st: " + result.size());
    //Point for Story-teller
    
    if (count_answer == pickRecord.size()) {
      result.put(gameTurn.getCurrTeller(), 0);
      for (String key: pickRecord.keySet()) {
        result.put(key, 2);
      }
    } else if (count_answer == 0) {
      result.put(gameTurn.getCurrTeller(), 0);
      for (String key: pickRecord.keySet()) {
        if (result.containsKey(key)) {
          result.put(key,  result.get(key) + 2);
        } else {
          result.put(key, 2);
        }
      }
    } else {
      result.put(gameTurn.getCurrTeller(), 3);
    }
    
    System.out.println("result size after st: " + result.size());
    
    //reset 
    pickRecord = new HashMap<String, Integer>();
    chosen = new HashMap<String, Integer>();
    
    //need to check if the game ended
    System.out.println(scoreBoard.values().toString());
    for (String id: scoreBoard.keySet()) {
      System.out.println("scoreboard point:" + scoreBoard.get(id));
    }
   
    for (String key: result.keySet()) {
      System.out.println("scoreBoard:" +scoreBoard.get(key));
      System.out.println("result" + result.get(key));
      int newScore = scoreBoard.get(key) + result.get(key);
      scoreBoard.put(key, newScore);
      if (newScore >= victoryPoint) {
        gameWon = key;
        for (String id: scoreBoard.keySet()) {
          scoreBoard.put(id, 0);
        }
      break;
      }
    }
    return result;
  }
 
  public int getAnswer() {
    return gameTurn.getAnswer();
  }

  public void setAnswer(int cardId) {
    gameTurn.setAnswer(cardId);
  }
  
  public String getStoryTeller() {
    return gameTurn.getCurrTeller();
  }
  
  public void incrementTurn() {
    gameTurn.incrementTurn();
  }
  
  public int getVictoryPoint() {
    return victoryPoint;
  }
  
  public String getPrompt() {
    return gameTurn.getPrompt();
  }
  
  public void setPrompt(String prompt) {
    gameTurn.setPrompt(prompt);
  }

  public Map<String, Integer> getPickRecord() {
    return pickRecord;
  }

  public void setPickRecord(String id, int selectedCard) {
    pickRecord.put(id, selectedCard);
  }

  public int getPickedSize() {
  	return pickRecord.size();
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
  
  public Turn getTurn() {
    return gameTurn;
  }
  
  public void addBoard(String id, int defPoint) {
    scoreBoard.put(id, defPoint);
  }
  
  public void removeBoard(String id){
	  scoreBoard.remove(id);
  }
  
  public String getWinner() {
    return gameWon;
  }
 
}
