package fr.inserm.u1078.tludwig.privas.messages;

/**
 * Message from the Client to the RPP Sending the Client's AES encrypted data, as well as the encrypted RSA encrypted AES Key and the selected algorithm
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-05-24
 *
 * Javadoc complete on 2019-08-07
 */
public class SendClientData extends SessionMessage {

  /**
   * Mandatory Empty Constructor, used through Java Reflection
   */
  @SuppressWarnings("unused")
  public SendClientData() {
  }

  /**
   * Constructor from parameters' values
   *
   * @param session                         the Session ID
   * @param encryptedAESKey                 the AES Key, encrypted with the TPS Public RSA Key
   * @param encryptedClientData             the Client Data, encrypted with the AES Key
   * @param encryptedClientExcludedVariants the variants excluded by the client's QC, encrypted with the AES Key
   * @param algorithm                       the selected algorithm, and its parameters
   * @throws EmptyParameterException if at least one of the parameters is empty
   */
  public SendClientData(String session, String encryptedAESKey, String encryptedClientData, String encryptedClientExcludedVariants, String algorithm) throws EmptyParameterException {
    super(session);
    this.setEncryptedAESKey(encryptedAESKey);
    this.setEncryptedClientData(encryptedClientData);
    this.setEncryptedClientExcludedVariants(encryptedClientExcludedVariants);
    this.setAlgorithm(algorithm);
  }

  /**
   * Gets the AES Key, encrypted with the TPS Public RSA Key
   *
   * @return the AES Key, encrypted with the TPS Public RSA Key
   */
  public final String getEncryptedAESKey() {
    return this.getValue(Key.ENCRYPTED_AES);
  }

  /**
   * Sets the encryptedAESKey parameter's value
   *
   * @param encryptedAESKey the AES Key, encrypted with the TPS Public RSA Key
   * @throws EmptyParameterException if the value is null
   */
  private void setEncryptedAESKey(String encryptedAESKey) throws EmptyParameterException {
    this.set(Key.ENCRYPTED_AES, encryptedAESKey);
  }

  /**
   * Gets the Client Data, encrypted with the AES Key
   *
   * @return the Client Data, encrypted with the AES Key
   */
  public final String getEncryptedClientData() {
    return this.getValue(Key.ENCRYPTED_CLIENT_DATA);
  }

  /**
   * Sets the encryptedClientData parameter's value
   *
   * @param encryptedClientData the Client Data, encrypted with the AES Key
   * @throws EmptyParameterException if the value is null
   */
  private void setEncryptedClientData(String encryptedClientData) throws EmptyParameterException {
    this.set(Key.ENCRYPTED_CLIENT_DATA, encryptedClientData);
  }

  /**
   * Gets the selected algorithm, and its parameters
   *
   * @return the selected algorithm, and its parameters
   */
  public final String getAlgorithm() {
    return this.getValue(Key.ALGORITHM);
  }

  /**
   * Sets the algorithm parameter's value
   *
   * @param algorithm the selected algorithm, and its parameters
   * @throws EmptyParameterException if the value is null
   */
  private void setAlgorithm(String algorithm) throws EmptyParameterException {
    this.set(Key.ALGORITHM, algorithm);
  }

  /**
   * Sets the encryptedClientExcludedVariants parameter
   * @param encryptedClientExcludedVariants the encrypted Client Excluded Variants parameter
   * @throws EmptyParameterException if the value is null
   */
  private void setEncryptedClientExcludedVariants(String encryptedClientExcludedVariants) throws EmptyParameterException {
    this.set(Key.ENCRYPTED_CLIENT_EXCLUDED_VARIANT, encryptedClientExcludedVariants);
  }
  
  /**
   * Gets the encryptedClientExcludedVariants parameter
   * @return the encryptedClientExcludedVariants parameter
   */
  public final String getEncryptedClientExcludedVariants() {
    return this.getValue(Key.ENCRYPTED_CLIENT_EXCLUDED_VARIANT);
  }
}
