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
import java.util.function.Supplier;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import pokerga.AggregatedResult.Counts;
import pokerga.mut.Mutator;

public final class Evaluator implements InitializingBean, DisposableBean {

  private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

  private HandReader handReader;
  private Supplier<Population> initialPopulation;
  private Interpreter interpreter;
  private Mutator mutator;
  private int maxGenerations;

  public void setHandReader(HandReader handReader) {
    this.handReader = handReader;
  }

  public void setInitialPopulation(Supplier<Population> initialPopulation) {
    this.initialPopulation = initialPopulation;
  }

  public void setInterpreter(Interpreter interpreter) {
    this.interpreter = interpreter;
  }

  public void setMutator(Mutator mutator) {
    this.mutator = mutator;
  }

  public void setMaxGenerations(int maxGenerations) {
    this.maxGenerations = maxGenerations;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Objects.requireNonNull(handReader);
    Objects.requireNonNull(initialPopulation);
    Objects.requireNonNull(interpreter);
    Objects.requireNonNull(mutator);
    if (maxGenerations < 1) {
      throw new IllegalArgumentException("Max Generations must be positive.");
    }
  }

  @Override
  public void destroy() throws Exception {
    executor.shutdown();
  }


  public void evaluate() throws IOException {
    // Initialize the population
    System.out.println("Initializing the population.");
    Population population = initialPopulation.get();
    System.out.println("Finished initializing the population with " + population.size() + " organisms.");

    // Iterate for the number of generations specified
    for (int i = 0; i < maxGenerations; i++) {
      System.out.println("Evaluating Generation: " + population.getGeneration());

      // Retrieve the list of organisms from the population
      List<Organism> organisms = population.getOrganisms();

      // Create a ResultAggregator which will help keep track of our results.
      AggregatedResult aggregator = new AggregatedResult();

      // Stores our future tasks
      List<Future<?>> futures = new ArrayList<>();

      // Read the data file for each hand and submit the organisms for evaluation
      handReader.read(hand -> {
        Future<?> future = executor.submit(() -> {
          evaluate(hand, organisms, aggregator);
        });
        futures.add(future);
      });

      // Wait for the evaluations to finish
      try {
        int count = 0;
        for (Future<?> future : futures) {
          if (count % 100 == 0 && count > 0) {
            System.out.println("Evaluating hand " + count);
          }
          future.get();
          count++;
        }

      } catch (ExecutionException e) {
        throw new IllegalStateException(e);

      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException(e);
      }

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


  private void evaluate(Hand hand, List<Organism> organisms, AggregatedResult aggregator) {
    for (Organism organism : organisms) {
      Result result = interpreter.process(hand, organism);
      aggregator.aggregate(result);
    }
  }

}
