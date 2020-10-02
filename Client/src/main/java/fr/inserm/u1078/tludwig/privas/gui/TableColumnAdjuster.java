package fr.inserm.u1078.tludwig.privas.gui;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Class that resizes columns to fit header and data width
 * @author Thomas E. Ludwig (INSERM - U1078)
 * 
 * Javadoc Complte on 2019-08-09
 */
public class TableColumnAdjuster {

  /**
   * Use this static Method to Resize Table Columns
   *
   * @param table the JTable to resize
   */
  public static void adjustColumns(JTable table) {
    final int SPACING = 6;    
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    TableColumnModel tcm = table.getColumnModel();
    //For each column
    for (int column = 0; column < tcm.getColumnCount(); column++) {
      TableColumn tableColumn = table.getColumnModel().getColumn(column);

      //if the column is resizable
      if (tableColumn.getResizable()) {
        //Get header size
        TableCellRenderer renderer = tableColumn.getHeaderRenderer();
        if (renderer == null)
          renderer = table.getTableHeader().getDefaultRenderer();
        int newWidth = renderer.getTableCellRendererComponent(table, tableColumn.getHeaderValue(), false, false, -1, column).getPreferredSize().width;
        
        //Get Data width
        int maxWidth = table.getColumnModel().getColumn(column).getMaxWidth();
        for (int row = 0; row < table.getRowCount() && newWidth < maxWidth; row++) {
          newWidth = Math.max(newWidth, table.prepareRenderer(table.getCellRenderer(row, column), row, column).getPreferredSize().width + table.getIntercellSpacing().width);
        }
        
        //Set new width to max of observed width + SPACING
        table.getTableHeader().setResizingColumn(tableColumn);
        tableColumn.setWidth(newWidth + SPACING);
      }
    }
  }
}