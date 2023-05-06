package pokerga.init;

import java.util.concurrent.atomic.AtomicInteger;
import pokerga.Organism;

public final class OrganismFactory {

  private AtomicInteger counter = new AtomicInteger();

  public void setCounter(AtomicInteger counter) {
    this.counter = counter;
  }

  public Organism create(String chromosome) {
    String name = String.format("%08X", counter.getAndIncrement());
    return new Organism(name, chromosome);
  }
}
