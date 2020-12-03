package fr.inserm.u1078.tludwig.privas.instances;

import fr.inserm.u1078.tludwig.privas.constants.Constants;
import fr.inserm.u1078.tludwig.privas.constants.FileFormat;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.utils.*;
import fr.inserm.u1078.tludwig.privas.listener.SessionListener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * The Client representation of an Association Test Session.
 * This objet is used by the GUI to update fields.
 * It is update by actions from the users as well as by messages from the RPP Server
 * This objet can be save/restored from text a file.
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-02-08
 *
 * Javadoc complete on 2019-08-09
 */
public class ClientSession {

  /**
   * the SessionListener list for the Session
   */
  private final ArrayList<SessionListener> sessionListeners;

  private static final String T = "\t";

  /**
   * The name of the last file used to save/load the session
   */
  private String lastFilename = null;

  /**
   * the ID of the Session
   */
  private String id = null;
  /**
   * The Hash Salt used to by the Client and the RPP
   */
  private String hash = null;
  /**
   * The AES key used by the Client and the Third Party Server
   */
  private String aesKey = null;
  /**
   * the Public RSA Key of the Client
   */
  private PublicKey clientPublicRSA = null;
  /**
   * the Private RSA Key of the Client
   */
  private PrivateKey clientPrivateRSA = null;
  /**
   * path to the Genotype File used by the Client
   */
  private String clientGenotypeFilename = null;
  /**
   * Size of the Genotype File used by the Client
   */
  private int clientGenotypeFileSize = -1;
  /**
   * Name of the Third Party Server
   */
  private String thirdPartyName = null;
  /**
   * List of datasets available on the RPP
   */
  private String availableDatasets = null;
  /**
   * Name of the RPP Dataset selected by the Client
   */
  private String selectedDataset = null;
  /**
   * Maximum Allele Frequency in GnomAD allowed in the variant selection
   */
  private double maxMAF = -1;
  
  /**
   * Maximum Allele Frequency in Gnomad_NFE allowed in the variant selection
   */
  private double maxMAFNFE = -1;
  
  /**
   * Least Severe vep consequence allowed in the variant selection
   */
  private String leastSevereConsequence = null;
  /**
   * Is variant selection limited to SNVs ?
   */
  private boolean limitToSNVs = false;
  /**
   * List of all well covered positions
   */
  private BedFile bedFile = null;

  /**
   * QC Parameters
   */
  private QCParam qcParam; //TODO (de)serialize, listener
  /**
   * Third Party Server's Public RSA Key
   */
  private PublicKey thirdPartyPublicKey = null;
  /**
   * Last known RPP Status for this Session
   */
  private String lastRPPStatus = "";
  /**
   * Address of the RPP Server
   */
  private String rppAddress = null;
  /**
   * Port number of the RPP Server
   */
  private int rppPort = -1;
  /**
   * Map linking Hashed gene names (key) to their clear text value (values)
   */
  private HashMap<String, String> geneHashDictionary;
  /**
   * Map linking Gene Names (key) to their positions (values)
   */
  private HashMap<String, String> genePositions;
  /**
   * Algorithm selected by the Client, and its trailing parameters
   */
  private String algorithm;

  /**
   * Empty Constructor
   *
   * @throws NoSuchAlgorithmException
   * @throws IOException
   */
  public ClientSession() throws NoSuchAlgorithmException, IOException {
    this.sessionListeners = new ArrayList<>();
    this.init();
    KeyPair kp = Crypto.generateRSAKeyPair();
    this.setClientPublicRSA(kp.getPublic());
    this.setClientPrivateRSA(kp.getPrivate());
    this.setAESKey(Crypto.generateAESKey());
  }

  /**
   * Registers a Session Listner, which will be alerted if some Session's parameters change<p>
   * Mainly used to update GUI in real time
   *
   * @param sessionListener
   */
  public void addSessionListener(SessionListener sessionListener) {
    this.sessionListeners.add(sessionListener);
  }

  /**
   * Initializes the Sessions value<p>
   * Object : null
   * Numerical : -1
   *
   * @throws IOException
   */
  private void init() throws IOException {
    this.setLastFilename(null);
    this.setId(null);
    this.setHash(null);
    this.setAESKey(null);
    this.setClientPublicRSA(null);
    this.setClientPrivateRSA(null);
    this.setThirdPartyName(null);
    this.setThirdPartyPublicKey(null);
    this.setClientGenotypeFile(null);
    this.setAvailableDatasets(null);
    this.setSelectedDataset(null);
    this.setMaxMAF(-1);
    this.setLeastSevereConsequence(null);
    this.setLimitToSNVs(false);
    this.setBedFile(null);
    this.setRPP(null, -1);
    this.setGeneHashDictionary(null);
    this.setGenePositions(null);
    this.setAlgorithm(null);
  }

  /**
   * Sets the Map linking Hashed gene names (key) to their clear text value (values)
   *
   * @param geneHashDictionary
   */
  public void setGeneHashDictionary(HashMap<String, String> geneHashDictionary) {
    this.geneHashDictionary = geneHashDictionary;
  }

  /**
   * Sets the Map linking Gene Names (key) to their positions (values)
   *
   * @param genePositions
   */
  public void setGenePositions(HashMap<String, String> genePositions) {
    this.genePositions = genePositions;
  }

  /**
   * Gets the name of the last file used to save/load the current Session
   *
   * @return null - if the save was never saved
   */
  public String getLastFilename() {
    return lastFilename;
  }

  /**
   * Sets the name of the last file used to save/load the current Session
   *
   * @param lastFilename
   */
  private void setLastFilename(String lastFilename) {
    this.lastFilename = lastFilename;
  }

  /**
   * Gets the RPP Server's address
   *
   * @return
   */
  public String getRPP() {
    return rppAddress;
  }

  /**
   * Gets the address and port number used to access the RPP, as one String
   *
   * @return
   */
  public String getRPPFullAddress() {
    return rppAddress + ":" + rppPort;
  }

  /**
   * Sets the address and port to access the RPP
   *
   * @param address the Address of the RPP Server
   * @param port    the Port number
   */
  public void setRPP(String address, int port) {
    this.rppAddress = address;
    this.rppPort = port;

    for (SessionListener listener : this.sessionListeners)
      listener.rppAddressUpdated(this.rppAddress, this.rppPort);
  }

  /**
   * Gets the Port number used to access the RPP
   *
   * @return
   */
  public int getPort() {
    return rppPort;
  }

  /**
   * Gets the name of the Third Party Server
   *
   * @return
   */
  public String getThirdPartyName() {
    return thirdPartyName;
  }

  /**
   * Sets the name of the Third Party Server
   *
   * @param thirdPartyName
   */
  public void setThirdPartyName(String thirdPartyName) {
    this.thirdPartyName = thirdPartyName;
    for (SessionListener listener : this.sessionListeners)
      listener.thirdPartyPublicNameUpdated(thirdPartyName);
  }

  public void setSessionReady(){
    for(SessionListener listener : this.sessionListeners)
      listener.sessionReady();
  }

  /**
   * Gets the ID of the Session
   *
   * @return
   */
  public String getId() {
    if (id == null)
      return "";
    return id;
  }

  /**
   * Sets the ID of the Session
   *
   * @param id
   */
  public void setId(String id) {
    this.id = id;
    for (SessionListener listener : this.sessionListeners)
      listener.idUpdated(this.id);
  }

  /**
   * Gets the value of the Hash Salt
   *
   * @return
   */
  public String getHash() {
    return hash;
  }

  /**
   * Sets the value of the Hash Salt
   *
   * @param hash
   */
  public void setHash(String hash) {
    this.hash = hash;
    for (SessionListener listener : this.sessionListeners)
      listener.hashUpdated(this.hash);
  }

  /**
   * Gets the path to the Genotype File used by the Client
   *
   * @return
   */
  public String getClientGenotypeFilename() {
    return clientGenotypeFilename;
  }

  /**
   * Gets the number of variants in the Genotype File used by the Client
   *
   * @return
   */
  public int getClientGenotypeFileSize() {
    return clientGenotypeFileSize;
  }

  /**
   * Gets the value of the selected RPP dataset
   *
   * @return
   */
  public String getSelectedDataset() {
    return selectedDataset;
  }

  /**
   * Sets the value of the selected RPP dataset
   *
   * @param selectedDataset
   */
  public void setSelectedDataset(String selectedDataset) {
    this.selectedDataset = selectedDataset;
    for (SessionListener listener : this.sessionListeners)
      listener.selectedDatasetUpdated(this.selectedDataset);
  }

  /**
   * Gets the lists of Datasets available on the RPP (as one line)
   *
   * @return
   */
  public String getAvailableDatasets() {
    return availableDatasets;
  }

  /**
   * Sets the lists of Datasets available on the RPP (as one line)
   *
   * @param availableDatasets
   */
  public void setAvailableDatasets(String availableDatasets) {
    this.availableDatasets = availableDatasets;
    for (SessionListener listener : this.sessionListeners)
      listener.availableDatasetsUpdated(this.availableDatasets);
  }

  /**
   * Sets the path to the Genotype File used by the Client
   *
   * @param genotypeFile
   * @return TRUE - if the File exists and is a valid Genotype File
   * @throws IOException
   */
  public boolean setClientGenotypeFile(String genotypeFile) throws IOException {
    this.clientGenotypeFilename = genotypeFile;
    this.clientGenotypeFileSize = GenotypesFileHandler.getNumberOfLinesGenotypes(genotypeFile);
    if (this.clientGenotypeFileSize == -1)
      return false;
    for (SessionListener listener : this.sessionListeners)
      listener.clientGenotypeFilenameUpdated(this.clientGenotypeFilename, this.clientGenotypeFileSize);
    return true;
  }

  /**
   * Gets the value for the algorithm to execute and its trailing parameters
   *
   * @return
   */
  public String getAlgorithm() {
    return algorithm;
  }

  /**
   * Sets the value for the algorithm to execute and its trailing parameters
   *
   * @param algorithm
   */
  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
    for (SessionListener listener : this.sessionListeners)
      listener.algorithmUpdated(this.algorithm);
  }

  /**
   * Gets the value for the maximum Allele Frequency allowed in the variants selection
   *
   * @return
   */
  public double getMaxMAF() {
    return maxMAF;
  }

  /**
   * Sets the value for the maximum Allele Frequency allowed in the variants selection
   *
   * @param maxMAF
   */
  public void setMaxMAF(double maxMAF) {
    this.maxMAF = maxMAF;
    for (SessionListener listener : this.sessionListeners)
      listener.maxMAFUpdated(this.maxMAF);
  }
  
  /**
   * Gets the value for the maximum Allele Frequency allowed in the variants selection
   *
   * @return
   */
  public double getMaxMAFNFE() {
    return maxMAFNFE;
  }

  /**
   * Sets the value for the maximum Allele Frequency allowed in the variants selection
   *
   * @param maxMAFNFE
   */
  public void setMaxMAFNFE(double maxMAFNFE) {
    this.maxMAFNFE = maxMAFNFE;
    for (SessionListener listener : this.sessionListeners)
      listener.maxMAFNFEUpdated(this.maxMAFNFE);
  }

  /**
   * Gets the value for the Least Severe Consequence allowed in the variants selection
   *
   * @return
   */
  public String getLeastSevereConsequence() {
    return leastSevereConsequence;
  }

  /**
   * Sets the value for the Least Severe Consequence allowed in the variants selection
   *
   * @param leastSevereConsequence
   */
  public void setLeastSevereConsequence(String leastSevereConsequence) {
    this.leastSevereConsequence = leastSevereConsequence;
    for (SessionListener listener : this.sessionListeners)
      listener.leastSeverConsequenceUpdated(this.leastSevereConsequence);
  }

  public boolean getLimitToSNVs() {
    return this.limitToSNVs;
  }

  public void setLimitToSNVs(boolean limitToSNVs) {
    this.limitToSNVs = limitToSNVs;
    for (SessionListener listener : this.sessionListeners)
      listener.limitToSNVsUpdated(limitToSNVs);
  }

  public BedFile getBedFile() {
    return bedFile;
  }

  public void setBedFile(BedFile bedFile) {
    this.bedFile = bedFile;
  }

  public QCParam getQCParam() {
    return qcParam;
  }

  public void setQCParam(QCParam qcParam) {
    this.qcParam = qcParam;
  }

  /**
   * Gets the value of the Client AES Key
   *
   * @return
   */
  public String getAesKey() {
    return aesKey;
  }

  /**
   * Gets the Client's Public RSA Key
   *
   * @return
   */
  public PublicKey getClientPublicRSA() {
    return clientPublicRSA;
  }

  /**
   * Gets the Client's Private RSA Key
   *
   * @return
   */
  public PrivateKey getClientPrivateRSA() {
    return clientPrivateRSA;
  }

  /**
   * Gets the value of the Client Private RSA Key (as a one-line pem String)
   *
   * @return
   */
  public String getClientPrivateRSAAsString() {
    if (this.clientPrivateRSA == null)
      return null;
    return Crypto.bytes2OneLinePem(this.clientPrivateRSA.getEncoded());
  }

  /**
   * Gets the value of the Client Public RSA Key (as a one-line pem String)
   *
   * @return
   */
  public String getClientPublicRSAAsString() {
    if (this.clientPublicRSA == null)
      return null;
    return Crypto.bytes2OneLinePem(this.clientPublicRSA.getEncoded());
  }

  /**
   * Gets the value of the Third Party Server Public RSA Key (as a one-line pem String)
   *
   * @return
   */
  public String getThirdPartyPublicKeyAsString() {
    if (this.thirdPartyPublicKey == null)
      return null;
    return Crypto.bytes2OneLinePem(this.thirdPartyPublicKey.getEncoded());
  }

  /**
   * Sets the value of the Client's AES Key
   *
   * @param aes the Client's AES Key
   */
  private void setAESKey(String aes) {
    this.aesKey = aes;
    for (SessionListener listener : this.sessionListeners)
      listener.aesKeyUpdated(this.aesKey);
  }

  /**
   * Sets the value of the Client's Private RSA Key
   *
   * @param clientPublicRSA the Client's Private RSA Key
   */
  private void setClientPublicRSA(PublicKey clientPublicRSA) {
    this.clientPublicRSA = clientPublicRSA;
    for (SessionListener listener : this.sessionListeners)
      listener.clientPublicKeyUpdated(this.getClientPublicRSAAsString());
  }

  /**
   * Sets the value of the Client's Private RSA Key
   *
   * @param clientPrivateRSA the Client's Private RSA Key
   */
  private void setClientPrivateRSA(PrivateKey clientPrivateRSA) {
    this.clientPrivateRSA = clientPrivateRSA;
    for (SessionListener listener : this.sessionListeners)
      listener.clientPrivateKeyUpdated(this.getClientPrivateRSAAsString());
  }

  /**
   * Gets the value of the Third Party Public Key
   *
   * @return
   */
  public PublicKey getThirdPartyPublicKey() {
    return thirdPartyPublicKey;
  }

  /**
   * Sets the value of the Third Party Public Key (as a one-line pem String)
   *
   * @param thirdPartyPublicKey
   */
  public void setThirdPartyPublicKey(PublicKey thirdPartyPublicKey) {
    this.thirdPartyPublicKey = thirdPartyPublicKey;
    for (SessionListener listener : this.sessionListeners)
      listener.thirdPartyPublicKeyUpdated(this.getThirdPartyPublicKeyAsString());
  }

  /**
   * Gets the value for the last known RPP Status
   *
   * @return
   */
  public String getLastRPPStatus() {
    return lastRPPStatus;
  }

  /**
   * Sets the value for the last known RPP Status
   *
   * @param lastRPPStatus
   */
  public void setLastRPPStatus(String lastRPPStatus) {
    this.lastRPPStatus = lastRPPStatus;
    for (SessionListener listener : this.sessionListeners)
      listener.rppStatusUpdated(this.lastRPPStatus);
  }

  /**
   * Load a Session File into memory, as the current Session
   *
   * @param filename the name of the Session File
   * @throws fr.inserm.u1078.tludwig.privas.instances.ClientSession.SessionFileException
   * @throws java.io.IOException
   */
  public void load(String filename) throws SessionFileException, IOException { 
    this.init();
    this.setLastFilename(filename);
    BufferedReader in = new BufferedReader(new FileReader(filename));
    String line;
    StringBuilder err = new StringBuilder();
    boolean foundLimitToSNVs = false;
    Parser parser = new Parser();
    while ((line = in.readLine()) != null) {
      String[] f = (line + T).split(T, -1);
      switch (f[0]) {
        case FileFormat.SESSION_ID:
          this.setId(parser.parseSessionID(f[1]));
          break;
        case FileFormat.SESSION_PUBLIC:
          this.setClientPublicRSA(parser.parseClientPublicRSA(f[1]));
          break;
        case FileFormat.SESSION_THIRD_PARTY_KEY:
          this.setThirdPartyPublicKey(parser.parseThirdPartyPublicKey(f[1]));
          break;
        case FileFormat.SESSION_THIRD_PARTY_NAME:
          this.setThirdPartyName(f[1]);
          break;
        case FileFormat.SESSION_PRIVATE:
          this.setClientPrivateRSA(parser.parseClientPrivateRSA(f[1]));
          break;
        case FileFormat.SESSION_AES:
          this.setAESKey(f[1]);
          break;
        case FileFormat.SESSION_RPP:
          try {
            String[] g = f[1].split(":");
            this.setRPP(g[0], new Integer(g[1]));
          } catch (NumberFormatException e) {
            err.append(MSG.SS_KO_PORT);
          }
          break;
        case FileFormat.SESSION_HASH:
          this.setHash(f[1]);
          break;
        case FileFormat.SESSION_GENOTYPE:
          this.setClientGenotypeFile(f[1]);
          break;
        case FileFormat.SESSION_SELECTED_DATASET:
          this.setSelectedDataset(f[1]);
          break;
        case FileFormat.SESSION_AVAILABLE_DATASETS:
          this.setAvailableDatasets(f[1]);
          break;
        case FileFormat.SESSION_MAF:
          this.setMaxMAF(parser.parseMaxMAF(f[1]));
          break;
        case FileFormat.SESSION_MAF_NFE:
          this.setMaxMAFNFE(parser.parseMaxMAF(f[1]));
          break;
        case FileFormat.SESSION_CSQ:
          this.setLeastSevereConsequence(parser.parseConsequence(f[1]));
          break;
        case FileFormat.SESSION_LIMIT_SNV:
          this.setLimitToSNVs("true".equalsIgnoreCase(f[1]));
          foundLimitToSNVs = true;
          break;
        case FileFormat.SESSION_BEDFILE:
          this.bedFile = parser.parseBedFile(f[1]);
          break;
        case FileFormat.SESSION_STATUS:
          this.setLastRPPStatus(parser.parserRPPStatus(f[2]));//here it is f[2] because the value contains a TAB
          break;
        case FileFormat.SESSION_DICTIONARY:
          this.setGeneHashDictionary(stringToDictionary(f[1]));
          break;
        case FileFormat.SESSION_POSITIONS:
          this.setGenePositions(stringToPositions(f[1]));
          break;
        case FileFormat.SESSION_ALGORITHM:
          this.setAlgorithm(f[1]);
          break;
        default:
          throw new SessionFileException(MSG.cat(MSG.SS_UNK_KEY, f[0]));
      }
    }
    in.close();

    if (this.id == null)
      err.append(MSG.SS_NO_KEY(FileFormat.SESSION_ID, filename));
    if (this.hash == null)
      err.append(MSG.SS_NO_KEY(FileFormat.SESSION_HASH, filename));
    if (this.aesKey == null)
      err.append(MSG.SS_NO_KEY(FileFormat.SESSION_AES, filename));
    if (this.clientPublicRSA == null)
      err.append(MSG.SS_NO_KEY(FileFormat.SESSION_PUBLIC, filename));
    if (this.clientPrivateRSA == null)
      err.append(MSG.SS_NO_KEY(FileFormat.SESSION_PRIVATE, filename));
    if (this.thirdPartyPublicKey == null)
      err.append(MSG.SS_NO_KEY(FileFormat.SESSION_THIRD_PARTY_KEY, filename));
    if (this.thirdPartyName == null)
      err.append(MSG.SS_NO_KEY(FileFormat.SESSION_THIRD_PARTY_NAME, filename));
    if (this.clientGenotypeFilename == null)
      err.append(MSG.SS_NO_KEY(FileFormat.SESSION_GENOTYPE, filename));
    if (this.selectedDataset == null)
      err.append(MSG.SS_NO_KEY(FileFormat.SESSION_SELECTED_DATASET, filename));
    if (this.availableDatasets == null)
      err.append(MSG.SS_NO_KEY(FileFormat.SESSION_AVAILABLE_DATASETS, filename));
    if (this.maxMAF == -1)
      err.append(MSG.SS_NO_KEY(FileFormat.SESSION_MAF, filename));
    if (this.maxMAFNFE == -1)
      err.append(MSG.SS_NO_KEY(FileFormat.SESSION_MAF_NFE, filename));
    if (this.leastSevereConsequence == null)
      err.append(MSG.SS_NO_KEY(FileFormat.SESSION_CSQ, filename));
    if (!foundLimitToSNVs)
      err.append(MSG.SS_NO_KEY(FileFormat.SESSION_LIMIT_SNV, filename));
    if (this.bedFile == null)
      err.append(MSG.SS_NO_KEY(FileFormat.SESSION_BEDFILE, filename));
    if (this.rppAddress == null)
      err.append(MSG.SS_NO_KEY(FileFormat.SESSION_RPP, filename));
    if (this.geneHashDictionary == null)
      err.append(MSG.SS_NO_KEY(FileFormat.SESSION_DICTIONARY, filename));
    if (this.genePositions == null)
      err.append(MSG.SS_NO_KEY(FileFormat.SESSION_POSITIONS, filename));
    if (this.algorithm == null)
      err.append(MSG.SS_NO_KEY(FileFormat.SESSION_ALGORITHM, filename));

    if (err.length() > 1)
      throw new SessionFileException(err.substring(1));
  }

  private static class Parser {

    private String parseSessionID(String input) throws SessionFileException {
      if (input == null || input.length() != 20)
        throw new SessionFileException(MSG.cat(MSG.SS_PARSE_SESSION_ID, input));
      return input;
    }

    private PublicKey parseClientPublicRSA(String input) throws SessionFileException {
      try {
        return Crypto.buildPublicRSAKey(input);
      } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
        throw new SessionFileException(MSG.cat(MSG.SS_PARSE_CLIENT_PUBLIC_RSA, input), ex);
      }
    }

    private PublicKey parseThirdPartyPublicKey(String input) throws SessionFileException {
      try {
        return Crypto.buildPublicRSAKey(input);
      } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
        throw new SessionFileException(MSG.cat(MSG.SS_PARSE_TPS_PUBLIC_RSA, input), ex);
      }
    }

    private double parseMaxMAF(String input) throws SessionFileException {
      try {
        return new Double(input);
      } catch (NumberFormatException ex) {
        throw new SessionFileException(MSG.cat(MSG.SS_PARSE_MAX_MAF, input), ex);
      }
    }

    private String parseConsequence(String input) throws SessionFileException {
      if (!Arrays.asList(Constants.VEP_CONSEQUENCES).contains(input))
        throw new SessionFileException(MSG.cat(MSG.SS_PARSE_CONSEQUENCE, input));
      return input;
    }

    private BedFile parseBedFile(String input) throws SessionFileException {
      try {
        return BedFile.deserialize(input);
      } catch (BedRegion.BedRegionException ex) {
        throw new SessionFileException(MSG.cat(MSG.SS_PARSE_BED_FILE, input), ex);
      }
    }

    private PrivateKey parseClientPrivateRSA(String input) throws SessionFileException {
      try {
        return Crypto.buildPrivateRSAKey(input);
      } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
        throw new SessionFileException(MSG.cat(MSG.SS_PARSE_CLIENT_PRIVATE_RSA, input), ex);
      }
    }

    private String parserRPPStatus(String string) {
      String[] f = string.split("\\s+", -1);
      return f[f.length-1];
    }
  }

  public static class SessionFileException extends Exception {

    public SessionFileException() {
      super();
    }

    public SessionFileException(String message) {
      super(message);
    }

    public SessionFileException(String message, Throwable cause) {
      super(message, cause);
    }

    public SessionFileException(Throwable cause) {
      super(cause);
    }
  }

  /**
   * Save the current Session to a File
   *
   * @param filename the name of the Session File
   * @throws Exception
   */
  public void save(String filename) throws Exception {
    if (id == null)
      throw new Exception(MSG.SS_UNDEFINED);
    PrintWriter out = new PrintWriter(new FileWriter(filename));
    out.println(FileFormat.SESSION_ID + T + this.id);
    out.println(FileFormat.SESSION_PUBLIC + T + this.getClientPublicRSAAsString());
    out.println(FileFormat.SESSION_PRIVATE + T + this.getClientPrivateRSAAsString());
    out.println(FileFormat.SESSION_THIRD_PARTY_NAME + T + this.thirdPartyName);
    out.println(FileFormat.SESSION_THIRD_PARTY_KEY + T + this.getThirdPartyPublicKeyAsString());
    out.println(FileFormat.SESSION_AES + T + this.aesKey);
    out.println(FileFormat.SESSION_RPP + T + this.rppAddress + ":" + this.rppPort);
    out.println(FileFormat.SESSION_HASH + T + this.hash);
    out.println(FileFormat.SESSION_GENOTYPE + T + this.clientGenotypeFilename);
    out.println(FileFormat.SESSION_AVAILABLE_DATASETS + T + this.availableDatasets);
    out.println(FileFormat.SESSION_SELECTED_DATASET + T + this.selectedDataset);
    out.println(FileFormat.SESSION_MAF + T + this.maxMAF);
    out.println(FileFormat.SESSION_MAF_NFE + T + this.maxMAFNFE);
    out.println(FileFormat.SESSION_CSQ + T + this.leastSevereConsequence);
    out.println(FileFormat.SESSION_LIMIT_SNV + T + this.limitToSNVs);
    out.println(FileFormat.SESSION_BEDFILE + T + this.bedFile.serialize());
    out.println(FileFormat.SESSION_STATUS + T + this.lastRPPStatus);
    out.println(FileFormat.SESSION_DICTIONARY + T + dictionaryToString(this.geneHashDictionary));
    out.println(FileFormat.SESSION_POSITIONS + T + positionsToString(this.genePositions));
    out.println(FileFormat.SESSION_ALGORITHM + T + this.algorithm);
    out.close();
    this.setLastFilename(filename);
  }

  /**
   * Is the saved file up-to-date with the Session in memory ?
   *
   * @return TRUE - if the file successfully reflects the Session
   */
  boolean isSaved() {
    try {
      BufferedReader in = new BufferedReader(new FileReader(this.getLastFilename()));
      String line;
      int check = 0;
      while ((line = in.readLine()) != null) {
        String[] f = line.split(T);
        switch (f[0]) {
          case FileFormat.SESSION_ID:
            if (f[1].equals(this.getId()))
              check++;
            else
              return false;
            break;
          case FileFormat.SESSION_PUBLIC:
            if (f[1].equals(this.getClientPublicRSAAsString()))
              check++;
            else
              return false;
            break;
          case FileFormat.SESSION_THIRD_PARTY_KEY:
            if (f[1].equals(this.getThirdPartyPublicKeyAsString()))
              check++;
            else
              return false;
            break;
          case FileFormat.SESSION_PRIVATE:
            if (f[1].equals(this.getClientPrivateRSAAsString()))
              check++;
            else
              return false;
            break;
          case FileFormat.SESSION_AES:
            if (f[1].equals(this.getAesKey()))
              check++;
            else
              return false;
            break;
          case FileFormat.SESSION_RPP:
            if (f[1].equals(this.getRPP() + ":" + this.getPort()))
              check++;
            else
              return false;
            break;
          case FileFormat.SESSION_HASH:
            if (f[1].equals(this.getHash()))
              check++;
            else
              return false;
            break;
          case FileFormat.SESSION_GENOTYPE:
            if (f[1].equals(this.getClientGenotypeFilename()))
              check++;
            else
              return false;
            break;
          case FileFormat.SESSION_MAF:
            if (f[1].equals(this.getMaxMAF() + ""))
              check++;
            else
              return false;
            break;
          case FileFormat.SESSION_CSQ:
            if (f[1].equals(this.getLeastSevereConsequence() + ""))
              check++;
            else
              return false;
            break;
        }
      }
      in.close();
      return check == 10;
    } catch (IOException e) {
      return false;
    }
  }

  /**
   * Convert a String from a Session file to a Map of the Hashed Gene names (Keys) and their clear text name (values)
   *
   * @param line the line from the Session File
   * @return
   */
  private static HashMap<String, String> stringToDictionary(String line) {
    HashMap<String, String> ret = new HashMap<>();
    for (String kv : line.split("\\|", -1)) {
      String[] f = kv.split(":");
      if (f.length > 1)
        ret.put(f[0], f[1]);
      else
        ret.put(f[0], Constants.SS_NO_GENE);
    }
    return ret;
  }

  /**
   * Converts a Map of the Hashed Gene names (Keys) and their clear text name (values) to a Single String. (When saving the session)
   *
   * @param dictionary Map of Genes to their positions
   * @return
   */
  private static String dictionaryToString(HashMap<String, String> dictionary) {
    StringBuilder sb = new StringBuilder();
    for (String key : dictionary.keySet()) {
      sb.append("|");
      sb.append(key);
      sb.append(":");
      sb.append(dictionary.get(key));
    }
    return sb.substring(1);
  }

  /**
   * Convert a String from a Session file to a Map of the Gene names (Keys) and positions (values)
   *
   * @param line the line from the Session File
   * @return
   */
  private static HashMap<String, String> stringToPositions(String line) {
    HashMap<String, String> ret = new HashMap<>();
    for (String kv : line.split("\\|", -1)) {
      String[] f = kv.split("\\*");
      if (f.length > 1)
        ret.put(f[0], f[1]);
    }
    return ret;
  }

  /**
   * Converts a Map of the Gene names (Keys) and positions (values) to a Single String. (When saving the session)
   *
   * @param positions Map of Genes to their positions
   * @return
   */
  private static String positionsToString(HashMap<String, String> positions) {
    StringBuilder sb = new StringBuilder();
    for (String key : positions.keySet()) {
      sb.append("|");
      sb.append(key);
      sb.append("*");
      sb.append(positions.get(key));
    }
    return sb.substring(1);
  }

  /**
   * Gets the clear text gene name and its position
   *
   * @param hashed the Hashed Gene Name
   * @return the name of the gene and its position separated by a tabulation
   */
  public String unhashAndPosition(String hashed) {
    String gene = this.geneHashDictionary.get(hashed);
    String pos = null;
    if (gene == null)
      gene = Constants.GENE_UNKNOWN;
    else
      pos = this.genePositions.get(gene);
    if (pos == null)
      pos = Constants.POS_UNKNOWN;
    return gene + T + pos;
  }
}
