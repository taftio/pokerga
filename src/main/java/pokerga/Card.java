package pokerga;

import java.util.Objects;

public final class Card {

  private final int rank;
  private final Suit suit;

  public Card(int rank, Suit suit) {
    if (rank < 1 || rank > 13) {
      throw new IllegalArgumentException("Invalid rank, must be [1..13] but was: " + rank);
    }
    Objects.requireNonNull(suit);

    this.rank = rank;
    this.suit = suit;
  }

  public int getRank() {
    return rank;
  }

  public int getSuit() {
    return suit.num;
  }

  public Suit getSuitEnum() {
    return suit;
  }

  @Override
  public String toString() {
    String rankstr;
    switch (rank) {
    case 1:
      rankstr = "A";
      break;
    case 11:
      rankstr = "J";
      break;
    case 12:
      rankstr = "Q";
      break;
    case 13:
      rankstr = "K";
      break;
    default:
      rankstr = Integer.toString(rank);
      break;
    }

    String suitstr;
    switch (suit) {
    case HEARTS:
      suitstr = "H";
      break;
    case SPADES:
      suitstr = "S";
      break;
    case DIAMONDS:
      suitstr = "D";
      break;
    case CLUBS:
      suitstr = "C";
      break;
    default:
      throw new AssertionError();
    }

    return rankstr + suitstr;
  }

  public enum Suit {
    HEARTS(1), SPADES(2), DIAMONDS(3), CLUBS(4);

    private int num;

    private Suit(int num) {
      this.num = num;
    }

    public int getNum() {
      return num;
    }

    public static Suit from(int num) {
      for (Suit suit : Suit.values()) {
        if (num == suit.num) {
          return suit;
        }
      }
      throw new IllegalArgumentException("Invalid suit number: " + num);
    }
  }
}
