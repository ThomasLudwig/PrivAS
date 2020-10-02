package fr.inserm.u1078.tludwig.privas.gui;

import fr.inserm.u1078.tludwig.privas.constants.Constants;
import fr.inserm.u1078.tludwig.privas.constants.GUI;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.listener.LogListener;
import java.awt.Color;
import java.util.Date;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * A JPanel used to Log events (listened on the Client)
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-02-11
 *
 * Javadoc Complete 2019-08-09
 */
public class LoggingPanel extends JPanel implements LogListener {

  /**
   * The JTextPane in which the Messages are displayed
   */
  private final JTextPane jtp;
  /**
   * The JScrollPane arounf the JTextPane
   */
  private final JScrollPane sp;
  /**
   * The document allowing the formatting of the Message
   */
  private final StyledDocument doc;

  /**
   * Styles associated to debug messages
   */
  private final SimpleAttributeSet debug;
  /**
   * Styles associated to info messages
   */
  private final SimpleAttributeSet info;
  /**
   * Styles associated to warning messages
   */
  private final SimpleAttributeSet warn;
  /**
   * Styles associated to error messages
   */
  private final SimpleAttributeSet error;
  /**
   * Styles associated to the OK Messages (successful operation)
   */
  private final SimpleAttributeSet ok;
  /**
   * Styles associated to the normal text
   */
  private final SimpleAttributeSet normal;
  /**
   * Styles associated to the brackets
   */
  private final SimpleAttributeSet bracket;
  /**
   * Styles associated to the time stamps
   */
  private final SimpleAttributeSet time;

  /**
   * Map of styles
   */
  private final HashMap<String, SimpleAttributeSet> styles;

  /**
   * Empty Constuctor
   */
  public LoggingPanel() {
    this.jtp = new JTextPane();
    this.doc = this.jtp.getStyledDocument();
    this.sp = new JScrollPane();
    debug = new SimpleAttributeSet();
    info = new SimpleAttributeSet();
    warn = new SimpleAttributeSet();
    error = new SimpleAttributeSet();
    ok = new SimpleAttributeSet();
    styles = new HashMap<>();
    styles.put(GUI.LP_TAG_DEBUG, debug);
    styles.put(GUI.LP_TAG_INFO, info);
    styles.put(GUI.LP_TAG_WARN, warn);
    styles.put(GUI.LP_TAG_ERROR, error);
    styles.put(GUI.LP_TAG_OK, ok);
    normal = new SimpleAttributeSet();
    bracket = new SimpleAttributeSet();
    time = new SimpleAttributeSet();
    init();
  }

  /**
   * Initializes the layout the the rendering styles
   */
  private void init() {
    StyleConstants.setForeground(debug, Color.GRAY);
    StyleConstants.setBold(debug, false);
    StyleConstants.setForeground(info, Color.BLACK);
    StyleConstants.setBold(info, false);
    StyleConstants.setForeground(warn, Color.ORANGE);
    StyleConstants.setBold(warn, true);
    StyleConstants.setForeground(error, Color.RED);
    StyleConstants.setBold(error, true);
    StyleConstants.setForeground(ok, Color.GREEN.darker().darker());
    StyleConstants.setBold(ok, false);
    StyleConstants.setForeground(normal, Color.BLACK);
    StyleConstants.setBold(normal, false);
    StyleConstants.setForeground(bracket, Color.BLACK);
    StyleConstants.setBold(bracket, true);
    StyleConstants.setForeground(time, Color.BLUE);
    StyleConstants.setBold(time, true);

    jtp.setEditable(false);

    jtp.setFont(GUI.DEFAULT_FONT);
    jtp.setPreferredSize(GUI.LP_DIM);
    sp.setViewportView(jtp);
    sp.setPreferredSize(GUI.LP_DIM);
    this.logInfo(MSG.MSG_WELCOME);
    this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
    this.add(sp);
    this.setBorder(BorderFactory.createTitledBorder(GUI.LP_TITLE));
  }

  /**
   * Append a Text at the end of the Panel
   * @param s the Text to append
   * @param level the Level (style) of the Text
   */
  private void append(String s, String level) {
    SimpleAttributeSet style = this.styles.get(level);
    if (style == null)
      style = normal;
    int length = doc.getLength();
    try {
      this.doc.insertString(length, length > 0 ? "\n" : "", normal);
      this.doc.insertString(doc.getLength(), "[", bracket);
      this.doc.insertString(doc.getLength(), Constants.DF_TIME.format(new Date()) + " ", time);
      this.doc.insertString(doc.getLength(), level, style);
      this.doc.insertString(doc.getLength(), "] ", bracket);
      this.doc.insertString(doc.getLength(), s, style);
    } catch (BadLocationException e) {
      System.err.println(s);
    }
    this.jtp.setCaretPosition(this.doc.getLength());
  }

  /**
   * Squeezes the StackTrace of an Exception in the Panel
   * @param e the Exception
   * @return 
   */
  private String squeeze(Throwable e) {
    StringBuilder sb = new StringBuilder(e.getClass().getSimpleName()+" "+e.getMessage());
    for (StackTraceElement elm : e.getStackTrace()) {
      sb.append("\n");
      sb.append(elm.toString());
    }
    return sb.toString();
  }

  @Override
  public void logDebug(String message) {
    //append(message, LEVEL_DEBUG);
  }

  @Override
  public void logInfo(String message) {
    append(message, GUI.LP_TAG_INFO);
  }

  @Override
  public void logWarning(String message) {
    append(message, GUI.LP_TAG_WARN);
  }

  @Override
  public void logError(String message) {
    append(message, GUI.LP_TAG_ERROR);
  }

  @Override
  public void logWarning(Throwable e) {
    append(squeeze(e), GUI.LP_TAG_WARN);
  }

  @Override
  public void logError(Throwable e) {
    append(squeeze(e), GUI.LP_TAG_ERROR);
  }

  @Override
  public void logSuccess(String message) {
    append(message, GUI.LP_TAG_OK);
  }
}
