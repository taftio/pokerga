package pokerga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class Population {

  private final int generation;
  private final List<Organism> organisms;

  public Population(int generation, List<Organism> organisms) {
    this.generation = generation;
    this.organisms = organisms;
  }

  public int getGeneration() {
    return generation;
  }

  public List<Organism> getOrganisms() {
    return organisms;
  }

  public int size() {
    return organisms.size();
  }

  /**
   * Initializes a new population with the specified number of organisms each
   * having a randomly generated chromosome.
   */
  public static class Initializer {
    private int populationSize = 100;
    private int chromosomeLength = 1024;
    private Random random = new Random();

    private final Set<String> previousNames = Collections.synchronizedSet(new HashSet<>());

    public void setPopulationSize(int populationSize) {
      if (populationSize < 1) {
        throw new IllegalArgumentException("Population size must be greater than zero.");
      }
      this.populationSize = populationSize;
    }

    public void setChromosomeLength(int chromosomeLength) {
      if (chromosomeLength <= 0 || chromosomeLength % 16 != 0) {
        throw new IllegalArgumentException("Chromosome length should be positive and divisible by 16.");
      }
      this.chromosomeLength = chromosomeLength;
    }

    public void setRandom(Random random) {
      Objects.requireNonNull(random);
      this.random = random;
    }

    public Population initialize() {
      // Using an executor speeds up intialization by about 3x.
      ExecutorService exs = Executors.newWorkStealingPool();
      try {
        List<Future<Organism>> futures = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
          Future<Organism> future = exs.submit(() -> {
            Organism org = new Organism(randomName(), randomChromosome());
            return org;
          });
          futures.add(future);
        }

        List<Organism> organisms = new ArrayList<>();

        for (Future<Organism> future : futures) {
          organisms.add(future.get());
        }

        previousNames.clear();

        return new Population(0, organisms);

      } catch (Exception e) {
        throw new IllegalStateException(e);

      } finally {
        exs.shutdown();
      }

    }

    private String randomName() {
      int n = 10;
      for (int i = 0; i < n; i++) {
        int val = random.nextInt(Integer.MAX_VALUE);
        String name = String.format("%08X", val);
        synchronized (previousNames) {
          if (!previousNames.contains(name)) {
            previousNames.add(name);
            return name;
          }
        }
      }
      throw new IllegalStateException("Unable to generate a unique name for organism after " + n + " tries.");
    }

    private String randomChromosome() {
      StringBuilder sb = new StringBuilder();
      for (int i = 0, n = chromosomeLength / 16; i < n; i++) {
        long val = random.nextLong();
        String s = String.format("%016X", val);
        sb.append(s);
      }
      return sb.toString();
    }

  }

}
