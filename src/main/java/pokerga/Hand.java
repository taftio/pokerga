package pokerga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import pokerga.Card.Suit;

/**
 * Represents a poker hand of five known cards.
 */
public final class Hand implements Iterable<Card> {

  private final List<Card> hand;
  private final int[] ranks;
  private final int[] suits;

  private Hand(List<Card> hand, int[] ranks, int[] suits) {
    this.hand = hand;
    this.ranks = ranks;
    this.suits = suits;
  }

  public static Builder builder() {
    return new Builder();
  }

  public List<Card> getHand() {
    return hand;
  }

  public Card get(int index) {
    return hand.get(index);
  }

  public int[] getRanks() {
    return ranks;
  }

  public int[] getSuits() {
    return suits;
  }

  @Override
  public Iterator<Card> iterator() {
    return hand.iterator();
  }

  public static class Builder {
    private final Set<Card> cards = new HashSet<>();

    public Builder addCard(int rank, Suit suit) {
      Card card = new Card(rank, suit);
      cards.add(card);
      return this;
    }

    public Hand build() {
      if (cards.size() != 5) {
        throw new IllegalStateException("Expected 5 cards in the hand, but instead was: " + cards.size());
      }

      List<Card> hand = new ArrayList<>(cards);
      hand = Collections.unmodifiableList(hand);

      int[] ranks = new int[5];
      for (int i = 0; i < ranks.length; i++) {
        ranks[i] = hand.get(i).getRank();
      }

      int[] suits = new int[5];
      for (int i = 0; i < suits.length; i++) {
        suits[i] = hand.get(i).getSuit();
      }

      return new Hand(hand, ranks, suits);
    }
  }
}
