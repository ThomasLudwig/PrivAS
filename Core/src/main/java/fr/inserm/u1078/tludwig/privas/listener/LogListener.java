package fr.inserm.u1078.tludwig.privas.listener;

/**
 * LogListeners are objects capable of handling logging messages
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-02-12
 */
public interface LogListener {

  /**
   * Logs a debugging message
   *
   * @param message the message to log
   */
  void logDebug(String message);

  /**
   * Logs an informative message
   *
   * @param message the message to log
   */
  void logInfo(String message);

  /**
   * Logs a warning message
   *
   * @param message the message to log
   */
  void logWarning(String message);

  /**
   * Logs an error message
   *
   * @param message the message to log
   */
  void logError(String message);

  /**
   * Logs a successful operation message
   *
   * @param message the message to log
   */
  void logSuccess(String message);

  /**
   * Logs an exception as a warning
   *
   * @param e the exception to log
   */
  void logWarning(Throwable e);

  /**
   * Logs an exception as an error
   *
   * @param e the exception to log
   */
  void logError(Throwable e);
}
