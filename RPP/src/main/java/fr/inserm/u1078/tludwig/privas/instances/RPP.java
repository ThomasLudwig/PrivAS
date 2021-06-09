package fr.inserm.u1078.tludwig.privas.instances;

import fr.inserm.u1078.tludwig.privas.constants.*;
import fr.inserm.u1078.tludwig.privas.listener.StandardErrorLogger;
import fr.inserm.u1078.tludwig.privas.messages.*;
import fr.inserm.u1078.tludwig.privas.utils.*;
import fr.inserm.u1078.tludwig.privas.utils.qc.QCException;
import fr.inserm.u1078.tludwig.privas.utils.qc.QCParam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * The RPP is the instance of the program that accesses Reference Panel Datasets, Handles request from the Client and acts as a bridge between the Client and
 * the Third Party Server
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-01-30
 *
 * Javadoc complete on 2019-08-08
 */
public class RPP extends Instance implements Runnable {

  //DONE?, when there is a problem with RPP, Client isn't always aware : (ex : from wss filename in the config file leads to FileNotFound)
  private final ArrayList<String> sessions;
  private final RPPThirdPartyConnector thirdPartyConnector;
  private final ArrayList<String> orderedDatasets;
  private final HashMap<String, RPPDataset> rppDatasets;
  private final ArrayList<String> whitelist;
  private final ArrayList<String> blacklist;
  private final String connectionLog;
  private final int maxPerDay;//default 5
  private final int maxPerWeek;//default 10
  private final int maxPerMonth;//default 30

  private final HashMap<String, String> datasetsBySession;
  private final HashMap<String, QCParam> qcParamsBySession;
  private final HashMap<String, String> hashesBySession;
  private final String sessionDirectory;
  private final String expiredSessionList;
  private final HashMap<String, MessageSocket> rppMonitors;
  private final HashMap<String, RPPSessionProcessor> rppSessionProcessors;
  private final String tpsName;

  private ServerSocket serverSocket;
  private boolean alive = true;
  
  /**
   * ThreadPool
   */
  private final ScheduledExecutorService repeatingTasks;
  

  //TODO  qsub on datarmor prints the job.id, it is then possible to get the job status, before the job start / tps has a third script getStatus(sessionId) if job is not started, display queue, else tps.status
  /**
   * Creates and runs a new Instance of RPP from a Configuration File
   *
   * @param configFile the path to the configuration fill
   * @throws fr.inserm.u1078.tludwig.privas.instances.RPP.ConfigFileParsingException
   */
  public RPP(String configFile) throws Exception {
    this.repeatingTasks = Executors.newScheduledThreadPool(Math.max(2, Math.min(4, Runtime.getRuntime().availableProcessors())));
    this.rppSessionProcessors = new HashMap<>();
    
    int port = -1;
    this.addLogListener(new StandardErrorLogger());
    orderedDatasets = new ArrayList<>();
    rppDatasets = new HashMap<>();
    datasetsBySession = new HashMap<>();
    qcParamsBySession = new HashMap<>();
    hashesBySession = new HashMap<>();
    whitelist = new ArrayList<>();
    blacklist = new ArrayList<>();
    boolean hasWhitelistTag = false;
    boolean hasBlacklistTag = false;
    String tmpConnectionLog = null;
    String sessionDir = null;
    String expiredList = null;
    String tpName = null;
    String tpsAddress = null;
    String tpsUser = null;
    String tpsLaunchCommand = null;
    String tpsGetKeyCommand = null;
    String tpsSessionDir = null;
    int unknown = -495837;
    int maxPDay = unknown;
    int maxPWeek = unknown;
    int maxPMonth = unknown;

    try {
      UniversalReader in = new UniversalReader(configFile);
      String line;
      while ((line = in.readLine()) != null)
        try {
          String[] f = line.split("#")[0].split("\t");
          switch (f[0].toLowerCase()) {
            case FileFormat.RPP_TAG_PORT:
              port = new Integer(f[1]);
              break;
            case FileFormat.RPP_TAG_DATA:
              for (String dataset : f[1].split(",")) {
                RPPDataset rppDataset = RPPDataset.parse(dataset);
                String name = rppDataset.getName();
                if(orderedDatasets.contains(name))
                  throw new RPP.ConfigFileParsingException("Duplicate dataset name ["+name+"], will be ignored");
                orderedDatasets.add(name);
                rppDatasets.put(name, rppDataset);
              }
              break;
            case FileFormat.RPP_TAG_RPP_SESSION_DIR:
              sessionDir = f[1];
              break;
            case FileFormat.RPP_TAG_RRP_EXPIRED_SESSION:
              expiredList = f[1];
              break;
            case FileFormat.RPP_TAG_TPS_ADDRESS:
              tpsAddress = f[1];
              break;
            case FileFormat.RPP_TAG_TPS_NAME:
              tpName = f[1];
              break;
            case FileFormat.RPP_TAG_TPS_USER:
              tpsUser = f[1];
              break;
            case FileFormat.RPP_TAG_TPS_LAUNCH_COMMAND:
              tpsLaunchCommand = f[1];
              break;
            case FileFormat.RPP_TAG_TPS_GETKEY_COMMAND:
              tpsGetKeyCommand = f[1];
              break;
            case FileFormat.RPP_TAG_TPS_SESSION_DIR:
              tpsSessionDir = f[1];
              break;
            case FileFormat.RPP_TAG_CONNECTION_LOG:
              tmpConnectionLog = f[1];
              break;
            case FileFormat.RPP_TAG_BLACKLIST:
              hasBlacklistTag = true;
              for(String address : f[1].split(",")){
                try{
                  blacklist.add(address);
                } catch (Exception e){
                  logWarning("Unable to parse IP ["+address+"]");
                }
              }
              break;
            case FileFormat.RPP_TAG_WHITELIST:
              hasWhitelistTag = true;
              for(String address : f[1].split(",")){
                try{
                  whitelist.add(address);
                } catch (Exception e){
                  logWarning("Unable to parse IP ["+address+"]");
                }
              }
              break;
            case FileFormat.RPP_TAG_MAX_PER_DAY:
              try{
                int val = Integer.parseInt(f[1]);
                if(val < 1)
                  val = Integer.MAX_VALUE;
                maxPDay = val;
              } catch(Exception e) {
                logWarning("Unable to parse line ["+line+"]");
              }
              break;
            case FileFormat.RPP_TAG_MAX_PER_WEEK:
              try{
                int val = Integer.parseInt(f[1]);
                if(val < 1)
                  val = Integer.MAX_VALUE;
                maxPWeek = val;
              } catch(Exception e) {
                logWarning("Unable to parse line ["+line+"]");//Nothing
              }
              break;
            case FileFormat.RPP_TAG_MAX_PER_MONTH:
              try{
                int val = Integer.parseInt(f[1]);
                if(val < 1)
                  val = Integer.MAX_VALUE;
                maxPMonth = val;
              } catch(Exception e) {
                logWarning("Unable to parse line ["+line+"]");//Nothing
              }
              break;
          }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
          //Nothing
        }
      in.close();
    } catch (IOException e) {
      throw new ConfigFileParsingException(MSG.cat(MSG.RPP_ERR_CONFIG, configFile), e);
    }

    connectionLog = tmpConnectionLog;

    if (port == -1)
      throw missingParameters(MSG.RPP_DESC_PORT, configFile, MSG.RPP_SYNTAX_PORT);
    //if (rppData == null)
    if (orderedDatasets.isEmpty())
      throw missingParameters(MSG.RPP_DESC_DATA, configFile, MSG.RPP_SYNTAX_DATA);
    if (sessionDir == null)
      throw missingParameters(MSG.RPP_DESC_RPP_SESSION_DIR, configFile, MSG.RPP_SYNTAX_RPP_SESSION_DIR);
    if (expiredList == null)
      throw missingParameters(MSG.RPP_DESC_RPP_EXPIRED_SESSION_LIST, configFile, MSG.RPP_SYNTAX_RPP_EXPIRED_SESSION_LIST);
    if (tpName == null)
      throw missingParameters(MSG.RPP_DESC_TPS_NAME, configFile, MSG.RPP_SYNTAX_TPS_NAME);
    if (tpsAddress == null)
      throw missingParameters(MSG.RPP_DESC_TPS_ADDRESS, configFile, MSG.RPP_SYNTAX_TPS_ADDRESS);
    if (tpsUser == null)
      throw missingParameters(MSG.RPP_DESC_TPS_USER, configFile, MSG.RPP_SYNTAX_TPS_USER);
    if (tpsLaunchCommand == null)
      throw missingParameters(MSG.RPP_DESC_TPS_LAUNCH_COMMAND, configFile, MSG.RPP_SYNTAX_TPS_LAUNCH_COMMAND);
    if (tpsGetKeyCommand == null)
      throw missingParameters(MSG.RPP_DESC_TPS_GETKEY_COMMAND, configFile, MSG.RPP_SYNTAX_TPS_GETKEY_COMMAND);
    if (tpsSessionDir == null)
      throw missingParameters(MSG.RPP_DESC_TPS_SESSION_DIR, configFile, MSG.RPP_SYNTAX_TPS_SESSION_DIR);
    if (connectionLog == null)
      throw missingParameters(MSG.RPP_DESC_CONNECTION_LOG, configFile, MSG.RPP_SYNTAX_CONNECTION_LOG);
    if(!hasBlacklistTag)
      throw missingParameters(MSG.RPP_DESC_BACKLIST, configFile, MSG.RPP_SYNTAX_BLACKLIST);
    if(!hasWhitelistTag)
      throw missingParameters(MSG.RPP_DESC_WHITELIST, configFile, MSG.RPP_SYNTAX_WHITELIST);
    if(maxPDay == unknown)
      throw missingParameters(MSG.RPP_DESC_MAX_PER_DAY, configFile, MSG.RPP_SYNTAX_MAX_PER_DAY);
    if(maxPWeek == unknown)
      throw missingParameters(MSG.RPP_DESC_MAX_PER_WEEK, configFile, MSG.RPP_SYNTAX_MAX_PER_WEEK);
    if(maxPMonth == unknown)
      throw missingParameters(MSG.RPP_DESC_MAX_PER_MONTH, configFile, MSG.RPP_SYNTAX_MAX_PER_MONTH);

    this.tpsName = tpName;
    this.rppMonitors = new HashMap<>();
    this.thirdPartyConnector = new RPPThirdPartyConnector(this, tpsAddress, tpsUser, tpsLaunchCommand, tpsGetKeyCommand, tpsSessionDir);
    this.sessionDirectory = sessionDir;
    this.expiredSessionList = expiredList;
    this.maxPerDay = maxPDay;
    this.maxPerWeek = maxPWeek;
    this.maxPerMonth = maxPMonth;
    this.sessions = new ArrayList<>();

    init(port);
    logInfo("RPP server listening on port : " + port);
    logInfo("Session directory : " + sessionDir);
    logInfo("Expired Session list : " + expiredList);
    logInfo("Available Datasets :");
    for (String key : orderedDatasets)
      logInfo("\t" + key + " : " + rppDatasets.get(key));
    logInfo("Connection log : "+ connectionLog);
    logInfo("Maximum number of connections per day/week/month : "+maxPDay+"/"+maxPWeek+"/"+maxPMonth);
    logInfo("Blacklisted addresses");
    for(String bl : blacklist)
      logInfo("\t" + bl);
    logInfo("Whitelisted addresses");
    for(String wh : whitelist)
      logInfo("\t" + wh);
    logInfo("TPS : " + tpName + "(" + tpsUser + "@" + tpsAddress + ":" + tpsSessionDir + ")");
    logInfo("TPS Launch : " + tpsLaunchCommand);
    logInfo("TPS Get : " + tpsGetKeyCommand);
  }

  /**
   * check is CI is authorized for connection
   */

  public static String getIP(String address){
    String[] f = address.split(":")[0].split("/",-1);
    if(f.length > 1)
      return f[1];
    return f[0];
  }

  public void tryToConnect(String address) throws UnauthorizedConnectionException {
    logDebug("Trying to connect from "+address);

    if (isWhitelisted(address))
      return;
    logDebug("No whitelist "+address);
    if (isBlacklisted(address))
      throw new UnauthorizedConnectionException("Your address has been blacklisted. Contact the administrator for more information");
    logDebug("No blacklist "+address);
    Instant now = Instant.now();
    logDebug("Now "+now);
    Instant day = now.minus(1, ChronoUnit.DAYS);
    logDebug("last day "+day);
    Instant week = now.minus(7, ChronoUnit.DAYS);//ChronoUnit.WEEKS not supported by minus
    logDebug("last week "+week);
    Instant month = now.minus(31, ChronoUnit.DAYS);//ChronoUnit.MONTHS not supported by minus
    logDebug("last month "+month);
    int nDay = 0;
    int nWeek = 0;
    int nMonth = 0;
    try {
      logDebug("will get connections "+address);
      for (Instant connect : getConnections(address)) {
        logDebug("found ["+connect+"] "+address);
        if (connect.isAfter(day))
          nDay++;
        if (connect.isAfter(week))
          nWeek++;
        if (connect.isAfter(month))
          nMonth++;
        if (nDay >= this.maxPerDay)
          throw new UnauthorizedConnectionException("You cannot connect to this server more than " + maxPerDay + " times per day");
        if (nWeek >= this.maxPerWeek)
          throw new UnauthorizedConnectionException("You cannot connect to this server more than " + maxPerWeek + " times per week");
        if (nMonth >= this.maxPerMonth)
          throw new UnauthorizedConnectionException("You cannot connect to this server more than " + maxPerMonth + " times per month");
      }
    } catch (IOException e) {
      logError("Could not check / write connection log");
      logError(e);
    }
  }

  private boolean isWhitelisted(String address){
    return ipAddressMatch(address, whitelist);
  }


  private boolean isBlacklisted(String address){
    return ipAddressMatch(address, blacklist);
  }

  private void addConnection(String address) {
    try {
      PrintWriter out = new PrintWriter(new FileWriter(connectionLog, true));//Append mode
      out.println(address + "\t" + new Date().getTime());
      out.close();
    } catch (IOException e){
      logWarning("Could not log Session in connection log");
      logWarning(e);
    }
  }

  private ArrayList<Instant> getConnections(String address) throws IOException {
    ArrayList<Instant> ret = new ArrayList<>();
    UniversalReader in = new UniversalReader(connectionLog);
    String line;
    while((line = in.readLine()) != null){
      String[] f = line.split("\t");
      if(ipAddressMatch(address, f[0]))
        ret.add(Instant.ofEpochMilli(Long.parseLong(f[1])));
    }
    in.close();
    return ret;
  }

  public static boolean ipAddressMatch(String address, ArrayList<String> list){
    for(String address2 : list)
      if(ipAddressMatch(address, address2))
        return true;
    return false;
  }

  public static boolean ipAddressMatch(String address1, String address2){
    //Addresses format:
    // *
    // *.*
    // *.*.*
    // *.*.*.*
    // 125.123
    // 125.123.*
    // 125.123.41.*
    // 125.123.41.17
    // 125.123.41.17-52
    String[] ip1 = address1.split("\\.");
    String[] ip2 = address2.split("\\.");
    for(int i = 0; i < ip1.length && i< ip2.length; i++){
      String p1 = ip1[i];
      String p2 = ip2[i];

      //wildcards always match
      if("0".equals(p1) || "*".equals(p1))
        continue;
      if("0".equals(p2) || "*".equals(p2))
        continue;
      if(!p1.equals(p2))
        return false;
      String[] sp1 = p1.split("-");
      String[] sp2 = p2.split("-");
      int min1 = Integer.parseInt(sp1[0]);
      int max1 = min1;
      int min2 = Integer.parseInt(sp2[0]);
      int max2 = min2;
      if(sp1.length > 1)
        max1 = Integer.parseInt(sp1[1]);
      if(sp2.length > 1)
        max2 = Integer.parseInt(sp2[1]);
      if(max1 < min2 || max2 < min1)
        return false;
    }
    return true;
  }

  /**
   * End the RPP Thread, and so exits to Program
   */
  private void kill() {
    alive = false;
  }
  
  public ScheduledFuture<?> submitRepeating(Runnable task, long msDelay){
    return this.repeatingTasks.scheduleWithFixedDelay(task, 0, msDelay, TimeUnit.MILLISECONDS);
  }
  
  public ScheduledFuture<?> submitLater(Runnable task, long msDelay) {
    return this.repeatingTasks.schedule(task, msDelay, TimeUnit.MILLISECONDS);
  }
  
  public ScheduledFuture<?> submitNow(Runnable task) {
    return this.repeatingTasks.schedule(task, 0, TimeUnit.MILLISECONDS);
  }

  /**
   * Everytime RPP gets a Message through its ServerSocket, it creates a MessageSocket to read the Message and passes it to a MessageHandler to Handle this
   * Message
   */
  @Override
  public void run() {
    this.logInfo("Main Thread Started");
    //Service that clears old sessions    
    submitRepeating(this::cleanExpiredSessions, Parameters.RPP_CLEAR_SESSION_DELAY);

    //RPP Message Handling
    while (alive)
      try {
        //Gets the next Message in the Server Socket and Wraps it in a MessageSocket
        MessageSocket clientSocket = new MessageSocket(serverSocket);
        //Handles the Message
        submitNow(new MessageHandler(clientSocket));
      } catch (IOException e) {
        RPP.this.logError(MSG.RPP_ERR_RPP_THREAD);
        RPP.this.logError(e);
      }
  }

  private void cleanExpiredSessions() {
    this.logDebug("Cleaning Expired Sessions...");
    for (String session : getRPPSessions()) {
      this.logDebug("Session [" + session + "]");
      if (isExpired(session)) {
        this.logDebug("...Is expired");
        try {
          clearExpired(session);
          this.logDebug("has been removed");
        } catch (IOException ex) {
          this.logDebug("could not be removed");
        }
      } else
        this.logDebug("...Isn't expired");
    }
  }

  private ArrayList<String> getRPPSessions() {
    ArrayList<String> ids = new ArrayList<>();
    File dir = new File(this.sessionDirectory);
    for (File file : dir.listFiles())
      if (file.isDirectory())
        ids.add(file.getName());
    return ids;
  }

  /**
   * Creates a new ConfigFileParsingException for a missing parameter
   *
   * @param missing    the name of the Missing Parameter
   * @param configFile the Config File being read
   * @param syntax     the proper Syntax for this Parameter
   * @return
   */
  private ConfigFileParsingException missingParameters(String missing, String configFile, String syntax) {
    return new ConfigFileParsingException(missing, configFile, syntax);
  }

  /**
   * Initializes the RPP object<p>
   * <ol><p>
   * <li>Creates a Server Socket to received Messages
   * <li>Starts processing Messages in the Socket
   * </ol><p>
   * @param port
   */
  private void init(int port) throws IOException {
    File sessionDir = new File(this.sessionDirectory);
    if (!sessionDir.exists() && !sessionDir.mkdirs())
      this.logError(MSG.cat(MSG.FAIL_MKDIR, sessionDir.getAbsolutePath()));

    for (String session : this.getRPPSessions())
      restore(session);

    serverSocket = new ServerSocket(port);
    new Thread(this).start(); //This must not be in a share Executor Pool, because the task is blocking (waiting IO from the client)
  }
  
  public void createSessionDirectory(String sessionId){
    File sessionDir = new File(this.getFilenameFor(sessionId, ""));
    if (!sessionDir.exists() && !sessionDir.mkdirs())
      this.logError(MSG.cat(MSG.FAIL_MKDIR, sessionDir.getAbsolutePath()));
  }

  /**
   * Deletes a directory for an expired Session
   *
   * @param session the Session ID
   * @throws IOException
   */
  private void clearExpired(String session) throws IOException {
    deleteDirectory(new File(getFilenameFor(session, null)));
    PrintWriter out = new PrintWriter(new FileWriter(this.expiredSessionList, true));
    out.println(session);
    out.close();
  }

  /**
   * Returns true if the session is expired
   *
   * @param session the Session ID
   * @return true if session is older than RPPConstants.SESSION_EXPIRED_AFTER_DAYS old
   */
  private boolean isExpired(String session) {
    return (new Date().getTime() - getStart(session).getTime()) / (1000 * 60 * 60 * 24) > RPPConstants.SESSION_EXPIRED_AFTER_DAYS;
  }

  /**
   * Returns the Start Date of a session
   *
   * @param session the Session ID
   * @return
   */
  private Date getStart(String session) {
    try {
      return Constants.DF_DAY_DOT_TIME.parse(session);//trailing characters in the Session ID are ignored by the date format
    } catch (ParseException ignore) {
      this.logWarning("Could not parse Session date for [" + session + "]");
      return new Date();
    }
  }

  private void restore(String session) {
    try {
      RPPSessionProcessor sp = new RPPSessionProcessor(this, session);
      sp.init();
    } catch (BedRegion.BedRegionException | IOException | NumberFormatException | QCException e) {
      logError("Unable to restore saved session ["+session+"]");
      logError(e);
    }
    
  }

  /**
   * Deletes a directory
   *
   * @param dir the Directory to delete
   * @throws IOException if the directory could not be deleted
   */
  private static void deleteDirectory(File dir) throws IOException {
    if (dir.isDirectory()) {
      File[] entries = dir.listFiles();
      if (entries != null)
        for (File entry : entries)
          deleteDirectory(entry);
    }
    if (!dir.delete())
      throw new IOException(MSG.cat(MSG.RPP_ERR_DELETE, dir.toString()));
  }

  /**
   * Gets the path to a file
   *
   * @param session     the Session ID
   * @param fileContent the code of the file being requested
   * @return
   */
  public String getFilenameFor(String session, String fileContent) {
    if (session == null || session.length() < 1) {
      this.logWarning(MSG.cat(MSG.RPP_ERR_INVALID_ID, session));
      return "";
    }

    String ret = this.sessionDirectory + File.separator + session;
    if (fileContent == null)
      return ret;
    return ret + File.separator + fileContent;
  }

  /**
   * Generates a new unique Session ID
   *
   * @return the Session ID in the format YYYY.MM.DD.hh.mm.ss.XXXX where the left part is the current Date and time, and XXXX a random alphanumeric sequence
   */
  private synchronized String generateSessionId() {
    String s = this.generateRandomSessionString();
    while (this.sessions.contains(s))
      s = this.generateRandomSessionString();
    this.sessions.add(s);
    return s;
  }

  /**
   * Generates a new Session ID (could be non unique)
   *
   * @return
   */
  private String generateRandomSessionString() {
    final int LENGTH = 4;
    StringBuilder ret = new StringBuilder(Constants.DF_DAY_DOT_TIME.format(new Date()) + ".");
    for (int i = 0; i < LENGTH; i++) { //Add LENGTH random letters
      int r = (int) (62 * Math.random()) + 48; //48 -> 110
      if (r > 57)
        r += 7;
      if (r > 90)
        r += 6;
      ret.append((char) r);
    }
    return ret.toString();
  }

//  /**
//   * Gets Map link a Dataset (key) to the path to a VCF File (value)
//   *
//   * @return
//   */
//v  public HashMap<String, String> getVCFFilenames() {
//    return vcfFilenames;
//  }

  /**
   * Gets the number of variant in a Genotype File
   *
   * @param filename the path to the Genotype File
   * @return
   */
  public int getGenotypeFileSize(String filename) {
    try {
      return GenotypesFileHandler.getNumberOfLinesGenotypes(filename);
    } catch (IOException e) {
      return -1;
    }
  }

  public RPPDataset getRPPDataset(String datasetName){
    return this.rppDatasets.get(datasetName);
  }

  /**
   * Starts procession a Session :
   * <p>
   * First:
   * <p>
   * <ol>
   * <li>Extract RPP data
   * <li>Wait for Client data to be received and RPP data to be extracted
   * </ol><p>
   * Once all data are available :
   * <p>
   * <ol>
   * <li>Send everything to the Third Party Server
   * <li>Monitor the Third Party Server
   * <li>Gets the Results from the Third Party
   * </ol>
   */
  private void processSession(String session, String datasetName, double maxMaf, double maxMafNFE, String minCsq, boolean limitToSNVs, BedFile bed, QCParam qcParam, String kHash) {
    RPPSessionProcessor sp = new RPPSessionProcessor(this, session, datasetName, maxMaf, maxMafNFE, minCsq, limitToSNVs, bed, qcParam, kHash);
    this.rppSessionProcessors.put(session, sp);
    sp.serialize();
    //sp.init();
  }
  
  /**
   * Updates the current Status for a Session
   *
   * @param session the Session ID
   * @param status  the current Status
   */
  public void setStatus(String session, RPPStatus status) {
    try {
      status.serialize(this.getFilenameFor(session, FileFormat.FILE_RPP_STATUS));
    } catch (IOException ex) {
      logWarning(MSG.cat(MSG.RPP_ERR_SAVE_STATUS, session, ex));
    }
    MessageSocket monitor = this.rppMonitors.get(session);
    if (monitor != null)
      try {
        monitor.writeMessage(new SendRPPStatus(session, status));
      } catch (Message.EmptyParameterException | IOException ex) {
        logInfo(MSG.cat(MSG.RPP_ERR_SEND_STATUS_CLIENT_LEFT, session));
        this.rppMonitors.put(session, null);
      }
  }

  /**
   * Gets the current status for a Session
   *
   * @param session the Session ID
   * @return
   */
  public RPPStatus getStatus(String session) {
    if (isExpired(session))
      return RPPStatus.expired();
    RPPStatus status = RPPStatus.unknown();
    try {
      status = RPPStatus.deserialize(this.getFilenameFor(session, FileFormat.FILE_RPP_STATUS));
    } catch (Exception e) {
      logDebug("Could not restore status from file : "+e.getMessage());
      this.setStatus(session, status);
    }
    return status;
  }

  /**
   * Writes all the Client Data to the appropriate files
   *
   * @param session                         the Session ID
   * @param encryptedAESKey                 the AES Key, encrypted with the TPS' Public RSA Key
   * @param encryptedClientData             the Client Genotype Data, encrypted with the AES Key
   * @param encryptedClientExcludedVariants the Client's Excluded Variants, encrypted with the AES Key
   * @param algorithm                       the selected Algorithm and its parameters
   * @throws IOException
   */
  private void writeClientData(String session, String encryptedAESKey, String encryptedClientData, String encryptedClientExcludedVariants, String algorithm) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
    PrintWriter out = new PrintWriter(new FileWriter(this.getFilenameFor(session, FileFormat.FILE_AES_KEY)));
    out.println(encryptedAESKey);
    out.close();

    out = new PrintWriter(new FileWriter(this.getFilenameFor(session, FileFormat.FILE_ALGORITHM)));
    out.println(algorithm);
    out.close();

    out = new PrintWriter(new FileWriter(this.getFilenameFor(session, FileFormat.FILE_ENCRYPTED_CLIENT_DATA)));
    out.println(encryptedClientData);
    out.close();

    out = new PrintWriter(new FileWriter(this.getFilenameFor(session, FileFormat.FILE_ENCRYPTED_CLIENT_EXCLUDED_VARIANTS)));
    out.println(encryptedClientExcludedVariants);
    out.close();

    String dataset = datasetsBySession.get(session);
    String hash = hashesBySession.get(session);
    QCParam qcParam = qcParamsBySession.get(session);
    VariantExclusionSet ves = rppDatasets.get(dataset).getVariantExclusionSet(hash, qcParam);
    out = new PrintWriter(new FileWriter(this.getFilenameFor(session, FileFormat.FILE_RPP_EXCLUDED_VARIANTS)));
    out.println(ves.serialize());
    out.close();

    out = new PrintWriter(new FileWriter(this.getFilenameFor(session, FileFormat.FILE_CLIENT_DATA_OK)));
    out.println(Constants.OK);
    out.close();
  }

  /**
   * Check if all client Data were fully received
   *
   * @param session the Session ID
   * @return TRUE if all data are present
   */
  boolean checkClientFile(String session) {
    String line;
    try {
      BufferedReader in = new BufferedReader(new FileReader(this.getFilenameFor(session, FileFormat.FILE_CLIENT_DATA_OK)));
      line = in.readLine();
      in.close();
    } catch (IOException e) {
      return false;
    }
    return Constants.OK.equals(line);
  }
  
  
  /**
   * Checks that the results were fully downloaded
   *
   * @param session the Session ID
   * @return TRUE if all data are present
   */
  boolean checkResultsFile(String session) {
    String line;
    try {
      BufferedReader in = new BufferedReader(new FileReader(this.getFilenameFor(session, FileFormat.FILE_RESULTS_OK)));
      line = in.readLine();
      in.close();
    } catch (IOException e) {
      return false;
    }
    return Constants.OK.equals(line);
  }
  
  /**
   * Checks if all rpp data were fully extracted
   * 
   * @param session
   * @return 
   */
  boolean checkRPPFile(String session) {
    String line;
    try {
      BufferedReader in = new BufferedReader(new FileReader(this.getFilenameFor(session, FileFormat.FILE_RPP_DATA_OK)));
      line = in.readLine();
      in.close();
    } catch (IOException e) {
      return false;
    }
    return line.charAt(0) == '0';
  }
  
  public void stopThread(){
    throw new RuntimeException("STOP");
  }

  /**
   * Registers the Client as a Monitor and Starts sending RPPStatus updates to the Client
   *
   * @param session the ID of the Session that the Client wants to monitor
   * @param socket  the MessageSocket through which the RPPStatus updates will be sent
   * @return
   */
  private Message sendStatus(String session, MessageSocket socket) {
    this.rppMonitors.put(session, socket);
    SendRPPStatus first = null;
    try {
      first = new SendRPPStatus(session, RPP.this.getStatus(session));
    } catch (Message.EmptyParameterException e) {
      //Impossible
    }

    submitLater(() -> {
      try {
        socket.writeMessage(new SendRPPStatus(session, RPP.this.getStatus(session)));
      } catch (Message.EmptyParameterException | IOException ex) {
        RPP.this.logWarning(MSG.RPP_ERR_SEND_AFTER_BINDING);
        RPP.this.logWarning(ex);
      }
    }, Parameters.RPP_MONITOR_FIRST_DELAY);
    return first;
  }

  public void sendTPSStatusToClient(String session, TPStatus tpStatus){
    try {
      SendTPSStatus sendTPSStatus = new SendTPSStatus(session, tpStatus);
      MessageSocket monitor = rppMonitors.get(session);
      monitor.writeMessage(sendTPSStatus);
    } catch (Message.EmptyParameterException ignore) {
      //That's impossible outside from java reflection
    } catch (IOException e) {
      RPP.this.logWarning(MSG.RPP_ERR_SEND_TPS);
      RPP.this.logWarning(e);
    }
  }
  
  public void sendDataAndStart(String session) {
    thirdPartyConnector.sendDataAndStartJob(session);
  }

  /**
   * Gets the Results File from the Third Party Server
   *
   * @param sessionId the Session ID
   */
  public void getThirdPartyResults(String sessionId) {
    this.thirdPartyConnector.getResults(sessionId);
  }

  /**
   * Gets the TPSStatus of the Third Party for the Session
   *
   * @param sessionId the Session ID
   * @return
   */
  public List<TPStatus> getThirdPartyStatuses(String sessionId, int last) throws Exception {
    return this.thirdPartyConnector.getStatuses(sessionId, last);
  }

  /**
   * Threaded class that Handles Messages received by the RPP through the MessageSocket
   * <p>
   * First the Request (Message) is read.
   * <p>
   * Then the appropriate method is called to produce the Response (Message)
   * <p>
   * The Response is sent back to the Client
   * <p>
   * If only one Message was expected as a Response (not Monitoring), the Message Socket is closed
   */
  private class MessageHandler implements Runnable {

    /**
     * the Socket where the Message will be read and the response (another Message) wil be written
     */
    private final MessageSocket socket;

    /**
     * Constructor
     *
     * @param messageSocket the Socket where the Message will be read and the response (another Message) wil be written
     */
    private MessageHandler(MessageSocket messageSocket) {
      this.socket = messageSocket;
    }

    @Override
    public void run() {
      Message request = null;
      Message reply;

      try {
        request = socket.readMessage();
        reply = this.getReply(request);
      } catch(IOException se) {
        logWarning("Socket Disconnected");
        try {
          socket.close();
        } catch(Exception ignore) {
          //nothing
        }
        return;
      }catch (MessageException | Message.EmptyParameterException e) {
        String errorMessage = MSG.cat(MSG.RPP_ERR_SOCKET, e);
        reply = new SendError(errorMessage);
        logWarning(e);
      }

      try {
        socket.writeMessage(reply);
        //If the message is of the Request/1-Reply format, close the socket
        //Monitor is in the format Request/n-Reply, the socket must remain open for further updates
        if (!(request instanceof AskMonitor))
          socket.close();
      } catch (IOException ex) {
        logError(ex);
      }
    }

    /**
     * Gets Response to the Request (Message)
     * <p>
     * Delegates to the appropriate method, depending on the Request's type
     *
     * @param request the Message received by the RPP
     * @return the Response or a Error Message
     * @throws fr.inserm.u1078.tludwig.privas.messages.Message.EmptyParameterException if some parameters are missing from the Message
     */
    private Message getReply(Message request) throws Message.EmptyParameterException {
      logInfo(MSG.cat(MSG.RPP_INF_REQUEST, request.getType(), MSG.RPP_INF_FROM, socket.getClientIP()));

      if (request instanceof AskSession)
        return this.sessionAsked((AskSession) request);
      if (request instanceof StartSession)
        return this.startSession((StartSession) request);
      if (request instanceof AskResults)
        return this.resultsAsked((AskResults) request);
      if (request instanceof AskRPPConfiguration)
        return this.getRPPConfiguration((AskRPPConfiguration) request);
      if (request instanceof SendClientData)
        return this.receiveData((SendClientData) request);
      if (request instanceof AskMonitor)
        return this.startMonitoringRPP((AskMonitor) request);

      String msg = MSG.cat(MSG.RPP_ERR_TYPE_UNKNOWN, request.getType());
      logWarning(msg);
      return new SendError(msg);
    }

    /**
     * Creates a Response Message containing the RPP Configuration (list of datasets available, name of the Third Party Server)
     *
     * @param askRPPConfiguration the initial Request
     * @return
     * @throws fr.inserm.u1078.tludwig.privas.messages.Message.EmptyParameterException
     */
    private Message getRPPConfiguration(AskRPPConfiguration askRPPConfiguration) throws Message.EmptyParameterException {
      StringBuilder datasets = new StringBuilder();
      for (String dataset : RPP.this.orderedDatasets)
        datasets.append(",").append(dataset);
      return new SendRPPConfiguration(datasets.substring(1), tpsName);
    }

    /**
     * Creates a Response Message acknowledging that the Client is registered to monitor a given Session
     *
     * @param askMonitor the initial Message (contains the ID of the Session to monitor)
     * @return
     */
    private Message startMonitoringRPP(AskMonitor askMonitor) {
      return sendStatus(askMonitor.getSession(), socket);
    }

    /**
     * Creates a Response Message acknowledging the the Client Data were received
     *
     * @param sendClientData the initial Message (contains the Client Data)
     * @return
     * @throws fr.inserm.u1078.tludwig.privas.messages.Message.EmptyParameterException
     */
    private Message receiveData(SendClientData sendClientData) {
      try {
        String session = sendClientData.getSession();
        writeClientData(session, sendClientData.getEncryptedAESKey(), sendClientData.getEncryptedClientData(), sendClientData.getEncryptedClientExcludedVariants(), sendClientData.getAlgorithm());
        return new AckClientData(session);
      } catch (Message.EmptyParameterException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
        String errorMessage = MSG.cat(MSG.RPP_ERR_RECEIVE_DATA, e);
        logWarning(errorMessage);
        logWarning(e);
        return new SendError(errorMessage);
      }
    }

    /**
     * Creates a Message contains the Results (encrypted with the AES Key)
     *
     * @param askResults the initial Message (contains the Session ID)
     * @return
     * @throws fr.inserm.u1078.tludwig.privas.messages.Message.EmptyParameterException
     */
    private Message resultsAsked(AskResults askResults) throws Message.EmptyParameterException {
      String session = askResults.getSession();
      if (session == null)
        return new SendError(MSG.RPP_ERR_ASK_RESULTS_NO_SESSION);
      String filename = RPP.this.getFilenameFor(session, FileFormat.FILE_RESULTS);
      try {
        StringBuilder sb = new StringBuilder();
        UniversalReader in = new UniversalReader(filename);
        String line;
        while ((line = in.readLine()) != null) {
          sb.append("\n");
          sb.append(line);
        }
        in.close();
        return new SendResults(session, sb.substring(1));
      } catch (IOException e) {
        logWarning(e);
        return new SendError(MSG.cat(MSG.RPP_ERR_ASK_RESULTS, filename, e));
      }
    }

    /**
     * Creates a Message containing the newly created Session's parameters (hash, session ID, Third Party Public RSA Key)
     *
     * @param askSession the initial Message (contains parameters such as the dataset name, cryptographic keys)
     * @return
     */
    private Message sessionAsked(AskSession askSession) {
      try {
        tryToConnect(getIP(socket.getClientIP()));
        logDebug("Connection allowed");
        double maxMaf = askSession.getMaxAF();
        double maxMafNFE = askSession.getMaxAFNFE();
        String minCsq = askSession.getMinCSQ();
        boolean limitToSNVs = askSession.getLimitToSNVs();
        BedFile bed = askSession.getBedFile();
        QCParam qcParam = askSession.getQCParam();
        String clientRSAString = askSession.getClientPublicKey();
        String datasetName = askSession.getDataset();

        logInfo(MSG.cat(MSG.RPP_INF_DATA, askSession.getType()));
        String session = generateSessionId();
        createSessionDirectory(session);
        datasetsBySession.put(session, datasetName);
        qcParamsBySession.put(session, qcParam);
        String thirdPartyPublicPem = thirdPartyConnector.getTPSPublicKey(session);
        if (thirdPartyPublicPem == null || thirdPartyPublicPem.isEmpty()) {
          logError(MSG.RPP_ERR_TPS_KEY);
          return new SendError(MSG.RPP_ERR_TPS_KEY);
        }
        String kHash = Crypto.generateHashKey(); 
        hashesBySession.put(session, kHash);
        PublicKey clientRSA = Crypto.buildPublicRSAKey(clientRSAString);
        String encryptedKHash = Crypto.encryptRSA(clientRSA, kHash);
        setStatus(session, RPPStatus.newSession());
        BedFile bed2 = BedFile.getIntersection(bed, rppDatasets.get(datasetName).getBedFile());
        SendSession sendSession = new SendSession(session, encryptedKHash, thirdPartyPublicPem, bed2);
        processSession(session, datasetName, maxMaf, maxMafNFE, minCsq, limitToSNVs, bed2, qcParam, kHash);
        return sendSession;
      } catch (UnauthorizedConnectionException f) {
        String msg = f.getMessage();
        logWarning(msg);
        return new SendError(msg);
      } catch (Message.EmptyParameterException | InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | IOException | BedRegion.BedRegionException | QCException e) {
        String errorMessage = MSG.cat(MSG.RPP_ERR_NEW_SESSION, e);
        logWarning(errorMessage);
        logWarning(e);
        return new SendError(errorMessage);
      }
    }

    private Message startSession(StartSession startSession) {
      try {
        addConnection(getIP(socket.getClientIP()));
        String session = startSession.getSession();
        RPPSessionProcessor sp = RPP.this.rppSessionProcessors.get(session);
        sp.init();
        return new SessionStarted(session);
      } catch (Message.EmptyParameterException | NullPointerException e){
        String errorMessage = MSG.cat(MSG.RPP_ERR_START_SESSION, e);
        logWarning(errorMessage);
        logWarning(e);
        return new SendError(errorMessage);
      }
    }
  }

  //public BedFile getBedFile(String datasetName) throws IOException, BedRegion.BedRegionException {
  //  return new BedFile(this.rppDatasets.get(datasetName).getBedFilename());
  //}

  /**
   * Exception thrown when there is a problem while parsing an RPP Configuration File
   */
  public static class ConfigFileParsingException extends Exception {

    ConfigFileParsingException(String missing, String configFile, String syntax) {
      this(MSG.CL_EX(missing, configFile, syntax));
    }

    ConfigFileParsingException(String message) {
      super(message);
    }

    ConfigFileParsingException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class UnauthorizedConnectionException extends Exception {

    UnauthorizedConnectionException(String message) {
      super(message);
    }

    UnauthorizedConnectionException(String message, Throwable cause) {
      super(message, cause);
    }

  }
}
