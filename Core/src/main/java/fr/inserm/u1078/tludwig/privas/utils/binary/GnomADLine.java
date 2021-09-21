package fr.inserm.u1078.tludwig.privas.utils.binary;

import fr.inserm.u1078.tludwig.privas.utils.CanonicalVariant;

import java.util.ArrayList;

/**
 * Representation of a line in a GnomAD File
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-08-31
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class GnomADLine implements Comparable<GnomADLine> {
  private static final String HD_AF = "AF";
  private static final String HD_AF_AFR = "AF_AFR";
  private static final String HD_AF_AMR = "AF_AMR";
  private static final String HD_AF_ASJ = "AF_ASJ";
  private static final String HD_AF_EAS = "AF_EAS";
  private static final String HD_AF_FIN = "AF_FIN";
  private static final String HD_AF_NFE = "AF_NFE";
  private static final String HD_AF_OTH = "AF_OTH";
  private static final String HD_AF_SAS = "AF_SAS";
  private static final String HD_AF_MALE = "AF_Male";
  private static final String HD_AF_FEMALE = "AF_Female";
  private static final String HD_AF_POPMAX = "AF_POPMAX";


  public static final GnomADLine NULL = new GnomADLine(CanonicalVariant.NULL, new double[]{0,0,0,0,0,0,0,0,0});
  private final CanonicalVariant canonicalVariant;
  private final double[] frequencies;

  public GnomADLine(String line) {
    //"1:12259+1:C\t0.0\t0.0\t0.0\t0.0\t0.0001541\t0.0\t0.0\t0.0\t0.0\t0.0\t0.0\t0.0\n" +
    String[] f = line.split("\t");
    this.canonicalVariant = new CanonicalVariant(f[0]);
    this.frequencies = new double[12];
    for(int i = 0 ; i < 12; i++)
      this.frequencies[i] = Double.parseDouble(f[i+1]);
  }

  public GnomADLine(CanonicalVariant canonicalVariant, double[] frequencies) {
    this.canonicalVariant = canonicalVariant;
    this.frequencies = frequencies;
  }

  public static ArrayList<GnomADLine> parseVCFLine(String line){
    String[] f = line.split("\t");
    CanonicalVariant[] variants = CanonicalVariant.getVariants(f[0], f[1], f[3], f[4].split(","));
    int nbAllele = variants.length;
    double[] afs = new double[nbAllele];
    double[] afr_afs = new double[nbAllele];
    double[] amr_afs = new double[nbAllele];
    double[] asj_afs = new double[nbAllele];
    double[] eas_afs = new double[nbAllele];
    double[] fin_afs = new double[nbAllele];
    double[] nfe_afs = new double[nbAllele];
    double[] oth_afs = new double[nbAllele];
    double[] sas_afs = new double[nbAllele];
    double[] male_afs = new double[nbAllele];
    double[] female_afs = new double[nbAllele];
    double[] popmax_afs = new double[nbAllele];

    String[] info = f[7].split(";");
    int found = 0;
    for(int i = 0 ; i < info.length && found < 12; i++){
      String[] kv = info[i].split("=");
      switch(kv[0]){
        case HD_AF :
          found++; afs = readVCFFrequencies(kv[1], nbAllele); break;
        case HD_AF_AFR :
          found++; afr_afs = readVCFFrequencies(kv[1], nbAllele); break;
        case HD_AF_AMR :
          found++; amr_afs = readVCFFrequencies(kv[1], nbAllele); break;
        case HD_AF_ASJ :
          found++; asj_afs = readVCFFrequencies(kv[1], nbAllele); break;
        case HD_AF_EAS :
          found++; eas_afs = readVCFFrequencies(kv[1], nbAllele); break;
        case HD_AF_FIN :
          found++; fin_afs = readVCFFrequencies(kv[1], nbAllele); break;
        case HD_AF_NFE :
          found++; nfe_afs = readVCFFrequencies(kv[1], nbAllele); break;
        case HD_AF_OTH :
          found++; oth_afs = readVCFFrequencies(kv[1], nbAllele); break;
        case HD_AF_SAS :
          found++; sas_afs = readVCFFrequencies(kv[1], nbAllele); break;
        case HD_AF_MALE :
          found++; male_afs = readVCFFrequencies(kv[1], nbAllele); break;
        case HD_AF_FEMALE :
          found++; female_afs = readVCFFrequencies(kv[1], nbAllele); break;
        case HD_AF_POPMAX :
          found++; popmax_afs = readVCFFrequencies(kv[1], nbAllele); break;
      }
    }

    ArrayList<GnomADLine> ret = new ArrayList<>();
    for (int i = 0; i < nbAllele; i++)
      if(afs[i] > 0)
        ret.add(new GnomADLine(variants[i], new double[]{
              afs[i],
              afr_afs[i],
              amr_afs[i],
              asj_afs[i],
              eas_afs[i],
              fin_afs[i],
              nfe_afs[i],
              oth_afs[i],
              sas_afs[i],
              male_afs[i],
              female_afs[i],
              popmax_afs[i]
      }));
    return ret;
  }

  public boolean isNull(){
    return this.canonicalVariant.isNull();
  }
/*
  public int getChrom() {
    return chrom;
  }

  public int getPos() {
    return pos;
  }

  public int getLength() {
    return length;
  }

  public String getAlt() {
    return alt;
  }
*/
  public CanonicalVariant getCanonicalVariant() {
    return this.canonicalVariant;
  }

  public double[] getFrequencies() {
    return frequencies;
  }

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder(getCanonicalVariant().toString());
    for(double d : frequencies)
      out.append("\t").append(d);
    return out.toString();
  }

  public static final int L = 128;
  public static final int F = 64;
  public static final int SL = 3;
  public static final int FS = 12;
  public static final int SS = 48;

  public static boolean isLength1(int flag){
    return (flag & L) == L;
  }

  public static boolean isNullFrequencies(int flag){
    return (flag & F) == F;
  }

  public static final String[] ACGT = {"A", "C", "G", "T"};

  public static String getSeq(int flag){
    switch (flag & SL){
      case 0 :
        return "-";
      case 1 :
        return ACGT[(flag & FS) / 4];
      case 2 :
        return ACGT[(flag & FS) / 4] + ACGT[(flag & SS) / 16];
    }
    return "";
  }

  public boolean hasNullFreq() {
    return isNullFrequencies(this.frequencies);
  }

  public static boolean isNullFrequencies(double[] ds){
    for(double d: ds)
      if(d != 0)
        return false;
    return true;
  }

  @Override
  public int compareTo(GnomADLine that) {
    return this.canonicalVariant.compareTo(that.canonicalVariant);
    //ignore frequencies
  }

  /**
   * Parses GnomAD frequencies as doubles
   * @param value the string values (comma separated)
   * @param altNumber the number of alternate alleles for the variant
   * @return array of frequencies
   */
  public static double[] readVCFFrequencies(String value, int altNumber){
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
}
