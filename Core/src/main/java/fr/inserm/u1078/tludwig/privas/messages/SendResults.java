package fr.inserm.u1078.tludwig.privas.messages;

/**
 * Message from the RPP to the Client sending the Encrypted Results of the computation
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-05-24
 *
 * Javadoc complete on 2019-08-07
 */
public class SendResults extends SessionMessage {

  /**
   * Mandatory Empty Constructor, used through Java Reflection
   */
  @SuppressWarnings("unused")
  public SendResults() {
  }

  /**
   * Constructor from a list of parameters' values
   *
   * @param session          the Session ID
   * @param encryptedResults the Computation Results, encrypted with the AES Key
   * @throws EmptyParameterException if at least one of the parameters is null
   */
  public SendResults(String session, String encryptedResults) throws EmptyParameterException {
    super(session);
    this.setEncryptedResults(encryptedResults);
  }

  /**
   * Gets the Computation Results, encrypted with the AES Key
   *
   * @return the Computation Results, encrypted with the AES Key
   */
  public final String getEncryptedResults() {
    return this.getValue(Key.ENCRYPTED_RESULTS);
  }

  /**
   * Sets the encryptedResults parameter's value
   *
   * @param encryptedResults the Computation Results, encrypted with the AES Key
   * @throws EmptyParameterException if the value is null
   */
  private void setEncryptedResults(String encryptedResults) throws EmptyParameterException {
    this.set(Key.ENCRYPTED_RESULTS, encryptedResults);
  }
}
