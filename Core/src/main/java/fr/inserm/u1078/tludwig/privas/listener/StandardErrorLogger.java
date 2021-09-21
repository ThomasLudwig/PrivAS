package fr.inserm.u1078.tludwig.privas.listener;

import fr.inserm.u1078.tludwig.privas.Main;
import fr.inserm.u1078.tludwig.privas.constants.Constants;

import java.util.Date;

/**
 * Sends logging messages to the Standard Error
 * Logging messages are prefixed by an human readable timestamp
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-02-12
 */
public class StandardErrorLogger implements LogListener {

  private static final String DEBUG = "DEBUG";
  private static final String INFO = "INFO ";
  private static final String WARN = "WARN ";
  private static final String ERROR = "ERROR";
  private static final String OK = "OK   ";

  /**
   * Logs a Message
   *
   * @param message the Message to log
   * @param level   the Level of the Message
   */
  private static void log(String message, String level) {
    System.err.println("[" + Constants.DF_TIME.format(new Date()) + " " + level + "] " + message);
  }

  /**
   * Logs an Exception
   *
   * @param e     the Exception to log
   * @param level the Level of the Exception
   */
  private static void log(Throwable e, String level) {
    StringBuilder sb = new StringBuilder(e.getClass().getSimpleName()+" "+e.getMessage());
    for (StackTraceElement elm : e.getStackTrace()) {
      sb.append("\n");
      sb.append(elm.toString());
    }
    log(sb.toString(), level);
  }

  @Override
  public void logDebug(String message) {
    if(Main.DEBUG)
      log(message, DEBUG);
  }

  @Override
  public void logInfo(String message) {
    log(message, INFO);
  }

  @Override
  public void logWarning(String message) {
    log(message, WARN);
  }

  @Override
  public void logError(String message) {
    log(message, ERROR);
  }

  @Override
  public void logWarning(Throwable e) {
    log(e, WARN);
  }

  @Override
  public void logError(Throwable e) {
    log(e, ERROR);
  }

  @Override
  public void logSuccess(String message) {
    log(message, OK);
  }
}
