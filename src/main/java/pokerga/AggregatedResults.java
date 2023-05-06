package pokerga;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The aggregated results from evaluating all of the organisms against all of
 * the hands. This class provides the basis for determining which organisms are
 * fit enough to be selected for replication or moved into the next generation.
 */
public final class AggregatedResults {

  private final ConcurrentMap<Organism, AggregatedResult> results = new ConcurrentHashMap<>();

  public void aggregate(Result result) {
    int evaluation = result.getHand().evaluation();
    AggregatedResult counts = results.computeIfAbsent(result.getOrganism(), o -> new AggregatedResult(o));
    counts.total(evaluation);
    if (result.getResult() == evaluation) {
      counts.correct(evaluation);
    }
  }

  public AggregatedResult getAggregatedResult(Organism organism) {
    return results.get(organism);
  }

  public Collection<AggregatedResult> getResults() {
    return results.values();
  }

  public static final class AggregatedResult {
    private final Organism organism;
    private final AtomicInteger[] correct = new AtomicInteger[10];
    private final AtomicInteger[] total = new AtomicInteger[10];

    public AggregatedResult(Organism organism) {
      this.organism = organism;
      for (int i = 0; i < correct.length; i++) {
        correct[i] = new AtomicInteger();
        total[i] = new AtomicInteger();
      }
    }

    public Organism getOrganism() {
      return organism;
    }

    private void correct(int index) {
      correct[index].incrementAndGet();
    }

    public int[] correct() {
      int[] arr = new int[correct.length];
      for (int i = 0; i < arr.length; i++) {
        arr[i] = correct[i].get();
      }
      return arr;
    }

    private void total(int index) {
      total[index].incrementAndGet();
    }

    public int[] total() {
      int[] arr = new int[total.length];
      for (int i = 0; i < arr.length; i++) {
        arr[i] = total[i].get();
      }
      return arr;
    }

  }
}
