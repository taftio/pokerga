package pokerga;

import java.util.Objects;

public final class ScoredResult implements Comparable<ScoredResult> {

  private final Organism organism;
  private final int[] correct;
  private final int[] total;
  private final double score;

  public ScoredResult(Organism organism, int[] correct, int[] total, double score) {
    this.organism = organism;
    this.correct = correct;
    this.total = total;
    this.score = score;
  }

  public Organism getOrganism() {
    return organism;
  }

  public int[] getCorrect() {
    return correct;
  }

  public int[] getTotal() {
    return total;
  }

  public double getScore() {
    return score;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof ScoredResult other) {
      return Objects.equals(organism, other.organism)
          && Objects.equals(correct, other.correct)
          && Objects.equals(total, other.total)
          && Objects.equals(score, other.score);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(organism, correct, total, score);
  }

  @Override
  public int compareTo(ScoredResult other) {
    return Double.compare(other.score, score);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(organism);
    sb.append("=[");
    for (int i = 0; i < correct.length; i++) {
      sb.append(correct[i]);
      sb.append("/");
      sb.append(total[i]);
      if (i < correct.length - 1) {
        sb.append(",");
      }
    }
    sb.append("] score=");
    sb.append(score);

    return sb.toString();
  }

}
