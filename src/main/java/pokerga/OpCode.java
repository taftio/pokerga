package pokerga;

public enum OpCode {

  NOP,
  PUSH,
  DROP, DUP, NEG,
  ADD, SUB, INCR, DECR,
  IFOP, ENDIF,
  CMP, RET,
  READ,
  CNT,
  LOOP, ENDLOOP,
  UNK, EOF,
  ;

  // This array is deliberately constructed to match the layout of the opcode
  // byte codes directly. The two dimensional array mirrors the two-digit hexcode
  // encoding used by the opcode table.
  //
  // A lookup can be performed against this array in order to find the correct
  // opcode, by effectively taking the index from the two characters of the hex
  // values.
  //
  // If the row (inner array) has a single entry in it, that indicates that the
  // opcode value takes any value in the second nibble of the byte. e.g. it takes
  // a parameter value in the opcode. PUSH, READ, CNT all do this.
  private static OpCode[][] opcodes = {
      { NOP },
      {PUSH},
      {DROP, DUP, NEG},
      {ADD, SUB, INCR, DECR},
      {IFOP, ENDIF},
      {CMP, RET},
      {READ},
      {CNT},
      {LOOP, ENDLOOP}
  };

  public static OpCode from(char[] tok) {
    int d1 = Character.digit(tok[0], 16);
    if (d1 < 0 || d1 >= opcodes.length) {
      return UNK;
    }

    OpCode[] row = opcodes[d1];
    if (row.length == 1) {
      return row[0];
    }

    int d2 = Character.digit(tok[1], 16);
    if (d2 < 0 || d2 >= row.length) {
      return UNK;
    }

    return row[d2];
  }

}
