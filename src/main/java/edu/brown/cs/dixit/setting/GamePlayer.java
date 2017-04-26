package edu.brown.cs.dixit.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePlayer implements Player {
  
  private final String playerId;
  private final String playerName;
  private int point;
  private boolean isGuesser;
  private List<Card> playerDeck;
  private Deck globalDeck;
  
  
  //Use ENUM for StoryTeller / Guesser
  
  //Player needs to switch around his/her roles
  public GamePlayer(String id, String name, Deck deck) {
    playerId = id;
    playerName = name;
    point = 0;
    isGuesser = true;
    playerDeck = new ArrayList<>();
    globalDeck = deck;
  }
  
  @Override
  public String playerId() {
    return playerId;
  }

  @Override
  public String playerName() {
    return playerName;
  }

  @Override
  public int point() {
    return point;
  }
  
  // receive initial cards
  public List<Card> getFirstHand() {
    Random rand = new Random();
    while (playerDeck.size() < 6) {
      int drawNumber = rand.nextInt(globalDeck.getDeckSize());
        playerDeck.add(globalDeck.drawCard(drawNumber));
    }
    return playerDeck;
  }
  
  // remove card after submitting
  public Card removeCard(int ithCard) {
    Card removed;
    removed = playerDeck.remove(ithCard);
    return removed;
  }
  
  //when one card is used, automatically refill until
  //none left in the deck
  public Card refillCard() {
    Random rand = new Random();
    Card newCard;
    if (globalDeck.getDeckSize() > 0) {
      int drawNumber = rand.nextInt(globalDeck.getDeckSize());
      newCard = globalDeck.drawCard(drawNumber);
      playerDeck.add(newCard);
    } else {
      return null;
    }
    return newCard;
  }
  
  public String getGuesser() {
    if (isGuesser) {
      return "False";
    } else {
      return "True";
    }
  }
 
}
