package fr.inserm.u1078.tludwig.privas.gui.helper;

import javax.swing.text.Document;

/**
 * TextField that check if the contained filename exists
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-08-17
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class FileCheckerTextField extends CheckedTextField {
  @SuppressWarnings("unused")
  public FileCheckerTextField() {
    super();
    this.setFile();
    this.setEnabled(false);
  }

  @SuppressWarnings("unused")
  public FileCheckerTextField(String text) {
    super(text);
    this.setFile();
    this.setEnabled(false);
  }

  public FileCheckerTextField(int columns) {
    super(columns);
    this.setFile();
    this.setEnabled(false);
  }

  @SuppressWarnings("unused")
  public FileCheckerTextField(String text, int columns) {
    super(text, columns);
    this.setFile();
    this.setEnabled(false);
  }

  @SuppressWarnings("unused")
  public FileCheckerTextField(Document doc, String text, int columns) {
    super(doc, text, columns);
    this.setFile();
    this.setEnabled(false);
  }
}
