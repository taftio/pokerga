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

  private static final Interpreter interpreter = new Interpreter();


  @Test
  void testUnderflow() {
    // Ensure that we don't throw any exceptions for these weird parse states.
    String[] subs = { "", " ", "  ", "x", "00", "01", "02", "01 02 02", "01 ", "01  " };

    for (String sub : subs) {
      Inst inst = new Inst(null, sub, interpreter);
      inst.process();
      try {
        inst.process();
      } catch (BufferUnderflowException e) {
        fail("Buffer Underflow detected for subject: ->" + sub + "<-", e);
      }
    }
  }

  @Test
  void testPush() {
    Inst inst = new Inst(null, "00 01 02 0A 0F", interpreter);
    ByteStack stack = inst.stack;

    inst.process();

    assertEquals(5, stack.size());

    byte[] expecteds = { 0xF, 0xA, 0x2, 0x1, 0x0 };
    for (byte expected : expecteds) {
      byte actual = stack.pop();
      assertEquals(expected, actual);
    }
    assertTrue(stack.isEmpty());
  }

  @Test
  void testNeg() {
    Inst inst = new Inst(null, "6", interpreter);
    ByteStack stack = inst.stack;
    stack.push((byte) 0);
    inst.process();
    assertEquals(1, stack.pop());
    assertTrue(stack.isEmpty());

    inst = new Inst(null, "6", interpreter);
    stack = inst.stack;
    stack.push((byte) 0);
    inst.process();
    assertEquals(1, stack.pop());
    assertTrue(stack.isEmpty());

    // A test for double negative
    new Inst(null, "66", interpreter);
    stack = inst.stack;
    stack.push((byte) 1);
    inst.process();
    assertEquals(1, stack.pop());
    assertTrue(stack.isEmpty());
  }

  @Test
  void testIf() {
    // Test when the stack is 'true'
    // PUSH(1), IF, PUSH(A), ENDIF
    String subj = "01 7 0A 8";
    Inst inst = new Inst(null, subj, interpreter);
    ByteStack stack = inst.stack;

    inst.process();

    assertEquals(0xA, stack.pop());

    // Test when the stack is 'false'
    // PUSH(0), IF, PUSH(A), ENDIF
    subj = "00 7 0A 8";
    inst = new Inst(null, subj, interpreter);
    stack = inst.stack;

    inst.process();

    assertTrue(stack.isEmpty());
  }

  @Test
  void testReturn() {
    String[] subs = {
        // PUSH(1), RET
        // Expected value = 1
        "01 9",

        // PUSH(1), RET, PUSH(10), RET
        // Expected value = 1
        // (10 is out of range for a valid return value)
        "01 9 0A 51",

        // PUSH(1), RET, PUSH(10), RET, PUSH(2), RET, PUSH(10)
        // Expected value = 2
        // (2 is the biggest in-range value)
        "01 9 0A 9 02 9",

        // PUSH(2), RET, PUSH(1), RET
        // Expected value = 2
        "02 9 01 9"
    };

    int[] expecteds = {
        0x1, 0x1, 0x2, 0x2
    };

    for (int i=0; i < subs.length; i++) {
      String subj = subs[i];
      Inst inst = new Inst(null, subj, interpreter);
      int expected = expecteds[i];
      int actual = inst.process();
      assertEquals(expected, actual, () -> "Return processing failed for subject: \"" + subj + "\"");
    }
  }

  @Test
  void testReadRanks() {
    Hand hand = Hand.newBuilder()
        .addCard(5, Suit.HEARTS)
        .addCard(7, Suit.DIAMONDS)
        .addCard(7, Suit.CLUBS)
        .addCard(8, Suit.CLUBS)
        .addCard(9, Suit.SPADES)
        .evaluation(1)
        .build();

    int[] expecteds = { 5, 7, 7, 8, 9 };

    for (int i = 0; i < expecteds.length; i++) {
      Inst inst = new Inst(hand, "A0", interpreter);
      ByteStack stack = inst.stack;
      stack.push((byte) i);

      inst.process();

      assertEquals(expecteds[i], stack.pop());
      assertTrue(stack.isEmpty());
    }

  }

  @Test
  void testReadSuits() {
    Hand hand = Hand.newBuilder()
        .addCard(5, Suit.HEARTS)
        .addCard(7, Suit.DIAMONDS)
        .addCard(7, Suit.CLUBS)
        .addCard(8, Suit.CLUBS)
        .addCard(9, Suit.SPADES)
        .evaluation(1)
        .build();

    int[] expecteds = { Suit.HEARTS.getNum(), Suit.DIAMONDS.getNum(), Suit.CLUBS.getNum(), Suit.CLUBS.getNum(),
        Suit.SPADES.getNum() };

    for (int i = 0; i < expecteds.length; i++) {
      Inst inst = new Inst(hand, "A1", interpreter);
      ByteStack stack = inst.stack;
      stack.push((byte) i);

      inst.process();

      assertEquals(expecteds[i], stack.pop());
      assertTrue(stack.isEmpty());
    }

  }


  @Test
  void testCountRanks() {
    Hand hand = Hand.newBuilder()
        .addCard(5, Suit.HEARTS)
        .addCard(7, Suit.DIAMONDS)
        .addCard(7, Suit.CLUBS)
        .addCard(8, Suit.CLUBS)
        .addCard(9, Suit.SPADES)
        .evaluation(1)
        .build();

    int[] expecteds = {
        0, 0, 0, 0, 1, 0, 2, 1, 1, 0, 0, 0, 0
    };

    for (int i = 0; i < expecteds.length; i++) {
      Inst inst = new Inst(hand, "B0", interpreter);
      ByteStack stack = inst.stack;
      stack.push((byte) (i + 1));
      inst.process();
      assertEquals(expecteds[i], stack.pop());
    }

  }

  @Test
  void testCountSuits() {
    Hand hand = Hand.newBuilder()
        .addCard(5, Suit.HEARTS)
        .addCard(7, Suit.DIAMONDS)
        .addCard(7, Suit.CLUBS)
        .addCard(8, Suit.CLUBS)
        .addCard(9, Suit.SPADES)
        .evaluation(1)
        .build();

    int[] inputs = {
        Suit.HEARTS.getNum(), Suit.DIAMONDS.getNum(), Suit.CLUBS.getNum(), Suit.SPADES.getNum()
    };
    int[] expecteds = {
        1, 1, 2, 1
    };

    for (int i = 0; i < inputs.length; i++) {
      Inst inst = new Inst(hand, "B1", interpreter);
      ByteStack stack = inst.stack;
      stack.push((byte) inputs[i]);
      inst.process();
      assertEquals(expecteds[i], stack.pop());
    }

  }


  @Test
  void testLoop() {
    String subj = "01 0A C D";
    Inst inst = new Inst(null, subj, interpreter);
    ByteStack stack = inst.stack;

    inst.process();

    assertEquals(10, stack.size());
    for (int i = 10; i >= 1; i--) {
      assertEquals(i, stack.pop());
    }
  }
}
