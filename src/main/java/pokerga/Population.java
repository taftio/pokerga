package pokerga;

import java.util.List;
import java.util.Objects;

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

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof Population other) {
      return Objects.equals(generation, other.generation)
          && Objects.equals(organisms, other.organisms);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(generation, organisms);
  }


}
