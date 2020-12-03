package fr.inserm.u1078.tludwig.privas.instances;

import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.constants.Parameters;
import fr.inserm.u1078.tludwig.privas.gui.ClientWindow;
import fr.inserm.u1078.tludwig.privas.messages.*;
import fr.inserm.u1078.tludwig.privas.utils.*;
import fr.inserm.u1078.tludwig.privas.utils.GenotypesFileHandler.GenotypeFileException;
import fr.inserm.u1078.tludwig.privas.utils.GenotypesFileHandler.HashAndPosition;
import fr.inserm.u1078.tludwig.privas.listener.ProgressListener;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * The Client is the instance of the program used by a User to perform Association Studies on his/her data against RPP data<p>
 * The Client is never launched in stand-alone mode, but through the GUI (@see fr.inserm.u1078.tludwig.privas.gui.ClientWindow)
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-01-30
 *
 * Javadoc Complete on 2019-08-08
 */
public class Client extends Instance {

  private boolean isConnected = false;
  private String lastError = null;
  private String data = null;
  private String saveFilename = null;
  private String excludedVariantsFileName = null;

  private final ClientSession session;
  private ClientWindow window;
  private RPPMonitor rppMonitor;

  /**
   * Creates a new Client
   *
   * @throws Exception
   */
  public Client() throws Exception {
    this.session = new ClientSession();
  }

  public void setWindow(ClientWindow window) {
    this.window = window;
  }

  /**
   * Gets the name of the Genotype File associated to the Session
   *
   * @return
   */
  public String getGenotypeFilename() {
    return this.session.getClientGenotypeFilename();
  }

  /**
   * Gets the number of variants in the Genotype File associated to the Session
   *
   * @return
   */
  public int getGenotypeFileSize() {
    return this.session.getClientGenotypeFileSize();
  }

  /**
   * Sets the Genotype File associated to the Session
   *
   * @param filename the name of the Genotype File
   */
  public void setGenotypeFilename(String filename) {
    logInfo(MSG.action(MSG.CL_LOAD_GENO, filename));
    try {
      if (this.session.setClientGenotypeFile(filename))
        logSuccess(MSG.done(MSG.CL_OK_GENO, getGenotypeFileSize() + ""));
      else
        logError(MSG.done(MSG.CL_KO_GENO, filename));
    } catch (IOException ex) {
      logError(MSG.done(MSG.CL_KO_GENO, filename, ex));
      logError(ex);
    }
  }

  /**
   * Gets the last known error encountered
   *
   * @return
   */
  public String getLastError() {
    return lastError;
  }

  /**
   * Converts a VCF File to a Genotype File
   *
   * @param vcfFilename the name of the VCF File to convert
   * @return TRUE - if the VCF File was successfully converted
   */
  public String convert(String vcfFilename) {
    try {
      this.session.setClientGenotypeFile(null);
      String outFilename = GenotypesFileHandler.vcfFilename2GenotypesFilename(vcfFilename);
      logInfo(MSG.action(MSG.CL_CONVERT_GENO, vcfFilename));
      GenotypesFileHandler.convertVCF2Genotypes(vcfFilename, outFilename);
      logSuccess(MSG.done(MSG.CL_OK_CONVERT_GENO, getGenotypeFileSize() + ""));
      this.setGenotypeFilename(outFilename);

      return outFilename;
    } catch (GenotypeFileException | IOException e) {
      this.lastError = e.getMessage();
      logError(MSG.done(MSG.CL_KO_CONVERT_GENO, vcfFilename, e));
      logError(e);
    }
    return null;
  }

  /**
   * Apply a QC Filer on a VCF File
   * @param inputVCF the input VCF File
   * @param qcParamFilename the name of the file containing the QC parameters
   * @return TRUE -if the QC was successful
   */
  public boolean applyQC(String inputVCF, String qcParamFilename){
    String outputVCF = "undefined";
    try{
      String message =
              MSG.cat("input VCF ", inputVCF) +
              MSG.cat(" QC Param ", qcParamFilename);
      logInfo(MSG.action(MSG.CL_APPLY_QC, message));
      QCParam qcParam = new QCParam(qcParamFilename);
      QualityControl.applyQC(inputVCF, qcParam);
      outputVCF = FileUtils.getQCVCFFilename(inputVCF, qcParam);
      logSuccess(MSG.done(MSG.Cl_QC_APPLIED, outputVCF));
      return true;
    } catch (QualityControl.QCException | IOException e) {
      logError(MSG.done(MSG.CL_KO_QC, outputVCF, e));
      logError(e);
    }
    return false;
  }

  /**
   * Sets the RPP address associated to the Session
   *
   * @param ip   the address of the RPP Server
   * @param port the Port number
   */
  public void setRPP(String ip, int port) {
    this.session.setRPP(ip, port);
  }

  /**
   * Sends a Message to the RPP
   *
   * @param msg the Message to send
   * @return the RPP's Response
   * @throws IOException
   * @throws MessageException
   */
  private Message sendMessage(Message msg) throws IOException, MessageException {
    return this.sendMessage(msg, null);
  }

  /**
   * Sends a Message to the RPP
   *
   * @param msg the Message to send
   * @param pd  that will be notified of the transfert progression
   * @return the RPP's Response
   * @throws IOException
   * @throws MessageException
   */
  private Message sendMessage(Message msg, ProgressListener pd) throws IOException, MessageException {
    //this.logDebug("SEND " + this.session.getRPP() + ":" + this.session.getPort() + " <-  [" + msg.getType() + "]");
    MessageSocket ms = new MessageSocket(this.session.getRPP(), this.session.getPort());
    ms.writeMessage(msg, pd);
    Message reply = ms.readMessage();
    ms.close();
    //this.logDebug("RECV " + this.session.getRPP() + ":" + this.session.getPort() + " ->  [" + reply.getType() + "]");
    return reply;
  }

  /**
   * Gets the Client AES Key
   *
   * @return
   */
  private String getAESKey() {
    return this.session.getAesKey();
  }

  /**
   * Gets the Client's RSA Private Key
   *
   * @return
   */
  private PublicKey getPublicKey() {
    return this.session.getClientPublicRSA();
  }

  /**
   * Gets the Client's RSA Private Key
   *
   * @return
   */
  private PrivateKey getPrivateKey() {
    return this.session.getClientPrivateRSA();
  }

  /**
   * Gets the Client's Public RSA Key as a one-line pem String
   *
   * @return
   */
  private String getPublicKeyString() {
    return Crypto.bytes2OneLinePem(this.getPublicKey().getEncoded());
  }

  /**
   * Decrypts a message, encrypted with the Client's Public RSA Key
   *
   * @param encrypted the encrypted message
   * @return the clear text message
   * @throws Exception
   */
  private String decryptRSA(String encrypted) throws Exception {
    return Crypto.decryptRSA(this.getPrivateKey(), encrypted);
  }

  /**
   * Encrypts a Message with the Client AES Key
   *
   * @param message the clear text message
   * @return the encrypted message
   * @throws Exception
   */
  private String encryptAES(String message) throws Exception {
    return Crypto.encryptAES(this.getAESKey(), message);
  }

  /**
   * Encrypts a Message with the Third Party Server's Public RSA Key
   *
   * @param message the clear text message
   * @return the encrypted message
   * @throws Exception
   */
  private String encryptThirdParty(String message) throws Exception {
    return Crypto.encryptRSA(this.session.getThirdPartyPublicKey(), message);
  }

  /**
   * Gets the Configuration of the RPP (datasets available, name of the third party server).
   */
  public void communicationGetRPPConfiguration() {
    this.isConnected = false;
    logInfo(MSG.action(MSG.CL_CONNECT, this.session.getRPPFullAddress()));
    try {
      Message reply = this.sendMessage(new AskRPPConfiguration());
      this.isConnected = (reply instanceof SendRPPConfiguration);
      if (this.isConnected) {
        this.session.setThirdPartyName(((SendRPPConfiguration) reply).getTPSName());
        this.session.setAvailableDatasets(((SendRPPConfiguration) reply).getDatasets());
      }
    } catch (IOException | MessageException e) {
      logError(MSG.done(MSG.CL_KO_CONNECT, this.session.getRPPFullAddress(), e));
    }
    if (this.isConnected)
      logSuccess(MSG.done(MSG.CL_OK_CONNECT, this.session.getRPPFullAddress()));
    else
      logError(MSG.done(MSG.CL_KO_CONNECT, this.session.getRPPFullAddress()));
  }

  /*private boolean connect(){
    this.isConnected = false;
    try {
      Message reply = this.sendMessage(new AskRPPConfiguration());
      this.isConnected = (reply instanceof SendRPPConfiguration);
    } catch (Message.EmptyParameterException | IOException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | InvocationTargetException ignore) {
    }
    if(this.window != null)
      this.window.reconnect(isConnected);
    return this.isConnected;
  }*/
  /**
   * Is the Client connected to the RPP ?
   *
   * @return
   */
  public boolean isConnected() {
    return this.isConnected;
  }

  /**
   * Method in charge of handling Messages (replies)
   *
   * @param reply    the Message
   * @param expected the Class (type) of Message expected as a reply
   */
  private void handleMessage(Message reply, Class expected) throws MessageException {
    //logDebug("Message type ["+reply.getType()+"] expected ["+expected.getName()+"]");

    //is reply null ? 
    if (reply == null) {
      String error = MSG.CL_MSG_NULL;
      logError(error);
      this.lastError = error;
      throw new MessageException(error);
    }

    //first handle ErrorMessage
    if (reply instanceof SendError) {
      SendError sendError = (SendError) reply;
      String error = sendError.getErrorMessage();
      logError(error);
      this.lastError = error;
      throw new MessageException(error);
    }

    //Then check is non error message is of the expected type
   /* if (!expected.isInstance(reply)) {
      String error = MSG.CL_MSG_EXPECTED_TYPE(reply, expected);
      logError(error);
      this.lastError = error;
      throw new MessageException(error);
    }*/

    //At last, handle message according to type
    if (reply instanceof AckClientData) {
      AckClientData ackClientData = (AckClientData) reply;
      if (!this.getSessionId().equals(ackClientData.getSession())) {
        String error = MSG.CL_MSG_SESSION_MISMATCH(ackClientData, this.getSessionId());
        logError(error);
        this.lastError = error;
        throw new MessageException(error);
      }
      return;
    }

    if (reply instanceof SendRPPStatus) {
      SendRPPStatus status = (SendRPPStatus) reply;
      if (this.getSessionId().equals(status.getSession()))
        Client.this.session.setLastRPPStatus(status.getStatus());
      return;
    }

    if(reply instanceof SendTPSStatus) {
      SendTPSStatus status = (SendTPSStatus) reply;
      if (this.getSessionId().equals(status.getSession()))

        Client.this.window.postTPStatus(status.getStatus());
      return;
    }

    if (reply instanceof SendSession) {
      SendSession sendSession = (SendSession) reply;
      this.session.setId(sendSession.getSession());
      String encryptedKHash = sendSession.getEncryptedHashKey();
      String thirdPartyKeyPEM = sendSession.getThirdPartyPublicKey();
      try {
        BedFile bed = sendSession.getBedFile();
        this.session.setThirdPartyPublicKey(Crypto.buildPublicRSAKey(thirdPartyKeyPEM));
        this.session.setBedFile(bed);
        this.session.setHash(this.decryptRSA(encryptedKHash));
      } catch (Exception e) {
        String error = MSG.cat(MSG.CL_MSG_ERROR_SESSION, e);
        logError(error);
        this.lastError = error;
        throw new MessageException(error);
      }
      return;
    }

    if( reply instanceof SessionStarted) {
      SessionStarted sessionStarted = (SessionStarted) reply;
      try{
        HashAndPosition hashPos = GenotypesFileHandler.buildHashDictionaryAndPosition(this.session.getClientGenotypeFilename(), this.session.getHash());
        this.session.setGeneHashDictionary(hashPos.getHash2gene());
        this.session.setGenePositions(hashPos.getGene2position());
        this.getSession().setSessionReady();
      } catch (Exception e) {
        String error = MSG.cat(MSG.CL_MSG_ERROR_SESSION, e);
        logError(error);
        this.lastError = error;
        throw new MessageException(error);
      }
      return;
    }

    if (reply instanceof SendResults) {
      SendResults sendResults = (SendResults) reply;
      if (this.getSessionId().equals(sendResults.getSession()))
        try {
          PrintWriter out = new PrintWriter(new FileWriter(saveFilename));
          String clearResults = Crypto.decryptAES(this.getAESKey(), sendResults.getEncryptedResults());
          for (String res : clearResults.split("\n"))
            out.println(unhash(res));
          out.close();
        } catch (IOException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
          logError(e);
          throw new MessageException("Unable to read results", e);
        }
      else {
        String error = MSG.CL_MSG_SESSION_MISMATCH(sendResults, this.getSessionId());
        logError(error);
        this.lastError = error;
        throw new MessageException(error);
      }
      return;
    }

    String error = MSG.CL_MSG_UNHANDLED(reply);
    logError(error);
    this.lastError = error;
    throw new MessageException(error);
  }

  /**
   * Asks the RPP to create a new Session
   *
   * @param dataset                  the dataset to use
   * @param maxMAF                   the Maximum Allele Frequency used to select variants
   * @param maxMAFNFE                the Maximum Allele Frequency in GnomADNFE used to select variants
   * @param minCSQ                   the Least Severe Consequence used to select variants
   * @param limitToSNVs              is variant selection limited to SNVs ?
   * @param bedFilename              filename of the list of all well covered positions
   * @param excludedVariantsFileName filename of the list of variants excluded due to bad QC
   * @throws fr.inserm.u1078.tludwig.privas.instances.MessageException
   *
   */
  public void communicationAskSession(String dataset, double maxMAF, double maxMAFNFE, String minCSQ, boolean limitToSNVs, String bedFilename, String excludedVariantsFileName, String qcParamFilename) throws MessageException {
    this.excludedVariantsFileName = excludedVariantsFileName;
    BedFile bed;
    QCParam qcParam;
    this.logDebug("Reading [" + bedFilename + "]");
    try {
      bed = new BedFile(bedFilename);
    } catch (BedRegion.BedRegionException | IOException e) {
      String error = "Unable to Read Bed File [" + bedFilename + "]";
      logError(error);
      logError(e);
      this.lastError = error;
      throw new MessageException(error, e);
    }

    try {
      qcParam = new QCParam(qcParamFilename);
    } catch(IOException | QualityControl.QCException e) {
      String error = "Unable to Read QC Parameter File File [" + qcParamFilename + "]";
      logError(error);
      logError(e);
      this.lastError = error;
      throw new MessageException(error, e);
    }


    this.logDebug("Asking Session");
    this.session.setSelectedDataset(dataset);
    this.session.setMaxMAF(maxMAF);
    this.session.setMaxMAFNFE(maxMAFNFE);
    this.session.setLeastSevereConsequence(minCSQ);
    this.session.setLimitToSNVs(limitToSNVs);
    this.session.setQCParam(qcParam);
    this.session.setBedFile(bed);
    logInfo(MSG.action(MSG.CL_NEW_SESSION(dataset, maxMAF, minCSQ, bedFilename, excludedVariantsFileName)));

    try {
      Message reply = this.sendMessage(new AskSession(this.getPublicKeyString(), dataset, maxMAF, maxMAFNFE, minCSQ.split("\\.")[1], limitToSNVs, bed, qcParam));
      this.logDebug("Reply received");
      this.handleMessage(reply, SendSession.class);
    } catch (MessageException | Message.EmptyParameterException | IOException e) {
      String error = MSG.done(MSG.CL_KO_NEW_SESSION, e);
      logError(error);
      logError(e);
      this.lastError = error;
      throw new MessageException(error, e);
    }
    logSuccess(MSG.done(MSG.CL_OK_NEW_SESSION, this.session.getId()));
  }

  public void communicationStartSession(String session) throws MessageException {
    try {
      Message reply = this.sendMessage(new StartSession(session));
      this.logDebug("Reply received : "+reply);
      this.handleMessage(reply, SessionStarted.class);
    } catch (Message.EmptyParameterException | IOException e){
      String error = MSG.done(MSG.CL_KO_START_SESSION, e);
      logError(error);
      logError(e);
      this.lastError = error;
      throw new MessageException(error, e);
    }
  }

  /**
   * Get the ID of the current Session
   *
   * @return
   */
  public String getSessionId() {
    return this.session.getId();
  }

  /**
   * Asks the RPP for the Results
   *
   * @param saveFilename the name of the File to which the Results will be saved
   * @throws fr.inserm.u1078.tludwig.privas.instances.MessageException
   */
  public void communicationAskResults(String saveFilename) throws MessageException {
    logInfo(MSG.action(MSG.CL_RESULTS, this.session.getId()));
    try {
      Message reply = this.sendMessage(new AskResults(this.getSessionId()));
      this.saveFilename = saveFilename;
      this.handleMessage(reply, SendResults.class);
      logSuccess(MSG.done(MSG.CL_OK_RESULTS, this.session.getId()));
    } catch (MessageException | Message.EmptyParameterException | IOException e) {
      String error = (MSG.done(MSG.CL_KO_RESULTS, this.session.getId(), e));
      logError(error);
      logError(e);
      this.lastError = error;
      throw new MessageException(error, e);
    }
  }

  /**
   * Sends the Data to the RPP
   *
   * @param pd the ProgressListener that will be notified of the transfert progression
   * @throws fr.inserm.u1078.tludwig.privas.instances.MessageException
   */
  public void communicationSendData(ProgressListener pd) throws MessageException {
    if (data == null || data.length() == 0) {
      String error = MSG.done(MSG.CL_SEND_EMPTY);
      logError(error);
      this.lastError = error;
      throw new MessageException(error);
    }

    try {
      String encryptedAESKey = this.encryptThirdParty(this.getAESKey());
      String encryptedData = this.encryptAES(data);
      String encryptedExcludedVariants = this.encryptAES(getExcludedVariants().serialize());
      logInfo(MSG.action(MSG.CL_SEND));
      Message reply = this.sendMessage(new SendClientData(this.getSessionId(), encryptedAESKey, encryptedData, encryptedExcludedVariants, session.getAlgorithm()), pd);
      this.handleMessage(reply, AckClientData.class);
      logSuccess(MSG.done(MSG.CL_OK_SEND));
    } catch (Exception e) {
      String error = MSG.done(MSG.cat(MSG.CL_KO_SEND, e));
      logError(error);
      logError(e);
      this.lastError = error;
      throw new MessageException(error, e);
    }
  }

  private VariantExclusionSet getExcludedVariants() {
    if (excludedVariantsFileName == null || excludedVariantsFileName.isEmpty())
      return new VariantExclusionSet();
    try {
      return new VariantExclusionSet(excludedVariantsFileName, this.session.getHash());
    } catch (IOException | InvalidKeyException | NoSuchAlgorithmException ex) {
      this.logError("Unable to read Excluded Variants from file [" + excludedVariantsFileName + "]");
      this.logError(ex);
      return new VariantExclusionSet();
    }
  }

  /**
   * Gets the clear text value of a Hashed String
   *
   * @param line the Hashed String
   * @return
   */
  private String unhash(String line) {
    String[] f = line.split("\t");
    StringBuilder ret = new StringBuilder(session.unhashAndPosition(f[0]));
    for (int i = 1; i < f.length; i++)
      ret.append("\t").append(f[i]);
    return ret.toString();
  }

  /**
   * Gets the Session object
   *
   * @return
   */
  public ClientSession getSession() {
    return this.session;
  }

  /**
   * Extracts data into memory.
   *
   * @param pd the ProgressListener that will be notified of the extraction progression
   * @return TRUE - if data were successfully extracted
   */
  public boolean extractData(ProgressListener pd) {
    try {
      logInfo(MSG.action(MSG.CL_EXTRACT));
      data = GenotypesFileHandler.extractGenotypes(this.session.getClientGenotypeFilename(), this.session.getClientGenotypeFileSize(), this.session.getMaxMAF(), this.session.getMaxMAFNFE(), this.session.getLeastSevereConsequence().split("\\.")[1], this.session.getLimitToSNVs(), this.session.getBedFile(), this.session.getHash(), pd);
      
      logSuccess(MSG.done(MSG.CL_OK_EXTRACT));
      return true;
    } catch (Exception ex) {
      this.lastError = ex.getMessage();
      data = null;
      logError(MSG.done(MSG.cat(MSG.CL_KO_EXTRACT, ex)));
      logError(ex);
      return false;
    }
  }

  /**
   * Loads a Session into the Client
   *
   * @param filename the name of the File from which to load the Session
   * @return TRUE - if the Session was loaded successfully
   */
  public boolean loadSession(String filename) {
    logInfo(MSG.action(MSG.cat(MSG.CL_LOAD_SESSION, filename)));
    try {
      session.load(filename);
      logSuccess(MSG.done(MSG.CL_OK_LOAD_SESSION));
      return true;
    } catch (ClientSession.SessionFileException | IOException e) {
      logError(MSG.done(MSG.CL_KO_LOAD_SESSION, e));
      logError(e);
      return false;
    }
  }

  /**
   * Saves the current Session
   *
   * @param filename the name of the File to which to save the Session
   * @return TRUE - if the Session was saved successfully
   */
  public boolean saveSession(String filename) {
    logInfo(MSG.action(MSG.cat(MSG.CL_SAVE_SESSION, filename)));
    try {
      session.save(filename);
      logSuccess(MSG.done(MSG.CL_OK_SAVE_SESSION));
      return true;
    } catch (Exception e) {
      logError(MSG.done(MSG.CL_KO_SAVE_SESSION, e));
      logError(e);
      return false;
    }
  }

  /**
   * Was the Session saved ?
   *
   * @return
   */
  public boolean isSessionSaved() {
    return session.isSaved();
  }

  /**
   * Gets the last known Session Filename
   *
   * @return
   */
  public String getLastSessionFilename() {
    return session.getLastFilename();
  }

  /**
   * Quits the Client. (Cleanly ends the Program)
   */
  public void quit() {
    this.logInfo(MSG.CL_QUIT);
    System.exit(0);
  }

  /**
   * Starts monitoring RPP Status associated to the current Session
   *
   * @throws fr.inserm.u1078.tludwig.privas.instances.MonitoringException
   */
  public void monitorRPP() throws MonitoringException {
    if(rppMonitor != null)
      rppMonitor.close();
    rppMonitor = new RPPMonitor(this.session.getId(), this.session.getRPP(), this.session.getPort());
    rppMonitor.start();
  }

  /**
   * Sets the algorithm and its trailing parameters
   *
   * @param algorithm
   */
  public void setAlgorithm(String algorithm) {
    this.session.setAlgorithm(algorithm);
  }

  private class RPPMonitor extends Thread {

    private final String sessionId;
    private final String rpp;
    private final int port;
    private boolean run = true;
    
    RPPMonitor(String sessionId, String rpp, int port) throws MonitoringException {
      super();

      if (sessionId == null || sessionId.length() < 1)
        throw new MonitoringException("Session ID empty");

      if (rpp == null || rpp.length() < 1)
        throw new MonitoringException("No RPP provided");

      if (port == -1)
        throw new MonitoringException("No port provided");

      this.sessionId = sessionId;
      this.rpp = rpp;
      this.port = port;
    }

    @Override
    public void run() {
      MessageSocket ms = null;
      Message msg = null;
      try {
        msg = new AskMonitor(sessionId);
      } catch (Message.EmptyParameterException ex) {
        //Impossible
      }

      boolean ok = true;
      boolean first = true;
      while (run) {//Once the socket has been opened, fetch any message
        try {
          if (ms == null) {
            ms = new MessageSocket(rpp, port);
            ms.writeMessage(msg, null);
          }
          handleMessage(ms.readMessage(), SendRPPStatus.class);
          if (first) {
            logSuccess(MSG.done(MSG.cat(MSG.CL_OK_MONITOR, sessionId)));
            first = false;
          }
          if (!ok) {
            ok = true;
            logSuccess(MSG.CL_RESTORED_MONITOR);
            window.reconnect(ok);
          }
        } catch (MessageException | IOException ex) {
          if (ok) {
            ok = false;
            logError(MSG.CL_KO_MONITOR);
            window.reconnect(ok);
            try {
              new AskMonitor(sessionId);
            } catch (Message.EmptyParameterException e) {
              //ignore
            }
          }
          try {
            ms = null;
            Thread.sleep(Parameters.CLIENT_RECONNECT_DELAY);
          } catch (InterruptedException ex1) {
            System.err.println(Message.INTERRUPT(this));
            break;
          }
        }
      }
    }
    
    void close(){
      this.run = false;
    }
  }

}
