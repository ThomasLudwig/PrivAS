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
   * Extension for GnomAD File
   */
  public static final String FILE_GNOMAD_EXTENSION = "bin"; //QUESTION gnomad ?
  /**
   * Extension for list of excluded variants
   */
  public static final String FILE_EXCLUSION_EXTENSION = "excluded";
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
   * Extension for list files
   */
  public static final String FILE_LIST_EXTENSION = "list";
  /**
   * Extension for gzipped files
   */
  public static final String FILE_GZ_EXTENSION = "gz";
  /**
   * Extension for VCF files
   */
  public static final String FILE_VCF_EXTENSION = "vcf";
  /**
   * Extension for VCF GZ files
   */
  public static final String FILE_VCF_GZ_EXTENSION = FILE_VCF_EXTENSION+"(."+FILE_GZ_EXTENSION+")";
  /**
   * Extension for Genotypes files
   */
  public static final String FILE_GENO_EXTENSION = "genotypes";
  /**
   * Extension for Genotypes Size files
   */
  public static final String FILE_GENO_SIZE_EXTENSION = "size";
  /**
   * Extension when receiving the results, writes the encrypted content in a file with this extension
   */
  public static final String FILE_ENCRYPTED_EXTENSION = "aes";
  /**
   * Extension when file as an OK flag
   */
  public static final String FILE_OK_EXTENSION = "OK";
  /**
   * Extension for status files
   */
  public static final String FILE_STATUS_EXTENSION = "status";
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
  public static final String FILE_RPP_DATA = "rpp." + FILE_GZ_EXTENSION;
  /**
   * Tag that guarantees that the rpp data file was complete
   */
  public static final String FILE_RPP_DATA_OK = "rpp.gz." + FILE_OK_EXTENSION;
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
  public static final String FILE_ENCRYPTED_CLIENT_DATA = "client.data." + FILE_ENCRYPTED_EXTENSION;
  /**
   * File containing AES encrypted list of variants excluded by the Client due to bad QC
   */
  public static final String FILE_ENCRYPTED_CLIENT_EXCLUDED_VARIANTS = "client.excludedvariants." + FILE_ENCRYPTED_EXTENSION;
  /**
   * File containing the list variants excluded by the RPP due to bad QC
   */
  public static final String FILE_RPP_EXCLUDED_VARIANTS = "rpp.excludedvariants.txt";
  /**
   * Tag that guarantees that the client data file was complete
   */
  public static final String FILE_CLIENT_DATA_OK = "client.data." + FILE_OK_EXTENSION;
  /**
   * File containing AES encrypted results
   */
  public static final String FILE_RESULTS = "results";
  /**
   * Tag that guarantees that the results file was complete
   */
  public static final String FILE_RESULTS_OK = "results." + FILE_OK_EXTENSION;
  /**
   * File that contains the AES key, encrypted with the TPS RSA Public Key
   */
  public static final String FILE_AES_KEY = "aes.key.tps-rsa";
  /**
   * File containing the last known RPP status
   */
  public static final String FILE_RPP_STATUS = "rpp." + FILE_STATUS_EXTENSION;
  /**
   * File containing the last known TPS status
   */
  public static final String FILE_TPS_STATUS = "tps." + FILE_STATUS_EXTENSION;
  /**
   * Prefix for QCed files
    */
  public static final String QC_PREFIX = "QC";

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
  public static final String SESSION_HASH = "HASH_KEY";
  /**
   * the tag referring to the Client Genotype File name for the Session when saving to/loading from a file
   */
  public static final String SESSION_GENOTYPE = "GENOTYPE_FILENAME";
  /**
   * the tag referring to the Selected Dataset for the Session when saving to/loading from a file
   */
  public static final String SESSION_SELECTED_DATASET = "SELECTED_DATASET";
  /**
   * the tag referring to the selected subpopulation index for the Session when saving to/loading from a file
   */
  public static final String SESSION_SUBPOP_INDEX = "SELECTED_SUBPOP_INDEX";
  /**
   * the tag referring to the Minor Allele Frequency Threshold in GnomAD for the Session when saving to/loading from a file
   */
  public static final String SESSION_MAF = "MAX_MINOR_ALLELE_FREQUENCY";
  /**
   * the tag referring to the Minor Allele Frequency Threshold in GnomAD_SUBPOP for the Session when saving to/loading from a file
   */
  public static final String SESSION_MAF_SUBPOP = "MAX_SUBPOP_MINOR_ALLELE_FREQUENCY";
  /**
   * the tag referring to the Consequence Threshold for the Session when saving to/loading from a file
   */
  public static final String SESSION_CSQ = "LEAST_SEVERE_CONSEQUENCE";
  /**
   * the tag referring to the limitation to SVNs during variant selection
   */
  public static final String SESSION_LIMIT_SNV = "LIMIT_SNV";
  /**
   * the tag referring to the filename of the binary GnomAD File
   */
  public static final String SESSION_GNOMAD_FILENAME = "GNOMAD_FILENAME";
  /**
   * the tag referring to the name of the Quality Control Parameters File
   */
  public static final String SESSION_QC_PARAM_FILENAME = "QC_PARAM_FILENAME";
  /**
   * the tag referring to the list of well covered positions
   */
  public static final String SESSION_BED_FILENAME = "BED_FILENAME";
  /**
   * the tag referring to the intersection of the Client's and RPP's bed file
   */
  public static final String SESSION_INTERSECT_BED = "INTERSECT_BED";
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
   * the tag referring to the RPP Server's Available GnomAD versions for the Session when saving to/loading from a file
   */
  public static final String SESSION_AVAILABLE_GNOMAD_VERSIONS = "RPP_AVAILABLE_GNOMAD_VERSIONS";
  /**
   * the tag referring to the selected GnomAD version for the Session when saving to/loading from a file
   */
  public static final String SESSION_SELECTED_GNOMAD_VERSION = "SELECTED_GNOMAD_VERSION";
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

  /**
   * the genotypes files header tag referring to the GnomAD filename that was used for the annotation
   */
  public static final String GENOPTYES_HEADER_GNOMAD_FILENAME = "GNOMAD_FILENAME";

  public static final String FILETYPE_GNOMAD = "GnomAD Binary File";
  public static final String FILETYPE_VCF = "VCF File";
  public static final String FILETYPE_BED = "Bed File";

  public static final String TAG_VERSION = "version";
  public static final String TAG_EXOME_PATH = "exome_path";
  public static final String TAG_GENOME_PATH = "genome_path";
  public static final String TAG_DATE = "date";
  public static final String TAG_EXOME_VARIANTS = "exome_variants";
  public static final String TAG_GENOME_VARIANTS = "genome_variants";
  public static final String TAG_EXOME_SIZE = "exome_size";
  public static final String TAG_GENOME_SIZE = "genome_size";
  public static final String TAG_BUFFER_SIZE = "buffer_size";

  public static final String FILE_DOC_EXTENSION = "rst";
  public static final String FILE_CLIENT_DOC = "Client."+FILE_DOC_EXTENSION;
  public static final String FILE_RPP_DOC = "RPP."+FILE_DOC_EXTENSION;
  public static final String FILE_TPS_DOC = "TPS."+FILE_DOC_EXTENSION;


}
