package edu.brown.cs.dixit.setting;

public class Card {

  private final int id;
  private final String imgLink;

  public Card(int id, String imgLink) {
    this.id = id;
    this.imgLink = imgLink;
  }

  public int getId() {
    return id;
  }

  public String getImgLink() {
    return imgLink;
  }

  @Override
  public String toString() {
    return "id:" + id + ":url:" + imgLink;
  }

}
