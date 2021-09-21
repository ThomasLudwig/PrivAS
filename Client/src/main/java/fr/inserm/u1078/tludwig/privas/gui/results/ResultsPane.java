package fr.inserm.u1078.tludwig.privas.gui.results;

import fr.inserm.u1078.tludwig.privas.constants.FileFormat;
import fr.inserm.u1078.tludwig.privas.constants.GUI;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.gui.ClientWindow;
import fr.inserm.u1078.tludwig.privas.gui.FileExtensionChooser;
import fr.inserm.u1078.tludwig.privas.gui.TableColumnAdjuster;
import fr.inserm.u1078.tludwig.privas.utils.CanonicalVariant;
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
import javax.swing.*;
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
public class ResultsPane extends JFrame {

  /**
   * The Client Window owning (calling) this Panel
   */
  private final ClientWindow clientWindow;
  private final JPanel mainPanel;

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
  static final int COL_NUM = 0;
  static final int COL_GENE = 1;
  static final int COL_POSITION = 2;
  static final int COL_PVALUE = 3;
  static final int COL_K0 = 4;
  static final int COL_K = 5;
  static final int COL_RANK = 6;
  static final int COL_TOTAL = 7;
  static final int COL_SHARED = 8;
  static final int COL_TIME = 9;

  private static final int PANEL_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width - 200;
  private static final int PANEL_HEIGHT = (Toolkit.getDefaultToolkit().getScreenSize().height - 170) / 2;

  /**
   * numerical comparator
   */
  private static final Comparator<String> COMPARATOR_NUMERICAL = (o1, o2) -> {
    double d1 = Double.MAX_VALUE;
    double d2 = d1;
    try {
      d1 = new Double(o1);
    } catch (NumberFormatException e) {/*Nothing*/
    }
    try {
      d2 = new Double(o2);
    } catch (NumberFormatException e) {/*Nothing*/
    }
    return Double.compare(d1, d2);
  };

  /**
   * duration comparator
   */
  private static final Comparator<String> COMPARATOR_TIME = (o1, o2) -> {
    double d1 = 0;
    double d2 = 0;
    try {
      d1 = new Double((o1).replace("s", ""));
    } catch (NumberFormatException e) {/*Nothing*/
    }
    try {
      d2 = new Double((o2).replace("s", ""));
    } catch (NumberFormatException e) {/*Nothing*/
    }
    return Double.compare(d1, d2);
  };

  /**
   * position comparator
   */
  private static final Comparator<String> COMPARATOR_POSITION = Comparator.comparing(Position::new);

  //private HashMap<String, Region> ccds;
  //DONE  manhattan plot view (dot size as a factor of gene size ?)
  //DONE  export table (HTML)
  //DONE  export table (TSV)
  //DONE  export Manhattan (PNG)
  //DONE  panel can be launched without re-downloading results
  //DONE  column "#" should not be sortable
  //DONE  gene position CCDS - first variant of client file
  /**
   * The File from which the results are read
   */
  private File resultFile;
  private JTable table;
  public static final String TEMPLATE_URL = "http://lysine.univ-brest.fr/privas/results.template.html";

  /**
   * Constructor
   *
   * @param clientWindow The Client Window owning (calling) this Panel
   */
  public ResultsPane(ClientWindow clientWindow) {
    this.clientWindow = clientWindow;
    this.mainPanel = new JPanel();
    this.init();
  }

  /**
   * Initializes the Panel (layouts)
   */
  private void init() {
    try {
      this.setIconImage(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource(GUI.IMAGE_PATH_LOGO)));
    } catch (Exception e) {
      //Nothing
    }
    mainPanel.setLayout(new BorderLayout());
    mainPanel.add(new JLabel(GUI.RP_NO_RESULTS), BorderLayout.CENTER);
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(mainPanel, BorderLayout.CENTER);
    this.setTitle(GUI.RP_TITLE);
    this.pack();
  }

  /**
   * Shows this panel as a JOptionPane
   */
  public void display() {
    this.pack();
    this.setVisible(true);
  }

  /**
   * Parses a lines from a Results File
   *
   * @param line the line to parse
   * @return a String array that will be visible in the Table of results
   * @throws ResultsPane.ParsingException if the line could not be parsed
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

  public void setTable() throws ParsingException {
    try {
      DefaultTableModel model = new DefaultTableModel();
      table = new JTable(model) {
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

      UniversalReader in = new UniversalReader(resultFile.getAbsolutePath());
      String line;
      while ((line = in.readLine()) != null)
        model.addRow(parseLine(line));
      in.close();
    } catch (IOException e) {
      String msg = MSG.cat(GUI.RP_KO_LOAD, resultFile.getAbsolutePath());
      mainPanel.add(new JLabel(msg), BorderLayout.CENTER);
      this.clientWindow.getClient().logError(msg);
      this.clientWindow.getClient().logError(e);
    }
  }

  /**
   * Updates this Panel according to a Results File
   *
   * @param results the Results File to load
   * @throws ResultsPane.ParsingException if the File could not be parsed
   */
  public void setResults(File results) throws ParsingException {
    this.resultFile = results;
    this.setTable();

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
  }

  /**
   * Builds the Panel and adjusts the Results Table
   *
   * @param table the Table that holds the Results
   */
  private void buildGUI(final JTable table) {
    //resizing
    TableColumnAdjuster.adjustColumns(table);
    //table.setPreferredSize(new Dimension(table.getPreferredSize().width, PANEL_HEIGHT));
    JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setPreferredSize(new Dimension(table.getPreferredSize().width + 50, PANEL_HEIGHT));

    JPanel south = new JPanel();
    south.setLayout(new BoxLayout(south, BoxLayout.LINE_AXIS));
    south.add(getManhattan((int) (0.9 * PANEL_WIDTH), PANEL_HEIGHT, Manhattan.THEME_DARK));

    mainPanel.removeAll();
    mainPanel.setLayout(new BorderLayout());
    mainPanel.add(createMenuBar(), BorderLayout.NORTH);
    mainPanel.add(scrollPane, BorderLayout.CENTER);
    mainPanel.add(south, BorderLayout.SOUTH);
  }

  /**
   * Builds a new MenuBar, the menu of which act on the current table
   *
   * @return the generated JMenuBar
   */
  private JMenuBar createMenuBar() {
    JMenuBar bar = new JMenuBar();
    JMenu export = new JMenu(GUI.RP_MN_EXPORT);
    JMenuItem tsv = new JMenuItem(GUI.RP_MI_TSV);
    tsv.setToolTipText(GUI.RP_TT_TSV);
    tsv.addActionListener(e -> exportTSV());
    JMenuItem html = new JMenuItem(GUI.RP_MI_HTML);
    html.setToolTipText(GUI.RP_TT_HTML);
    html.addActionListener(e -> exportHTML());
    JMenuItem png = new JMenuItem(GUI.RP_MI_PNG);
    png.setToolTipText(GUI.RP_TT_PNG);
    png.addActionListener(e -> exportManhattan());

    export.add(tsv);
    export.add(html);
    export.add(png);
    bar.add(export);
    return bar;
  }

  /**
   * Foreground (text) Color for unknown values
   */
  static final Color FG_NA = Color.GRAY;

  /**
   * Gets the text color according to the p-value
   *
   * @param pvalue a String representing a Double p-value
   * @return the Color for the p-value
   */
  static Color getColor(Object pvalue) {
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
   * @param width  the width of the plot (pixels)
   * @param height the height of the plot (pixels)
   * @param theme  the theme (0 for light or 1 for dark)
   * @return the ManhattanPlot for the table
   */
  private Manhattan getManhattan(int width, int height, int theme) {
    Manhattan manhattan = new Manhattan(width, height, theme);
    for (int row = 0; row < table.getRowCount(); row++) {
      String gene = (String) table.getValueAt(row, COL_GENE);
      String[] region = ((String) table.getValueAt(row, COL_POSITION)).split(":");
      int chr = 0;
      int pos = 0;
      try {
        chr = CanonicalVariant.getChrAsNumber(region[0]);
        pos = new Integer(region[1]);
      } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
        //Ignore
      }
      try {
        double pvalue = Double.parseDouble((String)table.getValueAt(row, COL_PVALUE));
        if (chr > 0)
          manhattan.add(gene, chr, pos, pvalue);
      } catch (NumberFormatException e) {
        //Ignores NA pvalue
      }
    }
    manhattan.dataLoaded();
    return manhattan;
  }

  /**
   * Exports the Manhattan plot of a Table to a PNG file
   */
  private void exportManhattan() {
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
        this.exportManhattan(png, Manhattan.THEME_LIGHT);
      } catch (IOException e) {
        clientWindow.showAndLogError(MSG.cat(GUI.RP_KO_SAVE, png, e), GUI.RP_KO_SAVE_PNG, e);
      }
    }
  }

  /**
   * Exports the Manhattan plot of a Table to a PNG file
   *
   * @param png   the name of the PNG File
   * @param theme the theme (0 for light or 1 for dark)
   * @throws IOException If an I/O error occurs while writing to the PNG file
   */
  private void exportManhattan(String png, int theme) throws IOException {
    Manhattan manhattan = getManhattan(GUI.RP_PNG_WIDTH, GUI.RP_PNG_HEIGHT, theme);
    manhattan.exportAsPNG(png);
  }

  /**
   * Exports the Results in a TSV File
   */
  private void exportTSV() {
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
        clientWindow.showAndLogError(MSG.cat(GUI.RP_KO_SAVE, tsv, e), GUI.RP_KO_SAVE_TSV, e);
      }
    }
  }

  public static final String N = "\n";
  private static final String TR = "tr";
  private static final String TD = "td";
  private static final String TH = "th";
  private static final String DIV = "div";
  private static final String CLASS = "class";
  private static final String SRC = "src";
  private static final String COLOR = "color";
  private static final String FONT = "font";

  public static final String ID_MAIN = "maintitle";
  public static final String ID_HEADER = "tableheader";
  public static final String ID_BODY = "tableheader";
  public static final String ID_MANHATTAN = "manhattan";
  public static final String DOC_TITLE = "Results from : ";
  public static final String MAIN_TITLE = "PrivAS Association Test Results from file ";

  private static String open(String tag){ return "<"+tag+">";}
  private static String open(String tag, String[]... attributes){
    StringBuilder out = new StringBuilder("<" + tag);
    for(String[] attribute : attributes)
      out.append(" ").append(attribute[0]).append("=\"").append(attribute[1]).append("\"");
    out.append(">");
    return out.toString();
  }
  private static String close(String tag){ return "</"+tag+">";}

  private static String openClose(String tag, String content){
    return open(tag) + content + close(tag);
  }

  private static String openClose(String tag, String content, String[]... attributes){
    return open(tag, attributes) + content + close(tag);
  }

  /**
   * Exports the Results Table to an HTML File, which a PNG File containing the Manhattan plot
   */
  private void exportHTML() {
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
        String png = html + "." + FileFormat.FILE_PNG_EXTENSION;
        this.exportManhattan(png, Manhattan.THEME_DARK);
        Document doc = Jsoup.connect(TEMPLATE_URL).get();
        doc.title(DOC_TITLE + resultFile.getName());
        doc.getElementById(ID_MAIN).appendText(MAIN_TITLE + resultFile.getName());

        StringBuilder line = new StringBuilder(open(TR));
        for (int col = 1; col < table.getColumnCount(); col++)
          line.append(openClose(TH, openClose(DIV, (String) table.getTableHeader().getColumnModel().getColumn(col).getHeaderValue())));
        line.append(close(TR)).append(N).append(open(TR));
        for (int col = 1; col < table.getColumnCount(); col++)
          line.append(openClose(TD, (String) table.getTableHeader().getColumnModel().getColumn(col).getHeaderValue(), new String[]{CLASS, "notvisible"}));
        line.append(close(TR)).append(N);
        doc.getElementById(ID_HEADER).append(line.toString());

        line = new StringBuilder();
        for (int row = 0; row < table.getRowCount(); row++) {
          line.append(N).append(open(TR));
          for (int col = 1; col < table.getColumnCount(); col++) {
            String val = (String) table.getValueAt(row, col);
            if (col == COL_PVALUE)
              val = openClose(FONT, val, new String[]{COLOR, getHTMLColor(val)});
            line.append(openClose(TD, val));
          }
          line.append(close(TR));
        }
        doc.getElementById(ID_BODY).append(line.toString());
        doc.getElementById(ID_MANHATTAN).attr(SRC, new File(png).getName());

        PrintWriter out = new PrintWriter(new FileWriter(html));
        out.println(doc.outerHtml());
        out.close();
      } catch (IOException e) {
        clientWindow.showAndLogError(MSG.cat(GUI.RP_KO_SAVE, html, e), GUI.RP_KO_SAVE_HTML, e);
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
   * Exception thrown when there is a problem parse lines from a Results File
   */
  public static class ParsingException extends Exception {
    ParsingException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
