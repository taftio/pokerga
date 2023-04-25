package pokerga;

import java.util.Random;
import java.util.function.Supplier;

public final class RandomGenerator implements Supplier<String> {

  private static final int defaultLength = 1024;

  private final int length;
  private final Random random;


  public RandomGenerator(int length, Random random) {
    if (length % 16 != 0) {
      throw new IllegalArgumentException("Length should be divisible by 16");
    }
    this.length = length;
    this.random = random;
  }

  public RandomGenerator() {
    this(defaultLength, new Random());
  }

  @Override
  public String get() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0, n = length / 16; i < n; i++) {
      long val = random.nextLong();
      String s = String.format("%016X", val);
      sb.append(s);
    }

    return sb.toString();
  }
}
