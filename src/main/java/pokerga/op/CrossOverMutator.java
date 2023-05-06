package pokerga.op;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import org.springframework.beans.factory.InitializingBean;
import pokerga.Organism;

/**
 * Performs a two-point crossover mutation with two selected organisms from the
 * list.
 */
public final class CrossOverMutator implements Mutator, InitializingBean {

  private int max = 20;
  private Random random = new Random();

  public void setMax(int max) {
    this.max = max;
  }

  public void setRandom(Random random) {
    this.random = random;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (max < 0) {
      throw new IllegalStateException("Max property must be positive.");
    }
    Objects.requireNonNull(random);
  }

  @Override
  public List<String> mutate(List<Organism> organisms) {
    int n = Math.min(organisms.size(), max);
    if (n % 2 == 1) {
      n -= 1;
    }

    List<String> list = new ArrayList<>();
    for (int i = 0; i < n; i += 2) {
      Organism o1 = organisms.get(i);
      Organism o2 = organisms.get(i + 1);

      String c1 = o1.getChromosome();
      String c2 = o2.getChromosome();

      int midpoint = c1.length() / 2;

      int x1 = random.nextInt(midpoint);
      int x2 = random.nextInt(midpoint) + midpoint;

      String n1 = c2.substring(0, x1) + c1.substring(x1, x2) + c2.substring(x2);
      String n2 = c1.substring(0, x1) + c2.substring(x1, x2) + c1.substring(x2);

      if (n1.length() != c1.length()) {
        throw new IllegalStateException(
            "Crossover length=" + n1.length() + " did not match original length=" + c1.length());
      }
      if (n2.length() != c2.length()) {
        throw new IllegalStateException(
            "Crossover length=" + n2.length() + " did not match original length=" + c2.length());
      }

      list.add(n1);
      list.add(n2);
    }
    return list;
  }
}
