package fr.inserm.u1078.tludwig.privas.messages;

/**
 * Error Message from the RPP to the Client
 * This Message is sent instead of the Expected Message when the RPP failed to produce the Expected Message
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-05-24
 *
 * Javadoc complete on 2019-08-07
 */
public class SendError extends Message {

  /**
   * Mandatory Empty Constructor, used through Java Reflection
   */
  public SendError() {
  }

  /**
   * Constructor from the error parameter's value
   *
   * @param error
   */
  public SendError(String error) {
    super();
    this.setErrorMessage(error);
  }

  /**
   * Sets the value of the error parameter
   *
   * @param error the error message from the RPP to the Client
   */
  private void setErrorMessage(String error) {
    try {
      if (error == null)
        this.set(Key.ERROR_MESSAGE, "");
      else
        this.set(Key.ERROR_MESSAGE, error);
    } catch (EmptyParameterException ex) {
      //Impossible
    }
  }

  /**
   * Gets the Error Message from the RPP to the Client
   *
   * @return
   */
  public final String getErrorMessage() {
    return this.getValue(Key.ERROR_MESSAGE);
  }
}
