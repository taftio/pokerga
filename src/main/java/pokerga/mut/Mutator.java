package pokerga.mut;

import pokerga.AggregatedResult;
import pokerga.Population;

public interface Mutator {

  Population mutate(Population population, AggregatedResult result);

}
