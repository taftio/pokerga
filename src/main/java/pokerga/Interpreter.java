package pokerga;

import com.carrotsearch.hppc.ByteStack;
import java.nio.CharBuffer;
import java.util.Objects;

/**
 * Provides parsing and interpretation of the hexstring that follows the
 * specification. The opcodes defined will be implemented by this class.
 * <p>
 * This class is thread safe and the public method(s) can be safely called
 * concurrently.
 */
public final class Interpreter {

  /**
   * Interprets and executions the instructions according to the opcodes provided
   * in the subject. Returns the best evaluation of the hand by the subject
   * (typically the highest value). Since multiple interpretive segments can
   * exists in the subject string (e.g. between multiple begin/end pairs), the
   * value returned here will be the best or highest value after all the segments
   * have been evaluated against the hand.
   *
   * @return The best evaluation of the specified hand by the subject
   */
  public int process(Hand hand, String subject) {
    Objects.requireNonNull(hand);
    Objects.requireNonNull(subject);
    Inst inst = new Inst(hand, subject);
    return inst.process();
  }


  /**
   * An inner private instance class that performs the work of the interpreter and
   * returns the value from the interpretation. This inner class is not thread
   * safe, thus the parent class needs to create a new instance of this for every
   * invocation.
   */
  static class Inst {
    // These are package protected for testing
    final char[] tok = { '0', '0' }; // initialize to noopß;
    final ByteStack stack = new ByteStack();

    // Required to be passed into constructor
    private final Hand hand;
    private final CharBuffer buf;

    // The return value is updated anytime the RET opcode is found. The RET code
    // will only update this if the newest interpretation is larger than this value.
    // Because multiple begin/end sequences can occur in the subject, the best
    // (e.g. highest) value is what should be returned. This defaults to zero,
    // which is the minimal score for any segment.
    int returnValue = 0;

    public Inst(Hand hand, CharBuffer buf) {
      this.hand = hand;
      this.buf = buf;
    }

    public Inst(Hand hand, String subject) {
      this(hand, CharBuffer.wrap(subject));
    }

    public int process() {
      // This main loop drives the overall processing. Other loops will
      // likely be called based on the opcodes consumed in this loop.
      while (buf.hasRemaining()) {

        // This will seek until it finds the BEG marker. At that point,
        // it will BEG token and continue with other codes.
        seekTo(OpCode.BEG);

        // This will execute operations until it reaches the END marker.
        // It will also call process with END before finishing out.
        execUntil(OpCode.END);
      }

      return returnValue;
    }


    /**
     * Reads the next token from the buffer, returning its enum value and storing
     * the full token into the {@link #tok} char array. The OpCode is effectively
     * determined by using the two character array called the
     * {@link OpCode#from(char[])} method to retrieve it. If the buffer has reached
     * the end of stream, then EOF is returned.
     *
     * @return The Code assoicated to the current char array token or EOF.
     */
    OpCode next() {
      skipNoise();
      if (buf.remaining() < 2) {
        return OpCode.EOF;
      }
      buf.get(tok);
      return OpCode.from(tok);
    }


    /**
     * Skips operations in the buffer until the specified OpCode is found. The
     * operation specified will then be processed before exiting the method.
     *
     * @param op The {@link OpCode} to seek to, which will then be executed after
     *           being found.
     */
    void seekTo(OpCode op) {
      Objects.requireNonNull(op);
      OpCode next = OpCode.EOF;
      while (buf.hasRemaining()) {
        next = next();
        if (OpCode.EOF.equals(next)) {
          return;
        }
        if (op.equals(next)) {
          break;
        }
      }
      if (op.equals(next)) {
        process(next);
      }
    }


    /**
     * Executes operations from the buffer until the specified OpCode is found. The
     * operation specified will be processed before returning.
     *
     * @param op The {@link OpCode} that will halt current execution
     */
    void execUntil(OpCode op) {
      Objects.requireNonNull(op);

      while (buf.hasRemaining()) {
        OpCode next = next();
        process(next);
        if (op.equals(next)) {
          break;
        }
      }
    }


    /**
     * Skips characters in the stream that are invalid for the domain. Specifically
     * skipping non-hexdigits, spaces, etc.
     */
    void skipNoise() {
      while (buf.hasRemaining()) {
        // Peek at the current character and check if its in the valid range.
        char c = buf.get(buf.position());
        if (c >= '0' && c <= '9') {
          break;
        }
        if (c >= 'A' && c <= 'F') {
          break;
        }
        // Consume the invalid character.
        buf.get();
      }
    }


    /**
     * Processes the specified operation, effectively delegating to methods
     * dedicated to each opcode.
     *
     * @param code
     */
    void process(OpCode code) {
      switch (code) {
      case NOP:
        nop();
        break;
      case BEG:
        begin();
        break;
      case END:
        end();
        break;
      case PUSH:
        push();
        break;
      case DROP:
        drop();
        break;
      case DUP:
        dup();
        break;
      case NEG:
        neg();
        break;
      case ADD:
        add();
        break;
      case SUB:
        sub();
        break;
      case INCR:
        incr();
        break;
      case DECR:
        decr();
        break;
      case IFOP:
        ifop();
        break;
      case ENDIF:
        endif();
        break;
      case CMP:
        cmp();
        break;
      case RET:
        ret();
        break;
      case READ:
        read();
        break;
      case CNT:
        count();
        break;
      case LOOP:
        loop();
        break;
      case ENDLOOP:
        endloop();
        break;
      case EOF:
      case UNK:
        break;
      }
    }


    void nop() {
      // And a good nop to you, dear sir.
    }


    void begin() {
      // Ignored. BEGIN is effectively a no-op the way we process.
    }


    void end() {
      // Anytime we receive an END, we're going to clear our stack.
      stack.clear();
    }


    void push() {
      // Returns -1 for values outside of the hex radix of 16.
      int val = Character.digit(tok[1], 16);
      if (val < 0) {
        return;
      }

      stack.push((byte) val);
    }


    void drop() {
      if (stack.isEmpty()) {
        return;
      }
      stack.discard();
    }


    void dup() {
      if (stack.isEmpty()) {
        return;
      }

      byte peek = stack.peek();
      stack.push(peek);
    }


    void neg() {
      if (stack.isEmpty()) {
        return;
      }

      int val = stack.pop();
      if (val == 0) {
        stack.push((byte) 1);
      } else {
        stack.push((byte) 0);
      }
    }


    void add() {
      if (stack.size() < 2) {
        return;
      }

      int b1 = stack.pop();
      int b2 = stack.pop();
      int sum = b1 + b2;
      stack.push((byte) sum);
    }


    void sub() {
      if (stack.size() < 2) {
        return;
      }

      int b1 = stack.pop();
      int b2 = stack.pop();
      int diff = b1 - b2;
      stack.push((byte) diff);
    }


    void incr() {
      if (stack.isEmpty()) {
        return;
      }

      int b = stack.pop();
      b += 1;
      stack.push((byte) b);
    }


    void decr() {
      if (stack.isEmpty()) {
        return;
      }

      int b = stack.pop();
      b -= 1;
      stack.push((byte) b);
    }


    void ifop() {
      if (stack.isEmpty()) {
        return;
      }

      // Evaluate the value at top of the stack.
      // Zero = false, Non-zero = true
      boolean eval = stack.pop() != 0;

      if (eval) {
        execUntil(OpCode.ENDIF);
      } else {
        seekTo(OpCode.ENDIF);
      }
    }


    void endif() {
      // nothing to do
    }


    void cmp() {
      if (stack.size() < 2) {
        return;
      }

      int b1 = stack.pop();
      int b2 = stack.pop();
      if (b1 == b2) {
        stack.push((byte) 1);
      } else {
        stack.push((byte) 0);
      }
    }


    void ret() {
      // Returns the value on the top of the stack. By return, we basically mean
      // capture it into our returnValue variable. We do this by checking if the
      // value on the stack is greater than our current return value, favoring
      // to always return the greatest value that this subject has evaluated.
      //
      // The dataset being evaluated only supports return values between 0..9
      // inclusive. If it's out of this range, we don't do anything. The potential
      // return value will always be removed from the stack.

      if (stack.isEmpty()) {
        return;
      }

      int ret = stack.pop();

      if (ret < 0 || ret > 9) {
        return;
      }

      if (ret > returnValue) {
        returnValue = ret;
      }

    }


    void read() {
      // Reads either the suit or rank of the specified card index and pushes
      // the value to the stack. The card index is taken from the stack. Whether we
      // are reading the suit or rank depends on the second nibble in the token.
      // For any encoding or state errors, we just return early.

      // Check if the stack is empty.
      if (stack.isEmpty()) {
        return;
      }

      // Param: 2nd nibble of Token:
      // 0x0 = Rank
      // 0x1 = Suit
      int param = Character.digit(tok[1], 16);
      if (param < 0x0 || param > 0x1) {
        return;
      }

      int idx = stack.pop();
      // We only have 5 cards, so the index here needs to reflect that.
      if (idx < 0 || idx > 4) {
        return;
      }

      int value;
      if (param == 0x0) {
        // Read the rank
        value = hand.get(idx).getRank();

      } else {
        // Read the suit
        value = hand.get(idx).getSuit();
      }

      // Push the read value (rank or suit) back onto the stack.
      stack.push((byte) value);
    }


    void count() {
      if (stack.isEmpty()) {
        return;
      }

      int param = Character.digit(tok[1], 16);
      if (param < 0 || param > 1) {
        return;
      }

      int count = 0;

      if (param == 0) {
        // Counts the rank in the range of 1..13
        int rank = stack.pop();
        if (rank < 1 || rank > 13) {
          return;
        }

        for (Card card : hand) {
          if (card.getRank() == rank) {
            count++;
          }
        }

      } else {
        // Counts the suit in the range of 1..4
        int suit = stack.pop();
        if (suit < 1 || suit > 4) {
          return;
        }

        for (Card card : hand) {
          if (card.getSuit() == suit) {
            count++;
          }
        }
      }

      // Store the count to the stack
      stack.push((byte) count);
    }


    void loop() {
      // We need to take two values from the stack. Ensure they are available.
      if (stack.size() < 2) {
        return;
      }

      // The two values are our low and high loop values
      int high = stack.pop();
      int low = stack.pop();

      // We need to record our current position in the buffer so we can
      // return for execution at the top of each loop.
      int pos = buf.position();

      // Execute a for loop with the high and low values.
      // The iterative value of the loop will be placed on the stack.
      // We reset our position in the buffer after every loop iteration.
      for (int i = low; i <= high; i++) {
        stack.push((byte) i);
        execUntil(OpCode.ENDLOOP);
        buf.position(pos);
      }
    }


    void endloop() {
      // Nothing to do.
    }

  }
}