package pokerga;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.DisposableBean;
import pokerga.AggregatedResult.Counts;
import pokerga.Population.Initializer;
import pokerga.mut.Mutator;

public final class Evaluator implements DisposableBean {

  private final ExecutorService executor = Executors.newWorkStealingPool();

  private DataReader dataReader;
  private Initializer initializer;
  private Interpreter interpreter;
  private Mutator mutator;
  private int maxGenerations;

  public void setDataReader(DataReader dataReader) {
    Objects.requireNonNull(dataReader);
    this.dataReader = dataReader;
  }

  public void setInitializer(Initializer initializer) {
    Objects.requireNonNull(initializer);
    this.initializer = initializer;
  }

  public void setInterpreter(Interpreter interpreter) {
    Objects.requireNonNull(interpreter);
    this.interpreter = interpreter;
  }

  public void setMutator(Mutator mutator) {
    Objects.requireNonNull(mutator);
    this.mutator = mutator;
  }

  public void setMaxGenerations(int maxGenerations) {
    if (maxGenerations < 1) {
      throw new IllegalArgumentException("Max Generations must be positive.");
    }
    this.maxGenerations = maxGenerations;
  }

  public void evaluate() throws IOException {
    // Initialize the population
    System.out.println("Initializing the population.");
    Population population = initializer.initialize();
    System.out.println("Finished initializing the population with " + population.size() + " organisms.");

    // Iterate for the number of generations specified
    for (int i = 0; i < maxGenerations; i++) {
      System.out.println("Evaluating Generation: " + population.getGeneration());

      // Retrieve the list of organisms from the population
      List<Organism> organisms = population.getOrganisms();

      // Create a ResultAggregator which will help keep track of our results.
      AggregatedResult aggregator = new AggregatedResult();

      // Keeps track how many hands we've received from the data file.
      AtomicInteger handCount = new AtomicInteger();

      // Read the data file for each hand and submit the organisms for evaluation
      dataReader.read(hand -> {
        int hc = handCount.get();
        if (hc % 100 == 0 && hc > 0) {
          System.out.println("Evaluating hand " + hc);
        }
        evaluate(hand, organisms, aggregator);
        handCount.incrementAndGet();
      });

      // Print out the results of this generation
      System.out.println("Results:");
      for (Map.Entry<Organism, Counts> entry : aggregator.getResults().entrySet()) {
        System.out.println(entry);
      }

      // Evolve the population if needed
      if (i < maxGenerations - 1) {
        System.out.println("Evolving the population.");
        population = mutator.mutate(population, aggregator);
      }

    } // end for max-generations

  }


  @Override
  public void destroy() throws Exception {
    executor.shutdown();
  }


  private void evaluate(Hand hand, List<Organism> organisms, AggregatedResult aggregator) {
    try {
      List<Future<Result>> futures = new ArrayList<>(organisms.size());

      for (Organism organism : organisms) {
        Future<Result> future = executor.submit(() -> {
          return interpreter.process(hand, organism);
        });
        futures.add(future);
      }

      for (Future<Result> future : futures) {
        Result result = future.get();
        aggregator.aggregate(result);
      }

    } catch (InterruptedException | ExecutionException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }

  }

}
