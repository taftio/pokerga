package pokerga.op;

import java.util.List;
import pokerga.Organism;

public interface Mutator {

  List<String> mutate(List<Organism> organisms);

}
