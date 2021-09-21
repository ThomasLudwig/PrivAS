package fr.inserm.u1078.tludwig.privas.gui.helper;

import fr.inserm.u1078.tludwig.privas.constants.Constants;

import javax.swing.text.Document;

/**
 * CheckedTextField, for QCParam
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-08-17
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
@SuppressWarnings("unused")
public class QCParamTextField extends DoubleCheckerTextField {

  public QCParamTextField() {
  }

  public QCParamTextField(String text) {
    super(text);
  }

  public QCParamTextField(int columns) {
    super(columns);
  }

  public QCParamTextField(String text, int columns) {
    super(text, columns);
  }

  public QCParamTextField(Document doc, String text, int columns) {
    super(doc, text, columns);
  }

  @Override
  boolean checkDouble(String text) {
    if(Constants.DISABLED.equalsIgnoreCase(text))
      return true;
    try {
      double d = Double.parseDouble(text);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
