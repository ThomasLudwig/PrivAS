package fr.inserm.u1078.tludwig.privas.messages;

/**
 * XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
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
