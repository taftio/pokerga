package pokerga.init;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import org.springframework.beans.factory.InitializingBean;
import pokerga.Organism;
import pokerga.Population;

public final class InitialPopulation implements Supplier<Population>, InitializingBean {

  private int size = 1000;
  private Supplier<Organism> organismSupplier = new RandomOrganism();

  public void setSize(int size) {
    this.size = size;
  }

  public void setOrganismSupplier(Supplier<Organism> organismSupplier) {
    this.organismSupplier = organismSupplier;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (size < 1) {
      throw new IllegalArgumentException("Population size must be greater than zero.");
    }
    Objects.requireNonNull(organismSupplier);
  }

  @Override
  public Population get() {
    ExecutorService executor = Executors.newWorkStealingPool();
    try {
      // Create the organisms using the executor service
      List<Future<Organism>> futures = new ArrayList<>(size);
      for (int i = 0; i < size; i++) {
        Future<Organism> future = executor.submit(() -> {
          return organismSupplier.get();
        });
        futures.add(future);
      }

      // Retrieve the organisms and add them to a new list
      List<Organism> organisms = new ArrayList<>(size);
      for (Future<Organism> future : futures) {
        organisms.add(future.get());
      }

      organisms = Collections.unmodifiableList(organisms);
      return new Population(0, organisms);

    } catch (ExecutionException e) {
      throw new IllegalStateException(e);

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);

    } finally {
      executor.shutdown();
    }
  }

}
