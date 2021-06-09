package fr.inserm.u1078.tludwig.privas.gui.results;

import fr.inserm.u1078.tludwig.privas.constants.GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Class responsible of rendering the Table Cells
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-05-21
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class FieldsRenderer extends DefaultTableCellRenderer {

  /**
   * Background Color of the normal cell
   */
  private static final Color BG_NORMAL_EVEN = Color.BLACK;//Color.WHITE;
  /**
   * Background Color of the normal cell
   */
  private static final Color BG_NORMAL_ODD = new Color(40,40,40);
  /**
   * Background Color of a selected cell
   */
  private static final Color BG_SELECTED = new Color(155, 226, 255);//Color.BLUE.brighter();

  /**
   * Foreground (text) Color of a selected cell
   */
  private static final Color FG_SELECTED = Color.BLACK;//Color.WHITE;
  /**
   * Foreground (text) Color of a normal cell
   */
  private static final Color FG_NORMAL = Color.WHITE;//Color.BLACK;

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    //style
    boolean na = Integer.parseInt((String) table.getValueAt(row, ResultsPane.COL_TOTAL)) == 0;

    c.setForeground(FG_NORMAL);
    c.setBackground(( row % 2 == 0) ? BG_NORMAL_EVEN : BG_NORMAL_ODD);

    if (column == ResultsPane.COL_NUM) {
      ((JLabel) c).setText("" + (1+row));
      c.setForeground(table.getTableHeader().getForeground());
      return c;
    }

    if (column == ResultsPane.COL_PVALUE) {
      c.setFont(GUI.DEFAULT_BOLD_FONT);
      c.setForeground(ResultsPane.getColor(value));
    }

    if (na)
      c.setForeground(ResultsPane.FG_NA);

    if (isSelected) {
      c.setForeground(FG_SELECTED);
      c.setBackground(BG_SELECTED);
    }

    //customize text
    if (column == ResultsPane.COL_TIME) {
      String v = (String) value;
      v = v.substring(0, v.length() - 1);
      int i = v.indexOf('.');
      String ms = v.substring(i + 1);
      int s = new Integer(v.substring(0, i));
      int m = s / 60;
      s = s % 60;
      String val = s + "." + ms + "s";
      if (m > 0) {
        int h = m / 60;
        m = m % 60;
        val = m + "m" + val;
        if (h > 0) {
          int d = h / 24;
          h = h % 24;
          val = h + "h" + val;
          if (d > 0)
            val = d + "d" + val;
        }
      }
      ((JLabel) c).setText(val);
    }
    return c;
  }
}
