package fr.inserm.u1078.tludwig.privas.constants;

import fr.inserm.u1078.tludwig.privas.Main;
import fr.inserm.u1078.tludwig.privas.messages.SessionMessage;
import fr.inserm.u1078.tludwig.privas.instances.RPPStatus;
import fr.inserm.u1078.tludwig.privas.messages.Message;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Class to store of constants, such as file name. This allows a better communication among the different parties.
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-05-10
 *
 * Javadoc will not be exhaustive for this class
 */
public class MSG {

  private static final String T = "\t";
  private static final String N = "\n";

  //Program name
  public static final String TITLE = "PrivAS";

  //Command line arguments
  //public static final String ARG_GUI = "gui";
  //public static final String ARG_RPPS = "rpp";
  //public static final String ARG_THIRDPARTY = "thirdparty";
  public static final String ARG_KEYGEN = "--keygen";
  public static final String ARG_DEBUG = "--debug";
  public static final String ARG_DOC = "--doc";
  public static final String ARG_CONVERT_VCF = "--vcf2genotype";
  public static final String ARG_ATTACK = "--attack";
  public static final String ARG_WSSKEY = "--wss";
  public static final String ARG_RANKSUMKEY = "--rank";
  public static final String ARG_QC = "--qc";
  public static final String ARG_QC_CONV = "--qc-convert";

  //general
  public static final String FAIL_MKDIR = "Could not create directory";

  public static final String MSG_MISSING_PARAMETER = "Missing value for parameter";

  //RPP
  public static final String RPP_DESC_PORT = "Port Number";
  public static final String RPP_DESC_DATA = "Data File";
  public static final String RPP_DESC_RPP_SESSION_DIR = "RPP Session Directory";
  public static final String RPP_DESC_RPP_EXPIRED_SESSION_LIST = "RPP Expired Session List";
  public static final String RPP_DESC_TPS_NAME = "Third-Party Server Name";
  public static final String RPP_DESC_TPS_ADDRESS = "Third-Party Server Address";
  public static final String RPP_DESC_TPS_USER = "Third-Party Server Username (ssh)";
  public static final String RPP_DESC_TPS_LAUNCH_COMMAND = "Third-Party Server Launch_command";
  public static final String RPP_DESC_TPS_GETKEY_COMMAND = "Third-Party Server Get_Key_command";
  public static final String RPP_DESC_TPS_SESSION_DIR = "Third-Party Server session directory";
  public static final String RPP_DESC_CONNECTION_LOG = "File containing the connection log";
  public static final String RPP_DESC_BACKLIST = "List of blacklisted addresses or ranges";
  public static final String RPP_DESC_WHITELIST = "List of whitelisted addresses or ranges";
  public static final String RPP_DESC_MAX_PER_DAY = "Maximum number of connections per day from the same address";
  public static final String RPP_DESC_MAX_PER_WEEK = "Maximum number of connections per week from the same address";
  public static final String RPP_DESC_MAX_PER_MONTH = "Maximum number of connections per month from the same address";

  public static final String RPP_SYNTAX_PORT = FileFormat.RPP_TAG_PORT + "<TAB>Integer";
  public static final String RPP_SYNTAX_DATA = FileFormat.RPP_TAG_DATA + "<TAB>Name1:datafile1(.gz),Name2:datafile2(.gz),...,NameN:datafileN(.gz)";
  public static final String RPP_SYNTAX_RPP_SESSION_DIR = FileFormat.RPP_TAG_RPP_SESSION_DIR + "<TAB>path_to_directory";
  public static final String RPP_SYNTAX_RPP_EXPIRED_SESSION_LIST = FileFormat.RPP_TAG_RRP_EXPIRED_SESSION + "<TAB>path_to_file";
  public static final String RPP_SYNTAX_TPS_NAME = FileFormat.RPP_TAG_TPS_NAME + "<TAB>name of the server";
  public static final String RPP_SYNTAX_TPS_ADDRESS = FileFormat.RPP_TAG_TPS_ADDRESS + "<TAB>address_or_IP";
  public static final String RPP_SYNTAX_TPS_USER = FileFormat.RPP_TAG_TPS_USER + "<TAB>username";
  public static final String RPP_SYNTAX_TPS_LAUNCH_COMMAND = FileFormat.RPP_TAG_TPS_LAUNCH_COMMAND + "<TAB>launch_command_or_path_to_script";
  public static final String RPP_SYNTAX_TPS_GETKEY_COMMAND = FileFormat.RPP_TAG_TPS_GETKEY_COMMAND + "<TAB>get_rsa_public_key_command_or_path_to_script";
  public static final String RPP_SYNTAX_TPS_SESSION_DIR = FileFormat.RPP_TAG_TPS_SESSION_DIR + "<TAB>path_to_directory";
  public static final String RPP_SYNTAX_CONNECTION_LOG = FileFormat.RPP_TAG_CONNECTION_LOG + "<TAB>path_to_file";
  public static final String RPP_SYNTAX_BLACKLIST = FileFormat.RPP_TAG_BLACKLIST + "<TAB>comma-separated_addresses_or_ranges";
  public static final String RPP_SYNTAX_WHITELIST = FileFormat.RPP_TAG_WHITELIST + "<TAB>comma-separated_addresses_or_ranges";
  public static final String RPP_SYNTAX_MAX_PER_DAY = FileFormat.RPP_TAG_MAX_PER_DAY + "<TAB>number_of_connections (0 for unlimited)";
  public static final String RPP_SYNTAX_MAX_PER_WEEK = FileFormat.RPP_TAG_MAX_PER_WEEK + "<TAB>number_of_connections (0 for unlimited)";
  public static final String RPP_SYNTAX_MAX_PER_MONTH = FileFormat.RPP_TAG_MAX_PER_MONTH + "<TAB>number_of_connections (0 for unlimited)";

  public static final String RPP_ERR_CONFIG = "Unable to read Configuration File";
  public static final String RPP_ERR_DELETE = "Failed to delete";
  public static final String RPP_ERR_INVALID_ID = "Invalid session ID :";
  public static final String RPP_ERR_SAVE_STATUS = "Unable to save status for session ";
  public static final String RPP_ERR_SEND_STATUS = "Unable to send status for session ";
  public static final String RPP_ERR_SEND_STATUS_CLIENT_LEFT = "Client has left, " + RPP_ERR_SEND_STATUS;
  public static final String RPP_ERR_SEND_AFTER_BINDING = "Unable to send status after monitor binding";
  public static final String RPP_ERR_SEND_TPS = "Unable to send TPS status to Client";
  public static final String RPP_ERR_RPP_THREAD = "Failed to open a new listener:";
  public static final String RPP_ERR_SOCKET = "Unable to read message from client:";
  public static final String RPP_ERR_TYPE_UNKNOWN = "I don't know what to do with message of type";
  public static final String RPP_ERR_RECEIVE_DATA = "RPP Server was unable to receive your data due to the following reason:";
  public static final String RPP_ERR_ASK_RESULTS_NO_SESSION = "Unable to get Results, no Session ID was provided";
  public static final String RPP_ERR_ASK_RESULTS = "Unable to read results from file";
  public static final String RPP_ERR_ASK_STATUS_NO_SESSION = "Unable to get Status, no Session ID was provided";
  public static final String RPP_ERR_ASK_STATUS = "Unable to get Status for Session";
  public static final String RPP_INF_DATA = "Data Received for";
  public static final String RPP_ERR_NEW_SESSION = "RPP Server was unable to create a new Session due to the following reason";
  public static final String RPP_ERR_START_SESSION = "RPP Server was unable to start Session due to the following reason";
  public static final String RPP_INF_TPS_KEY = "Getting RSA Public Key from TPS";
  public static final String RPP_ERR_TPS_KEY = "Unable to retrieve RSA Public Key from the Third-Party Server";
  public static final String RPP_INF_SENDING = "Sending";
  public static final String RPP_INF_STARTING = "Starting job";
  public static final String RPP_INF_STARTED = "Job Started";
  public static final String RPP_INF_REQUEST = "Received request";
  public static final String RPP_INF_FROM = "From";

  //Client
  public static final String CL_LOAD_GENO = "Loading Genotype File";
  public static final String CL_KO_GENO = "Failed to load Genotype File";
  public static final String CL_OK_GENO = "Genotype File Loaded Successfully, Lines present";
  public static final String CL_CONVERT_GENO = "Converting VCF to Genotype File";
  public static final String CL_OK_CONVERT_GENO = "VCF successfully converted to Genotype File. Lines written";
  public static final String CL_KO_CONVERT_GENO = "Failed to convert VCF to Genotype File";
  public static final String CL_CONNECT = "Connecting to RPP";
  public static final String CL_OK_CONNECT = "Connected to RPP";
  public static final String CL_KO_CONNECT = "Failed to Connect to RPP";

  public static final String CL_APPLY_QC = "Applying Quality control to VCF File";
  public static final String Cl_QC_APPLIED = "QC Successfully applied";
  public static final String CL_KO_QC = "Failed to apply QC";

  public static final String CL_NEW_SESSION_LABEL = "Asking a new Session";
  public static final String CL_NEW_SESSION_DATASET = "Selected Dataset";
  public static final String CL_NEW_SESSION_MAF = "Max Minor Allele Frequency";
  public static final String CL_NEW_SESSION_CSQ = "Least Severe Consequence";
  public static final String CL_NEW_SESSION_BED = "Bed File";
  public static final String CL_NEW_SESSION_EXCLUSION = "Exclusion File";

  public static String CL_NEW_SESSION(String dataset, double maxMAF, String minCSQ, String bedFilename, String exclusionFilename) {
    return CL_NEW_SESSION_LABEL
            + ": " + cat(CL_NEW_SESSION_MAF, maxMAF)
            + ", " + cat(CL_NEW_SESSION_CSQ, minCSQ)
            + ", " + cat(CL_NEW_SESSION_DATASET, dataset)
            + ", " + cat(CL_NEW_SESSION_BED, bedFilename)
            + ", " + cat(CL_NEW_SESSION_EXCLUSION, exclusionFilename);
  }
  public static final String CL_OK_NEW_SESSION = "Session created";
  public static final String CL_KO_NEW_SESSION = "RPP Failed to Create Session";
  public static final String CL_KO_START_SESSION = "RPP Failed to Start Session";

  public static final String CL_RESULTS = "Retrieving results";
  public static final String CL_OK_RESULTS = "Results received";
  public static final String CL_KO_RESULTS = "Failed to received result";

  public static final String CL_SEND_EMPTY = "Can't send empty data";
  public static final String CL_SEND = "Transferring data to RPP";
  public static final String CL_OK_SEND = "Data received by the RPP";
  public static final String CL_KO_SEND = "Data transfer failed";

  public static final String CL_EXTRACT = "Extracting and Hashing Data";
  public static final String CL_OK_EXTRACT = "Extraction complete";
  public static final String CL_KO_EXTRACT = "Failed to extract Data";

  public static final String CL_LOAD_SESSION = "Loading session from file";
  public static final String CL_OK_LOAD_SESSION = "Session successfully loaded";
  public static final String CL_KO_LOAD_SESSION = "Failed to load Session";

  public static final String CL_SAVE_SESSION = "Saving session to file";
  public static final String CL_OK_SAVE_SESSION = "Session successfully saved";
  public static final String CL_KO_SAVE_SESSION = "Failed to save Session";

  public static final String CL_OK_MONITOR = "Status Monitoring established for session";
  public static final String CL_KO_MONITOR = "Connection to RPP was lost. Monitoring will stop until connection is restored";
  public static final String CL_RESTORED_MONITOR = "Connection to RPP has been restored, monitoring will resume.";

  public static final String CL_QUIT = "Good bye";

  public static final String CL_EX_MISSING = "Missing";
  public static final String CL_EX_IN = "in";
  public static final String CL_EX_SYNTAX = "Syntax:";

  public static final String CL_MSG_NULL = "No Reply";

  public static final String CL_MSG_UNEXPECTED = "Unexpected Message Type";
  public static final String CL_MSG_EXPECTED = "instead of";

  public static String CL_MSG_EXPECTED_TYPE(Message unexpected, Class expected) {
    return cat(CL_MSG_UNEXPECTED, unexpected.getType()) + " " + cat(CL_MSG_EXPECTED, expected.getName());
  }

  public static final String CL_MSG_SESSION_MISMATCH_MSG = "SessionID mismatch between";
  public static final String CL_MSG_SESSION_MISMATCH_CLIENT = " and Client";

  public static String CL_MSG_SESSION_MISMATCH(SessionMessage message, String session) {
    return CL_MSG_SESSION_MISMATCH_MSG
            + " " + cat(message.getClass().getName(), message.getSession())
            + " " + cat(CL_MSG_SESSION_MISMATCH_CLIENT, session);
  }

  public static final String CL_MSG_UNHANDLED = "Message Type not Handled";

  public static String CL_MSG_UNHANDLED(Message message) {
    return cat(CL_MSG_UNHANDLED, message.getType());
  }

  public static final String CL_MSG_ERROR_SESSION = "Error while creating new Session";

  public static String CL_EX(String missing, String configFile, String syntax) {
    return cat(CL_EX_MISSING, missing) + " "
            + cat(CL_EX_IN, configFile) + N
            + CL_EX_SYNTAX + " " + syntax;
  }

  //Session
  public static final String SS_KO_PORT = "\nUnable to parse RPP address and port. Must be in format rpp.server.address:port";
  public static final String SS_UNK_KEY = "Unknown keyword";
  public static final String SS_NO_KEY1 = "Keyword";
  public static final String SS_NO_KEY2 = "not found in";
  
  public static final String SS_PARSE_SESSION_ID = "Unable to parse Session ID from Session File";
  public static final String SS_PARSE_CLIENT_PUBLIC_RSA = "Unable to parse Client Public RSA Key from Session File";
  public static final String SS_PARSE_TPS_PUBLIC_RSA = "Unable to parse Third Party Public RSA Key from Session File";
  public static final String SS_PARSE_MAX_MAF = "Unable to parse Max MAF from Session File";
  public static final String SS_PARSE_CONSEQUENCE = "Unable to parse Least Severe Consequence from Session File";
  public static final String SS_PARSE_BED_FILE = "Unable to parse Bed File content from Session File";
  public static final String SS_PARSE_EXCLUDED_VARIANT = "Unable to parse Excluded Variant List from Session File";
  public static final String SS_PARSE_CLIENT_PRIVATE_RSA = "Unable to parse Client Private RSA Key from Session File";

  public static String SS_NO_KEY(String keyword, String filename) {
    return N + cat(SS_NO_KEY1, keyword) + " " + cat(SS_NO_KEY2, filename);
  }
  public static final String SS_UNDEFINED = "Session ID undefined";

  //SessionProcessor
  public static final String SP_OK_CLIENT = "Client Data Written for session";
  public static final String SP_QC = "Applying QC to input VCF file";
  public static final String SP_CONVERT = "Converting QCed VCF file to genotype";
  public static final String SP_RPP = "Starting extraction for session";
  public static final String SP_OK_RPP = "Extraction Complete for session";
  public static final String SP_EMPTY_RPP = "RPP data extraction, according to selected criteria yielded empty results";
  public static final String SP_KO_RPP = "Extraction failed";

  //ThirdPartyServer
  public static final String TPS_STARTED = "TPS Started";
  public static final String TPS_RSA = "TPS RSA Private Key Loaded";
  public static final String TPS_AES = "Client AES Key Decrypted";
  public static final String TPS_ALGO = "Running Algorithm";
  public static final String TPS_DONE = "Association Tests Complete";

  //Crypto
  public static final String CRYPTO_FAIL_DELETE = "The Private Key file cannot be deleted, It will not be used";

  //WSS
  public static final String WSS_NO_COMMON_GENE = "No genes common to both dataset where found";
  public static final String WSS_CLIENT_GENES = "Number of Genes in Client's Data";
  public static final String WSS_CLIENT_VARIANTS = "Number of Variants in Client's Data";
  public static final String WSS_RPP_GENES = "Number of Genes in RPP's Data";
  public static final String WSS_RPP_VARIANTS = "Number of Variants in RPP's Data";
  public static final String WSS_COMMON_GENES = "Number of Genes common to all Datasets";
  public static final String WSS_FILTERED_FREQUENCY = "Number of Variants filtered due to pooled frequency";
  public static final String WSS_FILTERED_FISHER = "Number of Variants filtered due to significant call-rate discrepancy";

  public static final String WSS_OK_PARSE = "All data have been parsed.";
  public static final String WSS_OK_FILTER = "All filters have been applied.";
  public static final String WSS_NO_DATA = "No data to parse.";

  //WSSHandler
  public static final String WH_MAP_NULL = "Gene/Genotypes map is null.";
  public static final String WH_GENO_LIST_LOADED = "Genotypes files (genes) loaded";
  public static final String WH_NO_GENOTYPE = "No genotypes loaded for this gene.";
  public static final String WH_PERMUTATIONS = "Permutations so far";
  public static final String WH_LEFT = "Genes left";
  public static final String WH_ETA = "Estimated end at";

  /**
   * Token that will be replaced with \n
   */
  public static final String RET = "<RET>";
  
  public static String WH_PROGRESS(int permutations, int left, int total) {
    return cat(WH_PERMUTATIONS, permutations) + ". " + cat(WH_LEFT, left + "/" + total) + ".";
  }

  public static String WH_PROGRESS(int permutations, int left, int total, long eta) {
    return WH_PROGRESS(permutations, left, total) + " " + cat(WH_ETA, Constants.formatEnd(eta));
  
  }
  public static final String WH_START= "Start Association Tests";
  
  public static final String WH_END= "Computation complete. All genes have been processed in";
  public static String WH_END(long start, long end) {
    return WH_END + " " + (end-start)/1000D + "s";
  }
  
  public static final String WH_DONE = "Transferring Results to RPP Server.";

  //Status
  //public static final String STATUS_UNDEFINED = "Status undefined";
  public static final String STATUS_UNKNOWN = "Unable to find this session :(";
  public static final String STATUS_NO_SESSION = "No Session defined";
  public static final String STATUS_NEW_SESSION = "Session Created. RPP Data extraction started.";
  public static final String STATUS_WAITING_BOTH = "Waiting for Client Data. Waiting for RPP Data extraction.";
  public static final String STATUS_WAITING_CLIENT = "RPP Data extracted. Waiting for Client Data.";
  public static final String STATUS_WAITING_RPP = "Client Data Received. Waiting for RPP Data extraction.";
  public static final String STATUS_TPS_SENDING = "Sending Client and RPP Data to the " + Constants.TPS + ".";
  public static final String STATUS_TPS_PENDING = "Data sent to the " + Constants.TPS + ". Waiting to run.";
  public static final String STATUS_TPS_RUNNING = "Running on the TPS.";
  public static final String STATUS_TPS_ERROR = "Error on the TPS.";
  public static final String STATUS_TPS_UNKNOWN = "TPS is in an unknown state";
  public static final String STATUS_ERROR = "Error on the RPP.";
  public static final String STATUS_TPS_DONE = "Retrieving Results from the " + Constants.TPS + ".";
  public static final String STATUS_RESULTS_AVAILABLE = "Results Available.";
  public static final String STATUS_EXPIRED = "This Session has expired. All data were removed from the RPP.";

  public static String STATUS(RPPStatus.State state) {
    switch (state) {
/*      case UNDEFINED:
        return STATUS_UNDEFINED;*/
      case UNKNOWN:
        return STATUS_UNKNOWN;
      case NO_SESSION:
        return STATUS_NO_SESSION;
      case NEW_SESSION:
        return STATUS_NEW_SESSION;
      case WAITING_BOTH:
        return STATUS_WAITING_BOTH;
      case WAITING_CLIENT:
        return STATUS_WAITING_CLIENT;
      case WAITING_RPP:
        return STATUS_WAITING_RPP;
      case RPP_EMPTY_DATA:
        return SP_EMPTY_RPP;
      case TPS_SENDING:
        return STATUS_TPS_SENDING;
      case TPS_PENDING:
        return STATUS_TPS_PENDING;
      case TPS_RUNNING:
        return STATUS_TPS_RUNNING;
      case TPS_DONE:
        return STATUS_TPS_DONE;
      case TPS_ERROR:
        return STATUS_TPS_ERROR;
      case TPS_UNKNOWN:
        return STATUS_TPS_UNKNOWN;
      case RESULTS_AVAILABLE:
        return STATUS_RESULTS_AVAILABLE;
      case EXPIRED:
        return STATUS_EXPIRED;
      case ERROR:
        return STATUS_ERROR;
    }
    return STATUS_UNKNOWN;
  }

  public static String ETA(int percent) {
    return "Estimated completion : " + percent + "%";
  }

  public static String QUEUED(int before) {
    return before > -1 ? "There are " + before + " job(s) before this one." : null;
  }

  public static final String STS_KO_DESERIAL = "Unable to parse date";

  //Status
  public static final String STS_PARSING_ERROR = "Unable to parse status";

  //Message
  public static final String MSG_FAIL_DESERIALIZE = "Unable to deserialize message";

  //Messages and command line parsing
  public static final String MSG_WELCOME = "Welcome to " + MSG.getTitle();
  public static final String MSG_USAGE = "Usage :";
  public static final String MSG_INSTANCES = "Instances :";
  public static final String MSG_QC = "Quality Control :";
  public static final String MSG_TOOLS = "Tools :";

  public static final String MSG_CMD_JAVA_CLIENT = "java -jar PrivAS.Client.jar";
  public static final String MSG_CMD_JAVA_RPP = "java -jar PrivAS.RPP.jar";
  public static final String MSG_CMD_JAVA_TPS = "java -jar PrivAS.TPS.jar";

  public static final String MSG_DESC_GUI = "- Launch the Client GUI:";
  public static final String MSG_CMD_GUI = T + MSG_CMD_JAVA_CLIENT + " " + "[Directory]";
  public static final String MSG_FAIL_GUI = "Unable to start instance of GUI client: ";

  public static final String MSG_DESC_RPP = "- Launch the Reference Panel Provider Server: ";
  public static final String MSG_CMD_RPP = T + MSG_CMD_JAVA_RPP + " " + "ConfigFile.rpp";
  public static final String MSG_EXTRA_RPP = N
          + "ConfigFile Format :" + N
          + "___________________" + N
          + N
          + RPP_SYNTAX_PORT + N
          + RPP_SYNTAX_DATA + N
          + RPP_SYNTAX_RPP_SESSION_DIR + N
          + RPP_SYNTAX_TPS_ADDRESS + N
          + RPP_SYNTAX_TPS_USER + N
          + RPP_SYNTAX_TPS_LAUNCH_COMMAND + N
          + RPP_SYNTAX_TPS_GETKEY_COMMAND + N
          + RPP_SYNTAX_TPS_SESSION_DIR + N;
  public static final String MSG_FAIL_RPP = "Unable to start instance of RPP Server: ";
  public static final String MSG_DESC_TPS = "- Launch the Third-Party Server (should be used by the provided script, to manage scheduler issues):";
  
  //tps
  public static final String TPS_RANDOM = "random";
  
  public static final String MSG_CMD_TPS = T
          + " Session_ID"
          + " Work_Directory"
          + " NB_CORES"
          + " random_numerical_seed( or "+TPS_RANDOM+" for a new seed)";
  public static final String MSG_FAIL_TPS = "Unable to start instance of Third Party Server: ";

  public static final String MSG_DESC_KEYGEN = "- Generate a keypair for the Third-Party Server:";
  public static final String MSG_CMD_KEYGEN = T + MSG_CMD_JAVA_TPS + " " + MSG.ARG_KEYGEN
          + " Work_Directory"
          + " Session_ID";

  public static final String MSG_FAIL_KEYGEN = "Unable to save generated keypair: ";

  public static final String MSG_DESC_PREPARE = "- Convert a VCF file to a genotypes file:";
  public static final String MSG_CMD_PREPARE_CLIENT = T + MSG_CMD_JAVA_RPP + " " + MSG.ARG_CONVERT_VCF
          + " vep_annotated_file.vcf(.gz)";
  public static final String MSG_CMD_PREPARE_RPP = T + MSG_CMD_JAVA_RPP + " " + MSG.ARG_CONVERT_VCF
          + " vep_annotated_file.vcf(.gz)";
  public static final String MSG_FAIL_PREPARE = "Unable to save generated keypair: ";

  public static final String MSG_DESC_QC = "- Perform a Quality Control on a VCF file";
  public static final String MSG_CMD_QC_CLIENT = T + MSG_CMD_JAVA_CLIENT + " " + MSG.ARG_QC + " input.vcf(.gz) qc.param";
  public static final String MSG_CMD_QC_RPP = T + MSG_CMD_JAVA_RPP + " " + MSG.ARG_QC + " input.vcf(.gz) qc.param";
  public static final String MSG_FAIL_QC = "Unable to perform QC on VCF file: ";

  public static final String MSG_DESC_QC_CONV = "- Perform default Quality Control on a VCF file and convert results to genotype file.";
  public static final String MSG_CMD_QC_CONV_CLIENT = T + MSG_CMD_JAVA_CLIENT + " " + MSG.ARG_QC_CONV + " input.vcf(.gz)";
  public static final String MSG_CMD_QC_CONV_RPP = T + MSG_CMD_JAVA_RPP + " " + MSG.ARG_QC_CONV + " input.vcf(.gz)";

  public static final String MSG_DESC_WSS = "- Compute a WSS Association Test (locally):";
  public static final String MSG_CMD_WSS = T + MSG_CMD_JAVA_TPS + " " + MSG.ARG_WSSKEY
          + " genotype_files.lst"
          + " phenotypes.tsv"
          + " NB_CORES"
          + " RANDOM_SEED"
          + " results.tsv";
  
  public static final String MSG_DESC_RANKSUM = "- Compute a WSS Ranksum:";
  public static final String MSG_CMD_RANKSUM = T + MSG_CMD_JAVA_TPS + " " + MSG.ARG_RANKSUMKEY
          + " geneName"
          + " genotypes.tsv"
          + " statuses.bool";
  
  public static final String MSG_FAIL_WSS = "Unable to start WSS computation: ";

  //gui
  public static final String GUI_DEFAULT_DIRECTORY = ".";

  public static final String DOT = "...";
  //Methods

  public static String action(String msg, String param) {
    return action(cat(msg, param));
  }

  public static String done(String msg, String param) {
    return done(cat(msg, param));
  }

  public static String done(String msg, String param, Exception e) {
    return done(cat(msg, param, e));
  }

  public static String done(String msg, Exception e) {
    return done(cat(msg, e));
  }

  public static String action(String msg) {
    return msg + DOT;
  }

  public static String done(String msg) {
    return DOT + msg;
  }

  public static String cat(String msg, Number n){
    return cat(msg, n.toString());
  }

  public static String cat(String msg, String param) {
    return msg + " [" + param + "]";
  }

  public static String cat(String msg1, String param1, String msg2, String param2) {
    return cat(msg1, param1) + " " + cat(msg2, param2);
  }

  public static String cat(String msg, Exception e) {
    return msg + ": "+e.getClass().getSimpleName()+ " " + e.getMessage();
  }

  public static String cat(String msg, String param, Exception e) {
    return cat(cat(msg, param), e);
  }

  /**
   * Gets the name of this Program
   *
   * @return
   */
  public static String getTitle() {
    return TITLE + " " + Main.getVersion();
  }

  /**
   * Gets the version of this Program
   *
   * @return
   */
  public static String getVersion() {
    Date d = getClassBuildTime();
    String year = "????";
    String month = "??";
    String day = "??";

    if (d != null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(d);
      year = "" + cal.get(Calendar.YEAR);
      month = "" + (cal.get(Calendar.MONTH) + 1);
      day = "" + cal.get(Calendar.DAY_OF_MONTH);
      if (day.length() < 2)
        day = "0" + day;
      if (month.length() < 2)
        month = "0" + month;
    }
    return "v" + year + "-" + month + "-" + day;
  }

  /**
   * Gets the BuildTime of the current jar. This methods is used for generating the version number
   *
   * @return
   */
  private static Date getClassBuildTime() {
    Date d = null;
    Class<?> currentClass = new Object() {
    }.getClass().getEnclosingClass();
    URL resource = currentClass.getResource(currentClass.getSimpleName() + ".class");
    if (resource != null)
      switch (resource.getProtocol()) {
        case "file":
          try {
            d = new Date(new File(resource.toURI()).lastModified());
          } catch (URISyntaxException ignored) {
            //Nothing
          }
          break;

        case "jar": {
          String path = resource.getPath();
          d = new Date(new File(path.substring(5, path.indexOf("!"))).lastModified());
          break;
        }
        case "zip": {
          String path = resource.getPath();
          File jarFileOnDisk = new File(path.substring(0, path.indexOf("!")));
          try (JarFile jf = new JarFile(jarFileOnDisk)) {
            ZipEntry ze = jf.getEntry(path.substring(path.indexOf("!") + 2));//Skip the ! and the /
            long zeTimeLong = ze.getTime();
            d = new Date(zeTimeLong);
          } catch (IOException | RuntimeException ignored) {
          }
          break;
        }
        default:
          break;
      }
    return d;
  }
}
