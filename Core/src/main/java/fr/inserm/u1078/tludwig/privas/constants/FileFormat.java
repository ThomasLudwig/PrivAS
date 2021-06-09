package fr.inserm.u1078.tludwig.privas.constants;

/**
 * Constants relative to file names and file formats
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-06-07
 *
 * Javadoc complete on 2019-08-07
 */
public class FileFormat {

  /////////////////////// file, directory and extension names //////////////////////////
  /**
   * Subdirectory where session directories are stored
   */
  public static final String DIRECTORY_SESSIONS = "sessions";
  /**
   * Subdirectory where working files are stored
   */
  public static final String DIRECTORY_TMP = "tmp";
  /**
   * File containing the TPS rsa private key
   */
  public static final String FILE_PRIVATE_RSA_KEY = "rsa.private.key";
  /**
   * File containing the TPS rsa public key
   */
  public static final String FILE_PUBLIC_RSA_KEY = "rsa.public.key";
  /**
   * File containing compressed hashed RPP data
   */
  public static final String FILE_RPP_DATA = "rpp.gz";
  /**
   * VCF File after QC was applied
   */
  public static final String FILE_RPP_QC_VCF = "after.qc.vcf.gz";
  /**
   * VCF File after QC, converted to genotype file
   */
  public static final String FILE_RPP_GENOTYPE = "after.qc.genotype.gz";
  /**
   * Tag that guarantees that the rpp data file was complete
   */
  public static final String FILE_RPP_DATA_OK = "rpp.gz.ok";
  /**
   * File containing the session parameters
   */
  public static final String FILE_SESSION_PARAMETERS = "session.param";
  /**
   * File containing the algorithm name and its parameters
   */
  public static final String FILE_ALGORITHM = "algo";
  /**
   * File containing AES encrypted, hashed client data
   */
  public static final String FILE_ENCRYPTED_CLIENT_DATA = "client.data.aes";
  /**
   * File containing AES encrypted list of variants excluded by the Client due to bad QC
   */
  public static final String FILE_ENCRYPTED_CLIENT_EXCLUDED_VARIANTS = "client.excludedvariants.aes";
  /**
   * File containing the list variants excluded by the RPP due to bad QC
   */
  public static final String FILE_RPP_EXCLUDED_VARIANTS = "rpp.excludedvariants.txt";
  /**
   * Tag that guarantees that the client data file was complete
   */
  public static final String FILE_CLIENT_DATA_OK = "client.data.ok";
  /**
   * File containing AES encrypted results
   */
  public static final String FILE_RESULTS = "results";
  /**
   * Tag that guarantees that the results file was complete
   */
  public static final String FILE_RESULTS_OK = "results.ok";
  /**
   * File that contains the AES key, encrypted with the TPS RSA Public Key
   */
  public static final String FILE_AES_KEY = "aes.key.tps-rsa";
  /**
   * File containing the last known RPP status
   */
  public static final String FILE_RPP_STATUS = "rpp.status";
  /**
   * File containing the last known TPS status
   */
  public static final String FILE_TPS_STATUS = "tps.status";
  /**
   * Extension when receiving the results, writes the encrypted content in a file with this extension
   */
  public static final String FILE_ENCRYPTED_EXTENSION = ".aes";
  /**
   * Extension of the file logging CI connections. To prevent too frequent sessions
   */
  public static final String FILE_CONNECTION_LOG = ".cnnct";
  /**
   * Extension for Session files
   */
  public static final String FILE_SESSION_EXTENSION = "privas";
  /**
   * Extension for results files
   */
  public static final String FILE_RESULTS_EXTENSION = "results";
  /**
   * Extension for bed files
   */
  public static final String FILE_BED_EXTENSION = "bed";
  /**
   * Extension for QC Parameters File
    */
  public static final String FILE_QC_PARAM_EXTENSION = "param";
   /**
    * Extension for list of excluded variants
    */
  public static final String FILE_EXCLUSION_EXTENSION = "lst";
  /**
   * Extension for PNG Images
   */
  public static final String FILE_PNG_EXTENSION = "png";
  /**
   * Extension for HTML pages
   */
  public static final String FILE_HTML_EXTENSION = "html";
  /**
   * Extension for TSV tables
   */
  public static final String FILE_TSV_EXTENSION = "tsv";
  /**
   * Extension for gzipped files
   */
  public static final String FILE_GZ_EXTENSION = "gz";
  /**
   * Extension for VCF files
   */
  public static final String FILE_VCF_EXTENSION = "vcf";
  /**
   * Extension for Genotypes files
   */
  public static final String FILE_GENO_EXTENSION = "genotypes";

  //Format of RPP configuration file (.rpp)
  /**
   * TAG for the Port number of the RPP
   */
  public static final String RPP_TAG_PORT = "port_number";
  /**
   * TAG for the Lists of Data files. One Genotypes file per dataset
   */
  public static final String RPP_TAG_DATA = "data_file";
  /**
   * TAG for the Directory where session directories will be store on the RPP
   */
  public static final String RPP_TAG_RPP_SESSION_DIR = "rpp_session_dir";
  
  /**
   * TAG for the file listing expired sessions
   */
  public static final String RPP_TAG_RRP_EXPIRED_SESSION = "rpp_expired_session";
  /**
   * TAG for the Name of the Third Party Server
   */
  public static final String RPP_TAG_TPS_NAME = "tps_name";
  /**
   * Address of the Third Party Server
   */
  public static final String RPP_TAG_TPS_ADDRESS = "tps_address";
  /**
   * TAG for the user name on the Third Party Server
   */
  public static final String RPP_TAG_TPS_USER = "tps_user";
  /**
   * TAG for the command to launch jobs on the Third Party Server
   */
  public static final String RPP_TAG_TPS_LAUNCH_COMMAND = "tps_launch_command";
  /**
   * TAG for the command to get the Public RSA Key from the Third Party Server
   */
  public static final String RPP_TAG_TPS_GETKEY_COMMAND = "tps_get_key_command";
  /**
   * TAG for the directory where session directories will be stored on the Third Party Server
   */
  public static final String RPP_TAG_TPS_SESSION_DIR = "tps_session_dir";

  /**
   * TAG for the comma separated list of IP (addresses or blocks) that can always connect to the RPP
   */
  public static final String RPP_TAG_WHITELIST = "whitelist";
  /**
   * TAG for the comma separated list of IP (addresses or blocks) that can never connect to the RPP
   */
  public static final String RPP_TAG_BLACKLIST = "blacklist";
  /**
   * TAG for the filename containing the connection log
   */
  public static final String RPP_TAG_CONNECTION_LOG = "connection_log";
  /**
   * TAG for maximum number of connection from the same address in 1 day
   */
  public static final String RPP_TAG_MAX_PER_DAY = "max_per_day";
  /**
   * TAG for maximum number of connection from the same address in 1 week
   */
  public static final String RPP_TAG_MAX_PER_WEEK = "max_per_week";
  /**
   * TAG for maximum number of connection from the same address in 1 month
   */
  public static final String RPP_TAG_MAX_PER_MONTH = "max_per_month";

  //Format of Session file (.privas)
  /**
   * the tag referring to the ID for the Session when saving to/loading from a file
   */
  public static final String SESSION_ID = "SESSION_ID";
  /**
   * the tag referring to the Client's Public RSA Key for the Session when saving to/loading from a file
   */
  public static final String SESSION_PUBLIC = "PUBLIC_RSA_KEY";
  /**
   * the tag referring to the Client's Private RSA Key for the Session when saving to/loading from a file
   */
  public static final String SESSION_PRIVATE = "PRIVATE_RSA_KEY";
  /**
   * the tag referring to the Third Party Server's Public RSA Key for the Session when saving to/loading from a file
   */
  public static final String SESSION_THIRD_PARTY_KEY = "THIRD_PARTY_RSA_KEY";
  /**
   * the tag referring to the Third Party Server's name
   */
  public static final String SESSION_THIRD_PARTY_NAME = "THIRD_PARTY_NAME";
  /**
   * the tag referring to the AES Secret Key for the Session when saving to/loading from a file
   */
  public static final String SESSION_AES = "AES_KEY";
  /**
   * the tag referring to the Hash Salt for the Session when saving to/loading from a file
   */
  public static final String SESSION_HASH = "HASK_KEY";
  /**
   * the tag referring to the Client Genotype File name for the Session when saving to/loading from a file
   */
  public static final String SESSION_GENOTYPE = "GENOTYPE_FILENAME";
  /**
   * the tag referring to the Selected Dataset for the Session when saving to/loading from a file
   */
  public static final String SESSION_SELECTED_DATASET = "SELECTED_DATASET";
  /**
   * the tag referring to the Minor Allele Frequency Threshold in GnomAD for the Session when saving to/loading from a file
   */
  public static final String SESSION_MAF = "MAX_MINOR_ALLELE_FREQUENCY";
  /**
   * the tag referring to the Minor Allele Frequency Threshold in GnomAD_NFE for the Session when saving to/loading from a file
   */
  public static final String SESSION_MAF_NFE = "MAX_NFE_MINOR_ALLELE_FREQUENCY";
  /**
   * the tag referring to the Consequence Threshold for the Session when saving to/loading from a file
   */
  public static final String SESSION_CSQ = "LEAST_SEVERE_CONSEQUENCE";
  /**
   * the tag referring to the limitation to SVNs during variant selection
   */
  public static final String SESSION_LIMIT_SNV = "LIMIT_SNV";
  /**
   * the tag referring to the list of well covered positions
   */
  public static final String SESSION_BEDFILE = "BEDFILE";
  /**
   * the tag referring to the list of variants explicitly excluded (due to bad QC)
   */
  public static final String SESSION_EXCLUDED_VARIANTS = "EXCLUDED_VARIANTS";
  /**
   * the tag referring to the Last Known RPP Server's Status for the Session when saving to/loading from a file
   */
  public static final String SESSION_STATUS = "LAST_KNOWN_STATUS";
  /**
   * the tag referring to the RPP Server's address and port number for the Session when saving to/loading from a file
   */
  public static final String SESSION_RPP = "RPP_ADDRESS";
  /**
   * the tag referring to the RRP Server's Available Datasets for the Session when saving to/loading from a file
   */
  public static final String SESSION_AVAILABLE_DATASETS = "RPP_AVAILABLE_DATASETS";
  /**
   * the tag referring to the Hash Dictionary allowing to retrieve gene names from hashes for the Session when saving to/loading from a file
   */
  public static final String SESSION_DICTIONARY = "HASH_DICTIONARY";
  /**
   * the tag referring to the genomic position of the first known variant for each gene for the Session when saving to/loading from a file
   */
  public static final String SESSION_POSITIONS = "GENE_POSITIONS";
  /**
   * the tag referring to the algorithm and its parameters for the Session when saving to/loading from a file
   */
  public static final String SESSION_ALGORITHM = "ALGORITHM";
  
}
