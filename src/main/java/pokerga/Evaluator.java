package pokerga;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import pokerga.AggregatedResults.AggregatedResult;
import pokerga.init.InitialPopulation;
import pokerga.op.Operator;
import pokerga.score.Scorer;

public final class Evaluator implements InitializingBean, DisposableBean {

  private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

  private HandReader handReader;
  private InitialPopulation initialPopulation;
  private Interpreter interpreter;
  private Scorer scorer;
  private Operator operator;
  private int generations;

  public void setHandReader(HandReader handReader) {
    this.handReader = handReader;
  }

  public void setInitialPopulation(InitialPopulation initialPopulation) {
    this.initialPopulation = initialPopulation;
  }

  public void setInterpreter(Interpreter interpreter) {
    this.interpreter = interpreter;
  }

  public void setScorer(Scorer scorer) {
    this.scorer = scorer;
  }

  public void setOperator(Operator operator) {
    this.operator = operator;
  }

  public void setGenerations(int generations) {
    this.generations = generations;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Objects.requireNonNull(handReader);
    Objects.requireNonNull(initialPopulation);
    Objects.requireNonNull(interpreter);
    Objects.requireNonNull(scorer);
    Objects.requireNonNull(operator);
    if (generations < 1) {
      throw new IllegalArgumentException("Generations must be positive.");
    }
  }

  @Override
  public void destroy() throws Exception {
    executor.shutdown();
  }


  public void evaluate() throws IOException {

    System.out.println();
    System.out.println("-------------------------------------------------------");
    System.out.println();
    System.out.println("         Population size: " + initialPopulation.getPopulationSize());
    System.out.println("             Generations: " + generations);
    System.out.println("         Number of hands: " + handReader.getMaxHands());
    System.out.println();
    System.out.println("-------------------------------------------------------");
    System.out.println();

    // Initialize the population
    System.out.println("Initializing the population.");
    Population population = initialPopulation.get();
    System.out.println("Finished initializing the population.");

    // This is a list of scores for the final population. This is updated inside
    // of the loop, but we only care about the scores associated to the last
    // population.
    final List<ScoredResult> scores = new ArrayList<>();

    // Iterate for the number of generations specified
    for (int i = 0; i < generations; i++) {
      System.out.println("Generation: " + (population.getGeneration() + 1) + " / " + generations);

      // Retrieve the list of organisms from the population
      List<Organism> organisms = population.getOrganisms();

      // Create a ResultAggregator which will help keep track of our results.
      AggregatedResults aggregator = new AggregatedResults();

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
          future.get();
          if (count > 0 && count % 1000 == 0) {
            System.out.println("  Hand: " + count);
          }
          count++;
        }

      } catch (ExecutionException e) {
        throw new IllegalStateException(e);

      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException(e);
      }

      // Record the scores of this generation.
      // First we clear out any previous scores.
      scores.clear();

      // Calculate the scores of the current generation.
      // Sort the scores according to their natural ordering
      for (AggregatedResult result : aggregator.getResults()) {
        ScoredResult score = scorer.score(result);
        scores.add(score);
      }
      Collections.sort(scores);

      // Defensive copy of the scores to pass into the operator
      // This helps ensure that an operator doesn't manipulate
      // anything in the scores list.
      List<ScoredResult> copy = new ArrayList<>(scores);
      copy = Collections.unmodifiableList(copy);

      // Evolve the population if needed.
      // Create a new population object to hold the mutated results.
      if (i < generations - 1) {
        int next = population.getGeneration() + 1;
        List<Organism> evolved = operator.get(copy);
        population = new Population(next, evolved);
      }

    } // end for loop

    // Print out the scores of this generation
    System.out.println("Results:");
    for (ScoredResult score : scores) {
      System.out.println(score);
    }

  }


  private void evaluate(Hand hand, List<Organism> organisms, AggregatedResults aggregator) {
    for (Organism organism : organisms) {
      Result result = interpreter.process(hand, organism);
      aggregator.aggregate(result);
    }
  }

}
