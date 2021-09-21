package fr.inserm.u1078.tludwig.privas.constants;

import fr.inserm.u1078.tludwig.privas.gui.LookAndFeel;

import java.awt.*;

/**
 * Class that stores constants (file location in the jar and Strings values of GUI elements)
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-06-07
 *
 * Javadoc will not be exhaustive for this class
 */
public class GUI {

  //Paths to embedded PNG files
  public static final String IMAGE_PATH_LOGO = "logo.privas.128.png";
  public static final String IMAGE_PATH_LOG_LOGO = "log.png";
  public static final String IMAGE_PATH_LED_OFF = "led.off.png";
  public static final String IMAGE_PATH_LED_OK = "led.ok.png";
  public static final String IMAGE_PATH_LED_KO = "led.ko.png";
  
  //Fonts for GUI Components
  public static final Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
  public static final Font DEFAULT_BOLD_FONT = DEFAULT_FONT.deriveFont(Font.BOLD);

  //ResultsPane
  public static final String RP_TITLE = "Results";
  public static final String RP_TT_GENE = "Gene Symbol as seen in Client Data";
  public static final String RP_TT_POSITION = "Position of the gene's first variant in Client Data";
  public static final String RP_TT_PVALUE = "Computed p-value";
  public static final String RP_TT_K0 = "WSS K0";
  public static final String RP_TT_K = "Number of permutations";
  public static final String RP_TT_RANK = "WSS Ranking Sum";
  public static final String RP_TT_TOTAL = "Number of variants seen for this genes in Client and RPP data";
  public static final String RP_TT_SHARED = "Number of variants shared between Client and RPP for this gene";
  public static final String RP_TT_TIME = "Computation time on TPS Server";

  //TPSLogPane
  public static final String TPS_TITLE = "Third Party Server Log";
  public static final String TPS_LABEL_MESSAGES = "Messages from Third-Party Server";
  public static final String TPS_COL_NUM = "#";
  public static final String TPS_COL_TIME = "Time";
  public static final String TPS_COL_STATE = "Status";
  public static final String TPS_COL_MESSAGE = "Message";

  //Results Columns names
  public static final String RP_COL_NUM = "#";
  public static final String RP_COL_GENE = "gene";
  public static final String RP_COL_POSITION = "position";
  public static final String RP_COL_PVALUE = "p-value";
  public static final String RP_COL_K0 = "k0";
  public static final String RP_COL_K = "k";
  public static final String RP_COL_RANK = "ranksum";
  public static final String RP_COL_TOTAL = "total variants";
  public static final String RP_COL_SHARED = "shared variants";
  public static final String RP_COL_TIME = "duration";

  //ResultsPane Error
  public static final String RP_NO_RESULTS = "No results data loaded.";
  public static final String RP_KO_PARSE = "Unable to parse line";
  public static final String RP_KO_LOAD = "There was a problem while trying to read data from file";
  public static final String RP_KO_SAVE = "Unable to write to file";
  public static final String RP_KO_SAVE_PNG = "Unable to Save PNG Image";
  public static final String RP_KO_SAVE_TSV = "Unable to Save TSV File";
  public static final String RP_KO_SAVE_HTML = "Unable to Save HTML File";
  
  //Chromosomes
  public static final String RP_CHR_UNKNOWN = "?";
  public static final String RP_CHR_PREFIX = "chr";
  public static final String RP_CHR_X = "X";
  public static final String RP_CHR_Y = "Y";
  public static final String RP_CHR_MT = "MT";
  public static final String RP_CHR_M = "M";

  //ResultsPane Menu
  public static final String RP_MN_EXPORT = "Export";
  public static final String RP_MI_TSV = "Export Data As TSV";
  public static final String RP_MI_HTML = "Export Data As HTML";
  public static final String RP_MI_PNG = "Export Plot As PNG";
  
  //ResultsPane ToolTip
  public static final String RP_TT_TSV = "Export Data, in the current order, to a TSV File";
  public static final String RP_TT_HTML = "Export Formatted Data, in the current order, to an HTML File";
  public static final String RP_TT_PNG = "Export Manhattan Plot to a PNG File";

  //ResultsPane default size
  public static final int RP_PNG_WIDTH = 1200;
  public static final int RP_PNG_HEIGHT = 800;

  //Manhattan
  public static final String MNT_TITLE = "Manhattan Plot";
  public static final String MNT_DOMAIN = "Genomic position";
  public static final String MNT_RANGE = "p-value";

  //ProgressDialog
  public static final Dimension PD_DIM = new Dimension(400, 50);
  public static final Dimension PD_SIZE = new Dimension(300, 75);

  //LoggingPanel
  public static final String LP_TAG_DEBUG = "DEBUG";
  public static final String LP_TAG_INFO = "INFO ";
  public static final String LP_TAG_WARN = "WARN ";
  public static final String LP_TAG_ERROR = "ERROR";
  public static final String LP_TAG_OK = "OK   ";
  public static final String LP_TITLE = "Application Log";
  public static final Dimension LP_DIM = new Dimension(0, 150);

  //FileExtensionChooser
  public static final String FEC_FILES = "Files";

  //ConnectionPane
  public static final String CP_TITLE = "Choose an RPP Server";
  public static final String CP_LABEL_ADDRESS = "RPP Server Address";
  public static final String CP_LABEL_PORT = "Port";

  //CriteriaPane
  public static final String CRIT_TITLE = "Start a new Session";
  public static final String CRIT_TITLE_SELECT = "Variant selection criteria";
  public static final String CRIT_TITLE_WSS = "WSS (Weighted Sum Statistics) Algorithm";

  public static final String CHOOSE =  "Choose...";
  public static final String CREATE = "Create...";
  
  public static final String CRIT_RADIO_WSS = "Perform WSS ?";
  public static final String CRIT_LABEL_DATASET = "Available Datasets";
  public static final String CRIT_LABEL_AVAILABLE_GNOMAD_VERSION = "Available GnomAD versions";
  public static final String CRIT_LABEL_MAF = "Minor Allele Frequency Threshold (in GnomAD)";
  public static final String CRIT_LABEL_SUBPOP = "GnomAD Subpopulation";
  public static final String CRIT_LABEL_MAF_SUBPOP = "Minor Allele Frequency Threshold (in Subpopulation)";
  public static final String CRIT_LABEL_CSQ = "Least Severe Consequence";
  public static final String CRIT_LABEL_BEDFILE = "Bed File of well covered positions";
  public static final String CRIT_LABEL_EXCLUDED_VARIANTS = "List of excluded Variants";
  public static final String CRIT_LABEL_QC_PARAMETERS = "QC Parameters File";
  public static final String CRIT_LABEL_PERM = "Number of Permutations";
  public static final String CRIT_LABEL_FRQ = "Maximum Allele Frequency Threshold (in pooled data)";
  public static final String CRIT_LABEL_MIN_PVALUE = "Minimal p-value";
  public static final String CRIT_LABEL_DURATION = "Estimated Duration";
  public static final String CRIT_TOOLTIP_DISCLAIMER = "Duration is roughly estimated and will depend on the number of variants, number of genes, p-value associated to genes....";

  public static final String CRIT_MSG_EMPTY_QC = "You have not chosen a QC Parameters file.";
  public static final String CRIT_TIT_EMPTY_QC = "Missing QC Parameters file";
  public static final String CRIT_MSG_EMPTY_EXCL = "You have not chosen a file containing the variants excluded by the QC.";
  public static final String CRIT_TIT_EMPTY_EXCL = "Missing Excluded Variants file";
  public static final String CRIT_MSG_EMPTY_BED = "You have not chosen a Bed file of well covered positions. Do you want to continue anyway?";
  public static final String CRIT_TIT_EMPTY_BED = "Missing Bed File";

  public static String CRIT_MSG_NOT_FOUND_QC(String filename){ return "The QC Parameters file ["+filename+"] does not exist.";}
  public static String CRIT_MSG_NOT_FOUND_EXCL(String filename){ return "The file containing the variants excluded by the QC ["+filename+"] does not exist.";}
  public static String CRIT_MSG_NOT_FOUND_BED(String filename){ return "The Bed file of well covered positions ["+filename+"] does not exist. Do you want to continue anyway?";}

  //Apply QC Panel
  public static final String QC_EDITOR_TITLE = "QC Parameters Editor";
  public static final String APQC_TITLE = "Apply Quality Control to a VCF File";
  public static final String APQC_LABEL_INPUT_VCF = "Input VCF File";
  public static final String APQC_LABEL_QC_PARAM = "QC Parameters";
  public static final String APQC_LABEL_GNOMAD = "GnomAD File";
  public static final String APQC_TIT_VCF_NULL = "No VCF File";
  public static final String APQC_TIT_VCF_MISSING = "VCF File does not exist";
  public static final String APQC_TIT_QC_NULL = "No QC Parameters File";
  public static final String APQC_TIT_QC_MISSING = "QC Parameters File does not exist";
  public static final String APQC_TIT_GNOMAD_NULL = "No GnomAD File";
  public static final String APQC_TIT_GNOMAD_MISSING = "GnomAD File does not exist";
  public static final String APQC_TIT_QC_SAVE_FAILED = "Could not save QC Parameters";
  public static final String APQC_MSG_QC_SAVE_FAILED = "Could not save QC Parameters";

  public static final String APQC_MSG_VCF_NULL = "No VCF File specified";
  public static final String APQC_MSG_QC_NULL = "No QC Parameters File specified";
  public static final String APQC_MSG_GNOMAD_NULL = "No GnomAD File specified";
  public static String APQC_MSG_VCF_MISSING(String inputVCF){ return "Specified VCF File ["+inputVCF+"] does not exist";}
  public static String APQC_MSG_QC_MISSING(String qcParamFilename) {return "Specified QC Parameters File ["+qcParamFilename+"] does not exist";}
  public static String APQC_MSG_GNOMAD_MISSING(String gnomadFilename) {return "Specified GnomAD File ["+gnomadFilename+"] does not exist";}

  //SessionPanel
  public static final String SP_LABEL_ID = "Session ID";
  public static final String SP_LABEL_HASH = "Hash Key";
  public static final String SP_LABEL_PUBLIC = "Public RSA Key";
  public static final String SP_LABEL_PRIVATE = "Private RSA Key";
  public static final String SP_LABEL_AES = "AES Key";
  public static final String SP_LABEL_DATASET = "Dataset";
  public static final String SP_LABEL_GNOMAD_VERSION = "GnomAD Version";
  public static final String SP_LABEL_MAF = "Max. MAF (GnomAD)";
  public static final String SP_LABEL_SUBPOP = "GnomAD Subpopulation";
  public static final String SP_LABEL_MAF_SUBPOP = "Max. MAF (GnomAD Subpopulation)";
  public static final String SP_LABEL_CSQ = "Least Severe Consequence";
  public static final String SP_LABEL_LIMIT_SNV = "Limit variants to SNVs ?";
  public static final String SP_LABEL_RPP = "RPP Server";
  public static final String SP_LABEL_THIRD_PARTY_NAME = "Third Party Server";
  public static final String SP_LABEL_THIRD_PARTY_KEY = "Third Party Public Key";
  public static final String SP_LABEL_STATUS = "Last Known Status";
  public static final String SP_LABEL_ALGORITHM = "Algorithm Parameters";
  public static final String SP_LABEL_GENOTYPE = "Genotype Filename";
  public static final String SP_LABEL_GNOMAD_FILENAME = "GnomAD filename";
  public static final String SP_LABEL_BED_FILENAME = "Bed of well covered position";
  public static final String SP_LABEL_EXCLUDED_VARIANTS_FILENAME = "File containing the list of Excluded Variants";
  public static final String SP_LABEL_QC_PARAM_FILENAME = "File containing the Quality Control Parameters";

  public static final String SP_TIT_RPP_ERROR = "RPP reported an error";
  public static final String SP_TIT_RPP_MESSAGE = "Message from RPP";

  public static final String SP_HTML_YOU = "<font color=\"green\">the <b>Client</b></font>";
  public static final String SP_HTML_RPP = "<font color=\"blue\">the <b>Reference Panel Provider</b></font>";
  public static final String SP_HTML_TPS = "<font color=\"red\">the <b>Third-Party Server</b></font>";

  public static final String SP_TOOLTIP_ID = "<b>Session ID</b>"
          + "<p>Uniquely identifies your work session for<ul>"
          + "<li>" + SP_HTML_YOU + "</li>"
          + "<li>" + SP_HTML_RPP + "</li>"
          + "<li>" + SP_HTML_TPS + "</li></ul></p>";
  public static final String SP_TOOLTIP_HASH = "<b>Hash Key</b>"
          + "<p>Shared between<ul>"
          + "<li>" + SP_HTML_YOU + "</li>"
          + "<li>" + SP_HTML_RPP + "</li></ul>"
          + "<p>This key will be used to hash gene names and variant information, so that " + SP_HTML_TPS + " can do comparison and computing while"
          + " not being able the read data either from " + SP_HTML_YOU + " or " + SP_HTML_RPP + ".</p>";
  public static final String SP_TOOLTIP_PUBLIC = "<b>Public RSA Key</b>"
          + "<p>" + SP_HTML_RPP + " uses this key to encrypt the <b>Hash Key</b>, so that it is not legible on the network</p>";
  public static final String SP_TOOLTIP_PRIVATE = "<b>Private RSA Key</b>"
          + "<p>" + SP_HTML_YOU + " use this key to decrypt the <b>Hash Key</b>.<br/>"
          + "Only " + SP_HTML_YOU + " know this key.</p>";
  public static final String SP_TOOLTIP_AES = "<b>AES Key</b>"
          + "<p>Shared between</p>"
          + "<ul><li>" + SP_HTML_YOU + "</li>"
          + "<li>" + SP_HTML_TPS + "</li></ul>"
          + "<p>Data exchanged are encrypted/decrypted using this key.<br/>"
          + "Thus " + SP_HTML_RPP + " (that serves as a bridge) cannot read these data.</p>";
  public static final String SP_TOOLTIP_DATASET = "Name of the reference Dataset";
  public static final String SP_TOOLTIP_GNOMAD_VERSION = "Version of the GnomAD Version on "+SP_HTML_RPP;
  public static final String SP_TOOLTIP_MAF = "<b>Maximum Minor Allele Frequency Threshold</b>"
          + "<p>When selecting variants, " + SP_HTML_YOU + " and " + SP_HTML_RPP + " will only keep variants with MAF below or equal to this threshold.</p>";
  public static final String SP_TOOLTIP_SUBPOP = "Selected subpopulation on GnomAD";
  public static final String SP_TOOLTIP_MAF_SUBPOP = "Maximum Minor Allele Frequency Threshold for GnomAD subpopulation";
  public static final String SP_TOOLTIP_CSQ = "<b>Least Severe Consequence</b>"
          + "<p>When selecting variants, " + SP_HTML_YOU + " and " + SP_HTML_RPP + " will only keep variants with Consequence above or equal to this threshold.</p>";
  public static final String SP_TOOLTIP_LIMIT_SNV = "<b>Limit To SNVs ?</b>"
          + "<p>When selecting variants, " + SP_HTML_YOU + " and " + SP_HTML_RPP + " will only keep variants that are SNVs.</p>";
  public static final String SP_TOOLTIP_RPP = "<b>Reference Panel Provider (RPP) Server</b>"
          + "<p>The address and port of the RPP Server. The Color indicates the state of the server :"
          + "<ul><li>Grey : Unknown</li>"
          + "<li>Green : Up</li>"
          + "<li>Red : Down</li></ul></p>";
  public static final String SP_TOOLTIP_THIRD_PARTY_NAME = "<b>Third Party Server (TPS)</b>"
          + "<p>Name of the Server that will perform the actual calculations</p>";
  public static final String SP_TOOLTIP_THIRD_PARTY_KEY = "<b>Third Party Public Key</b>"
          + "<p>This key is used to encrypt your <b>AES key</b> and share it with " + SP_HTML_TPS + ".<br/>"
          + "This encryption prevents " + SP_HTML_RPP + " from reading the AES key.</p>";
  public static final String SP_TOOLTIP_STATUS = "<b>Last Known Status</b>"
          + "<p>The Last known message sent by " + SP_HTML_RPP + ".</p>";
  public static final String SP_TOOLTIP_ALGORITHM = "<b>Algorithm Parameters</b>"
          + "<p>The algorithm and parameters that will be used by " + SP_HTML_TPS + ".</p>";
  public static final String SP_TOOLTIP_GENOTYPE = "<p>The Genotype File that was/will be used to extract/hash the data matching selection criteria.</p>";
  public static final String SP_TOOLTIP_GNOMAD_FILENAME = "<p>GnomAD binary file</p>";
  public static final String SP_TOOLTIP_BED_FILENAME = "<p>Bed file of well covered positions</p>";
  public static final String SP_TOOLTIP_EXCLUDED_VARIANTS_FILENAME = "<p>File that contains the list of Variants Excluded by the QC</p>";
  public static final String SP_TOOLTIP_QC_PARAM_FILENAME = "<p>File that contains the Quality Control Parameters</p>";

  //Client Window
  public static final String CW_MN_FILE = "File";
  public static final String CW_MN_SERVER = "Server";
  public static final String CW_MI_APPLY_QC_VCF_ANNOTATE = "Load VCF, Apply QC and Annotate...";
  public static final String CW_MI_APPLY_QC_VCF_ONLY = "Apply QC to VCF...";
  public static final String CW_MI_LOAD_QCED_VCF_ANNOTATE = "Load QCed VCF and Annotate...";
  public static final String CW_MI_LOAD_GENO = "Load Annotated QCed Genotypes...";
  public static final String CW_MI_LOAD_SSS = "Load Session...";
  public static final String CW_MI_SAVE_SSS = "Save Session...";
  public static final String CW_MI_LOAD_RES = "Load Results...";
  public static final String CW_MI_QUIT = "Quit";
  public static final String CW_MI_CONNECT = "Connect to RPP Server...";
  public static final String CW_MI_NEW_SSS = "Start new Session...";
  public static final String CW_MI_SHOW_TPS = "Show TPS Log";
  public static final String CW_MI_GET_RESULTS = "Get Results";

  public static final String CW_DG_OK_CONNECT = "Connected";
  public static final String CW_DG_KO_CONNECT = "Connection Failed";
  public static final String CW_DG_OK_MSG_CONNECT = "Connected to RPP Server";
  public static final String CW_DG_KO_MSG_CONNECT = "Failed to connect to RPP Server";

  public static String CW_DG_OK_MSG_CONNECT(String address, int port) {
    return MSG.cat(CW_DG_OK_MSG_CONNECT, address + ":" + port);
  }

  public static String CW_DG_KO_MSG_CONNECT(String address, int port) {
    return MSG.cat(CW_DG_KO_MSG_CONNECT, address + ":" + port);
  }

  public static String CW_TITLE(String session) {
    return MSG.getTitle() + " - Session #" + session;
  }

  public static final String CW_DG_NO_GENO = "No Genotype File";
  public static final String CW_DG_MSG_NO_GENO = "Please Load/Generate a Genotype File First";

  public static final String CW_DG_NOT_CONNECTED = "No Connection";
  public static final String CW_DG_MSG_NOT_CONNECTED = "You Need to Connect First\nWould you like to connect now ?";
  public static final String CW_DG_NEW_SESSION = "New Session Started";
  public static final String CW_DG_OK_SESSION = "New Session";
  public static final String CW_DG_KO_SESSION = "No Session";

  public static final String CW_DG_MSG_QUIT = "Would you like to save your Session before exiting ?";
  public static final String CW_DG_QUIT = "Save Session and Exit ?";

  public static final String CW_CONVERTING_TITLE = "Converting VCF to Genotype File";
  public static final String CW_CONVERTING_NORTH = "Converting VCF to Genotype File";
  public static final String CW_CONVERTING_SOUTH = "This may take some time";

  public static final String CW_APPLYING_QC_TITLE = "Applying QC to VCF File";
  public static final String CW_APPLYING_QC_NORTH = "Applying QC to VCF File";
  public static final String CW_APPLYING_QC_SOUTH = "This may take some time";

  public static final String CW_DG_OK_CONVERT = "Conversion Complete";
  public static final String CW_DG_MSG_OK_CONVERT = "VCF File successfully converted to Genotype File";
  public static final String CW_DG_KO_CONVERT = "Conversion Failed";
  public static final String CW_DG_MSG_KO_CONVERT = "Failed to convert VCF to Genotype File\nError : ";

  public static final String CW_DG_OK_QC = "QC Complete";
  public static final String CW_DG_MSG_OK_QC = "QC successfully applied to VCF File";
  public static final String CW_DG_KO_QC = "QC Failed";
  public static final String CW_DG_MSG_KO_QC = "Failed to applied QC to VCF File\nError : ";

  public static final String CW_LOADING_TITLE = "Loading Genotypes";
  public static final String CW_LOADING_NORTH = "Loading a Genotype File";
  public static final String CW_LOADING_SOUTH = "Please wait";
  
  public static final String CW_CREATING_SESSION_TITLE = "Creating a new Session";
  public static final String CW_CREATING_SESSION_NORTH = "Waiting for RPP to reply";
  public static final String CW_CREATING_SESSION_SOUTH = "Please wait";

  public static final String CW_DG_KO_LOAD_SSS = "Could not load Session";
  public static final String CW_DG_MSG_KO_LOAD_SSS = "Failed to load Session from file";
  public static final String CW_DG_KO_SAVE_SSS = "Could not save Session";
  public static final String CW_DG_MSG_KO_SAVE_SSS = "Failed to save Session to file";
  public static final String CW_DG_KO_SAVE_RES = "Could not save results";
  public static final String CW_DG_MSG_KO_SAVE_RES = "Failed to save results to file";
  public static final String CW_DG_KO_LOAD_RES = "Unable to parse results file";
  public static final String CW_DG_MSG_KO_LOAD_RES = "Unable to parse results file";

  public static final String CW_EXTRACTING_TITLE = "Extracting Data";
  public static final String CW_EXTRACTING_NORTH = "Extracting and Hashing Data";
  public static final String CW_EXTRACTING_SOUTH = "Please wait";

  public static final String CW_DG_KO_EXTRACT = "Data extraction Failed";
  public static final String CW_DG_MSG_KO_EXTRACT = "Failed to extract Data.\n";

  public static final String CW_SENDING_TITLE = "Sending Data";
  public static final String CW_SENDING_NORTH = "Sending Data to the RPP server";
  public static final String CW_SENDING_SOUTH = "Please wait";

  public static final String CW_DG_OK_SEND = "Data Transferred";
  public static final String CW_DG_MSG_OK_SEND = "Data received by the RPP server";
  public static final String CW_DG_KO_SEND = "Data Transfer Failed";
  public static final String CW_DG_MSG_KO_SEND = "Failed to send Data to the RPP server.\n";
  
  public static final int HSP_MEDIUM = 15;

  public static final Color COLOR_PENDING = new Color(170,200,170);
  public static final Color COLOR_STARTED = new Color(64,160,160);
  public static final Color COLOR_RUNNING = new Color(64,160,255);
  public static final Color COLOR_DONE = new Color(64,255,96);
  public static final Color COLOR_ERROR = new Color(220,100,100);
  public static final Color COLOR_UNREACHABLE = new Color(230,130,80);
  public static final Color COLOR_UNKNOWN = new Color(200,160,120);

  public static final Color BACKGROUND_COLOR_KO = COLOR_ERROR;
  public static final Color BACKGROUND_COLOR_OK = LookAndFeel.getBackgroundTextColor();
}
