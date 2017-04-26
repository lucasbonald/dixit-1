package edu.brown.cs.dixit.setting;

public class Card {

  private final int id;
  private final String imgLink;
  private boolean isFaceDown;
  private boolean isSelected;
  private boolean isAnswer;
  private Player belongWhom;
 
  public Card(int id, String imgLink) {
    this.id = id;
    this.imgLink = imgLink;
    this.isFaceDown = true;
    this.isSelected = false;
    this.belongWhom = null;
  }

  public int getId() {
    return id;
  }

  public String getImgLink() {
    return imgLink;
  }
  
  public boolean isFaceDown() {
    return isFaceDown;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public boolean isAnswer() {
    return isAnswer;
  }

  public Player getBelongWhom() {
    return belongWhom;
  }
  
  @Override
  public String toString() {
    return "id:" + id + "url:" + imgLink;    
  }
}
