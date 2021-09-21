package fr.inserm.u1078.tludwig.privas.gui.helper;

import javax.swing.text.Document;

/**
 * CheckTextField for Double values
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-08-17
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class DoubleCheckerTextField extends LimitedDoubleCheckerTextField {
  public DoubleCheckerTextField() {
    super(new double[]{Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY});
  }

  public DoubleCheckerTextField(String text) {
    super(text, new double[]{Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY});
  }

  public DoubleCheckerTextField(int columns) {
    super(columns, new double[]{Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY});
  }

  public DoubleCheckerTextField(String text, int columns) {
    super(text, columns, new double[]{Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY});
  }

  public DoubleCheckerTextField(Document doc, String text, int columns) {
    super(doc, text, columns, new double[]{Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY});
  }
}
