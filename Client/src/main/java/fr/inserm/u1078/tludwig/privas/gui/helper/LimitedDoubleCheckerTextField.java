package fr.inserm.u1078.tludwig.privas.gui.helper;

import javax.swing.text.Document;

/**
 * CheckTextField for Double within a range
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-08-17
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */

public class LimitedDoubleCheckerTextField extends CheckedTextField {
  public LimitedDoubleCheckerTextField(double[] minmax) {
    this.setDouble(minmax[0], minmax[1]);
  }

  public LimitedDoubleCheckerTextField(String text, double[] minmax) {
    super(text);
    this.setDouble(minmax[0], minmax[1]);
  }

  public LimitedDoubleCheckerTextField(int columns, double[] minmax) {
    super(columns);
    this.setDouble(minmax[0], minmax[1]);
  }

  public LimitedDoubleCheckerTextField(String text, int columns, double[] minmax) {
    super(text, columns);
    this.setDouble(minmax[0], minmax[1]);
  }

  public LimitedDoubleCheckerTextField(Document doc, String text, int columns, double[] minmax) {
    super(doc, text, columns);
    this.setDouble(minmax[0], minmax[1]);
  }
}
