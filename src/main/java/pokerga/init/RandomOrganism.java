package pokerga.init;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.springframework.beans.factory.InitializingBean;
import pokerga.Organism;

public final class RandomOrganism implements Supplier<Organism>, InitializingBean {

  private int length = 256;
  private AtomicInteger counter = new AtomicInteger();
  private Random random = new Random();

  public void setLength(int length) {
    this.length = length;
  }

  public void setCounter(AtomicInteger counter) {
    this.counter = counter;
  }

  public void setRandom(Random random) {
    this.random = random;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (length <= 0 || length % 16 != 0) {
      throw new IllegalArgumentException("Chromosome length should be positive and divisible by 16.");
    }
    Objects.requireNonNull(counter);
    Objects.requireNonNull(random);
  }

  @Override
  public Organism get() {
    String name = String.format("%08X", counter.getAndIncrement());
    String chromosome = createChromosome();
    return new Organism(name, chromosome);
  }

  private String createChromosome() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length / 16; i++) {
      long r = random.nextLong();
      String s = String.format("%016X", r);
      sb.append(s);
    }
    return sb.toString();
  }
}
