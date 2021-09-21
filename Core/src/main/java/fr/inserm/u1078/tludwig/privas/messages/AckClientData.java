package fr.inserm.u1078.tludwig.privas.messages;

/**
 * Message from the RPP to the Client Acknowledging Client Data successful reception
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-05-24
 *
 * Javadoc complete on 2019-08-07
 */
public class AckClientData extends SessionMessage {

  /**
   * Mandatory Empty Constructor, used through Java Reflection
   */
  @SuppressWarnings("unused")
  public AckClientData() {
  }

  /**
   * Constructor from the Session parameter's value
   *
   * @param session the session ID
   * @throws EmptyParameterException if session is null
   */
  public AckClientData(String session) throws EmptyParameterException {
    super(session);
  }
}
