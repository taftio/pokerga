package pokerga.init;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import pokerga.Organism;

class RandomOrganismTest {

  @Test
  void test() {
    RandomOrganism rando = new RandomOrganism();
    rando.setLength(8);
    rando.setCounter(new AtomicInteger(8));
    rando.setRandom(new Random(0));

    String[] names = {
        "00000008", "00000009", "0000000A", "0000000B"
    };
    String[] chromes = {
        "5731257A052F0E4E", "781A1120802E596B", "100C740E55214144", "17076F2E24500244"
    };

    for (int i=0; i < names.length; i++) {
      Organism expected = new Organism(names[i], chromes[i]);
      Organism actual = rando.get();
      assertEquals(expected, actual);
    }
  }

  @Test
  void testMaxOpCode() throws Exception {
    RandomOrganism rando = new RandomOrganism();
    rando.setLength(8);
    rando.setMaxOpCode(1);
    rando.afterPropertiesSet();

    Organism expected = new Organism("00000000", "0000000000000000");
    Organism actual = rando.get();

    assertEquals(expected, actual);
  }
}
