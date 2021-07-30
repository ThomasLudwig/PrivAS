package fr.inserm.u1078.tludwig.privas.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Toolkit to extract annotations from reference files
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-07-30
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class ExtractAnnotations {

    public enum GnomADType {EXOME, GENOME}

  /**
   * Extract a GnomAD Genome File in an easily parsable form
   * @param inputVCF the input GnomAD Genome VCF File
   * @param outputFile the converted file
   * @param type EXOME or GENOME
   * @throws IOException if there is a problem while reading the file
   * @throws GnomADException if there is a problem while parsing the file
   */
  public static void extractGnomAD(String inputVCF, String outputFile, GnomADType type) throws IOException, GnomADException {
    UniversalReader in = new UniversalReader(inputVCF);
    PrintWriter out = new PrintWriter(new FileWriter(outputFile)); //TODO compress
    String line;

    if(type == GnomADType.EXOME){
      out.println(GnomADExome.HEADER);
      while((line = in.readLine()) != null)
        if(!line.startsWith("#"))
          out.println(new GnomADExome(line).toString());
    } else {
      out.println(GnomADGenome.HEADER);
      while((line = in.readLine()) != null)
        if(!line.startsWith("#"))
          out.println(new GnomADGenome(line).toString());
    }

    in.close();
    out.close();
  }

  /**
   * Gets all the variants in canonical form
   * @param chr the chromosome
   * @param pos the position
   * @param ref the reference allele
   * @param alts the alternate alleles
   * @return
   */
  public static String[] getVariants(String chr, String pos, String ref, String[] alts){
    String[] ret = new String[alts.length];
    for(int i = 0; i < ret.length; i++)
      ret[i] = GenotypesFileHandler.getCanonical(chr, pos, ref, alts[i]);
    return ret;
  }

  /**
   * Parses GnomAD frequencies as doubles
   * @param value the string values (comma separated)
   * @param altNumber the number of alternate alleles for the variant
   * @return
   */
  public static double[] readFrequencies(String value, int altNumber){
    String[] values = value.split(",");
    double[] ret = new double[altNumber];
    for(int i = 0 ; i < altNumber; i++){
      ret[i] = 0;
      try{
        ret[i] = Double.parseDouble(values[i]);
      } catch(NumberFormatException | ArrayIndexOutOfBoundsException ignore){
        //ignore
      }
    }
    return ret;
  }

  /**
   * Class representing a line from a GnomAD Exome File
   */
  private static class GnomADExome {
    public static final String HD_VARIANT = "VARIANT";
    public static final String HD_AF = "AF";
    public static final String HD_AF_AFR = "AF_AFR";
    public static final String HD_AF_AMR = "AF_AMR";
    public static final String HD_AF_ASJ = "AF_ASJ";
    public static final String HD_AF_EAS = "AF_EAS";
    public static final String HD_AF_FIN = "AF_FIN";
    public static final String HD_AF_NFE = "AF_NFE";
    public static final String HD_AF_OTH = "AF_OTH";
    public static final String HD_AF_SAS = "AF_SAS";
    public static final String HD_AF_MALE = "AF_Male";
    public static final String HD_AF_FEMALE = "AF_Female";
    public static final String HD_AF_POPMAX = "AF_POPMAX";

    public static final String HEADER = String.join("\t", HD_VARIANT,HD_AF, HD_AF_AFR, HD_AF_AMR, HD_AF_ASJ, HD_AF_EAS, HD_AF_FIN, HD_AF_NFE, HD_AF_OTH, HD_AF_SAS, HD_AF_MALE, HD_AF_FEMALE, HD_AF_POPMAX);

    private String[] variants;
    private double[] afs;
    private double[] afr_afs;
    private double[] amr_afs;
    private double[] asj_afs;
    private double[] eas_afs;
    private double[] fin_afs;
    private double[] nfe_afs;
    private double[] oth_afs;
    private double[] sas_afs;
    private double[] male_afs;
    private double[] female_afs;
    private double[] popmax_afs;

    GnomADExome(String line) throws GnomADException{
      String[] f = line.split("\t");
      variants = getVariants(f[0], f[1], f[3], f[4].split(","));
      final int n = variants.length;
      String[] infos = f[7].split(";");
      int found = 0;
      for(int i = 0 ; i < infos.length && found < 12; i++){
        String[] kv = infos[i].split("=");
        switch(kv[0]){
          case HD_AF :
            found++; afs = readFrequencies(kv[1], n); break;
          case HD_AF_AFR :
            found++; afr_afs = readFrequencies(kv[1], n); break;
          case HD_AF_AMR :
            found++; amr_afs = readFrequencies(kv[1], n); break;
          case HD_AF_ASJ :
            found++; asj_afs = readFrequencies(kv[1], n); break;
          case HD_AF_EAS :
            found++; eas_afs = readFrequencies(kv[1], n); break;
          case HD_AF_FIN :
            found++; fin_afs = readFrequencies(kv[1], n); break;
          case HD_AF_NFE :
            found++; nfe_afs = readFrequencies(kv[1], n); break;
          case HD_AF_OTH :
            found++; oth_afs = readFrequencies(kv[1], n); break;
          case HD_AF_SAS :
            found++; sas_afs = readFrequencies(kv[1], n); break;
          case HD_AF_MALE :
            found++; male_afs = readFrequencies(kv[1], n); break;
          case HD_AF_FEMALE :
            found++; female_afs = readFrequencies(kv[1], n); break;
          case HD_AF_POPMAX :
            found++; popmax_afs = readFrequencies(kv[1], n); break;
        }
      }
      if(found != 12)
        throw new GnomADException("Should have found 12 annotations, but only found ["+found+"]");
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      for(int i = 0 ; i < variants.length; i++){
        sb.append("\n");
        sb.append(variants[i]).append("\t");
        sb.append(afs[i]).append("\t");
        sb.append(afr_afs[i]).append("\t");
        sb.append(amr_afs[i]).append("\t");
        sb.append(asj_afs[i]).append("\t");
        sb.append(eas_afs[i]).append("\t");
        sb.append(fin_afs[i]).append("\t");
        sb.append(nfe_afs[i]).append("\t");
        sb.append(oth_afs[i]).append("\t");
        sb.append(sas_afs[i]).append("\t");
        sb.append(male_afs[i]).append("\t");
        sb.append(female_afs[i]).append("\t");
        sb.append(popmax_afs[i]).append("\t");
      }
      return sb.substring(1);
    }
  }

  /**
   * Class representing a line from a GnomAD Genome File
   */
  private static class GnomADGenome {
    public static final String HD_VARIANT = "VARIANT";
    public static final String HD_AF = "AF";
    public static final String HD_AF_AFR = "AF_AFR";
    public static final String HD_AF_AMR = "AF_AMR";
    public static final String HD_AF_ASJ = "AF_ASJ";
    public static final String HD_AF_EAS = "AF_EAS";
    public static final String HD_AF_FIN = "AF_FIN";
    public static final String HD_AF_NFE = "AF_NFE";
    public static final String HD_AF_OTH = "AF_OTH";
    public static final String HD_AF_MALE = "AF_Male";
    public static final String HD_AF_FEMALE = "AF_Female";
    public static final String HD_AF_POPMAX = "AF_POPMAX";

    public static final String HEADER = String.join("\t", HD_VARIANT,HD_AF, HD_AF_AFR, HD_AF_AMR, HD_AF_ASJ, HD_AF_EAS, HD_AF_FIN, HD_AF_NFE, HD_AF_OTH, HD_AF_MALE, HD_AF_FEMALE, HD_AF_POPMAX);

    private String[] variants;
    private double[] afs;
    private double[] afr_afs;
    private double[] amr_afs;
    private double[] asj_afs;
    private double[] eas_afs;
    private double[] fin_afs;
    private double[] nfe_afs;
    private double[] oth_afs;
    private double[] male_afs;
    private double[] female_afs;
    private double[] popmax_afs;

    GnomADGenome(String line) throws GnomADException {
      String[] f = line.split("\t");
      variants = getVariants(f[0], f[1], f[3], f[4].split(","));
      final int n = variants.length;
      String[] infos = f[7].split(";");
      int found = 0;
      for(int i = 0 ; i < infos.length && found < 11; i++){
        String[] kv = infos[i].split("=");
        switch(kv[0]){
          case HD_AF :
            found++; afs = readFrequencies(kv[1], n); break;
          case HD_AF_AFR :
            found++; afr_afs = readFrequencies(kv[1], n); break;
          case HD_AF_AMR :
            found++; amr_afs = readFrequencies(kv[1], n); break;
          case HD_AF_ASJ :
            found++; asj_afs = readFrequencies(kv[1], n); break;
          case HD_AF_EAS :
            found++; eas_afs = readFrequencies(kv[1], n); break;
          case HD_AF_FIN :
            found++; fin_afs = readFrequencies(kv[1], n); break;
          case HD_AF_NFE :
            found++; nfe_afs = readFrequencies(kv[1], n); break;
          case HD_AF_OTH :
            found++; oth_afs = readFrequencies(kv[1], n); break;
          case HD_AF_MALE :
            found++; male_afs = readFrequencies(kv[1], n); break;
          case HD_AF_FEMALE :
            found++; female_afs = readFrequencies(kv[1], n); break;
          case HD_AF_POPMAX :
            found++; popmax_afs = readFrequencies(kv[1], n); break;
        }
      }
      if(found != 11)
        throw new GnomADException("Should have found 12 annotations, but only found ["+found+"]");
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      for(int i = 0 ; i < variants.length; i++){
        sb.append("\n");
        sb.append(variants[i]).append("\t");
        sb.append(afs[i]).append("\t");
        sb.append(afr_afs[i]).append("\t");
        sb.append(amr_afs[i]).append("\t");
        sb.append(asj_afs[i]).append("\t");
        sb.append(eas_afs[i]).append("\t");
        sb.append(fin_afs[i]).append("\t");
        sb.append(nfe_afs[i]).append("\t");
        sb.append(oth_afs[i]).append("\t");
        sb.append(male_afs[i]).append("\t");
        sb.append(female_afs[i]).append("\t");
        sb.append(popmax_afs[i]).append("\t");
      }
      return sb.substring(1);
    }
  }

  /**
   * Exception thrown while trying to parse a line from a GnomAD File
   */
  private static class GnomADException extends Exception {
    public GnomADException(String message) {
      super(message);
    }

    public GnomADException(String message, Throwable cause) {
      super(message, cause);
    }

    public GnomADException(Throwable cause) {
      super(cause);
    }
  }
}
