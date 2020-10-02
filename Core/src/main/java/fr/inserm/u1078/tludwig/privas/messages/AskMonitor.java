package fr.inserm.u1078.tludwig.privas.messages;

/**
 * Message from the Client to the RPP Asking to monitor the RPP status
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-05-24
 *
 * Javadoc complete on 2019-08-07
 */
public class AskMonitor extends SessionMessage {

  /**
   * Mandatory Empty Constructor, used through Java Reflection
   */
  public AskMonitor() {
  }

  /**
   * Constructor from the Session parameter's value
   *
   * @param session the session ID
   * @throws EmptyParameterException if session is null
   */
  public AskMonitor(String session) throws EmptyParameterException {
    super(session);
  }
}
