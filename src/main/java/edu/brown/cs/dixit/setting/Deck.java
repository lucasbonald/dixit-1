package edu.brown.cs.dixit.setting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Deck {
  // default deck of 84 cards
  private int SIZE = Setting.NUM_DEFAULT_CARDS;
  private List<Card> deck;
  
  public Deck () {
    deck = new ArrayList<>();
  }
  
  public void initializeDeck(String imgLink) {
    // need to call the imgLink for every card
    for (int i = 0; i < SIZE; i++) {
      String eachLink = imgLink + String.valueOf(i);
      Card temp = new Card(i, eachLink);
      deck.add(temp);
    }
  }
 
  public Card drawCard(int number) {
    return deck.remove(number);
  }

  public int getDeckSize() {
    return deck.size();
  }
}