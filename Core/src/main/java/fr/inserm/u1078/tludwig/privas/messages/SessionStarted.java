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
  public SessionStarted() {
  }

  public SessionStarted(String session) throws EmptyParameterException {
    super(session);
  }
}
