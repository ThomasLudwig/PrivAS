package fr.inserm.u1078.tludwig.privas.gui;

import fr.inserm.u1078.tludwig.privas.constants.Constants;
import fr.inserm.u1078.tludwig.privas.constants.GUI;
import fr.inserm.u1078.tludwig.privas.instances.TPStatus;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Window showing Third Party Server Messages
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-10-06
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class TPSLogWindow extends JFrame {
  public static final int COL_NUM = 0;
  public static final int COL_TIME = 1;
  public static final int COL_STATE = 2;
  public static final int COL_MSG = 3;
  private static final int MIN_COL_WIDTH = 20;

  private final JTable table;
  private final TPStatusTableModel tableModel;
  private final JPanel mainPanel;
  private boolean wasClosed = false;

  public TPSLogWindow() {
    mainPanel = new JPanel();
    tableModel = new TPStatusTableModel();
    table = new JTable(tableModel);
    init();
  }

  private void init() {
    //this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        wasClosed = true;
      }
    });
    this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    this.table.getTableHeader().setReorderingAllowed(false);
    this.table.getTableHeader().setFont(this.table.getTableHeader().getFont().deriveFont(Font.BOLD));
    table.setDefaultRenderer(Object.class, new FieldsRenderer());
    table.setFont(GUI.DEFAULT_FONT);

    mainPanel.setLayout(new BorderLayout());
    mainPanel.add(new JLabel(GUI.TPS_LABEL_MESSAGES), BorderLayout.NORTH);
    JScrollPane scroll = new JScrollPane(this.table);
    mainPanel.add(scroll, BorderLayout.CENTER);

    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(mainPanel, BorderLayout.CENTER);
    this.setTitle(GUI.TPS_TITLE);
    this.pack();
    try {
      this.setIconImage(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource(GUI.IMAGE_PATH_LOG_LOGO)));
    } catch (Exception e) {
      //Nothing
    }
  }

  public void add(TPStatus tpStatus) {
    this.tableModel.add(tpStatus);
    if(!wasClosed)
      this.display();
  }

  public void display(){
    this.resizeColumnWidth(this.table);
    this.table.revalidate();
    this.table.repaint();
    this.setVisible(true);
    this.wasClosed = false;
  }

  public void resizeColumnWidth(JTable table) {
    final TableColumnModel columnModel = table.getColumnModel();

    for (int column = 0; column < table.getColumnCount(); column++) {
        int width = MIN_COL_WIDTH;
        for (int row = 0; row < table.getRowCount(); row++) {
          TableCellRenderer renderer = table.getCellRenderer(row, column);
          Component comp = table.prepareRenderer(renderer, row, column);
          width = Math.max(comp.getPreferredSize().width + 15, width);
        }
        columnModel.getColumn(column).setPreferredWidth(width);
    }
  }

  private static class FieldsRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      //style
      c.setForeground(table.getTableHeader().getForeground());
      this.setHorizontalAlignment((column == COL_NUM || column == COL_TIME) ? SwingConstants.RIGHT : SwingConstants.LEFT);

      if(column == COL_STATE) {
        c.setForeground(getForeground((TPStatus.State)value));
        c.setFont(c.getFont().deriveFont(Font.BOLD));
      }

      if(column == COL_TIME) {
        Date when = new Date((long)value);
        String day = Constants.DF_DAY.format(when);
        String time = Constants.DF_TIME.format(when);
        String today = Constants.DF_DAY.format(new Date());
        String text = time;
        if(!day.equals(today)){
          text = day+" "+time;
        }
        ((JLabel) c).setText(text);
      }

      return c;
    }

    private Color getForeground(TPStatus.State state){
      switch(state){
        case PENDING : return GUI.COLOR_PENDING;
        case STARTED : return GUI.COLOR_STARTED;
        case RUNNING : return GUI.COLOR_RUNNING;
        case DONE : return GUI.COLOR_DONE;
        case ERROR : return GUI.COLOR_ERROR;
        case UNREACHABLE: return GUI.COLOR_UNREACHABLE;
        case UNKNOWN: return GUI.COLOR_UNKNOWN;
      }
      return Color.BLACK;
    }
  }

  static class TPStatusTableModel extends AbstractTableModel {
    private final List<TPStatus> statuses;

    TPStatusTableModel(){
      statuses = new ArrayList<>();
    }

    public void add(TPStatus tpStatus) {
      this.statuses.add(tpStatus);
    }

    @Override
    public String getColumnName(int columnIndex) {
      switch(columnIndex){
        case COL_NUM:
          return GUI.TPS_COL_NUM;
        case COL_TIME:
          return GUI.TPS_COL_TIME;
        case COL_STATE:
          return GUI.TPS_COL_STATE;
        case COL_MSG:
          return GUI.TPS_COL_MESSAGE;
        default :
          return null;
      }
    }

    @Override
    public boolean isCellEditable(int row, int col){
      return false;
    }

    @Override
    public int getRowCount() {
      return this.statuses.size();
    }

    @Override
    public int getColumnCount() {
      return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      switch(columnIndex){
        case 0 :
          return rowIndex+1;
        case 1 :
          return this.statuses.get(rowIndex).getEpoch();
        case 2 :
          return this.statuses.get(rowIndex).getState();
        case 3 :
          return this.statuses.get(rowIndex).getDetails();
        default :
          return null;
      }
    }
  }
}
