package pokerga.init;

import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
import org.springframework.beans.factory.InitializingBean;
import pokerga.Organism;

public final class RandomOrganism implements Supplier<Organism>, InitializingBean {

  private int chromosomeLength = 256;
  private OrganismFactory factory;
  private Random random = new Random();

  public void setChromosomeLength(int chromosomeLength) {
    this.chromosomeLength = chromosomeLength;
  }

  public void setFactory(OrganismFactory factory) {
    this.factory = factory;
  }

  public void setRandom(Random random) {
    this.random = random;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (chromosomeLength <= 0 || chromosomeLength % 16 != 0) {
      throw new IllegalArgumentException("Chromosome length should be positive and divisible by 16.");
    }
    Objects.requireNonNull(factory);
    Objects.requireNonNull(random);
  }

  @Override
  public Organism get() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < chromosomeLength / 16; i++) {
      long r = random.nextLong();
      String s = String.format("%016X", r);
      sb.append(s);
    }
    String chromosome = sb.toString();
    return factory.create(chromosome);
  }

}
