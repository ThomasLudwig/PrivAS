package fr.inserm.u1078.tludwig.privas.gui.results;

import fr.inserm.u1078.tludwig.privas.constants.GUI;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;

/**
 * A Panel containing a Manhattan Plot
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-05-15
 *
 * Javadoc Complete on 2019-08-09
 */
public class Manhattan extends JPanel {

  /**
   * Light Theme KEY
   */
  public static final int THEME_LIGHT = 0;
  /**
   * Dark Theme KEY
   */
  public static final int THEME_DARK = 1;
  
  public static final String REF_GRCH37 = "GRCH37";
  public static final String REF_GRCH38 = "GRCH38";

  private static final Color[] BACKGROUND = {Color.WHITE, Color.BLACK};
  private static final Color[] FOREGROUND = {Color.BLACK, Color.WHITE};

  public static final long[] CHROM_LENGTH_38 = {
    248956422, //1
    242193529, //2
    198295559, //3
    190214555, //4
    181538259, //5
    170805979, //6
    159345973, //7
    145138636, //8
    138394717, //9
    133797422, //10
    135086622, //11
    133275309, //12
    114364328, //13
    107043718, //14
    101991189, //15
    90338345, //16
    83257441, //17
    80373285, //18
    58617616, //19
    64444167, //20
    46709983, //21
    50818468, //22
    156040895, //X
    57227415, //Y
    16569 //MT
  };

  public static final long[] CHROM_LENGTH_37 = {
    249250621, //1
    243199373, //2
    198022430, //3
    191154276, //4
    180915260, //5
    171115067, //6
    159138663, //7
    146364022, //8
    141213431, //9
    135534747, //10
    135006516, //11
    133851895, //12
    115169878, //13
    107349540, //14
    102531392, //15
    90354753, //16
    81195210, //17
    78077248, //18
    59128983, //19
    63025520, //20
    48129895, //21
    51304566, //22
    155270560, //X
    59373566, //Y
    16569 //MT
  };

  public static final long[] CHROM_LENGTH = max(CHROM_LENGTH_37, CHROM_LENGTH_38);

  private static final Color[][] CHROM_COLORS = {chromColors(CHROM_LENGTH_37.length, THEME_LIGHT), chromColors(CHROM_LENGTH_37.length, THEME_DARK)};
  
  private final ArrayList<Gene> genes;
  private final int targetWidth;
  private final int targetHeight;
  private JFreeChart chart;
  private final int theme;

  private final long[] nonMissingOffset;

  /**
   * Constructor
   *
   * @param width  the width of the plot (pixels)
   * @param height the height of the plot (pixels)
   * @param theme  the theme (@See fr.inserm.u1078.tludwig.privas.gui.Manhattan.THEME_LIGHT for light or @See fr.inserm.u1078.tludwig.privas.gui.Manhattan.THEME_DARK for dark)
   */
  public Manhattan(int width, int height, int theme) {
    super();
    this.genes = new ArrayList<>();
    this.targetWidth = width;
    this.targetHeight = height;
    this.theme = theme;
    this.nonMissingOffset = new long[25];
  }

  /**
   * Exports the Plot as a PNG File
   *
   * @param filename the name of the PNG File
   * @throws IOException
   */
  public void exportAsPNG(String filename) throws IOException {
    OutputStream out = new FileOutputStream(filename);
    ChartUtilities.writeChartAsPNG(out, chart, this.targetWidth, this.targetHeight);
  }

  /**
   * Generates chromosome colors for a theme
   *
   * @param l     number of different colors
   * @param theme the theme for which the color are generated
   * @return
   */
  private static Color[] chromColors(int l, int theme) {
    Color[] selected = new Color[]{Color.decode("#e41a1c"), Color.decode("#377eb8"), Color.decode("#4daf4a"), Color.decode("#984ea3"), Color.decode("#ff7f00"), Color.decode("#e4e400"), Color.decode("#a65628"), Color.decode("#f781bf")};
    Color[] colors = new Color[l];
    for (int i = 0; i < l; i++) {
      colors[i] = selected[i % selected.length];
      if (theme == THEME_DARK)
        colors[i] = colors[i].brighter();
    }
    return colors;
  }
  
  /**
   * Gets the maximum values of two arrays of same length
   * @param a first array
   * @param b second array of the same size
   * @return an array of the same size where every values is the max of the values at the same indices in the given arrays
   */
  private static long[] max(long[] a, long[] b){
    long[] max = new long[a.length];
    for(int i = 0 ; i < a.length; i++)
      max[i] = Math.max(a[i], b[i]);
    return max;
  }

  /**
   * Adds a new value (Gene) to the Dataset
   *
   * @param geneName the name of the Gene
   * @param chrom    its chromosome
   * @param position its genomic position
   * @param pvalue   its associated p-value
   */
  public void add(String geneName, int chrom, int position, double pvalue) {
    genes.add(new Gene(chrom, position, pvalue));
  }

  /**
   * Once the data are loaded, sorts the data, clears the plot, sets a new plot with the data
   */
  public void dataLoaded() {
    this.genes.sort((g1, g2) -> {
      double c1 = g1.x;
      double c2 = g2.x;
      if (c1 == c2) {
        c1 = g1.y;
        c2 = g2.y;
      }
      if (c1 == c2)
        return 0;
      return c1 > c2 ? 1 : -1;
    });
    this.removeAll();
    this.add(this.getChartPanel());
    this.validate();
  }

  /**
   * Gets the name of a Chromosome from its number (1 to 25)
   *
   * @param i
   * @return
   */
  private static String getChromName(int i) {
    return ("chr" + (i + 1)).replace("23", "X").replace("24", "Y").replace("25", "MT");
  }

  /**
   * Computes the Offset (x-coordinates) for non missing chromosomes<p>
   * Use all available width, leave no gaps between chromosomes
   */
  private void computeNonMissingOffset() {
    boolean[] found = new boolean[25]; //initialized to false

    for (Gene gene : genes)
      found[gene.chrom - 1] = true;

    for (int i = 1; i < this.nonMissingOffset.length; i++) {
      this.nonMissingOffset[i] = this.nonMissingOffset[i - 1];
      if (found[i - 1])
        this.nonMissingOffset[i] += CHROM_LENGTH[i - 1];
    }
  }

  /**
   * Size of the Dots in the Plot
   */
  private final static double DOT = 1.5;

  /**
   * Ges the actual Chart
   *
   * @return
   */
  private ChartPanel getChartPanel() {

    computeNonMissingOffset();

    final XYSeriesCollection dataset = new XYSeriesCollection();
    final XYSeries[] series = new XYSeries[25];
    for (int i = 0; i < 25; i++)
      series[i] = new XYSeries(getChromName(i));
    double maxY = 0;
    int minChr = 25;
    int maxChr = -1;
    for (Gene gene : this.genes) {
      if (maxY < gene.y)
        maxY = gene.y;
      double x = gene.x + nonMissingOffset[(gene.chrom - 1)];
      series[gene.chrom - 1].add(x, gene.y);
      if(gene.chrom < minChr)
        minChr = gene.chrom;
      if(maxChr < gene.chrom)
        maxChr = gene.chrom;
    }
    
    double minX = nonMissingOffset[minChr-1];
    double maxX = nonMissingOffset[maxChr-1]+CHROM_LENGTH[maxChr-1];
    
    maxY *= 1.05;
    double labelY = -.05 * maxY;

    this.chart = ChartFactory.createScatterPlot(
            GUI.MNT_TITLE,
            GUI.MNT_DOMAIN,
            GUI.MNT_RANGE,
            dataset,
            PlotOrientation.VERTICAL,
            true, false, false);

    chart.removeLegend();
    XYPlot plot = chart.getXYPlot();
    plot.setDomainGridlinesVisible(false);
    plot.setRangeGridlinesVisible(false);
    plot.getDomainAxis().setVisible(false);

    XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

    
    
    //compute offset to center text
    double nbBP = maxX - minX; // min and max X coordinate
    double nbPX = 900 - 100; //min and max X projection
    //double nbPX = 1177 - 89; //min and max X projection
    double bpPerPX = nbBP / nbPX; //X ratio
    float currentFontSize = new XYTextAnnotation("x", 0, 0).getFont().getSize();
    float fontSize = currentFontSize / 404 * targetHeight;
    double offset = bpPerPX * 2.7 * fontSize / currentFontSize;

    int last = -1;

    for (int chr = 0; chr < 25; chr++) {
      XYSeries serie = series[chr];
      String name = (String) serie.getKey();
      if (serie.getItemCount() > 0) {
        last++;
        dataset.addSeries(serie);
        renderer.setSeriesShape(last, new Ellipse2D.Double(-DOT, -DOT, 2 * DOT, 2 * DOT));
        renderer.setSeriesPaint(last, CHROM_COLORS[theme][last]);
        renderer.setSeriesShapesFilled(last, Boolean.FALSE); //DONE use non filled points

        //DONE  to center the points, convert boundaries from chart.coord to 2D.coord, adjust, convert back, create translated annotation
        final XYTextAnnotation mark = new XYTextAnnotation(name, nonMissingOffset[chr] + .5 * CHROM_LENGTH[chr] + offset, labelY);
        mark.setFont(mark.getFont().deriveFont(fontSize));
        mark.setPaint(CHROM_COLORS[theme][last]);
        mark.setRotationAnchor(TextAnchor.CENTER);
        mark.setRotationAngle(-Math.PI * .5);
        mark.setTextAnchor(TextAnchor.CENTER);
        plot.addAnnotation(mark);
      }
    }

    //needs to be at the end
    applyTheme(chart, minX, maxX, maxY);
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setMaximumDrawWidth(this.targetWidth); //draw size
    chartPanel.setMaximumDrawHeight(this.targetHeight); //draw size
    chartPanel.setPreferredSize(new java.awt.Dimension(this.targetWidth, this.targetHeight)); //display size
    return chartPanel;
  }

  /**
   * Customizes the Chart and applies the theme
   *
   * @param chart the Chart
   * @param minX  minimum X value (leftmost gene)
   * @param maxX  maximum X value (rightmost gene)
   * @param max   maximum value (-log10(pvalue)
   */
  private void applyTheme(JFreeChart chart, double minX, double maxX, double max) {
    XYPlot plot = chart.getXYPlot();

    plot.getRangeAxis().setRange(-0.1 * max, max);

    double len = maxX - minX;
    minX -= 0.01 * len;
    maxX += 0.01 * len;
    plot.getDomainAxis().setRange(minX, maxX);

    TickUnits units = new TickUnits();
    for (int i = 0; i <= max + 1; i++)
      units.add(new CustomTickUnit(i));

    plot.getRangeAxis().setStandardTickUnits(units);

    Color b = BACKGROUND[this.theme];
    chart.setBackgroundPaint(b);
    plot.setBackgroundPaint(b);

    Color f = FOREGROUND[theme];
    chart.setBorderPaint(f);
    chart.getTitle().setPaint(f);
    plot.setOutlinePaint(f);
    plot.getRangeAxis().setTickLabelPaint(f);
    plot.getDomainAxis().setTickLabelPaint(f);
    plot.getRangeAxis().setLabelPaint(f);
    plot.getDomainAxis().setLabelPaint(f);
  }

  /**
   * Class to customize TickUnit spaces and labels
   */
  private static class CustomTickUnit extends NumberTickUnit {
    CustomTickUnit(double size) {
      super(size);
    }

    @Override
    public String valueToString(double value) {
      if (value < 0)
        return "";
      return Math.pow(10, -value) + "";
    }
  }

  /**
   * Class to represent the data in the dataset<p>
   * <ul>
   * a chromosome
   * a position on the chromosome
   * a p-value
   * </ul>
   */
  private static class Gene {
    private final int chrom;
    private final double x;
    private final double y;

    Gene(int chrom, int position, double pvalue) {
      this.chrom = chrom;
      this.x = position;
      this.y = -Math.log10(pvalue);
    }
  }
}
