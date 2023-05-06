package pokerga.score;

import pokerga.AggregatedResults.AggregatedResult;
import pokerga.ScoredResult;

public final class StandardScorer implements Scorer {

  @Override
  public ScoredResult score(AggregatedResult result) {
    double score = 0.0;

    int[] correct = result.correct();
    int[] total = result.total();

    for (int i = 0; i < correct.length; i++) {
      if (total[i] == 0) {
        // If the total is zero for any given result, this just means that the hands
        // we've evaluated didn't include an example of that hand type. We just skip
        // over this problem, because running against a full dataset, this wouldn't
        // happen.
        continue;
      }

      score += 1.0 * correct[i] / total[i];
    }

    score /= correct.length;

    return new ScoredResult(result.getOrganism(), result.correct(), result.total(), score);
  }
}
