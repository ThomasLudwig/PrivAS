package fr.inserm.u1078.tludwig.privas.messages;

/**
 * Message from the RPP to the Client, acknowledging it's presence.
 * The message also carries it's configuration (list of available datasets, associated Third Party Server)
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-05-24
 */
public class SendRPPConfiguration extends Message {

  /**
   * Mandatory Empty Constructor, used through Java Reflection
   */
  public SendRPPConfiguration() {
  }

  /**
   * Constructor from parameters value
   *
   * @param datasets the list of available datasets
   * @param tpsName  the name of the Third Party Server
   * @throws Message.EmptyParameterException if at least one of the parameters is null
   */
  public SendRPPConfiguration(String datasets, String tpsName) throws EmptyParameterException {
    super();
    this.setDatasets(datasets);
    this.setTPSName(tpsName);
  }

  /**
   * Sets the datasets parameter's value
   *
   * @param datasets the list of available datasets
   * @throws Message.EmptyParameterException if the value is null
   */
  private void setDatasets(String datasets) throws EmptyParameterException {
    this.set(Key.DATASETS, datasets);
  }

  /**
   * Gets the value for the datasets parameter
   *
   * @return
   */
  public final String getDatasets() {
    return this.getValue(Key.DATASETS);
  }

  /**
   * Sets the tpsName parameter's value
   *
   * @param tpsName the name of the Third Party Server
   * @throws Message.EmptyParameterException if the value is null
   */
  private void setTPSName(String tpsName) throws EmptyParameterException {
    this.set(Key.TPS_NAME, tpsName);
  }

  /**
   * Gets the value for the tpsName parameter
   *
   * @return
   */
  public final String getTPSName() {
    return this.getValue(Key.TPS_NAME);
  }
}
