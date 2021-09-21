package fr.inserm.u1078.tludwig.privas.gui.helper;

import javax.swing.text.Document;

/**
 * CheckedTextField for Positive Long
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-08-17
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
@SuppressWarnings("unused")
public class PositiveLongCheckTextField extends CheckedTextField {
  public PositiveLongCheckTextField() {
    super();
    this.setLong(1L, Long.MAX_VALUE);
  }

  public PositiveLongCheckTextField(String text) {
    super(text);
    this.setLong(1L, Long.MAX_VALUE);
  }

  public PositiveLongCheckTextField(int columns) {
    super(columns);
    this.setLong(1L, Long.MAX_VALUE);
  }

  public PositiveLongCheckTextField(String text, int columns) {
    super(text, columns);
    this.setLong(1L, Long.MAX_VALUE);
  }

  public PositiveLongCheckTextField(Document doc, String text, int columns) {
    super(doc, text, columns);
    this.setLong(1L, Long.MAX_VALUE);
  }
}
