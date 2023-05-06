package pokerga.op;

import java.util.ArrayList;
import java.util.List;
import pokerga.ScoredResult;

public final class DuplicateEliminator implements Filter {

  @Override
  public List<ScoredResult> filter(List<ScoredResult> scores) {
    List<ScoredResult> result = new ArrayList<>();
    for (int i = 0, n = scores.size(); i < n - 1; i++) {
      ScoredResult sr1 = scores.get(i);
      ScoredResult sr2 = scores.get(i + 1);
      if (!duplicate(sr1.getCorrect(), sr2.getCorrect())) {
        result.add(sr1);
      }
    }
    return result;
  }

  private boolean duplicate(int[] a1, int[] a2) {
    for (int i = 0; i < a1.length; i++) {
      if (a1[i] != a2[i]) {
        return false;
      }
    }
    return true;

  }

}
