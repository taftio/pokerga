package pokerga;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class HandReaderTest {

  @Test
  void test() throws IOException {
    // These hands are sorted versions of the test data. And are
    // not identical to the data being read. We always sort our
    // hands in the Hand.Builder, so this is the expected output
    // after reading our test data.
    //
    // Note that in the sort, the ACE always sorts as a '1'
    // even though in poker, the ACE can be high or low
    // depending on the circumstance (when evaluating a straight).
    String[] hands = {
        "AH 10H JH QH KH [9]",
        "AS 10S JS QS KS [9]",
        "AD 10D JD QD KD [9]",
        "AC 10C JC QC KC [9]",
        "AC 10C JC QC KC [9]",
        "2H 3H 4H 5H 6H [8]",
        "9H 10H JH QH KH [8]",
        "AS 2S 3S 4S 5S [8]",
        "5D 6D 7D 8D 9D [8]",
        "AC 2C 3C 4C 5C [8]",
    };

    File file = new File("src/test/resources/data.test");
    HandReader reader = new HandReader();
    reader.setFile(file);

    List<Hand> list = new ArrayList<>();
    reader.read(list::add);

    assertEquals(hands.length, list.size());

    for (int i=0; i < hands.length; i++) {
      Hand hand = list.get(i);
      assertEquals(hands[i], hand.toString());
    }

  }

  @Test
  void testMaxHands() throws IOException {
    int[] input = { -1, 0, 1, 2 };
    int[] expected = { 10, 10, 1, 2 };

    for (int i = 0; i < input.length; i++) {
      File file = new File("src/test/resources/data.test");
      HandReader reader = new HandReader();
      reader.setFile(file);
      reader.setMaxHands(input[i]);

      List<Hand> list = new ArrayList<>();
      reader.read(list::add);

      assertEquals(expected[i], list.size());
    }
  }

}
