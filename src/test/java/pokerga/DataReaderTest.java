package pokerga;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DataReaderTest {

  @Test
  void test() throws IOException {
    String[] hands = {
        "10H JH KH QH AH [9]",
        "JS KS 10S QS AS [9]",
        "QD JD KD 10D AD [9]",
        "10C JC AC KC QC [9]",
        "AC KC QC JC 10C [9]",
        "2H 4H 5H 3H 6H [8]",
        "9H QH 10H JH KH [8]",
        "AS 2S 3S 4S 5S [8]",
        "5D 6D 9D 7D 8D [8]",
        "AC 4C 2C 3C 5C [8]",
    };

    File file = new File("src/test/resources/data.test");
    DataReader parser = new DataReader(file);

    List<Hand> list = new ArrayList<>();
    parser.read(list::add);

    assertEquals(hands.length, list.size());

    for (int i=0; i < hands.length; i++) {
      Hand hand = list.get(i);
      assertEquals(hands[i], hand.toString());
    }

  }

}
