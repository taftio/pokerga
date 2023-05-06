package pokerga.op;

import java.util.List;
import pokerga.Organism;
import pokerga.ScoredResult;

/**
 * Selects organisms from a list of ScoredResults which are considered fit for
 * carrying into the next generation. The organisms returned in the list are
 * simply selected from the previous generation without any changes or
 * manipulation being performed in this selection process. The returned list of
 * organisms will then have their genes updated using crossover or mutation
 * processes to create the next generation.
 */
public interface Selector {

  List<Organism> select(List<ScoredResult> scores);

  /**
   * A notification to callers of whether they can mutate the returned list of
   * selected organisms from calling {@link #select(List)}. Only if this signals
   * true should callers consider to cross or mutate the returned list. If an
   * implementation wants its selections to go into the next generation
   * unmanipulated, it should return false here.
   *
   * @return True if the selected list of organisms can be subject to crossover or
   *         mutation updates
   */
  boolean canMutate();
}
