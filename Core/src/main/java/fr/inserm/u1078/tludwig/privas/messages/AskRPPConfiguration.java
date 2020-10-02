package fr.inserm.u1078.tludwig.privas.messages;

/**
 * Message from the Client to the RPP to check if a connection can be established
 * The message is also used to ask for the RPP configuration (list of available datasets, associated Third Party Server)
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-05-24
 *
 * Javadoc complete on 2019-08-07
 */
public class AskRPPConfiguration extends Message {

  /**
   * Mandatory Empty Constructor, used through Java Reflection
   */
  public AskRPPConfiguration() {
  }
}
