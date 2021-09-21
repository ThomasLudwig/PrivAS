package fr.inserm.u1078.tludwig.privas.instances;

import fr.inserm.u1078.tludwig.privas.constants.Constants;

import java.util.Date;

/**
 * Third Party Server Status
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-03-06
 *
 * Javadoc Complete on 2019-08-07
 */
public class TPStatus {

  /**
   * Creation time of the status
   */
  private final long epoch;

  /**
   * The State of the Status
   */
  private final State state;
  /**
   * Optional details associated to the status
   */
  private final String details;

  /**
   * Builds a TPSStatus from a Key and details
   *
   * @param epoch   timestamp (from TPS's point of view)
   * @param state   the State
   * @param details details associated to the status
   */
  public TPStatus(long epoch, State state, String details) {
    this.epoch = epoch;
    this.state = state;
    this.details = details == null ? "" : details.replace("\n", Constants.RET).replace("\t",Constants.TAB);
  }

  public TPStatus(String serialized){
    long e = new Date().getTime();
    State s = State.UNKNOWN;
    String d = "";

    String[] f = serialized.split("\t", -1);
    try{
      e = Long.parseLong(f[0]);
    } catch (Exception ignore){
      //whatever
    }
    try{
      s = State.valueOf(f[1]);
    } catch (Exception ignore){
      //whatever
    }
    try{
      d = f[2];
    } catch (Exception ignore){
      //whatever
    }

    this.epoch = e;
    this.state = s;
    this.details = d;
  }

  /**
   * Gets the State of the TPSStatus
   *
   * @return the State of the TPSStatus
   */
  public State getState() {
    return state;
  }

  /**
   * Gets the optional details associated to the TPSStatus
   *
   * @return the optional details associated to the TPSStatus
   */
  public String getDetails() {
    return details.replace(Constants.TAB,"\t").replace(Constants.RET,"\n");
  }

  public long getEpoch() {
    return epoch;
  }

  @Override
  public String toString() {
    return this.epoch + "\t" + this.state + "\t" + this.details;
  }

  /**
   * Possible States for a TPSStatus
   */
  public enum State {
    /**
     * Job is Pending
     */
    PENDING /*(1, "Job has been submitted.")*/, //QUESTION how to determine PENDING, since the java instance is not started ?
    /**
     * Job is Started
     */
    STARTED/*(2, "Job has started, Data validation/preparation.")*/,
    /**
     * Job is Running
     */
    RUNNING/*(3, "Association Tests are running.")*/,
    /**
     * Job is Done
     */
    DONE/*(4, "Association Tests are complete.")*/,
    /**
     * Job has encountered an error
     */
    ERROR/*(-1, "There was an error on TPS.")*/,
    /**
     * Can't read TPS's status file
     */
    UNREACHABLE/*(-2, "RPP was not able to retrieve TPS's status file")*/,
    /**
     * Job is in an unknown state
     */
    UNKNOWN/*(0, "TPS is in an unknown state.")*/
  }
}
