package pokerga;

public enum OpCode {

  PUSH, DUP, DROP, ADD, SUB, CMP, NOT, IFOP, ENDIF, RET, READ, CNT, LOOP, ENDLOOP,
  UNK, EOF,
  ;

  // Used to speed up lookup of OpCode values using the static #from method
  // This array needs to be kept in sync with the above codes
  private static OpCode[] opcodes = {
      PUSH, DUP, DROP, ADD, SUB, CMP, NOT, IFOP, ENDIF, RET, READ, CNT, LOOP, ENDLOOP,
  };

  public static OpCode from(int code) {
    if (code < 0 || code >= opcodes.length) {
      return UNK;
    }
    return opcodes[code];
  }


}
