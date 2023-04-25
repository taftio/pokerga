package pokerga;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.carrotsearch.hppc.ByteStack;
import java.nio.BufferUnderflowException;
import org.junit.jupiter.api.Test;
import pokerga.Card.Suit;
import pokerga.Interpreter.Inst;

class InterpreterInstTest {

  @Test
  void testDanglingPush() {
    // It's not an error if we don't finish with an END marker.
    // That is, we just simply interpret until we reach an END
    // marker or we reach EOF. If we reach EOF, our stack won't
    // clear, which is convenient for testing.
    String subj = "01 1A";
    Inst inst = new Inst(null, subj);
    inst.process();

    assertEquals((byte) 0xA, inst.stack.peek());
  }

  @Test
  void testBeginEndBoundary() {
    // This will test that we don't PUSH any items outside of a BEG/END boundary.
    // We have two pushes here, but our stack will be cleared with the call to END.
    // So we're only going to see the last one (1D) in this test.
    String subj = "1A 01 1B 02 1C 01 1D";
    Inst inst = new Inst(null, subj);
    inst.process();

    assertEquals((byte) 0xD, inst.stack.peek());
  }

  @Test
  void testUnderflow() {
    // Ensure that we don't throw any exceptions for these weird parse states.
    String[] subs = { "", " ", "  ", "x", "00", "01", "02", "01 02 02", "01 ", "01  " };

    for (String sub : subs) {
      Inst inst = new Inst(null, sub);
      inst.process();
      try {
        inst.process();
      } catch (BufferUnderflowException e) {
        fail("Buffer Underflow detected for subject: ->" + sub + "<-", e);
      }
    }
  }

  @Test
  void testNestedBegin() {
    String subj = "01 01 02";
    Inst inst = new Inst(null, subj);
    inst.process();
  }

  @Test
  void testNestedEnd() {
    String subj = "01 02 02";
    Inst inst = new Inst(null, subj);
    inst.process();
  }

  @Test
  void testPush() {
    Inst inst = new Inst(null, "");
    inst.tok[0] = '1';

    char[] chars = new char[] { '0', '1', '2', 'A', 'F' };
    for (char c : chars) {
      inst.tok[1] = c;
      inst.push();
    }

    assertEquals(5, inst.stack.elementsCount);

    byte[] expecteds = { 0xF, 0xA, 0x2, 0x1, 0x0 };
    for (byte expected : expecteds) {
      byte actual = inst.stack.pop();
      assertEquals(expected, actual);
    }

    // Check for invalid ranges. None of these should get pushed.
    chars = new char[] { 'G', 'H', 'Z', ' ', '*', '\'' };
    for (char c : chars) {
      inst.tok[1] = c;
      inst.push();
    }
    assertEquals(0, inst.stack.elementsCount);
  }

  @Test
  void testNeg() {
    String subj = "01 10 22";
    Inst inst = new Inst(null, subj);
    ByteStack stack = inst.stack;

    inst.process();

    assertEquals(1, stack.pop());

    subj = "01 11 22";
    inst = new Inst(null, subj);
    stack = inst.stack;

    inst.process();

    assertEquals(0, stack.pop());
  }

  @Test
  void testIf() {
    // Test when the stack is 'true'
    // BEGIN, PUSH(1), IF, PUSH(A), ENDIF
    String subj = "01 11 40 1A 41";
    Inst inst = new Inst(null, subj);
    ByteStack stack = inst.stack;

    inst.process();

    assertEquals(0xA, stack.pop());

    // Test when the stack is 'false'
    // BEGIN, PUSH(0), IF, PUSH(A), ENDIF
    subj = "01 10 40 1A 41";
    inst = new Inst(null, subj);
    stack = inst.stack;

    inst.process();

    assertTrue(stack.isEmpty());
  }

  @Test
  void testReturn() {
    String[] subs = {
        // PUSH(1), RET
        // Expected value = 1
        "01 11 51",

        // PUSH(1), RET, PUSH(10), RET
        // Expected value = 1
        // (10 is out of range for a valid return value)
        "01 11 51 1A 51",

        // PUSH(1), RET, PUSH(10), RET, PUSH(2), RET, PUSH(10)
        // Expected value = 2
        // (2 is the biggest in-range value)
        "01 11 51 1A 51 12 51 1A 51",

        // PUSH(2), RET, PUSH(1), RET
        // Expected value = 2
        "01 12 51 11 51"
    };

    int[] expecteds = {
        0x1, 0x1, 0x2, 0x2
    };

    for (int i=0; i < subs.length; i++) {
      String subj = subs[i];
      Inst inst = new Inst(null, subj);
      int expected = expecteds[i];
      int actual = inst.process();
      assertEquals(expected, actual, () -> "Return processing failed for subject: \"" + subj + "\"");
    }
  }

  @Test
  void testRead() {
    Hand hand = Hand.builder()
        .addCard(5, Suit.HEARTS)
        .addCard(7, Suit.DIAMONDS)
        .addCard(7, Suit.CLUBS)
        .addCard(8, Suit.CLUBS)
        .addCard(9, Suit.SPADES)
        .build();

    Inst inst = new Inst(hand, "");
    char[] tok = inst.tok;
    ByteStack stack = inst.stack;
    assertTrue(stack.isEmpty());

    tok[0] = '7';

    // Check for a read without anything on the stack. This just returns
    // without doing anything.
    inst.read();
    assertTrue(stack.isEmpty());

    // Check for invalid param '2'
    tok[1] = '2';
    stack.push((byte) 0);
    inst.read();
    assertEquals(0, stack.pop());

    // Check for invalid param '-1'
    tok[1] = (char) -1;
    stack.push((byte) 0);
    inst.read();
    assertEquals(0, stack.pop());

    // Set tok[1] back to '0' to continue in the method
    tok[1] = '0';

    // Check when the index value is out of range. This will pop a value
    // of the stack when we read it. The valid range for the index is 0..4
    stack.push((byte) 5);
    inst.read();
    assertTrue(stack.isEmpty());

    stack.push((byte) -1);
    inst.read();
    assertTrue(stack.isEmpty());


    // Read all of the ranks, ensure that they produce expected values
    // on the stack.
    int[] ranks = hand.getRanks();
    for (int i = 0; i < ranks.length; i++) {
      stack.push((byte) i);
      inst.read();
      assertEquals(ranks[i], stack.pop());
    }

    // Read all of the card suits
    // 0x1 specifies to read the suit of the card
    tok[1] = '1';

    int[] suits = hand.getSuits();
    for (int i = 0; i < suits.length; i++) {
      stack.push((byte) i);
      inst.read();
      assertEquals(suits[i], stack.pop());
    }

    assertTrue(stack.isEmpty());
  }


  @Test
  void testCount() {
    Hand hand = Hand.builder()
        .addCard(5, Suit.HEARTS)
        .addCard(7, Suit.DIAMONDS)
        .addCard(7, Suit.CLUBS)
        .addCard(8, Suit.CLUBS)
        .addCard(9, Suit.SPADES)
        .build();

    Inst inst = new Inst(hand, "");
    char[] tok = inst.tok;
    ByteStack stack = inst.stack;
    assertTrue(stack.isEmpty());

    tok[0] = '8';
    tok[1] = '0';
    // Check for an empty stack
    inst.count();
    assertTrue(stack.isEmpty());

    // Check for invalid params
    stack.push((byte) 0);
    tok[1] = '2';
    inst.count();
    assertEquals(0, stack.pop());

    stack.push((byte) 0);
    tok[1] = (char) -1;
    inst.count();
    assertEquals(0, stack.pop());
    assertTrue(stack.isEmpty());

    // Check for counts of rank
    tok[1] = '0';

    // Invalid rank < 1 or > 13
    stack.push((byte) 0);
    inst.count();
    assertTrue(stack.isEmpty());

    stack.push((byte) 14);
    inst.count();
    assertTrue(stack.isEmpty());

    // Ranks are 1..13 (not zero based)
    int[] expectedRankCounts = { 0, 0, 0, 0, 1, 0, 2, 1, 1, 0, 0, 0, 0 };
    for (int i = 0; i < expectedRankCounts.length; i++) {
      stack.push((byte) (i + 1));
      inst.count();
      assertEquals(expectedRankCounts[i], stack.pop());
    }

    // Check for the count of suits
    tok[1] = '1';

    // Invalid rank < 1 or > 4
    stack.push((byte) 0);
    inst.count();
    assertTrue(stack.isEmpty());

    stack.push((byte) 5);
    inst.count();
    assertTrue(stack.isEmpty());

    stack.push((byte) Suit.HEARTS.getNum());
    inst.count();
    assertEquals(1, stack.pop()); // We have 1 heart in the hand

    stack.push((byte) Suit.SPADES.getNum());
    inst.count();
    assertEquals(1, stack.pop()); // We have 1 spade in the hand

    stack.push((byte) Suit.DIAMONDS.getNum());
    inst.count();
    assertEquals(1, stack.pop()); // We have 1 diamond in the hand

    stack.push((byte) Suit.CLUBS.getNum());
    inst.count();
    assertEquals(2, stack.pop()); // We have 2 clubs in the hand

    assertTrue(stack.isEmpty());

  }

  @Test
  void testLoop() {
    Inst inst = new Inst(null, "");

    char[] tok = inst.tok;
    ByteStack stack = inst.stack;

    tok[0] = '9';
    tok[1] = '0';
    inst.loop();
    assertTrue(stack.isEmpty());

    stack.push((byte) 0);
    inst.loop();
    assertEquals(0, stack.pop());

    stack.push((byte) 1);
    stack.push((byte) 10);

    inst.loop();

    assertEquals(10, stack.size());
    for (int i = 10; i >= 1; i--) {
      assertEquals(i, stack.pop());
    }
  }

  @Test
  void testLoopFull() {
    String subj = "01 11 1A 80 00 81";
    Inst inst = new Inst(null, subj);
    ByteStack stack = inst.stack;

    inst.process();

    assertEquals(10, stack.size());
    for (int i = 10; i >= 1; i--) {
      assertEquals(i, stack.pop());
    }

    // TODO test for run off conditions
    // or nested loops
  }
}
