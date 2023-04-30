package pokerga.mut;

import pokerga.Population;

public interface Mutator {

  Population mutate(Population previous);

}
