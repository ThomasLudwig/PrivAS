package fr.inserm.u1078.tludwig.privas.messages;

/**
 * Message sent by Client to Start the Session
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-10-02
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class StartSession extends SessionMessage {

  /**
   * Mandatory Empty Constructor, used through Java Reflection
   */
  @SuppressWarnings("unused")
  public StartSession() {
  }

  /**
   * Constructor from the Session parameter's value
   *
   * @param session the session ID
   * @throws Message.EmptyParameterException if session is null
   */
  public StartSession(String session) throws EmptyParameterException {
    super(session);
  }
}
