package fr.inserm.u1078.tludwig.privas.constants;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Constants String and Numerical values
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-06-07
 *
 * Javadoc will not be exhaustive for this class
 */
public class Constants {

  /**
   * Dot separated Date Format
   */
  public static final String DAY = "yyyy.MM.dd";
  /**
   * Hour Format
   */
  public static final String TIME = "HH:mm:ss";
  
  //DateFormat
  /**
   * TPS Prefixed Date/Time
   */
  public static final SimpleDateFormat DF_TPS = new SimpleDateFormat("'[TPS '" + DAY + " " + TIME + "'] '");
  /**
   * RPP Prefixed Date/Time
   */
  public static final SimpleDateFormat DF_RPP = new SimpleDateFormat("'[RPP '" + DAY + " " + TIME + "']\t'");
  /**
   * Date/Time Dot separated
   */
  public static final SimpleDateFormat DF_DAY_DOT_TIME = new SimpleDateFormat("yyyyMMdd.HHmmss");
  /**
   * Dot separated Date Format
   */
  public static final SimpleDateFormat DF_TIME = new SimpleDateFormat(TIME);
  /**
   * * Hour Format
   */
  public static final SimpleDateFormat DF_DAY = new SimpleDateFormat(DAY);

  /**
   * Formatted expected end of task Date/Time
   * @param duration estimated duration left (ms)
   * @return 
   */
  public static String formatEnd(long duration) {
    final Date now = new Date();
    final Date end = new Date(now.getTime() + duration);

    final String endDay = Constants.DF_DAY.format(end);
    final String endTime = Constants.DF_TIME.format(end);

    if (Constants.DF_DAY.format(now).equals(endDay))
      return endTime;
    return endTime + " on " + endDay;
  }

  //Default Text for some variables

  /**
  * Tabulation
  */  
  public static final String T = "\t";

  /**
   * Replaces tabulation character where it is forbidden
   */
  public static final String TAB = "<TAB>";

  /**
   * Replaces new line character where it is forbidden
   */
  public static final String RET = "<RET>";
  /**
   * OK String
   */
  public static final String OK = "OK";
  /**
   * KO String
   */
  public static final String KO = "KO";
  /**
   * Not Available String
   */
  public static final String NA = "N/A";
  /**
   * Third Party Server String
   */
  public static final String TPS = "Third-Party Server";
  /**
   * Unknown Genomic Position
   */
  public static final String POS_UNKNOWN = "?:?";
  /**
   * Unknown Gene Name
   */
  public static final String GENE_UNKNOWN = "???";
  /**
   * Unknown Details
   */
  public static final String DETAILS_UNKNOWN = "???";
  /**
   * Unknown IP Address
   */
  public static final String IP_UNKNOWN = "???.???.???.???";
  /**
   * Name provided when there are no gene names in the annotation
   */
  public static final String SS_NO_GENE = "--No-Gene--";
  
  //Algorithm names
  
  /**
   * WSS
   */
  public static final String ALGO_WSS = "wss";

  //WSS results file columns / header
  
  /**
   * WSS results file gene column
   */
  public static final String WWS_HEADER_GENE = "gene";
  /**
   * WSS results file p-value column
   */
  public static final String WWS_HEADER_P_VALUE = "p_value";
  /**
   * WSS results file k0 column
   */
  public static final String WWS_HEADER_K0 = "k0";
  /**
   * WSS results file k column
   */
  public static final String WWS_HEADER_K = "k";
  /**
   * WSS results file ranksum column
   */
  public static final String WWS_HEADER_RANKSUM = "ranksum";
  /**
   * WSS results file Total Number of Variants column
   */
  public static final String WWS_HEADER_TOTAL_VAR = "total_variants";
  /**
   * WSS results file Shared Variants (between Client and RPP) column
   */
  public static final String WWS_HEADER_SHARED_VAR = "shared_variants";
  /**
   * WSS results file computation duration column
   */
  public static final String WWS_HEADER_DURATION = "duration";
  /**
   * WSS results file header
   */
  public static final String WWS_HEADER = "#" + WWS_HEADER_GENE
          + T + WWS_HEADER_P_VALUE
          + T + WWS_HEADER_K0
          + T + WWS_HEADER_K
          + T + WWS_HEADER_RANKSUM
          + T + WWS_HEADER_TOTAL_VAR
          + T + WWS_HEADER_SHARED_VAR
          + T + WWS_HEADER_DURATION;

  /**
   * Missing Genotype Information
   */
  public static final int GENO_MISSING = -1;
  /**
   * Homozygous to reference allele
   */
  public static final int GENO_REF = 0;
  /**
   * Heterozygous
   */
  public static final int GENO_HET = 1;
  /**
   * Homozygous to alternate allele
   */
  public static final int GENO_ALT = 2;

  /**
   * All VEP Consequences, from lest to most severe
   */
  public static final String[] VEP_CONSEQUENCES = {
    "1.intergenic_variant",
    "2.feature_truncation",
    "3.regulatory_region_variant",
    "4.feature_elongation",
    "5.regulatory_region_amplification",
    "6.regulatory_region_ablation",
    "7.TF_binding_site_variant",
    "8.TFBS_amplification",
    "9.TFBS_ablation",
    "10.downstream_gene_variant",
    "11.upstream_gene_variant",
    "12.non_coding_transcript_variant",
    "13.NMD_transcript_variant",
    "14.intron_variant",
    "15.non_coding_transcript_exon_variant",
    "16.3_prime_UTR_variant",
    "17.5_prime_UTR_variant",
    "18.mature_miRNA_variant",
    "19.coding_sequence_variant",
    "20.synonymous_variant",
    "21.stop_retained_variant",
    "22.incomplete_terminal_codon_variant",
    "23.splice_region_variant",
    "24.protein_altering_variant",
    "25.missense_variant",
    "26.inframe_deletion",
    "27.inframe_insertion",
    "28.transcript_amplification",
    "29.start_lost",
    "30.stop_lost",
    "31.frameshift_variant",
    "32.stop_gained",
    "33.splice_donor_variant",
    "34.splice_acceptor_variant",
    "35.transcript_ablation"};
}
