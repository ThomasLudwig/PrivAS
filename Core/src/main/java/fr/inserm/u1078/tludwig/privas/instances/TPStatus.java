package fr.inserm.u1078.tludwig.privas.instances;

import fr.inserm.u1078.tludwig.privas.constants.Constants;

/**
 * Third Party Server Status
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-03-06
 *
 * Javadoc Complete on 2019-08-07
 */
public class TPStatus {

  /**
   * The State of the Status
   */
  private final State state;
  /**
   * Optionnal details associated to the status
   */
  private final String details;

  /**
   * Builds a TPSStatus from a Key and details
   *
   * @param state   the State
   * @param details details associated to the status
   */
  public TPStatus(State state, String details) {
    this.state = state;
    this.details = details;
  }

  /**
   * Parses a String to build a Status (deserialization)
   *
   * @param line serialized TPSStatus
   */
  public TPStatus(String line) {
    if (line != null) {
      String[] f = line.split("\t");
      String k = f[0];
      String d = (f.length > 1) ? f[1] : null;
      State tmpKey = State.UNKNOWN;
      try {
        tmpKey = State.valueOf(k);
      } catch (Exception e) {
        //Ignore
      }
      this.state = tmpKey;
      this.details = d;
    } else {
      this.state = State.UNKNOWN;
      this.details = Constants.DETAILS_UNKNOWN;
    }
  }

  /**
   * Gets the State of the TPSStatus
   *
   * @return
   */
  public State getState() {
    return state;
  }

  /**
   * Gets the optionnal details associated to the TPSStatus
   *
   * @return
   */
  public String getDetails() {
    return details;
  }

  @Override
  public String toString() {
    if (this.details == null)
      return this.state.toString();
    else
      return this.state + "\t" + this.details;
  }

  /**
   * Possible States for a TPSStatus
   */
  public enum State {
    /**
     * Job is Pending
     */
    PENDING,
    /**
     * Job is Started
     */
    STARTED,
    /**
     * Job is Running
     */
    RUNNING, 
    /**
     * Job is Done
     */
    DONE, 
    /**
     * Job has encountered an error
     */
    ERROR, 
    /**
     * Job is in an unknown state
     */
    UNKNOWN
  }
}
