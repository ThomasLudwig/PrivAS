package fr.inserm.u1078.tludwig.privas.messages;

/**
 * Message that has the Session as a Parameter
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-05-24
 */
public abstract class SessionMessage extends Message {

  /**
   * Mandatory Empty Constructor, used through Java Reflection
   */
  SessionMessage() {
    super();
  }

  /**
   * Constructor from the Session parameter's value
   *
   * @param session the session ID
   * @throws Message.EmptyParameterException if session is null
   */
  SessionMessage(String session) throws EmptyParameterException {
    this();
    this.setSession(session);
  }

  /**
   * Sets the Session parameter's value
   *
   * @param session the session ID
   * @throws Message.EmptyParameterException if session is null
   */
  private void setSession(String session) throws EmptyParameterException {
    this.set(Key.SESSION, session);
  }

  /**
   * Gets the Session ID
   *
   * @return the Session ID
   */
  public final String getSession() {
    return this.getValue(Key.SESSION);
  }
}
