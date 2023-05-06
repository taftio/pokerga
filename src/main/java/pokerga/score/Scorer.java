package pokerga.score;

import pokerga.AggregatedResults.AggregatedResult;
import pokerga.ScoredResult;

public interface Scorer {

  /**
   * Calculates a score for the given aggregated result that is associated with an
   * organism after it has interpretted all its hands. The score returned is in
   * the range [0.0 - 1.0], with '0.0' indicating that the organism didn't solve
   * any of the hands correctly.
   *
   * A ScoredResult has a total ordering based on its score property. The return
   * value can be collected and sorted to find the highest scoring organisms in
   * the total population.
   *
   * @param result
   * @return
   */
  ScoredResult score(AggregatedResult result);

}
