package pokerga.op;

import java.util.List;
import pokerga.ScoredResult;

public interface Filter {

  List<ScoredResult> filter(List<ScoredResult> scores);
}
