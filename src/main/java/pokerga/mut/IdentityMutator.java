package pokerga.mut;

import pokerga.Population;

public final class IdentityMutator implements Mutator {

  @Override
  public Population mutate(Population previous) {
    return previous.next();
  }
}
