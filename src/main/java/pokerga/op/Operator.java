package pokerga.op;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.springframework.beans.factory.InitializingBean;
import pokerga.Organism;
import pokerga.ScoredResult;
import pokerga.init.InitialPopulation;
import pokerga.init.OrganismFactory;

/**
 * A {@link Selector} implementation that delegates evaluation to a list of
 * other other selectors and mutators, guaranteeing the final returned organism
 * list is the correct size.
 */
public final class Operator implements InitializingBean {

  private List<Filter> filters;
  private List<Selector> selectors;
  private List<Mutator> mutators;
  private OrganismFactory factory;
  private Supplier<Organism> supplier;
  private int populationSize = InitialPopulation.DEFAULT_SIZE;

  public void setFilters(List<Filter> filters) {
    this.filters = filters;
  }

  public void setSelectors(List<Selector> selectors) {
    this.selectors = selectors;
  }

  public void setMutators(List<Mutator> mutators) {
    this.mutators = mutators;
  }

  public void setFactory(OrganismFactory factory) {
    this.factory = factory;
  }

  public void setSupplier(Supplier<Organism> supplier) {
    this.supplier = supplier;
  }

  public void setPopulationSize(int populationSize) {
    this.populationSize = populationSize;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Objects.requireNonNull(selectors);
    Objects.requireNonNull(mutators);
    Objects.requireNonNull(factory);
    Objects.requireNonNull(supplier);
    if (selectors.isEmpty()) {
      throw new IllegalStateException("Selector list was empty.");
    }
    if (mutators.isEmpty()) {
      throw new IllegalStateException("Mutator list was empty.");
    }
    if (populationSize < 1) {
      throw new IllegalStateException("Population size must be positive.");
    }
  }

  public List<Organism> get(List<ScoredResult> scores) {
    // Filter the scores
    for (Filter filter : filters) {
      scores = filter.filter(scores);
    }

    List<Organism> mutables = new ArrayList<>();
    List<Organism> immutables = new ArrayList<>();

    // Iterate the selectors to receive selected organisms from the
    // previous generation.
    for (Selector selector : selectors) {
      List<Organism> selected = selector.select(scores);

      if (selector.canMutate()) {
        mutables.addAll(selected);
      } else {
        immutables.addAll(selected);
      }
    }

    // This is our output list.
    List<Organism> organisms = new ArrayList<>();

    // Immediately store our immutables at the top of the list
    // They get priority on the list to carry over.
    organisms.addAll(immutables);

    // For any of the selected organisms that can be mutated,
    // perform the mutations using the specified mutators
    List<String> mutations = new ArrayList<>();
    for (Mutator mutator : mutators) {
      mutations.addAll(mutator.mutate(mutables));
    }

    // For all the mutations, create a new organism and store to our list
    for (String chromosome : mutations) {
      Organism neworg = factory.create(chromosome);
      organisms.add(neworg);
    }

    // Resize the organisms list to its final size
    while (organisms.size() < populationSize) {
      organisms.add(supplier.get());
    }
    while (organisms.size() > populationSize) {
      organisms.remove(organisms.size() - 1);
    }

    if (organisms.size() != populationSize) {
      throw new AssertionError();
    }

    return organisms;
  }

}
