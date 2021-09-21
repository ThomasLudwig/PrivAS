package fr.inserm.u1078.tludwig.privas.messages;

/**
 * Message sent by RPP to indicate that Session has Started
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-10-02
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class SessionStarted extends SessionMessage {

  /**
   * Mandatory Empty Constructor, used through Java Reflection
   */
  @SuppressWarnings("unused")
  public SessionStarted() {
  }

  /**
   * Constructor from the Session parameter's value
   *
   * @param session the session ID
   * @throws Message.EmptyParameterException if session is null
   */
  public SessionStarted(String session) throws EmptyParameterException {
    super(session);
  }
}
