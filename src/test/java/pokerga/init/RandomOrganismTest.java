package pokerga.init;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import pokerga.Organism;

class RandomOrganismTest {

  @Test
  void test() throws Exception {
    RandomOrganism rando = new RandomOrganism();
    rando.setLength(16);
    rando.setCounter(new AtomicInteger(8));
    rando.setRandom(new Random(0));
    rando.afterPropertiesSet();

    String[] names = {
        "00000008", "00000009", "0000000A", "0000000B"
    };
    String[] chromes = {
        "BB20B45FD4D95138", "3D93CB799B3970BE", "A32DC9F64F1DF03A", "8CE970B71DF42503"
    };

    for (int i=0; i < names.length; i++) {
      Organism expected = new Organism(names[i], chromes[i]);
      Organism actual = rando.get();
      assertEquals(expected, actual);
    }
  }

}
