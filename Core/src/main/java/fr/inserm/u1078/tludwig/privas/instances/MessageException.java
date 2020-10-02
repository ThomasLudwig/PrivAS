package fr.inserm.u1078.tludwig.privas.instances;

/**
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-12-19
 */
public class MessageException extends Exception {

  public MessageException(String message) {
    super(message);
  }

  public MessageException(String message, Throwable cause) {
    super(message, cause);
  }
}
