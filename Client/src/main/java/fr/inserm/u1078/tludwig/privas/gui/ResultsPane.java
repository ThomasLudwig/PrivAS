package fr.inserm.u1078.tludwig.privas.gui;

import fr.inserm.u1078.tludwig.privas.constants.FileFormat;
import fr.inserm.u1078.tludwig.privas.constants.GUI;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.utils.UniversalReader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Panel used to shows computation Results
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-03-08
 *
 * Javadoc Complete on 2019-08-09
 */
public class ResultsPane extends JPanel {

  /**
   * The Client Window owning (calling) this Panel
   */
  private final ClientWindow clientWindow;

  //FIXED  wrong color and Point Shape when only chrom2 is present
  private static final String[] COLUMN_TOOLTIP = {"",
    GUI.RP_TT_GENE,
    GUI.RP_TT_POSITION,
    GUI.RP_TT_PVALUE,
    GUI.RP_TT_K0,
    GUI.RP_TT_K,
    GUI.RP_TT_RANK,
    GUI.RP_TT_TOTAL,
    GUI.RP_TT_SHARED,
    GUI.RP_TT_TIME
  };
  private static final int COL_NUM = 0;
  private static final int COL_GENE = 1;
  private static final int COL_POSITION = 2;
  private static final int COL_PVALUE = 3;
  private static final int COL_K0 = 4;
  private static final int COL_K = 5;
  private static final int COL_RANK = 6;
  private static final int COL_TOTAL = 7;
  private static final int COL_SHARED = 8;
  private static final int COL_TIME = 9;

  private static final int PWIDTH = Toolkit.getDefaultToolkit().getScreenSize().width - 200;
  private static final int PHEIGHT = (Toolkit.getDefaultToolkit().getScreenSize().height - 170) / 2;

  /**
   * numerical comparator
   */
  private static final Comparator COMPARATOR_NUMERICAL = (o1, o2) -> {
    double d1 = Double.MAX_VALUE;
    double d2 = d1;
    try {
      d1 = new Double((String) o1);
    } catch (NumberFormatException e) {/*Nothing*/
    }
    try {
      d2 = new Double((String) o2);
    } catch (NumberFormatException e) {/*Nothing*/
    }
    return Double.compare(d1, d2);
  };

  /**
   * duration comparator
   */
  private static final Comparator COMPARATOR_TIME = (o1, o2) -> {
    double d1 = 0;
    double d2 = 0;
    try {
      d1 = new Double(((String) o1).replace("s", ""));
    } catch (NumberFormatException e) {/*Nothing*/
    }
    try {
      d2 = new Double(((String) o2).replace("s", ""));
    } catch (NumberFormatException e) {/*Nothing*/
    }
    return Double.compare(d1, d2);
  };

  /**
   * position comparator
   */
  private static final Comparator COMPARATOR_POSITION = (o1, o2) -> new Position((String) o1).compareTo(new Position((String) o2));

  //private HashMap<String, Region> ccds;
  //DONE  manathan plot view (dot size as a factor of gene size ?)
  //DONE  export table (HTML)
  //DONE  export table (TSV)
  //DONE  export Manhattan (PNG)
  //DONE  panel can be launched without re-downloading results
  //DONE  FIXME : column "#" should not be sortable
  //DONE  gene position CCDS - first variant of client file
  /**
   * The File from which the results are read
   */
  private File resultFile;
  public static final String TEMPLATE_URL = "http://lysine.univ-brest.fr/privas/results.template.html";

  /**
   * Constructor
   *
   * @param clientWindow The Client Window owning (calling) this Panel
   */
  public ResultsPane(ClientWindow clientWindow) {
    this.clientWindow = clientWindow;
    this.init();
  }

  /**
   * Initializes the Panel (layouts)
   */
  private void init() {
    this.setLayout(new BorderLayout());
    this.add(new JLabel(GUI.RP_NO_RESULTS), BorderLayout.CENTER);
  }

  /**
   * Shows this panel as a JOptionPane
   */
  public void display() {
    JOptionPane.showMessageDialog(this.clientWindow, this, GUI.RP_TITLE, JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Parses a lines from a Results File
   *
   * @param line the line to parse
   * @return a String array that will be showable in the Table of results
   * @throws fr.inserm.u1078.tludwig.privas.gui.ResultsPane.ParsingException if the line could not be parsed
   */
  private String[] parseLine(String line) throws ParsingException {
    try {
      String[] f = line.split("\t");
      String[] ret = new String[]{"", "", "", "", "", "", "", "", "", ""};
      ret[COL_GENE] = f[0];
      if (f.length > 1) {
        ret[COL_POSITION] = f[1];
        ret[COL_PVALUE] = f[2];
        ret[COL_K0] = f[3];
        ret[COL_K] = f[4];
        ret[COL_RANK] = f[5];
        ret[COL_TOTAL] = f[6];
        ret[COL_SHARED] = f[7];
        ret[COL_TIME] = f[8];
      }
      return ret;
    } catch (Exception e) {
      throw new ParsingException(MSG.cat(GUI.RP_KO_PARSE, line), e);
    }
  }

  /**
   * Updates this Panel according to a Results File
   *
   * @param results the Results File to load
   * @throws fr.inserm.u1078.tludwig.privas.gui.ResultsPane.ParsingException if the File could not be parsed
   */
  void setResults(File results) throws ParsingException {
    this.resultFile = results;
    try {
      DefaultTableModel model = new DefaultTableModel();
      JTable table = new JTable(model) {
        @Override
        public boolean isCellEditable(int row, int column) {
          return false;
        }

        @Override
        protected JTableHeader createDefaultTableHeader() {
          return new JTableHeader(columnModel) {
            @Override
            public String getToolTipText(MouseEvent e) {
              int index = columnModel.getColumnIndexAtX(e.getPoint().x);
              int realIndex
                      = columnModel.getColumn(index).getModelIndex();
              return COLUMN_TOOLTIP[realIndex];
            }
          };
        }
      };

      table.setDefaultRenderer(Object.class, new FieldsRenderer());
      table.setFont(GUI.DEFAULT_FONT);
      model.addColumn(GUI.RP_COL_NUM);
      model.addColumn(GUI.RP_COL_GENE);
      model.addColumn(GUI.RP_COL_POSITION);
      model.addColumn(GUI.RP_COL_PVALUE);
      model.addColumn(GUI.RP_COL_K0);
      model.addColumn(GUI.RP_COL_K);
      model.addColumn(GUI.RP_COL_RANK);
      model.addColumn(GUI.RP_COL_TOTAL);
      model.addColumn(GUI.RP_COL_SHARED);
      model.addColumn(GUI.RP_COL_TIME);

      TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
      sorter.setSortable(COL_NUM, false);

      for (int col = 0; col < table.getColumnCount(); col++)
        switch (col) {
          case COL_GENE:
            /*Nothing*/
            break;
          case COL_TIME:
            sorter.setComparator(col, COMPARATOR_TIME);
            break;
          case COL_POSITION:
            sorter.setComparator(col, COMPARATOR_POSITION);
            break;
          default:
            sorter.setComparator(col, COMPARATOR_NUMERICAL);
            break;
        }

      table.setAutoCreateRowSorter(true);
      table.setRowSorter(sorter);

      UniversalReader in = new UniversalReader(results.getAbsolutePath());
      String line;
      while ((line = in.readLine()) != null)
        model.addRow(parseLine(line));
      in.close();
      final TableColumnModel cModel = table.getColumnModel();
      for (int c = 0; c < table.getColumnCount(); c++) {
        int width = 15; // Min width
        for (int row = 0; row < table.getRowCount(); row++) {
          TableCellRenderer renderer = table.getCellRenderer(row, c);
          Component cp = table.prepareRenderer(renderer, row, c);
          width = Math.max(cp.getPreferredSize().width, width);
        }
        cModel.getColumn(c).setPreferredWidth(width);
      }

      buildGUI(table);
    } catch (IOException e) {
      String msg = MSG.cat(GUI.RP_KO_LOAD, results.getAbsolutePath());
      this.add(new JLabel(msg), BorderLayout.CENTER);
      this.clientWindow.getClient().logError(msg);
      this.clientWindow.getClient().logError(e);
    }
  }

  /**
   * Builds the Panel and adjusts the Results Table
   *
   * @param table the Table that holds the Results
   */
  private void buildGUI(JTable table) {
    //resizing
    TableColumnAdjuster.adjustColumns(table);
    //table.setPreferredSize(new Dimension(table.getPreferredSize().width, PHEIGHT));
    JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setPreferredSize(new Dimension(table.getPreferredSize().width + 50, PHEIGHT));

    JPanel south = new JPanel();
    south.setLayout(new BoxLayout(south, BoxLayout.LINE_AXIS));
    south.add(getManhattan(table, (int) (0.9 * PWIDTH), PHEIGHT, Manhattan.THEME_DARK));

    this.removeAll();
    this.setLayout(new BorderLayout());
    this.add(getMenuBar(table), BorderLayout.NORTH);
    this.add(scrollPane, BorderLayout.CENTER);
    this.add(south, BorderLayout.SOUTH);
  }

  /**
   * Builds a new MenuBar, the menu of which act on the current table
   *
   * @param table the Results Table
   * @return
   */
  private JMenuBar getMenuBar(JTable table) {
    JMenuBar bar = new JMenuBar();
    JMenu export = new JMenu(GUI.RP_MN_EXPORT);
    JMenuItem tsv = new JMenuItem(GUI.RP_MI_TSV);
    tsv.setToolTipText(GUI.RP_TT_TSV);
    tsv.addActionListener(e -> exportTSV(table));
    JMenuItem html = new JMenuItem(GUI.RP_MI_HTML);
    html.setToolTipText(GUI.RP_TT_HTML);
    html.addActionListener(e -> exportHTML(table));
    JMenuItem png = new JMenuItem(GUI.RP_MI_PNG);
    png.setToolTipText(GUI.RP_TT_PNG);
    png.addActionListener(e -> exportManhattan(table));

    export.add(tsv);
    export.add(html);
    export.add(png);
    bar.add(export);
    return bar;
  }

  /**
   * Foreground (text) Color for unknown values
   */
  private static final Color FG_NA = Color.GRAY;
  
  /**
   * Class responsible of rendering the Table Cells
   */
  private static class FieldsRenderer extends DefaultTableCellRenderer {

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
      boolean na = Integer.valueOf((String) table.getValueAt(row, COL_TOTAL)) == 0;

      c.setForeground(FG_NORMAL);
      c.setBackground(( row % 2 == 0) ? BG_NORMAL_EVEN : BG_NORMAL_ODD);
      
      if (column == COL_NUM) {
        ((JLabel) c).setText("" + (1+row));
        c.setForeground(table.getTableHeader().getForeground());
        return c;
      }

      if (column == COL_PVALUE) {
        c.setFont(GUI.DEFAULT_BOLD_FONT);
        c.setForeground(getColor(value));
      }

      if (na)
        c.setForeground(FG_NA);

      if (isSelected) {
        c.setForeground(FG_SELECTED);
        c.setBackground(BG_SELECTED);
      }

      //customize text
      if (column == COL_TIME) {
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

  /**
   * Gets the text color according to the p-value
   *
   * @param pvalue a String representing a Double p-value
   * @return
   */
  private static Color getColor(Object pvalue) {
    try {
      float log = (float) (-Math.log10(new Double((String) pvalue)) / 10);
      if (log < 0)
        log = 0;
      if (log > 1)
        log = 1;
      log *= 0.4f;
      return Color.getHSBColor(log, 1, 1);
    } catch (NumberFormatException e) {
      /*Nothing*/
    }

    return FG_NA;
  }

  /**
   * Gets a Manhattan plot of the Data in the Table
   *
   * @param table  the Results Table
   * @param width  the width of the plot (pixels)
   * @param height the height of the plot (pixels)
   * @param theme  the theme (0 for light or 1 for dark)
   * @return
   */
  private Manhattan getManhattan(JTable table, int width, int height, int theme) {
    Manhattan manhattan = new Manhattan(width, height, theme);
    for (int row = 0; row < table.getRowCount(); row++) {
      String gene = (String) table.getValueAt(row, COL_GENE);
      String[] region = ((String) table.getValueAt(row, COL_POSITION)).split(":");
      int chr = 0;
      int pos = 0;
      try {
        chr = new Integer(region[0].replace("X", "23").replace("Y", "24").replace("MT", "25"));
        pos = new Integer(region[1]);
      } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
        //Ignore
      }
      String pvalue = (String) table.getValueAt(row, COL_PVALUE);
      try {
        double pval = new Double(pvalue);
        if (chr > 0)
          manhattan.add(gene, chr, pos, pval);
      } catch (NumberFormatException e) {
        //Ignores NA pvalue
      }
    }
    manhattan.dataLoaded();
    return manhattan;
  }

  /**
   * Exports the Manhattan plot of a Table to a PNG file
   *
   * @param table the Results Table
   */
  private void exportManhattan(JTable table) {
    String defaultName = this.resultFile.getAbsolutePath();
    int idx = defaultName.lastIndexOf(".");
    if (idx != -1)
      defaultName = defaultName.substring(0, idx + 1);
    defaultName += FileFormat.FILE_PNG_EXTENSION;

    JFileChooser jfc = new FileExtensionChooser(FileFormat.FILE_PNG_EXTENSION, false, this.resultFile.getParent());
    jfc.setSelectedFile(new File(defaultName));
    if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      String png = jfc.getSelectedFile().getAbsolutePath();
      if (!png.endsWith(FileFormat.FILE_PNG_EXTENSION))
        png += "." + FileFormat.FILE_PNG_EXTENSION;
      try {
        this.exportManhattan(table, png, Manhattan.THEME_LIGHT);
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this, MSG.cat(GUI.RP_KO_SAVE, png, e), GUI.RP_KO_SAVE_PNG, JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  /**
   * Exports the Manhattan plot of a Table to a PNG file
   *
   * @param table the Results Table
   * @param png   the name of the PNG File
   * @param theme the theme (0 for light or 1 for dark)
   * @throws IOException
   */
  private void exportManhattan(JTable table, String png, int theme) throws IOException {
    Manhattan manhattan = getManhattan(table, GUI.RP_PNG_WIDTH, GUI.RP_PNG_HEIGHT, theme);
    manhattan.exportAsPNG(png);
  }

  /**
   * Exports the Results in a TSV File
   *
   * @param table
   */
  private void exportTSV(JTable table) {
    String defaultName = this.resultFile.getAbsolutePath();
    int idx = defaultName.lastIndexOf(".");
    if (idx != -1)
      defaultName = defaultName.substring(0, idx + 1);
    defaultName += FileFormat.FILE_TSV_EXTENSION;

    JFileChooser jfc = new FileExtensionChooser(FileFormat.FILE_TSV_EXTENSION, false, this.resultFile.getParent());
    jfc.setSelectedFile(new File(defaultName));
    if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      String tsv = jfc.getSelectedFile().getAbsolutePath();
      if (!tsv.endsWith(FileFormat.FILE_TSV_EXTENSION))
        tsv += "." + FileFormat.FILE_TSV_EXTENSION;
      try {
        PrintWriter out = new PrintWriter(new FileWriter(tsv));

        StringBuilder line = new StringBuilder();
        for (int col = 1; col < table.getColumnCount(); col++) {
          line.append('\t');
          line.append((String) table.getTableHeader().getColumnModel().getColumn(col).getHeaderValue());
        }
        out.println(line.substring(1));

        for (int row = 0; row < table.getRowCount(); row++) {
          line = new StringBuilder();
          for (int col = 1; col < table.getColumnCount(); col++) {
            line.append('\t');
            line.append((String) table.getValueAt(row, col));
          }
          out.println(line.substring(1));
        }

        out.close();
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this, MSG.cat(GUI.RP_KO_SAVE, tsv, e), GUI.RP_KO_SAVE_TSV, JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  /**
   * Exports the Results Table to an HTML File, which a PNG File containing the Manhattan plot
   *
   * @param table
   */
  private void exportHTML(JTable table) {
    String defaultName = this.resultFile.getAbsolutePath();
    int idx = defaultName.lastIndexOf(".");
    if (idx != -1)
      defaultName = defaultName.substring(0, idx + 1);
    defaultName += FileFormat.FILE_HTML_EXTENSION;

    JFileChooser jfc = new FileExtensionChooser(FileFormat.FILE_HTML_EXTENSION, false, this.resultFile.getParent());
    jfc.setSelectedFile(new File(defaultName));
    if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      String html = jfc.getSelectedFile().getAbsolutePath();
      if (!html.endsWith(FileFormat.FILE_HTML_EXTENSION))
        html += "." + FileFormat.FILE_HTML_EXTENSION;
      try {
        String png = html + ".png";
        this.exportManhattan(table, png, Manhattan.THEME_DARK);
        Document doc = Jsoup.connect(TEMPLATE_URL).get();
        doc.title("Results from : " + resultFile.getName());
        doc.getElementById("maintitle").appendText("PrivAS Association Test Results from file " + resultFile.getName());

        StringBuilder line = new StringBuilder("<tr>");
        for (int col = 1; col < table.getColumnCount(); col++)
          line.append("<th><div>").append((String) table.getTableHeader().getColumnModel().getColumn(col).getHeaderValue()).append("</div></th>");
        line.append("</tr>\n<tr>");
        for (int col = 1; col < table.getColumnCount(); col++)
          line.append("<td class=\"notvisible\">").append((String) table.getTableHeader().getColumnModel().getColumn(col).getHeaderValue()).append("</td>");
        line.append("</tr>\n");
        doc.getElementById("tableheader").append(line.toString());

        line = new StringBuilder();
        for (int row = 0; row < table.getRowCount(); row++) {
          line.append("\n<tr>");
          for (int col = 1; col < table.getColumnCount(); col++) {
            line.append("<td>");
            String val = (String) table.getValueAt(row, col);
            if (col == COL_PVALUE)
              line.append("<font color=\"").append(getHTMLColor(val)).append("\">").append(val).append("</font>");
            else
              line.append(val);
            line.append("</td>");
          }
          line.append("</tr>");
        }
        doc.getElementById("tablebody").append(line.toString());
        doc.getElementById("manhattan").attr("src", new File(png).getName());

        PrintWriter out = new PrintWriter(new FileWriter(html));
        out.println(doc.outerHtml());
        out.close();
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this, MSG.cat(GUI.RP_KO_SAVE, html, e), GUI.RP_KO_SAVE_HTML, JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private static String getHTMLColor(String val) {
    Color color = getColor(val);
    String r = (color.getRed() < 16) ? "0" + Integer.toHexString(color.getRed()) : Integer.toHexString(color.getRed());
    String g = (color.getGreen() < 16) ? "0" + Integer.toHexString(color.getGreen()) : Integer.toHexString(color.getGreen());
    String b = (color.getBlue() < 16) ? "0" + Integer.toHexString(color.getBlue()) : Integer.toHexString(color.getBlue());
    return "#" + r + g + b;
  }

  /**
   * Class representing a Genomic Position (chromosome + position)
   */
  private static class Position {

    private final int chr;
    private final int pos;

    /**
     * Constructor
     *
     * @param str the position in form chr:pos
     */
    Position(String str) {
      String[] f = str.split(":");
      String c = f[0]
              .replace(GUI.RP_CHR_PREFIX, "")
              .replace(GUI.RP_CHR_X, "23")
              .replace(GUI.RP_CHR_Y, "24")
              .replace(GUI.RP_CHR_MT, "25")
              .replace(GUI.RP_CHR_M, "25");
      String p = f[1];
      int ch = -1;
      try {
        ch = new Integer(c);
      } catch (NumberFormatException e) {
        //Ignore
      }

      this.chr = ch;
      this.pos = new Integer(p);
    }

    /**
     * Gets the Chromosome name
     *
     * @return
     */
    private String getChromosomeName() {
      if (chr > 0 && chr < 23)
        return chr + "";
      if (chr == 23)
        return GUI.RP_CHR_X;
      if (chr == 24)
        return GUI.RP_CHR_Y;
      if (chr == 25)
        return GUI.RP_CHR_MT;
      return GUI.RP_CHR_UNKNOWN;
    }

    @Override
    public String toString() {
      String chromosome = getChromosomeName();
      return chromosome + ':' + pos;
    }

    /**
     * Compare another position to this one
     *
     * @param p the other position
     * @return 0 - if both position are the same, negative if the other position is before this one, positive otherwise
     */
    private int compareTo(Position p) {
      if (this.chr == p.chr)
        return this.pos - p.pos;
      return this.chr - p.chr;
    }
  }

  /**
   * Exception thrown when there is a problem parse lines from a Results File
   */
  public class ParsingException extends Exception {

    ParsingException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
