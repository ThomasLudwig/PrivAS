package fr.inserm.u1078.tludwig.privas.instances;

import fr.inserm.u1078.tludwig.privas.constants.Constants;
import fr.inserm.u1078.tludwig.privas.constants.FileFormat;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.constants.Parameters;
import fr.inserm.u1078.tludwig.privas.listener.ProgressListener;
import fr.inserm.u1078.tludwig.privas.utils.BedFile;
import fr.inserm.u1078.tludwig.privas.utils.BedRegion;
import fr.inserm.u1078.tludwig.privas.utils.GenotypesFileHandler;
import fr.inserm.u1078.tludwig.privas.utils.UniversalReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * SessionProcessor handles the Session Management for an RPP.
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
 * <p>
 * Javadoc complete on 2019-08-08
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2020-01-16
 */
public class RPPSessionProcessor {

  /**
   * The RPP Server to which this processor is attached
   */
  private final RPP rpp;

  /**
   * the Session ID
   */
  private final String session;
  /**
   * the selected Dataset name (for the extraction)
   */
  private final String datasetName;
  /**
   * the Maximum Allele Frequencies threshold (for the extraction)
   */
  private final double maxMaf;
  /**
   * the Maximum Allele Frequencies threshold (for the extraction)
   */
  private final double maxMafNFE;
  /**
   * the least severe vep consequence (for the extraction)
   */
  private final String minCsq;
  /**
   * are the variants limited to SNVs ? (for the extraction)
   */
  private final boolean limitToSNVs;
  /**
   * bed file listing all well covered positions
   */
  private final BedFile bed;
  /**
   * the Hash Salt (to hash fields during the extraction)
   */
  private final String kHash;

  /**
   * were Client Data received
   */
  private boolean clientDataReceived;
  /**
   * were RPP Data extracted
   */
  private boolean rppExtracted;

  /**
   * has extraction failed
   */
  private String rppExtractionFailed;

  /**
   * was TPS triggered
   */
  private boolean tpsTriggered;

  /**
   * Constructor for a new session
   *
   * @param session     the Session ID
   * @param datasetName the selected Dataset name (for the extraction)
   * @param maxMaf      the Maximum Allele Frequencies threshold (for the extraction)
   * @param maxMafNFE   the Maximum Allele Frequencies threshold (for the extraction)
   * @param minCsq      the least severe vep consequence (for the extraction)
   * @param limitToSNVs is variant selection limited to SNVs ?
   * @param kHash       the Hash Salt (to hash fields during the extraction)
   */
  RPPSessionProcessor(RPP rpp, String session, String datasetName, double maxMaf, double maxMafNFE, String minCsq, boolean limitToSNVs, BedFile bed, String kHash) {
    this.rpp = rpp;
    this.session = session;
    this.datasetName = datasetName;
    this.maxMaf = maxMaf;
    this.maxMafNFE = maxMafNFE;
    this.minCsq = minCsq;
    this.limitToSNVs = limitToSNVs;
    this.bed = bed;
    this.kHash = kHash;
    rpp.logDebug("SessionProcessor created");
  }

  RPPSessionProcessor(RPP rpp, String session) throws IOException, BedRegion.BedRegionException, NumberFormatException {
    this.rpp = rpp;
    this.session = session;
    UniversalReader in = new UniversalReader(rpp.getFilenameFor(session, FileFormat.FILE_SESSION_PARAMETERS));
    this.datasetName = in.readLine();
    this.kHash = in.readLine();
    this.maxMaf = new Double(in.readLine());
    this.maxMafNFE = new Double(in.readLine());
    this.minCsq = in.readLine();
    this.limitToSNVs = "TRUE".equalsIgnoreCase(in.readLine());
    this.bed = BedFile.deserialize(in.readLine());
    in.close();
    rpp.logDebug("SessionProcessor restored");
  }

  void serialize() {
    try {
      PrintWriter out = new PrintWriter(new FileWriter(rpp.getFilenameFor(session, FileFormat.FILE_SESSION_PARAMETERS)));
      out.println(this.datasetName);
      out.println(this.kHash);
      out.println(this.maxMaf);
      out.println(this.maxMafNFE);
      out.println(this.minCsq);
      out.println(this.limitToSNVs);
      out.println(this.bed.serialize());
      out.close();
    } catch (IOException e) {
      rpp.logError("Unable to save session parameters");
      rpp.logError(e);
    }
  }

  public void init() {
    clientDataReceived = rpp.checkClientFile(session);
    rppExtracted = rpp.checkRPPFile(session);
    rppExtractionFailed = null;
    RPPStatus status = rpp.getStatus(session);

    tpsTriggered = false;
    // is TPS currently active and needs monitoring ?
    boolean needTPSMonitoring = false;
    
    switch (status.getState()) {//TODO restore works almost well, more test are need dans RPP crashes and TPS hasn't produces its first message
      //maybe are a new TPS status, data received, job submited
      case NEW_SESSION:
      case WAITING_BOTH:
      case WAITING_CLIENT:
      case WAITING_RPP:
      case TPS_UNKNOWN: //probably the data were not received
        tpsTriggered = false;
        needTPSMonitoring = false;
        break;
      case UNKNOWN:
      case ERROR:
      case TPS_SENDING:
      case TPS_PENDING:
      case TPS_RUNNING:
      case TPS_ERROR: //intermitent error might need status update
        tpsTriggered = true;
        needTPSMonitoring = true;
        break;
      case NO_SESSION:
      case TPS_DONE:
      case RESULTS_AVAILABLE:
      case EXPIRED:
        tpsTriggered = true;
        needTPSMonitoring = false;
        break;
      default: 
    }

    if (!tpsTriggered)
      this.waitForData();
    if (!this.rppExtracted)
      this.startExtraction();
    if (needTPSMonitoring)
      this.startMonitoringTPS();
    if (status.getState() == RPPStatus.State.TPS_DONE)
      this.getResults();
  }

  public void waitForData() {
    rpp.submitRepeating(() -> {
      if (rppExtractionFailed != null) {
        rpp.setStatus(session, RPPStatus.error(rppExtractionFailed));
        rpp.stopThread();
      }
      if (clientDataReceived && rppExtracted) {
        sendDataAndStart();
        startMonitoringTPS();
        rpp.stopThread();
      }
      if (!clientDataReceived && rpp.checkClientFile(session)) {//wait for clientData
        rpp.logInfo(MSG.done(MSG.SP_OK_CLIENT, session));
        rpp.setStatus(session, RPPStatus.clientDataReceived(rpp.getStatus(session)));
        clientDataReceived = true;
      }
    }, Parameters.RPP_RECEIVER_REFRESH_DELAY);
  }

  /**
   * Starts a Sender Thread, which is responsible for sending all the Data to the Third Party Server and launching the job. This is a Non looping thread.
   * <p>
   * Then starts a ThirdPartyMonitor Thread, which is responsible for monitoring the Status of the Third Party Server.
   * <p>
   */
  private void sendDataAndStart() {
    //Send Data
    rpp.submitNow(() -> {
      rpp.sendDataAndStart(session);
      this.tpsTriggered = true;
      //rpp.setStatus(session, RPPStatus.pending(-1)); //no the status is placed be the monitoring thread, otherwise data might not be there yet
    });
  }

  /**
   * It Periodically gets via ssh the serialized
   * <p>
   * TPSStatus file Updates the RRP Status according to the TPSStatus
   */
  private void startMonitoringTPS() {
    //Start Monitoring Thrid Party
    rpp.submitRepeating(() -> {
      TPStatus tpStatus = rpp.getThirdPartyStatus(session);
      switch (tpStatus.getState()) {
        case PENDING:
          int before = -1;
          try {
            before = new Integer(tpStatus.getDetails());
          } catch (NumberFormatException e) {
            //Nothing
          }
          rpp.setStatus(session, RPPStatus.pending(before));
          break;
        case RUNNING:
          rpp.setStatus(session, RPPStatus.running(tpStatus.getDetails()));
          break;
        case DONE:
          rpp.setStatus(session, RPPStatus.retrieving(tpStatus.getDetails()));
          getResults();
          rpp.stopThread();
        case ERROR:
          rpp.setStatus(session, RPPStatus.tpsError(tpStatus.getDetails()));
          rpp.stopThread();
        case UNKNOWN:
          rpp.setStatus(session, RPPStatus.tpsUnknown());
          break;
        default:
      }
    }, Parameters.RPP_THIRD_PARTY_REFRESH_DELAY);
  }

  /**
   * Starts a ResultGetter Thread, which is responsible for Getting Results from the Third Party Server Periodically checks of the existence of the Results
   * file on the Third Party Server. Once the file is available, gets it via ssh
   */
  private void getResults() {
    rpp.getThirdPartyResults(session);
    rpp.submitRepeating(() -> {
      if (rpp.checkResultsFile(session)) {
        rpp.setStatus(session, RPPStatus.available());
        rpp.stopThread();
      }
    }, Parameters.SP_RESULT_GETTER_REFRESH_DELAY);
  }

  /**
   * Starts an Extractor Thread, which is responsible for extracting RPP Data according to the selected filters
   */
  private void startExtraction() {
    String filename = rpp.getGenotypeFilenames().get(datasetName);
    rpp.submitNow(() -> {
      ProgressListener progress = percent -> rpp.setStatus(session, RPPStatus.extracting(rpp.getStatus(session), percent));
      try {
        File d = new File(rpp.getFilenameFor(session, null /* directory */));
        if (!d.exists() && !d.mkdirs())
          rpp.logError(MSG.cat(MSG.FAIL_MKDIR, d.getAbsolutePath()));
        rpp.logInfo(MSG.action(MSG.SP_RPP, session));
        int nbLines = GenotypesFileHandler.extractGenotypesToFile(filename, rpp.getFilenameFor(session, FileFormat.FILE_RPP_DATA), rpp.getGenotypeFileSize(filename), maxMaf, maxMafNFE, minCsq, limitToSNVs, bed, kHash, progress);
        PrintWriter out = new PrintWriter(new FileWriter(rpp.getFilenameFor(session, FileFormat.FILE_RPP_DATA_OK)));
        out.println(nbLines);
        out.close();
        if(nbLines == 0) {
          rpp.logInfo(MSG.done(MSG.SP_EMPTY_RPP, session));
          rpp.setStatus(session, RPPStatus.rppDataEmpty());
        } else {
          rpp.logInfo(MSG.done(MSG.SP_OK_RPP, session));
          rpp.setStatus(session, RPPStatus.rppDataExtracted(rpp.getStatus(session)));
        }
        rppExtracted = true;
      } catch (GenotypesFileHandler.GenotypeFileException | IOException | InvalidKeyException | NoSuchAlgorithmException ex) {
        rpp.logError(MSG.done(MSG.SP_KO_RPP, ex));
        rpp.logError(ex);
        rppExtractionFailed = ex.getMessage();
      }
    });
  }
}