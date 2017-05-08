package edu.brown.cs.dixit.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePlayer implements Player {
  
  private final String playerId;
  private final String playerName;
  private List<Card> playerDeck;
  private Deck globalDeck;
 
  //Player needs to switch around his/her roles
  public GamePlayer(String id, String name, Deck deck) {
    playerId = id;
    playerName = name;
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
  public void removeCard(int cardId) {
    Card removed = null;
    for (Card c: playerDeck) {
      if (c.getId() == cardId) {
        removed = c;
      }
    }
    playerDeck.remove(removed);
  }
  
  //when one card is used, automatically refill until
  //none left in the deck
  public List<Card> refillCard() {
    Random rand = new Random();
    Card newCard;
    if (globalDeck.getDeckSize() > 0) {
      int drawNumber = rand.nextInt(globalDeck.getDeckSize());
      newCard = globalDeck.drawCard(drawNumber);
      playerDeck.add(newCard);
    }
    
    return playerDeck;
  }
  
  public void resetHand(Deck deck) {
    globalDeck = deck;
  }
}
