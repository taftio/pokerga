package pokerga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import pokerga.Card.Suit;

/**
 * Represents a poker hand of five known cards.
 */
public final class Hand implements Iterable<Card> {

  private final List<Card> hand;
  private final int evaluation;

  private Hand(List<Card> hand, int evaluation) {
    this.hand = hand;
    this.evaluation = evaluation;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static Hand from(String[] data) {
    return newBuilder()
        .addCard(data[1], data[0])
        .addCard(data[3], data[2])
        .addCard(data[5], data[4])
        .addCard(data[7], data[6])
        .addCard(data[9], data[8])
        .evaluation(data[10])
        .build();
  }

  public List<Card> getHand() {
    return hand;
  }

  public Card get(int index) {
    return hand.get(index);
  }

  public int evaluation() {
    return evaluation;
  }


  @Override
  public Iterator<Card> iterator() {
    return hand.iterator();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Card card : hand) {
      sb.append(card.toString());
      sb.append(" ");
    }
    sb.append("[").append(evaluation).append("]");
    return sb.toString();
  }

  public static class Builder {
    private final List<Card> cards = new ArrayList<>();
    private int evaluation = -1;

    public Builder addCard(int rank, Suit suit) {
      Card card = new Card(rank, suit);
      cards.add(card);
      return this;
    }

    public Builder addCard(int rank, int suit) {
      return addCard(rank, Suit.from(suit));
    }

    public Builder addCard(String rank, String suit) {
      return addCard(Integer.parseInt(rank), Integer.parseInt(suit));
    }

    public Builder evaluation(int evaluation) {
      this.evaluation = evaluation;
      return this;
    }

    public Builder evaluation(String evaluation) {
      this.evaluation = Integer.parseInt(evaluation);
      return this;
    }

    public Hand build() {
      if (cards.size() != 5) {
        throw new IllegalStateException("Expected 5 cards in the hand, but instead was: " + cards.size());
      }

      if (evaluation < 0) {
        throw new IllegalStateException("Evaluation must be set.");
      }

      List<Card> hand = new ArrayList<>(cards);
      hand = Collections.unmodifiableList(hand);

      return new Hand(hand, evaluation);
    }
  }
}
