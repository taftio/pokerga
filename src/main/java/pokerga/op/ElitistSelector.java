package pokerga.op;

import java.util.ArrayList;
import java.util.List;
import pokerga.Organism;
import pokerga.ScoredResult;

/**
 * This mutator implementation will carry forward the top {@link #count}
 * organisms into the next generation. All other organisms will be removed if
 * that aren't in the top count.
 */
public final class ElitistSelector implements Selector {

  private int count = 10;

  /**
   * The count of organisms to return from the implementation.
   *
   * @param count
   */
  public void setCount(int count) {
    this.count = count;
  }


  @Override
  public List<Organism> select(List<ScoredResult> scores) {
    List<Organism> elites = new ArrayList<>();
    int n = Math.min(count, scores.size());
    for (int i = 0; i < n; i++) {
      elites.add(scores.get(i).getOrganism());
    }

    return elites;
  }

  @Override
  public boolean canMutate() {
    return false;
  }
}
