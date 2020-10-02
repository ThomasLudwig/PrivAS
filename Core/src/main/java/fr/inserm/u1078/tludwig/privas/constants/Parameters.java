package fr.inserm.u1078.tludwig.privas.constants;

/**
 * Parameters that will tweak the application
 * Some parameters should be shared between the Client and the Server *
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-06-07
 *
 * Javadoc complete on 2019-08-07
 */
public class Parameters {

  /**
   * Delay in ms when RPP asks for TPS status
   */
  public static final int RPP_THIRD_PARTY_REFRESH_DELAY = 250;

  /**
   * Delay in ms when RPP checks completion of writting of Client Data (received by another thread)
   */
  public static final int RPP_RECEIVER_REFRESH_DELAY = 1000;
  
  /**
   * Delay in ms between 2 Client-RPP reconnections attempt 
   */
  public static final int CLIENT_RECONNECT_DELAY = 1000;

  /**
   * Delay in ms when RPP checks completion of writting of Results File (through ssh)
   */
  public static final int SP_RESULT_GETTER_REFRESH_DELAY = 1000;

  /**
   * Delay in ms when RPP sends the Client its first RRPStatus (as a Message). Allows for the socket to be registred
   */
  public static final int RPP_MONITOR_FIRST_DELAY = 2000;
  
  /**
   * Delay between two checks for expired sessions
   */
  public static final long RPP_CLEAR_SESSION_DELAY = 1000*60*60;
  
  /**
   * Delay in ms when loading a Genotype File. The Loading has no duration and it might give the illusion to the User that nothing happens
   */
  public static final int LOAD_GENOTYPE_DELAY = 250;
  
  //Default values for the WSS Algorithm
  /**
   * By default each WSS loop contains this number of permutations
   */
  public static final int WSS_DEFAULT_LOOP_SIZE = 100;
  /**
   * By default WSS stops when it reaches this value of k0
   */
  public static final int WSS_DEFAULT_MIN_K0 = 5;
  /**
   * By default WSS stops when it reaches this value of k
   */
  public static final int WSS_DEFAULT_MAX_K = 10000000;
  /**
   * By default WSS stops if the pvalue is above this value
   */
  public static final double WSS_DEFAULT_REJECTION_PVALUE = 0.05;
  /**
   * By default WSS uses this value has a stop condition when comparing the current values of p-value and k0
   */
  public static final double WSS_DEFAULT_MIN_PVALUE_MINUS_LOG = 8;

  //Default RPP Server
  /**
   * The default RPP Server's address
   */
  public static final String RPP_DEFAULT_ADDRESS = "alanine.univ-brest.fr";
  /**
   * The default RPP Server's port
   */
  public static final int RPP_DEFAULT_PORT = 6666;

  //Default algorithm parameters and variant selection criteria
  /**
   * Default Allele Frequency above which variants are rejected
   */
  public static final double CRIT_DEFAULT_MAF = 0.05;
  /**
   * Default vep consequence below which variants are rejected
   */
  public static final int CRIT_DEFAULT_CSQ = 24;
  /**
   * Default number of maximum permutation in the WSS algorithm
   */
  public static final int CRIT_DEFAULT_WSS_PERM = 10000000;
  
  /**
   * Default maximum allele frequency for variants in pooled data
   */
  public static final double CRIT_DEFAULT_WSS_FRQ = 0.1;

  /**
   * Size of the blocks read from/written to a MessageSocket
   */
  public static final double SOCKET_BLOCK_SIZE = 1024;
}
