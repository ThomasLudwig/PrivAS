package fr.inserm.u1078.tludwig.privas.instances;

import fr.inserm.u1078.tludwig.privas.constants.Constants;
import fr.inserm.u1078.tludwig.privas.constants.MSG;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Status of an RPP Instance
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-02-14
 *
 * Javadoc Complete on 2019-08-07
 */
public class RPPStatus {

  /**
   * The State of the Status
   */
  private final State state;
  /**
   * Optionnal details associated to the status
   */
  private final String details; //TODO force TPS message, the whole log is a single string
  /**
   * Date of creation of the Status
   */
  private final Date date;

  /**
   * Full Constructor
   *
   * @param state   State of the Status
   * @param details Details of the Status
   * @param date    creation Details of the Status
   */
  private RPPStatus(State state, String details, Date date) {
    this.state = state;
    this.details = details;
    this.date = date;
  }

  /**
   * Creates an RPPStatus from a State and Details
   *
   * @param state   State of the Status
   * @param details Details of the Status
   */
  private RPPStatus(State state, String details) {
    this(state, details, new Date());
  }

  @Override
  public String toString() {
    String s = Constants.DF_RPP.format(this.date) + this.state;
    if (this.details != null)
      s += "\t" + details;
    return s;
  }

  /**
   * Creates a new RPPStatus :
   * RPP is Extracting its data
   *
   * @param previous Previous RPPStatus
   * @param percent  Completion of the Extraction in percent
   * @return
   */
  public static RPPStatus extracting(RPPStatus previous, int percent) {
    if (previous.state.equals(State.WAITING_RPP))
      return new RPPStatus(State.WAITING_RPP, MSG.ETA(percent));
    return new RPPStatus(State.WAITING_BOTH, MSG.ETA(percent));
  }

  /**
   * Creates a new RPPStatus :
   * RPP Data have been fully Extracted
   *
   * @param previous Previous RPPStatus
   * @return
   */
  public static RPPStatus rppDataExtracted(RPPStatus previous) {
    if (previous.state.equals(State.NEW_SESSION) || previous.state.equals(State.WAITING_BOTH))
      return new RPPStatus(State.WAITING_CLIENT, null);
    return new RPPStatus(State.TPS_SENDING, null);
  }

  public static RPPStatus rppDataEmpty() {
    return new RPPStatus(State.RPP_EMPTY_DATA, null);
  }

  /**
   * Creates a new RPPStatus :
   * RPP has received Client Data
   *
   * @param previous Previous RPPStatus
   * @return
   */
  public static RPPStatus clientDataReceived(RPPStatus previous) {
    if(previous.state.equals(State.RPP_EMPTY_DATA))
      return new RPPStatus(State.RPP_EMPTY_DATA, null);
    if (previous.state.equals(State.NEW_SESSION) || previous.state.equals(State.WAITING_BOTH))
      return new RPPStatus(State.WAITING_RPP, previous.details);
    return new RPPStatus(State.TPS_SENDING, null);
  }

  /**
   * Creates a new RPPStatus :
   * A new Session has been created by RPP
   *
   * @return
   */
  public static RPPStatus newSession() {
    return new RPPStatus(State.NEW_SESSION, null);
  }

  /**
   * Creates a new RPPStatus :
   * RPP is in an unknown State
   *
   * @return
   */
  public static RPPStatus unknown() {
    return new RPPStatus(State.UNKNOWN, null);
  }

  /**
   * Creates a new RPPStatus :
   * The launched Job is pending on the Third Party Server
   *
   * @param before Number of jobs in the queue before this one
   * @return
   */
  public static RPPStatus pending(int before) {
    return new RPPStatus(State.TPS_PENDING, MSG.QUEUED(before));
  }

  /**
   * Creates a new RPPStatus :
   * The launched Job is running on the Third Party Server
   *
   * @param details Additionnal information from the Third Party Server
   * @return
   */
  public static RPPStatus running(String details) {
    return new RPPStatus(State.TPS_RUNNING, details);
  }

  /**
   * Creates a new RPPStatus :
   * The Results are available on the Third Party Server and are being retrieved by the RPP
   *
   * @param details
   * @return
   */
  public static RPPStatus retrieving(String details) {
    return new RPPStatus(State.TPS_DONE, details);
  }

  /**
   * Creates a new RPPStatus :
   * The Results are available on the RPP for the Client
   *
   * @return
   */
  public static RPPStatus available() {
    return new RPPStatus(State.RESULTS_AVAILABLE, null);
  }

  /**
   * Creates a new RPPStatus :
   * The Session has expired, all associated files were deleted from the RPP
   *
   * @return
   */
  public static RPPStatus expired() {
    return new RPPStatus(State.EXPIRED, null);
  }
  
  /**
   * Creates a new RPPStatus :
   * There was an error on the RPP
   * @param details
   * @return 
   */
  public static RPPStatus error(String details){
    return new RPPStatus(State.ERROR, details);
  }
  
  /**
   * Creates a new RPPStatus :
   * There was an error coming from the TPS
   * @param details
   * @return 
   */
  public static RPPStatus tpsError(String details){
    return new RPPStatus(State.TPS_ERROR, details);
  }

  /**
   * Creates a new RPPStatus :
   * TPS is in an unknown state
   * @return
   */
  public static RPPStatus tpsUnknown(){
    return new RPPStatus(State.TPS_UNKNOWN, null);
  }

  /**
   * Creates a new RPPStatus :
   * TPS is in an unknown state
   * @return
   */
  public static RPPStatus tpsUnreachable(String details){
    return new RPPStatus(State.TPS_UNREACHABLE, details);
  }

  /**
   * Serializes the RPPStatus to a file
   *
   * @param filename the name of the file where the RPPStatus will be serialized to
   * @throws IOException
   */
  synchronized public void serialize(String filename) throws IOException {
    String ser = 
            Constants.DF_DAY_DOT_TIME.format(date) + "\n"
            + this.state + "\n";
    if (this.details != null)
      ser += this.details+"\n";
    PrintWriter out = new PrintWriter(new FileWriter(filename));
    out.println(ser);
    out.close();
  }

  /**
   * Deserializes an RPPStatus from a file
   *
   * @param filename the name of the file where the RPPStatus will be deserialized from
   * @return the deserialized RPPStatus
   * @throws Exception
   */
  public static RPPStatus deserialize(String filename) throws Exception {
    BufferedReader in = new BufferedReader(new FileReader(filename));
    String time = in.readLine();
    State state = State.valueOf(in.readLine());
    Date date = new Date();
    try {
      date = Constants.DF_DAY_DOT_TIME.parse(time);
    } catch (ParseException e) {
      System.err.println(MSG.cat(MSG.STS_KO_DESERIAL, time));
      //Nothing
    }
    String extra = null;
    try {
      extra = in.readLine();
    } catch (IOException e) {
      //Nothing
    }
    in.close();
    return new RPPStatus(state, extra, date);
  }
  
  public State getState(){
    return this.state;
  }

  /**
   * Possible State values of the RPPStatus
   */
  public enum State {

    /**
     * Session is in an unknown state
     */
    UNKNOWN,
    /**
     * There are no sessions with this ID
     */
    NO_SESSION,
    /**
     * Session has just be created
     */
    NEW_SESSION,
    /**
     * RPP is waiting on Client AND RPP data extraction
     */
    WAITING_BOTH,
    /**
     * RPP is waiting on Client data extraction
     */
    WAITING_CLIENT,
    /**
     * RPP is waiting on RPP data extraction
     */
    WAITING_RPP,
    /**
     * RPP Extraction yielded an empty File
     */
    RPP_EMPTY_DATA,
    /**
     * RPP is sending data to TPS
     */
    TPS_SENDING,
    /**
     * Job on TPS is pending
     */
    TPS_PENDING,
    /**
     * Job on TPS is running
     */
    TPS_RUNNING,
    /**
     * Job on TPS is Done
     */
    TPS_DONE,
    /**
     * There was an error on the TPS for this session
     */
    TPS_ERROR,
    /**
     * TPS is in an unknown state
     */
    TPS_UNKNOWN,
    /**
     * RPP cannot reach TPS
     */
    TPS_UNREACHABLE,
    /**
     * Results are available on RPP
     */
    RESULTS_AVAILABLE,
    /**
     * There was an error somewhere for this session
     */
    ERROR,
    /**
     * Session has expired
     */
    EXPIRED
  }
}
