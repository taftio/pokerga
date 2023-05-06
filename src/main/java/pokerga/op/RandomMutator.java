package pokerga.op;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import pokerga.Organism;

public final class RandomMutator implements Mutator {

  private static final char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

  private int organismChance;
  private int geneChance;

  private Random random = new Random();

  public void setOrganismChance(int organismChance) {
    this.organismChance = organismChance;
  }

  public void setGeneChance(int geneChance) {
    this.geneChance = geneChance;
  }

  public void setRandom(Random random) {
    this.random = random;
  }

  @Override
  public List<String> mutate(List<Organism> organisms) {
    List<String> result = new ArrayList<>();
    for (Organism organism : organisms) {
      String chromosome = organism.getChromosome();
      if (random.nextInt(100) < organismChance) {
        chromosome = mutate(chromosome);
      }
      result.add(chromosome);
    }
    return result;
  }

  private String mutate(String chromosome) {
    StringBuilder sb = new StringBuilder();
    for (char c : chromosome.toCharArray()) {
      if (random.nextInt(100) < geneChance) {
        char x = hex[random.nextInt(hex.length)];
        sb.append(x);
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }
}
