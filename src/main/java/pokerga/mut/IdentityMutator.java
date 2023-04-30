package pokerga.mut;

import pokerga.AggregatedResult;
import pokerga.Population;

public final class IdentityMutator implements Mutator {

  @Override
  public Population mutate(Population population, AggregatedResult result) {
    return population;
  }
}
