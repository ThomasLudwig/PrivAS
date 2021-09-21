package fr.inserm.u1078.tludwig.privas.constants;

import java.text.CharacterIterator;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
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
  ///**
  // * TPS Prefixed Date/Time
  // */
  //public static final SimpleDateFormat DF_TPS = new SimpleDateFormat("'[TPS '" + DAY + " " + TIME + "'] '");
  /**
   * RPP Prefixed Date/Time
   */
  public static final SimpleDateFormat DF_RPP = new SimpleDateFormat("'[RPP '" + DAY + " " + TIME + "']\t'");
  /**
   * Date/Time Dot separated
   */
  public static final SimpleDateFormat DF_DAY_DOT_TIME = new SimpleDateFormat("yyyyMMdd.HHmmss");
  /**
   * Hour Format
   */
  public static final SimpleDateFormat DF_TIME = new SimpleDateFormat(TIME);
  /**
   * * Day Format
   */
  public static final SimpleDateFormat DF_DAY = new SimpleDateFormat(DAY);

  /**
   * Day and Hour Format
   */
  public static final SimpleDateFormat DF_DAY_TIME = new SimpleDateFormat(DAY + " "+ TIME);

  /**
   * Formatted expected end of task Date/Time
   * @param duration estimated duration left (ms)
   * @return  the duration in a formatted string
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

  /**
   * Returns the number of seconds between two dates
   * @param start the start of the interval
   * @param end the end of the interval
   * @return the number of seconds
   */
  public static long durationInSeconds(Date start, Date end){
    return (end.getTime() - start.getTime()) / 1000;
  }

  public static String duration(Date start, Date end) {
    long dur = durationInSeconds(start, end);
    String s = dur%60+"";
    if(s.length() < 2)
      s = "0" + s;
    String out = s+"s";
    dur = dur / 60;
    if(dur > 1) {
      String m = dur%60+"";
      if(m.length() < 2)
        m = "0" + m;
      out = m+"m"+out;
      dur = dur / 60;
      if(dur > 1){
        String h = dur%24+"";
        if(h.length() < 2 && dur/24 > 1)
          h = "0"+h;
        out = h+"h"+out;
        dur = dur / 24;
        if(dur > 1)
          out = dur+"days "+out;
      }
    }
    return out;
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

  public static boolean parseBoolean(String s){
    switch(s){
      case "0" : return false;
      case "1" : return true;
      default : return Boolean.parseBoolean(s.toLowerCase());
    }
  }
  
  /**
   * WSS
   */
  public static final String ALGO_WSS = "wss";

  public static final String ALGO_RAVAGES_WSS = "ravages_wss";
  public static final String ALGO_RAVAGES_SKAT = "ravages_skat";
  public static final String ALGO_RAVAGES_SKAT_O = "ravages_skat_o";

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
  @SuppressWarnings("unused")
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

  public static final String[] GNOMAD_SUBPOPS = {
          "None",
          "AFR",
          "AMR",
          "ASJ",
          "EAS",
          "FIN",
          "NFE",
          "OTH",
          "SAS",
          "Male",
          "Female",
          "POPMAX"
  };

  public static int getSubpopIndex(String subpop){
    for(int i = 0; i < GNOMAD_SUBPOPS.length; i++){
      if(GNOMAD_SUBPOPS[i].equalsIgnoreCase(subpop))
        return i;
    }
    return -1;
  }

  public static String humanReadableByteCountBin(long bytes) {
    long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
    if (absB < 1024) {
      return bytes + " B";
    }
    long value = absB;
    CharacterIterator ci = new StringCharacterIterator("KMGTPE");
    for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
      value >>= 10;
      ci.next();
    }
    value *= Long.signum(bytes);
    return String.format("%.1f %ciB", value / 1024.0, ci.current());
  }

  public static String HTML(String text) {
    return "<html>" + text + "</html>";
  }

  public static final String DISABLED = "disabled";

  public static final String SCP = "scp";
  public static final String SSH = "ssh";
  public static String scp(String src, String dest){
    return scp(src, dest, false);
  }
  public static String scp(String src, String dest, boolean recursively){
    return SCP+(recursively ? " -r":"")+" "+src+" "+dest;
  }
  public static String[] ssh(String login, String address, String command, String... arguments){
    StringBuilder commandline = new StringBuilder(command);
    for(String arg : arguments)
      commandline.append(" '").append(arg).append("'");
    return new String[]{
            SSH,
            "-t",
            login + "@" + address,
            commandline.toString()
    };
  }

  public static String distant(String login, String address, String path){
    return login + "@" + address + ":" + path;
  }
}
