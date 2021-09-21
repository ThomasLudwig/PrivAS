package fr.inserm.u1078.tludwig.privas.instances;

import fr.inserm.u1078.tludwig.privas.listener.LogListener;
import java.util.ArrayList;

/**
 * An Instance of this Program : Can be a Client, an Reference Panel Provider Server or a Third-Party Server
 * An instance has logging capabilities, by sending logging messages to LogListeners
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-02-12
 *
 * Javadoc Complete on 2019-08-07
 */
public abstract class Instance {

  /**
   * List of LogListeners listening to this Instance
   */
  private final ArrayList<LogListener> logListeners;

  /**
   * Empty Constructor
   */
  public Instance() {
    this.logListeners = new ArrayList<>();
  }

  /**
   * Adds a LogListener to this Instance
   *
   * @param logListener the listener to add
   */
  public void addLogListener(LogListener logListener) {
    if (this.logListeners.contains(logListener))
      return;
    this.logListeners.add(logListener);
  }

  /**
   * Logs a successful operation message
   *
   * @param message the successful operation message
   */
  public void logSuccess(String message) {
    for (LogListener l : this.logListeners)
      l.logSuccess(message);
  }

  /**
   * Logs an informative message
   *
   * @param message the informative message
   */
  public void logInfo(String message) {
    for (LogListener l : this.logListeners)
      l.logInfo(message);
  }

  /**
   * Logs a debugging message
   *
   * @param message the debugging message
   */
  public void logDebug(String message) {
    for (LogListener l : this.logListeners)
      l.logDebug(message);
  }

  /**
   * Logs an error message
   *
   * @param message the error message
   */
  public void logError(String message) {
    for (LogListener l : this.logListeners)
      l.logError(message);
  }

  /**
   * Logs a warning message
   *
   * @param message the warning message
   */
  public void logWarning(String message) {
    for (LogListener l : this.logListeners)
      l.logWarning(message);
  }

  /**
   * Logs an exception as an error
   *
   * @param e the exception
   */
  public void logError(Throwable e) {
    for (LogListener l : this.logListeners)
      l.logError(e);
  }

  /**
   * Logs an exception as a warning
   *
   * @param e the exception
   */
  public void logWarning(Exception e) {
    for (LogListener l : this.logListeners)
      l.logWarning(e);
  }
}
