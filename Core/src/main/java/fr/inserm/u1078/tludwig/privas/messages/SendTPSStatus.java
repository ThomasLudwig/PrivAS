package fr.inserm.u1078.tludwig.privas.messages;

import fr.inserm.u1078.tludwig.privas.instances.TPStatus;

/**
 * Message used by TPS to send its status
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-10-06
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class SendTPSStatus extends SessionMessage {
  /**
   * Mandatory Empty Constructor, used through Java Reflection
   */
  public SendTPSStatus() {
  }

  /**
   * Constructor from a set of parameters' values
   *
   * @param sessionId the Session ID
   * @param status    the status of the RPP
   * @throws EmptyParameterException if at least one of the parameters is null
   */
  public SendTPSStatus(String sessionId, TPStatus status) throws EmptyParameterException {
    super(sessionId);
    this.setStatus(status);
  }

  /**
   * Gets the status of the RPP
   *
   * @return
   */
  public TPStatus getStatus() {
    String val = this.getValue(Key.STATUS);
    return new TPStatus(val);
  }

  /**
   * The status parameter's value
   *
   * @param status the status of the RPP
   * @throws EmptyParameterException if the value is null
   */
  private void setStatus(TPStatus status) throws EmptyParameterException {
    this.set(Key.STATUS, status.toString());
  }
}
