package pokerga;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class RandomGeneratorTest {

  @Test
  void test() {
    Random rnd = new Random(0L);
    Supplier<String> gen = new RandomGenerator(16, rnd);

    String expected = "BB20B45FD4D95138";
    String actual = gen.get();

    assertEquals(expected, actual);
  }

}
