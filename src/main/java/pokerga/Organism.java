package pokerga;

import java.util.concurrent.atomic.AtomicInteger;

public final class Organism {

  private final AtomicInteger score = new AtomicInteger();

  private final String name;
  private final String chromosome;

  public Organism(String name, String chromosome) {
    this.name = name;
    this.chromosome = chromosome;
  }

  public String getName() {
    return name;
  }

  public String getChromosome() {
    return chromosome;
  }

  public int getScore() {
    return score.get();
  }

  public void incrementScore() {
    score.incrementAndGet();
  }

  public void resetScore() {
    score.set(0);
  }

  @Override
  public String toString() {
    return "Organism [" + name + "] score=" + score;
  }

}
