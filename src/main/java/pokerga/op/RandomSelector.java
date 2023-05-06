package pokerga.op;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import pokerga.Organism;
import pokerga.ScoredResult;

public final class RandomSelector implements Selector {

  private int count = 100;
  private Random random = new Random();

  public void setCount(int count) {
    this.count = count;
  }

  public void setRandom(Random random) {
    this.random = random;
  }

  @Override
  public List<Organism> select(List<ScoredResult> scores) {
    // Copy the list of scored results
    List<Organism> copy = new ArrayList<>();
    for (ScoredResult score : scores) {
      copy.add(score.getOrganism());
    }

    // Create a result list from the copy by randomly
    // selecting individuals.
    List<Organism> result = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      if (copy.size() <= 0) {
        break;
      }
      int idx = random.nextInt(copy.size());
      Organism org = copy.remove(idx);
      result.add(org);
    }

    return result;
  }

  @Override
  public boolean canMutate() {
    return true;
  }
}
