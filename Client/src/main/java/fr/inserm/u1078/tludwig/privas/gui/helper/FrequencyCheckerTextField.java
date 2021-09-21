package fr.inserm.u1078.tludwig.privas.gui.helper;

import javax.swing.text.Document;

/**
 * CheckedTextField for Frequencies
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-08-17
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
@SuppressWarnings("unused")
public class FrequencyCheckerTextField extends CheckedTextField {
  public FrequencyCheckerTextField() {
    super();
    this.setDouble(0, 1);
  }

  public FrequencyCheckerTextField(String text) {
    super(text);
    this.setDouble(0, 1);
  }

  public FrequencyCheckerTextField(int columns) {
    super(columns);
    this.setDouble(0, 1);
  }

  public FrequencyCheckerTextField(String text, int columns) {
    super(text, columns);
    this.setDouble(0, 1);
  }

  public FrequencyCheckerTextField(Document doc, String text, int columns) {
    super(doc, text, columns);
    this.setDouble(0, 1);
  }
}
