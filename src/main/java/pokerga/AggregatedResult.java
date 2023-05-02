package pokerga;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The aggregated results from evaluating all of the organisms against all of
 * the hands. This class provides the basis for determining which organisms are
 * fit enough to be selected for replication or moved into the next generation.
 */
public final class AggregatedResult {

  private final ConcurrentMap<Organism, Counts> results = new ConcurrentHashMap<>();

  public void aggregate(Result result) {
    int evaluation = result.getHand().evaluation();
    Counts counts = results.computeIfAbsent(result.getOrganism(), x -> new Counts());
    counts.total(evaluation);
    if (result.getResult() == evaluation) {
      counts.correct(evaluation);
    }
  }

  public Counts getCounts(Organism organism) {
    return results.get(organism);
  }

  public Map<Organism, Counts> getResults() {
    return results;
  }

  public static final class Counts {
    private final AtomicInteger[] correct = new AtomicInteger[10];
    private final AtomicInteger[] total = new AtomicInteger[10];

    public Counts() {
      for (int i = 0; i < correct.length; i++) {
        correct[i] = new AtomicInteger();
        total[i] = new AtomicInteger();
      }
    }

    public void correct(int index) {
      correct[index].incrementAndGet();
    }

    public void total(int index) {
      total[index].incrementAndGet();
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("[");
      for (int i = 0; i < correct.length; i++) {
        sb.append(correct[i].get());
        sb.append("/");
        sb.append(total[i].get());
        if (i < correct.length - 1) {
          sb.append(",");
        }
      }
      sb.append("]");
      return sb.toString();
    }
  }
}
