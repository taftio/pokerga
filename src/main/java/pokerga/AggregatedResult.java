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
    if (result.getResult() == evaluation) {
      counts.increment(evaluation);
    }
  }

  public Counts getCounts(Organism organism) {
    return results.get(organism);
  }

  public Map<Organism, Counts> getResults() {
    return results;
  }

  public static final class Counts {
    private final AtomicInteger[] counts = new AtomicInteger[10];

    public Counts() {
      for (int i = 0; i < counts.length; i++) {
        counts[i] = new AtomicInteger();
      }
    }

    public void increment(int index) {
      counts[index].incrementAndGet();
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("[");
      for (int i = 0; i < counts.length; i++) {
        sb.append(counts[i].get());
        if (i < counts.length - 1) {
          sb.append(",");
        }
      }
      sb.append("]");
      return sb.toString();
    }
  }
}
