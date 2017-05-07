package edu.brown.cs.dixit.setting;

import java.util.ArrayList;
import java.util.List;

public class Deck {
  // default deck of 40 cards
  private int SIZE = 84;
  private List<Card> deck;
  
  public Deck () {
    deck = new ArrayList<>();
  }
  
  public void initializeDeck(String imgLink) {
    // need to call the imgLink for every card
    for (int i = 1; i < SIZE + 1; i++) {
      String eachLink = imgLink + String.valueOf(i) + ".jpg";
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