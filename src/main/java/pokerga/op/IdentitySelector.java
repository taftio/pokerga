package pokerga.op;

import java.util.ArrayList;
import java.util.List;
import pokerga.Organism;
import pokerga.ScoredResult;

public final class IdentitySelector implements Selector {

  @Override
  public List<Organism> select(List<ScoredResult> scores) {
    List<Organism> list = new ArrayList<>(scores.size());
    for (ScoredResult score : scores) {
      list.add(score.getOrganism());
    }
    return list;
  }

  @Override
  public boolean canMutate() {
    return false;
  }
}
