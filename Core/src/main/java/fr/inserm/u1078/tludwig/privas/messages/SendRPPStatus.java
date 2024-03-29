package fr.inserm.u1078.tludwig.privas.messages;

import fr.inserm.u1078.tludwig.privas.instances.RPPStatus;

/**
 * Message from the RPP to the Client sending its Status
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-05-24
 *
 * Javadoc complete on 2019-08-07
 */
public class SendRPPStatus extends SessionMessage {

  /**
   * Mandatory Empty Constructor, used through Java Reflection
   */
  @SuppressWarnings("unused")
  public SendRPPStatus() {
  }

  /**
   * Constructor from a set of parameters' values
   *
   * @param sessionId the Session ID
   * @param status    the status of the RPP
   * @throws EmptyParameterException if at least one of the parameters is null
   */
  public SendRPPStatus(String sessionId, RPPStatus status) throws EmptyParameterException {
    super(sessionId);
    this.setStatus(status);
  }

  /**
   * Gets the status of the RPP
   *
   * @return the status of the RPP
   */
  public final String getStatus() {
    return this.getValue(Key.STATUS);
  }

  /**
   * The status parameter's value
   *
   * @param status the status of the RPP
   * @throws EmptyParameterException if the value is null
   */
  private void setStatus(RPPStatus status) throws EmptyParameterException {
    this.set(Key.STATUS, status.toString());
  }
}
