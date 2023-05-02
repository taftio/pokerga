package pokerga;

import java.util.Objects;

public final class Organism {

  private final String name;
  private final String chromosome;

  public Organism(String name, String chromosome) {
    this.name = name;
    this.chromosome = chromosome;
  }

  public String getName() {
    return name;
  }

  public String getChromosome() {
    return chromosome;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof Organism other) {
      return Objects.equals(name, other.name)
          && Objects.equals(chromosome, other.chromosome);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, chromosome);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Organism[");
    sb.append(name);
    sb.append("] ");
    if (chromosome.length() <= 16) {
      sb.append(chromosome);
    } else {
      sb.append(chromosome.substring(0, 16));
      sb.append("[...]");
    }
    return sb.toString();
  }

}
