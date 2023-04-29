package pokerga;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.springframework.beans.factory.DisposableBean;
import pokerga.Population.Initializer;
import pokerga.mut.Mutator;

public final class Evaluator implements DisposableBean {

  private final DataReader dataReader;
  private final Initializer initializer;
  private final Interpreter interpreter;
  private final Mutator mutator;
  private final int maxGenerations;

  private final ExecutorService executor = Executors.newWorkStealingPool();

  public Evaluator(DataReader dataReader, Initializer initializer, Interpreter interpreter, Mutator mutator,
      int maxGenerations) {
    this.dataReader = dataReader;
    this.initializer = initializer;
    this.interpreter = interpreter;
    this.mutator = mutator;
    this.maxGenerations = maxGenerations;
  }

  public void evaluate() throws IOException {
    // Initialize the population
    System.out.println("Initializing the population.");
    Population population = initializer.initialize();
    System.out.println("Finished initializing the population with " + population.size() + " organisms.");

    // Iterate for the number of generations specified
    for (int i = 0; i < maxGenerations; i++) {
      System.out.println("Evaluating the population with gen: " + population.getGeneration());

      List<Organism> organisms = population.getOrganisms();

      // Read the data file for each hand and submit the organisms for evaluation
      dataReader.read(hand -> {
        System.out.println("Evaluating: " + hand);
        evaluate(hand, organisms);
      });

      // Evolve the population if appropriate
      if (i < maxGenerations - 1) {
        System.out.println("Evolving the population.");
        population = mutator.mutate(population);
      }
    }

    // Sort the final results for display
    List<Organism> organisms = population.getOrganisms();
    Collections.sort(organisms, (o1, o2) -> {
      return Integer.compare(o2.getScore(), o1.getScore());
    });

    System.out.println();
    System.out.println("Final scores. Organisms ranked by fitness score.");
    organisms.forEach(System.out::println);
  }


  @Override
  public void destroy() throws Exception {
    executor.shutdown();
  }


  private void evaluate(Hand hand, List<Organism> organisms) {
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
        if (result.getResult() == hand.evaluation()) {
          result.getOrganism().incrementScore();
        }
      }

    } catch (InterruptedException | ExecutionException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }

  }

}
