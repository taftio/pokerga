package pokerga;

public final class Result {
  private final Hand hand;
  private final Organism organism;
  private final int result;

  public Result(Hand hand, Organism organism, int result) {
    this.hand = hand;
    this.organism = organism;
    this.result = result;
  }

  public Hand getHand() {
    return hand;
  }

  public Organism getOrganism() {
    return organism;
  }

  public int getResult() {
    return result;
  }
}
