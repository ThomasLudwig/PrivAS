package fr.inserm.u1078.tludwig.privas.instances;

/**
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-12-19
 */
public class MonitoringException extends Exception {

  @SuppressWarnings("unused")
  public MonitoringException() {
  }

  public MonitoringException(String message) {
    super(message);
  }

  @SuppressWarnings("unused")
  public MonitoringException(String message, Throwable cause) {
    super(message, cause);
  }
}
