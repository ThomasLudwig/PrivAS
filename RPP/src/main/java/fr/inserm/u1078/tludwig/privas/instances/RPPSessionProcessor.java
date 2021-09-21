package fr.inserm.u1078.tludwig.privas.instances;

import fr.inserm.u1078.tludwig.privas.constants.Constants;
import fr.inserm.u1078.tludwig.privas.constants.FileFormat;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.constants.Parameters;
import fr.inserm.u1078.tludwig.privas.listener.ProgressListener;
import fr.inserm.u1078.tludwig.privas.utils.*;
import fr.inserm.u1078.tludwig.privas.utils.qc.QCException;
import fr.inserm.u1078.tludwig.privas.utils.qc.QCParam;
import fr.inserm.u1078.tludwig.privas.utils.qc.QualityControl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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

  public static final String DIRECTORY = null;

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
   * the selected version of GnomAD
   */
  private final String gnomADVersion;
  /**
   * the Maximum Allele Frequencies threshold (for the extraction)
   */
  private final double maxMaf;
  /**
   * the selected subpopulation
   */
  private final String subpop;
  /**
   * the Maximum Allele in the subpopulation Frequencies threshold (for the extraction)
   */
  private final double maxMafSubpop;
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
   * Quality Control Parameters;
   */
  private final QCParam qcParam;
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

  private final List<TPStatus> tpStatuses;

  private int tpsStatusesSentToClient;

  /**
   * Constructor for a new session
   *
   * @param session       the Session ID
   * @param datasetName   the selected Dataset name (for the extraction)
   * @param version       the selected version of GnomAD
   * @param maxMaf        the Maximum Allele Frequencies threshold (for the extraction)
   * @param subpop        the selected subpopulation
   * @param maxMafSubpop  the Maximum Allele Frequencies threshold in the subpopulation (for the extraction)
   * @param minCsq        the least severe vep consequence (for the extraction)
   * @param limitToSNVs   is variant selection limited to SNVs ?
   * @param kHash         the Hash Salt (to hash fields during the extraction)
   */
  RPPSessionProcessor(RPP rpp, String session, String datasetName, String version, double maxMaf, String subpop, double maxMafSubpop, String minCsq, boolean limitToSNVs, BedFile bed, QCParam qcParam, String kHash) {
    this.rpp = rpp;
    this.session = session;
    this.datasetName = datasetName;
    this.gnomADVersion = version;
    this.maxMaf = maxMaf;
    this.subpop = subpop;
    this.maxMafSubpop = maxMafSubpop;
    this.minCsq = minCsq;
    this.limitToSNVs = limitToSNVs;
    this.bed = bed;
    this.qcParam = qcParam;
    this.kHash = kHash;
    this.tpStatuses = new ArrayList<>();
    rpp.logDebug(MSG.RPP_DEBUG_CREATED);
  }

  //TODO check/test this and it's opposite (serial)
  RPPSessionProcessor(RPP rpp, String session) throws IOException, BedRegion.BedRegionException, QCException, NumberFormatException {
    this.rpp = rpp;
    this.session = session;
    UniversalReader in = new UniversalReader(rpp.getFilenameFor(session, FileFormat.FILE_SESSION_PARAMETERS));
    this.datasetName = in.readLine();
    this.gnomADVersion = in.readLine();
    this.kHash = in.readLine();
    this.maxMaf = new Double(in.readLine());
    this.subpop = in.readLine();
    this.maxMafSubpop = new Double(in.readLine());
    this.minCsq = in.readLine();
    this.limitToSNVs = Constants.parseBoolean(in.readLine());
    this.bed = BedFile.deserialize(in.readLine());
    this.qcParam = QCParam.deserialize(in.readLine());
    in.close();
    this.tpStatuses = new ArrayList<>();
    rpp.logDebug(MSG.RPP_DEBUG_RESTORED);
  }

  void serialize() {
    try {
      PrintWriter out = new PrintWriter(new FileWriter(rpp.getFilenameFor(session, FileFormat.FILE_SESSION_PARAMETERS)));
      out.println(this.datasetName);
      out.println(this.gnomADVersion);
      out.println(this.kHash);
      out.println(this.maxMaf);
      out.println(this.subpop);
      out.println(this.maxMafSubpop);
      out.println(this.minCsq);
      out.println(this.limitToSNVs);
      out.println(this.bed.serialize());
      out.println(this.qcParam.serialize());
      out.close();
    } catch (IOException e) {
      rpp.logError(MSG.RPP_SAVE_KO);
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
    
    switch (status.getState()) {//TODO restore works almost well, more test are needed : RPP crashes and TPS hasn't produces its first message
      //maybe are a new TPS status, data received, job submitted
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
      case TPS_ERROR: //intermittent error might need status update
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
    if (needTPSMonitoring) {
      this.startMonitoringTPS();
      this.startForwardingTPSToClient();
    }
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
        this.startForwardingTPSToClient();
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

  private void startForwardingTPSToClient() {
    tpsStatusesSentToClient = 0;
    rpp.submitRepeating(() -> {
      if(tpsStatusesSentToClient < this.tpStatuses.size())
        rpp.sendTPSStatusToClient(session, this.tpStatuses.get(tpsStatusesSentToClient++));
    }, Parameters.RPP_TP_TO_CLIENT_REFRESH_DELAY);
  }

  /**
   * It Periodically gets via ssh the serialized
   * <p>
   * TPSStatus file Updates the RRP Status according to the TPSStatus
   */
  private void startMonitoringTPS() {
    //Start Monitoring Third Party
    rpp.submitRepeating(() -> {
      try{
        List<TPStatus> latestTPStatuses = rpp.getThirdPartyStatuses(session, this.tpStatuses.size());
        if(!latestTPStatuses.isEmpty()) {
          this.tpStatuses.addAll(latestTPStatuses);
          TPStatus tpStatus = latestTPStatuses.get(latestTPStatuses.size() - 1);
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
            case STARTED:
            case RUNNING:
              rpp.setStatus(session, RPPStatus.running(tpStatus.getDetails()));
              break;
            case DONE:
              rpp.setStatus(session, RPPStatus.retrieving(tpStatus.getDetails()));
              getResults();
              rpp.stopThread();
              break;
            case ERROR:
              rpp.setStatus(session, RPPStatus.tpsError(tpStatus.getDetails()));
              rpp.stopThread();
              break;
            default:
          }
        }
      } catch(Exception ex){
        rpp.setStatus(session, RPPStatus.tpsUnreachable(ex.getMessage()));
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

    rpp.submitNow(() -> {
      RPPDataset rppDataset = rpp.getRPPDataset(datasetName);
      String genotypeFilename = rppDataset.getGenotypeFilename(qcParam, gnomADVersion);
      String excludedVariantFilename = rppDataset.getExcludedVariantFilename(qcParam);

      boolean exists = FileUtils.exists(genotypeFilename) && FileUtils.exists(excludedVariantFilename);


      ProgressListener progress = percent -> rpp.setStatus(session, RPPStatus.extracting(rpp.getStatus(session), percent));
      long nbRec;
      try {
        File d = new File(rpp.getFilenameFor(session, DIRECTORY));
        if (!d.exists() && !d.mkdirs())
          rpp.logError(MSG.cat(MSG.FAIL_MKDIR, d.getAbsolutePath()));
        String[] genoExc = {genotypeFilename, excludedVariantFilename};
        if(exists)
          rpp.logDebug(MSG.RPP_FILE_FOUND(genoExc));
        else
          rpp.logDebug(MSG.RPP_FILE_MISSING(genoExc));
        if(exists){
          nbRec = rpp.getRPPDataset(datasetName).getGenotypeSize(qcParam, gnomADVersion);
        } else {
          String inputVCFFilename = rppDataset.getVCFFilename();
          String qcVCFFilename = rppDataset.getQCVCFFilename(qcParam);
          if(new File(qcVCFFilename).exists()) {
            rpp.logDebug(MSG.RPP_FILE_FOUND(qcVCFFilename));
          } else {
            //applyQC
            rpp.logInfo(MSG.action(MSG.SP_QC, session));
            QualityControl.applyQC(inputVCFFilename, this.qcParam, qcVCFFilename, excludedVariantFilename);
          }
          //convertToGenotype
          rpp.logInfo(MSG.action(MSG.SP_CONVERT, session));
          nbRec = GenotypesFileHandler.convertVCF2Genotypes(qcVCFFilename, rpp.getGnomADReferences().get(gnomADVersion), rpp).getSize();
        }
        //Extract genotype
        rpp.logInfo(MSG.action(MSG.SP_RPP, session));
        int nbLines = GenotypesFileHandler.extractGenotypesToFile(
                genotypeFilename,
                rpp.getFilenameFor(session, FileFormat.FILE_RPP_DATA),
                nbRec,
                maxMaf,
                subpop,
                maxMafSubpop,
                minCsq,
                limitToSNVs,
                bed,
                kHash,
                rpp,
                progress
        );
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
      } catch (GenotypesFileHandler.GenotypeFileException | IOException ex) {
        rpp.logError(MSG.done(MSG.SP_KO_RPP, ex));
        rpp.logError(ex);
        rppExtractionFailed = ex.getMessage();
      }
    });
  }
}
