package pokerga;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import pokerga.Population.Initializer;

class InitializerTest {

  @Test
  void test() {

    Initializer initializer = new Initializer();
    initializer.setPopulationSize(1);
    initializer.setChromosomeLength(16);
    initializer.setRandom(new Random(0));

    Population population = initializer.initialize();

    assertEquals(0, population.getGeneration());

    List<Organism> organisms = population.getOrganisms();
    assertEquals(1, organisms.size());

    Organism org = organisms.get(0);
    assertEquals("5D905A30", org.getName());
    assertEquals("D4D951383D93CB7A", org.getChromosome());
  }

}
