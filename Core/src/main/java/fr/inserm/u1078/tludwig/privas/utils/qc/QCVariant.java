package fr.inserm.u1078.tludwig.privas.utils.qc;

import java.util.HashMap;

/**
 * Mini Variant (on which to perform QC)
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-11-13
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
class QCVariant {
  //public static final String KEY_DP = "DP";
  public static final String KEY_AD = "AD";
  public static final String KEY_GQ = "GQ";

  public static final String KEY_QD = "QD";
  public static final String KEY_FS = "FS";
  public static final String KEY_SOR = "SOR";
  public static final String KEY_MQ = "MQ";
  public static final String KEY_READ_POS_RANKSUM = "ReadPosRankSum";
  public static final String KEY_INBREEDING = "InbreedingCoeff";
  public static final String KEY_MQRANKSUM = "MQRankSum";

  public static final int FILTER_QD = 1;
  public static final int FILTER_INBREEDING = 2;
  public static final int FILTER_MQ_RANKSUM = 4;
  public static final int FILTER_FS = 8;
  public static final int FILTER_SOR = 16;
  public static final int FILTER_MQ = 32;
  public static final int FILTER_READ_POS_RANKSUM = 64;
  public static final int FILTER_AB_HET_DEV = 128;
  public static final int FILTER_CALLRATE = 256;
  //public static final int FILTER_HQ = 512;
  public static final int FILTER_NUMBER = 512;

  private final boolean hasSNP;
  private final int alleleNumber;
  private final HashMap<String, String> infos;
  //private final int iDP;
  private final int iGQ;
  private final int iAD;
  private final Genotype[] genotypes;

  QCVariant(String line){
    String[] f = line.split("\t");
    String ref = f[3];
    String[] alt = f[4].split(",");
    alleleNumber = 1 + alt.length;

    boolean snp = false;
    for (int a = 1; a < alleleNumber; a++)
      if (isSNP(ref, alt[a-1])) {
        snp = true;
        break;
      }
    hasSNP = snp;

    infos = new HashMap<>();
    for(String info : f[7].split(";")){
      String[] kv = info.split("=");
      if(kv.length == 1)
        infos.put(kv[0], ".");
      else
        infos.put(kv[0], kv[1]);
    }
    String[] format = f[8].split(":");
    //int dp = -1;
    int gq = -1;
    int ad = -1;
    for(int i = 0 ; i < format.length; i++){
      switch(format[i]){
       /* case KEY_DP:
          dp = i;
          break;*/
        case KEY_GQ:
          gq = i;
          break;
        case KEY_AD:
          ad = i;
          break;
      }
    }
    //this.iDP = dp;
    this.iAD = ad;
    this.iGQ = gq;
    genotypes = new Genotype[f.length - 9];
    for(int i = 9; i < f.length; i++)
      genotypes[i - 9] = new Genotype(f[i], alleleNumber);

    //printSummary();
  }

  static boolean isSNP(String ref, String alt){
    if (ref.length() != alt.length())
      return false;

    if (ref.charAt(0) == '.')
      return false;
    if (alt.charAt(0) == '.')
      return false;

    int diff = 0;
    for (int i = 0; i < ref.length(); i++)
      if (ref.charAt(i) != alt.charAt(i))
        diff++;

    return (diff == 1);
  }

  /**
   * filters variant with a value below the threshold (or missing)
   * @param threshold the threshold from QCParam
   * @param value the value from the variant
   * @return TRUE - if the variant should be filtered according to these values
   */
  boolean filterMinRejectMissing(double threshold, double value){
    if(isEnabled(threshold)) {
      if (isMissing(value))
        return true;
      if (value < threshold)
        return true;
    }
    return false;
  }

  /**
   * filters variant with a value above the threshold (or missing)
   * @param threshold the threshold from QCParam
   * @param value the value from the variant
   * @return TRUE - if the variant should be filtered according to these values
   */
  boolean filterMaxRejectMissing(double threshold, double value){
    if(isEnabled(threshold)) {
      if (isMissing(value))
        return true;
      if (value > threshold)
        return true;
    }
    return false;
  }

  /**
   * filters variant with a value below the threshold (missing values are not filtered)
   * @param threshold the threshold from QCParam
   * @param value the value from the variant
   * @return TRUE - if the variant should be filtered according to these values
   */
  boolean filterMinAllowMissing(double threshold, double value){
    if(isEnabled(threshold)) {
      if (isMissing(value))
        return false;
      if (value < threshold)
        return true;
    }
    return false;
  }

  /**
   * filters variant with a value above the threshold (missing values are not filtered)
   * @param threshold the threshold from QCParam
   * @param value the value from the variant
   * @return TRUE - if the variant should be filtered according to these values
   */
  boolean filterMaxAllowMissing(double threshold, double value){
    if(isEnabled(threshold)) {
      if (isMissing(value))
        return false;
      if (value > threshold)
        return true;
    }
    return false;
  }

  boolean filterMinAllowMissing(double threshold, String key){
    //double val = getValue(key);
    return filterMinAllowMissing(threshold, getValue(key));
    //System.out.println(key + " : "+val+" < "+threshold+" ["+filter+"]");
  }

  boolean filterMinRejectMissing(double threshold, String key){
    //double val = getValue(key);
    return filterMinRejectMissing(threshold, getValue(key));
    //System.out.println(key + " : "+val+" < "+threshold+" ["+filter+"]");
  }

  @SuppressWarnings("unused")
  boolean filterMaxAllowMissing(double threshold, String key){
    //double val = getValue(key);
    return filterMaxAllowMissing(threshold, getValue(key));
    //System.out.println(key + " : "+val+" > "+threshold+" ["+filter+"]");
  }

  boolean filterMaxRejectMissing(double threshold, String key){
    //double val = getValue(key);
    return filterMaxRejectMissing(threshold, getValue(key));
    //System.out.println(key + " : "+val+" > "+threshold+" ["+filter+"]");
  }

  int filter(QCParam qcParam){//In doc, multiallelic variants should be split
    //Variant level filters
    int filter = 0;
    // QD
    if(filterMinRejectMissing(qcParam.getMinQD(), KEY_QD))
      filter += FILTER_QD;
    // Inbreeding coefficient
    if(filterMinAllowMissing(qcParam.getMinInbreeding(), KEY_INBREEDING))
      filter += FILTER_INBREEDING;
    // MQRankSum
    if(filterMinAllowMissing(qcParam.getMinMQRanksum(), KEY_MQRANKSUM))
      filter += FILTER_MQ_RANKSUM;
    //FS
    if(hasSNP) {
      if (filterMaxRejectMissing(qcParam.getSnpMaxFS(), KEY_FS))
        filter += FILTER_FS;
    } else {
      if (filterMaxRejectMissing(qcParam.getIndelMaxFS(), KEY_FS))
        filter += FILTER_FS;
    }
    //SOR
    if(hasSNP) {
      if (filterMaxRejectMissing(qcParam.getSnpMaxSOR(), KEY_SOR))
        filter += FILTER_SOR;
    } else {
      if(filterMaxRejectMissing(qcParam.getIndelMaxSOR(), KEY_SOR))
        filter += FILTER_SOR;
    }
    //MQ
    if(hasSNP) {
      if (filterMinRejectMissing(qcParam.getSnpMinMQ(), KEY_MQ))
        filter += FILTER_MQ;
    } else {
      if(filterMinRejectMissing(qcParam.getIndelMinMQ(), KEY_MQ))
        filter += FILTER_MQ;
    }
    //ReadPosRankSum
    if(hasSNP) {
      if (filterMinAllowMissing(qcParam.getSnpMinRPRS(), KEY_READ_POS_RANKSUM))
        filter += FILTER_READ_POS_RANKSUM;
    } else {
      if(filterMinAllowMissing(qcParam.getIndelMinRPRS(), KEY_READ_POS_RANKSUM))
        filter += FILTER_READ_POS_RANKSUM;
    }

    //detailed
    double totalCalled = 0;
    //double totalHQ = 0;
    final double totalSample = this.genotypes.length;

    double[] numeratorHets = new double[alleleNumber];
    double[] denominatorHets = new double[alleleNumber];

    for(Genotype geno : genotypes){
      if(!geno.isMissing()){
        if(geno.isHQ(qcParam) && geno.checkAB(qcParam)) {
          totalCalled++;
          //totalHQ++;
          if (geno.isHeterozygousDiploid()) {
            final int[] ad = geno.getAD();
            numeratorHets[geno.gt0] += ad[geno.gt0];
            numeratorHets[geno.gt1] += ad[geno.gt1];
            denominatorHets[geno.gt0] += ad[geno.gt0] + ad[geno.gt1];
            denominatorHets[geno.gt1] += ad[geno.gt0] + ad[geno.gt1];
          }
        }
      }
    }

    double maxABHetDev = qcParam.getMaxABHetDev();
    if (isEnabled(maxABHetDev))
      for (int h = 0; h < alleleNumber; h++)
        if (denominatorHets[h] != 0 && Math.abs(0.5 - (numeratorHets[h] / denominatorHets[h])) > maxABHetDev) {
          filter += FILTER_AB_HET_DEV;
          break;
        }

    double callRate = totalCalled / totalSample;
    //double hqRatio = totalHQ / totalSample;

    //CallRate
    if(filterMinRejectMissing(qcParam.getMinCallrate(), callRate))
      filter += FILTER_CALLRATE;

    ////HQ Ratio
    //if(filterMinRejectMissing(qcParam.getMinHQRatio(), hqRatio))
    //  filter += FILTER_HQ;

    return filter;
  }

  static boolean isEnabled(double d){
    return !Double.isNaN(d);
  }

  static boolean isMissing(double d){
    return Double.isNaN(d);
  }

  private double getValue(String key){
    try {
      return Double.parseDouble(this.infos.get(key));
    } catch (NumberFormatException | NullPointerException e) {
      return Double.NaN;
    }
  }

  public class Genotype{
    final int gt0;
    final int gt1;
    //final double dp;
    final double sumAD;
    final int[] ad;
    final double gq;

    public Genotype(String geno, int alleleNumber){
      String[] f = geno.split(":");
      //parse genotype
      if(f[0].startsWith(".")) {
        gt0 = -1;
        gt1 = -1;
      } else {
        String[] g = f[0].split("\\|");
        if(g.length == 1)
          g = f[0].split("/");
        gt0 = Integer.parseInt(g[0]);
        if(g.length > 1)
          gt1 = Integer.parseInt(g[1]);
        else
          gt1 = -1;
      }
      double tmp = Double.NaN;
      /*
      //parse dp
      try {
        tmp = Integer.parseInt(f[iDP]);
      } catch (NumberFormatException |ArrayIndexOutOfBoundsException ignore) {
        //ignore
      }
      this.dp = tmp;
      tmp = Double.NaN;
       */
      //parse gq

      try {
        tmp = Integer.parseInt(f[iGQ]);
      } catch (NumberFormatException |ArrayIndexOutOfBoundsException ignore) {
        //ignore
      }
      this.gq = tmp;
      //parse sumAD
      int[] tmpAD;
      try {
        tmp = 0;
        String[] ads = f[iAD].split(",");
        tmpAD = new int[ads.length];
        for(int i = 0 ; i < ads.length; i++) {
          tmpAD[i] = Integer.parseInt(ads[i]);
          tmp += tmpAD[i];
        }
      } catch (NumberFormatException |ArrayIndexOutOfBoundsException ignore) {
        tmp = Double.NaN;
        tmpAD = new int[alleleNumber];
      }
      this.sumAD = tmp;
      this.ad = tmpAD;
    }

    boolean isMissing(){
      return gt0 == -1;
    }

    boolean checkAB(QCParam qcParam){
      if(isEnabled(qcParam.getMaxABGenoDev()))
      if (gt0 != -1 && gt1 != -1 && gt0 != gt1) {
        if (ad != null) {
          double numerator = ad[gt0];
          double denominator = ad[gt0] + ad[gt1];
          double val = Math.abs(0.5 - (numerator / denominator));
          return denominator != 0 && val <= qcParam.getMaxABGenoDev();
        }
      }
      return true;
    }

    boolean isHQ(QCParam qcParam){
      if(isEnabled(qcParam.getMinDP())){
        if(filterMinAllowMissing(qcParam.getMinDP(), sumAD))
          return false;
        if(filterMaxAllowMissing(qcParam.getMaxDP(), sumAD))
          return false;
        if(filterMinAllowMissing(qcParam.getMinGQ(), gq))
          return false;
      }
      return true;
    }

    public int[] getAD() {
      return ad;
    }

    boolean isHeterozygousDiploid(){
      if(gt0 == -1 || gt1 == -1)
        return false;
      return gt0 != gt1;
    }
  }
}
