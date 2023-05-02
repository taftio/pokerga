package pokerga.init;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import pokerga.Organism;
import pokerga.Population;

class InitialPopulationTest {

  @Test
  void test() {
    Supplier<Organism> organismSupplier = () -> {
      return new Organism("name", "chromosome");
    };

    List<Organism> organisms = new ArrayList<>();
    organisms.add(organismSupplier.get());

    InitialPopulation supplier = new InitialPopulation();
    supplier.setSize(1);
    supplier.setOrganismSupplier(organismSupplier);

    Population expected = new Population(0, organisms);
    Population actual = supplier.get();

    assertEquals(expected, actual);

  }

}
