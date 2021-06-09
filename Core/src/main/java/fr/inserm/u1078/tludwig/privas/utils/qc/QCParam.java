package fr.inserm.u1078.tludwig.privas.utils.qc;

import java.io.*;
import java.util.Objects;

/**
 * deserialization of QC Parameters file content
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2020-11-12
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class QCParam {

  private static final double DISABLED = Double.NaN;

  public static final String LABEL_MIN_QD              = "Minimum quality by depth";
  public static final String LABEL_MAX_ABHET_DEV       = "Maximum deviation from 0.5 in Mean allelic balance calculated over heterozygous genotypes";
  public static final String LABEL_MAX_AB_GENO_DEV     = "Maximum deviation from 0.5 in Allele Balance, for a genotype to be HQ";
  public static final String LABEL_MIN_INBREEDING      = "Minimum Inbreeding coefficient";
  public static final String LABEL_MIN_MQRANKSUM       = "Minimum Z-score From Wilcoxon rank sum test of Alt vs. Ref read mapping qualities";
  public static final String LABEL_MAX_FS_INDEL        = "Maximum phred-scaled p-value using Fisher's exact test to detect strand bias for INDELs";
  public static final String LABEL_MAX_FS_SNP          = "Maximum phred-scaled p-value using Fisher's exact test to detect strand bias for SNVs";
  public static final String LABEL_MAX_SOR_INDEL       = "Maximum Symmetric Odds Ratio of 2x2 contingency table to detect strand bias for INDELs";
  public static final String LABEL_MAX_SOR_SNP         = "Maximum Symmetric Odds Ratio of 2x2 contingency table to detect strand bias for SNVs";
  public static final String LABEL_MIN_MQ_INDEL        = "Minimum overall mapping quality of reads supporting a variant call for INDELs";
  public static final String LABEL_MIN_MQ_SNP          = "Minimum overall mapping quality of reads supporting a variant call for SNVs";
  public static final String LABEL_MIN_RPRS_INDEL      = "Minimum Z-score from Wilcoxon rank sum test of Alt vs. Ref read position bias for INDELs";
  public static final String LABEL_MIN_RPRS_SNP        = "Minimum Z-score from Wilcoxon rank sum test of Alt vs. Ref read position bias for SNVs";
  public static final String LABEL_MIN_GQ              = "Minimum genotype quality for a genotype to be HQ";
  public static final String LABEL_MIN_DP              = "Minimum depth(Sum of ADs) for a genotype to be HQ";
  public static final String LABEL_MAX_DP              = "Maximum depth(Sum of ADs) for a genotype to be HQ (exceptionally high DP might be a sign of misalignment)";
  public static final String LABEL_MIN_CALLRATE        = "Minimum call rate";
  //public static final String LABEL_MIN_HQ_RATIO        = "Minimum ratio of High Quality Genotype (ie MIN_DP<= DP <= MAX_DP and MIN_GQ <= GQ)";
  //public static final String LABEL_MIN_ALT_HQ          = "Minimum number of High Quality Alternate Genotype";
  public static final String LABEL_MIN_FISHER_CALLRATE = "Minimum p-value of Fisher's exact test comparing number of missing genotypes between groups";

  public static final String KEY_MIN_QD              = "MIN_QD";
  public static final String KEY_MAX_ABHET_DEV       = "MAX_ABHET_DEV";
  public static final String KEY_MAX_AB_GENO_DEV     = "MAX_AB_GENO_DEV";
  public static final String KEY_MIN_INBREEDING      = "MIN_INBREEDING";
  public static final String KEY_MIN_MQRANKSUM       = "MIN_MQRANKSUM";
  public static final String KEY_MAX_FS_INDEL        = "MAX_FS_INDEL";
  public static final String KEY_MAX_FS_SNP          = "MAX_FS_SNP";
  public static final String KEY_MAX_SOR_INDEL       = "MAX_SOR_INDEL";
  public static final String KEY_MAX_SOR_SNP         = "MAX_SOR_SNP";
  public static final String KEY_MIN_MQ_INDEL        = "MIN_MQ_INDEL";
  public static final String KEY_MIN_MQ_SNP          = "MIN_MQ_SNP";
  public static final String KEY_MIN_RPRS_INDEL      = "MIN_RPRS_INDEL";
  public static final String KEY_MIN_RPRS_SNP        = "MIN_RPRS_SNP";
  public static final String KEY_MIN_GQ              = "MIN_GQ";
  public static final String KEY_MIN_DP              = "MIN_DP";
  public static final String KEY_MAX_DP              = "MAX_DP";
  public static final String KEY_MIN_CALLRATE        = "MIN_CALLRATE";
  //public static final String KEY_MIN_HQ_RATIO        = "MIN_HQ_RATIO";
  //public static final String KEY_MIN_ALT_HQ          = "MIN_ALT_HQ"; //not implementable in a distributed QC
  public static final String KEY_MIN_FISHER_CALLRATE = "MIN_FISHER_CALLRATE";

  public static final double DEF_MIN_QD = 2;
  public static final double DEF_MAX_ABHET_DEV = 0.25;
  public static final double DEF_MAX_AB_GENO_DEV = 0.25;
  public static final double DEF_MIN_INBREEDING = -.8;
  public static final double DEF_MIN_MQRANKSUM = -12.5;
  public static final double DEF_MAX_FS_INDEL = 200;
  public static final double DEF_MAX_FS_SNP = 60;
  public static final double DEF_MAX_SOR_INDEL = 10;
  public static final double DEF_MAX_SOR_SNP = 3;
  public static final double DEF_MIN_MQ_INDEL = 10;
  public static final double DEF_MIN_MQ_SNP = 40;
  public static final double DEF_MIN_RPRS_INDEL = -20;
  public static final double DEF_MIN_RPRS_SNP = -8;
  public static final double DEF_MIN_GQ = 20;
  public static final double DEF_MIN_DP = 10;
  public static final double DEF_MAX_DP = DISABLED;
  public static final double DEF_MIN_CALLRATE = .9;
  //public static final double DEF_MIN_HQ_RATIO = .8;
  //public static final double DEF_MIN_ALT_HQ = 1;
  public static final double DEF_MIN_FISHER_CALLRATE = 0.001;

  public static final QCParam DEFAULT_QC_PARAM = new QCParam();

  public static final String FIELD_SEP = ";";
  public static final String KV_SEP = "=";

  private double minQD = DEF_MIN_QD;
  private double maxABHetDev = DEF_MAX_ABHET_DEV;
  private double maxABGenoDev = DEF_MAX_AB_GENO_DEV;
  private double minInbreeding = DEF_MIN_INBREEDING;
  private double minMQRanksum = DEF_MIN_MQRANKSUM;
  private double indelMaxFS = DEF_MAX_FS_INDEL;
  private double snpMaxFS = DEF_MAX_FS_SNP;
  private double indelMaxSOR = DEF_MAX_SOR_INDEL;
  private double snpMaxSOR = DEF_MAX_SOR_SNP;
  private double indelMinMQ = DEF_MIN_MQ_INDEL;
  private double snpMinMQ = DEF_MIN_MQ_SNP;
  private double indelMinRPRS = DEF_MIN_RPRS_INDEL;
  private double snpMinRPRS = DEF_MIN_RPRS_SNP;
  private double minGQ = DEF_MIN_GQ;
  private double minDP = DEF_MIN_DP;
  private double maxDP = DEF_MAX_DP;
  private double minCallrate = DEF_MIN_CALLRATE;
  //private double minHQRatio = DEF_MIN_HQ_RATIO;
  //private double minAltHQ = DEF_MIN_ALT_HQ;
  private double minFisherCallrate = DEF_MIN_FISHER_CALLRATE;

  public QCParam(){

  }

  public QCParam(String filename) throws IOException, QCException{
    BufferedReader in = new BufferedReader(new FileReader(filename));
    String line;
    while((line = in.readLine()) != null){
      process(line);
    }
    in.close();
  }

  public void process(String line) throws QCException {
    if(line == null)
      return;
    if(line.isEmpty())
      return;

    String[] kv = line.trim().split("#")[0].split("\\s+");
    if(kv.length < 2)
      return;
    this.setValue(kv[0], kv[1]);
  }

  public void save(String filename) throws IOException {
    PrintWriter out = new PrintWriter(new FileWriter(filename));

    out.println("#"+LABEL_MIN_QD);
    out.println(KEY_MIN_QD+"\t"+asString(minQD));
    out.println("#"+LABEL_MAX_AB_GENO_DEV);
    out.println(KEY_MAX_AB_GENO_DEV+"\t"+asString(maxABGenoDev));
    out.println("#"+LABEL_MAX_ABHET_DEV);
    out.println(KEY_MAX_ABHET_DEV+"\t"+asString(maxABHetDev));
    out.println("#"+LABEL_MIN_INBREEDING);
    out.println(KEY_MIN_INBREEDING+"\t"+asString(minInbreeding));
    out.println("#"+LABEL_MIN_MQRANKSUM);
    out.println(KEY_MIN_MQRANKSUM+"\t"+asString(minMQRanksum));
    out.println("#"+LABEL_MAX_FS_INDEL);
    out.println(KEY_MAX_FS_INDEL+"\t"+asString(indelMaxFS));
    out.println("#"+LABEL_MAX_FS_SNP);
    out.println(KEY_MAX_FS_SNP+"\t"+asString(snpMaxFS));
    out.println("#"+LABEL_MAX_SOR_INDEL);
    out.println(KEY_MAX_SOR_INDEL+"\t"+asString(indelMaxSOR));
    out.println("#"+LABEL_MAX_SOR_SNP);
    out.println(KEY_MAX_SOR_SNP+"\t"+asString(snpMaxSOR));
    out.println("#"+LABEL_MIN_MQ_INDEL);
    out.println(KEY_MIN_MQ_INDEL+"\t"+asString(indelMinMQ));
    out.println("#"+LABEL_MIN_MQ_SNP);
    out.println(KEY_MIN_MQ_SNP+"\t"+asString(snpMinMQ));
    out.println("#"+LABEL_MIN_RPRS_INDEL);
    out.println(KEY_MIN_RPRS_INDEL+"\t"+asString(indelMinRPRS));
    out.println("#"+LABEL_MIN_RPRS_SNP);
    out.println(KEY_MIN_RPRS_SNP+"\t"+asString(snpMinRPRS));
    out.println("#"+LABEL_MIN_GQ);
    out.println(KEY_MIN_GQ+"\t"+asString(minGQ));
    out.println("#"+LABEL_MIN_DP);
    out.println(KEY_MIN_DP+"\t"+asString(minDP));
    out.println("#"+LABEL_MAX_DP);
    out.println(KEY_MAX_DP+"\t"+asString(maxDP));
    out.println("#"+LABEL_MIN_CALLRATE);
    out.println(KEY_MIN_CALLRATE+"\t"+asString(minCallrate));
    //out.println("#"+LABEL_MIN_HQ_RATIO);
    //out.println(KEY_MIN_HQ_RATIO+"\t"+asString(minHQRatio));
    //out.println("#"+LABEL_MIN_ALT_HQ);
    //out.println(KEY_MIN_ALT_HQ+"\t"+asString(minAltHQ));
    out.println("#"+LABEL_MIN_FISHER_CALLRATE);
    out.println(KEY_MIN_FISHER_CALLRATE+"\t"+asString(minFisherCallrate));

    out.close();
  }

  public String serialize(){
    StringBuilder sb = new StringBuilder();
    sb.append(KEY_MIN_QD).append(KV_SEP).append(minQD).append(FIELD_SEP);
    sb.append(KEY_MAX_ABHET_DEV).append(KV_SEP).append(maxABHetDev).append(FIELD_SEP);
    sb.append(KEY_MAX_AB_GENO_DEV).append(KV_SEP).append(maxABGenoDev).append(FIELD_SEP);
    sb.append(KEY_MIN_INBREEDING).append(KV_SEP).append(minInbreeding).append(FIELD_SEP);
    sb.append(KEY_MIN_MQRANKSUM).append(KV_SEP).append(minMQRanksum).append(FIELD_SEP);
    sb.append(KEY_MAX_FS_INDEL).append(KV_SEP).append(indelMaxFS).append(FIELD_SEP);
    sb.append(KEY_MAX_FS_SNP).append(KV_SEP).append(snpMaxFS).append(FIELD_SEP);
    sb.append(KEY_MAX_SOR_INDEL).append(KV_SEP).append(indelMaxSOR).append(FIELD_SEP);
    sb.append(KEY_MAX_SOR_SNP).append(KV_SEP).append(snpMaxSOR).append(FIELD_SEP);
    sb.append(KEY_MIN_MQ_INDEL).append(KV_SEP).append(indelMinMQ).append(FIELD_SEP);
    sb.append(KEY_MIN_MQ_SNP).append(KV_SEP).append(snpMinMQ).append(FIELD_SEP);
    sb.append(KEY_MIN_RPRS_INDEL).append(KV_SEP).append(indelMinRPRS).append(FIELD_SEP);
    sb.append(KEY_MIN_RPRS_SNP).append(KV_SEP).append(snpMinRPRS).append(FIELD_SEP);
    sb.append(KEY_MIN_GQ).append(KV_SEP).append(minGQ).append(FIELD_SEP);
    sb.append(KEY_MIN_DP).append(KV_SEP).append(minDP).append(FIELD_SEP);
    sb.append(KEY_MAX_DP).append(KV_SEP).append(maxDP).append(FIELD_SEP);
    sb.append(KEY_MIN_CALLRATE).append(KV_SEP).append(minCallrate).append(FIELD_SEP);
    //sb.append(KEY_MIN_HQ_RATIO).append(KV_SEP).append(minHQRatio).append(FIELD_SEP);
    //sb.append(KEY_MIN_ALT_HQ).append(KV_SEP).append(minAltHQ).append(FIELD_SEP);
    sb.append(KEY_MIN_FISHER_CALLRATE).append(KV_SEP).append(minFisherCallrate);
    return sb.toString();
  }

  public static QCParam deserialize(String content) throws QCException {
    QCParam qcParam = new QCParam();
    for(String s : content.split(FIELD_SEP)){
      String[] kv = s.split(KV_SEP);
      qcParam.setValue(kv[0], kv[1]);
    }
    return qcParam;
  }

  public void unsafeSetValue(String key, String asString){
    try {
      this.setValue(key, asString);
    } catch (QCException ignore) {
      //Ignore
    }
  }

  public void setValue(String key, String asString) throws QCException {
    double value = parse(asString);
    switch(key){
      case KEY_MIN_QD :
        this.setMinQD(value);
        break;
      case KEY_MAX_ABHET_DEV :
        this.setMaxABHetDev(value);
        break;
      case KEY_MAX_AB_GENO_DEV :
        this.setMaxABGenoDev(value);
        break;
      case KEY_MIN_INBREEDING :
        this.setMinInbreeding(value);
        break;
      case KEY_MIN_MQRANKSUM :
        this.setMinMQRanksum(value);
        break;
      case KEY_MAX_FS_INDEL :
        this.setIndelMaxFS(value);
        break;
      case KEY_MAX_FS_SNP :
        this.setSnpMaxFS(value);
        break;
      case KEY_MAX_SOR_INDEL :
        this.setIndelMaxSOR(value);
        break;
      case KEY_MAX_SOR_SNP :
        this.setSnpMaxSOR(value);
        break;
      case KEY_MIN_MQ_INDEL :
        this.setIndelMinMQ(value);
        break;
      case KEY_MIN_MQ_SNP :
        this.setSnpMinMQ(value);
        break;
      case KEY_MIN_RPRS_INDEL :
        this.setIndelMinRPRS(value);
        break;
      case KEY_MIN_RPRS_SNP :
        this.setSnpMinRPRS(value);
        break;
      case KEY_MIN_GQ :
        this.setMinGQ(value);
        break;
      case KEY_MIN_DP :
        this.setMinDP(value);
        break;
      case KEY_MAX_DP :
        this.setMaxDP(value);
        break;
      case KEY_MIN_CALLRATE :
        this.setMinCallrate(value);
        break;
      //case KEY_MIN_HQ_RATIO :
      //  this.setMinHQRatio(value);
      //  break;
      //case KEY_MIN_ALT_HQ :
      //  this.setMinAltHQ(value);
      //  break;
      case KEY_MIN_FISHER_CALLRATE :
        this.setMinFisherCallrate(value);
        break;
      default :
        throw new QCException("Unexpected key ["+key+"] value {"+asString+"}");
    }
  }

  public static String asString(double d){
    return Double.isNaN(d) ? "disabled" : ""+d;
  }

  public double parse(String s){
    try{
      return Double.parseDouble(s);
    } catch (NumberFormatException e){
      return DISABLED;
    }
  }

  public double getMinQD() {
    return minQD;
  }

  public void setMinQD(double minQD) {
    this.minQD = minQD;
  }

  public double getMaxABHetDev() {
    return maxABHetDev;
  }

  public void setMaxABHetDev(double maxABHetDev) {
    this.maxABHetDev = maxABHetDev;
  }

  public double getMaxABGenoDev() {
    return maxABGenoDev;
  }

  public void setMaxABGenoDev(double maxABGenoDev) {
    this.maxABGenoDev = maxABGenoDev;
  }

  public double getMinInbreeding() {
    return minInbreeding;
  }

  public void setMinInbreeding(double minInbreeding) {
    this.minInbreeding = minInbreeding;
  }

  public double getMinMQRanksum() {
    return minMQRanksum;
  }

  public void setMinMQRanksum(double minMQRanksum) {
    this.minMQRanksum = minMQRanksum;
  }

  public double getIndelMaxFS() {
    return indelMaxFS;
  }

  public void setIndelMaxFS(double indelMaxFS) {
    this.indelMaxFS = indelMaxFS;
  }

  public double getSnpMaxFS() {
    return snpMaxFS;
  }

  public void setSnpMaxFS(double snpMaxFS) {
    this.snpMaxFS = snpMaxFS;
  }

  public double getIndelMaxSOR() {
    return indelMaxSOR;
  }

  public void setIndelMaxSOR(double indelMaxSOR) {
    this.indelMaxSOR = indelMaxSOR;
  }

  public double getSnpMaxSOR() {
    return snpMaxSOR;
  }

  public void setSnpMaxSOR(double snpMaxSOR) {
    this.snpMaxSOR = snpMaxSOR;
  }

  public double getIndelMinMQ() {
    return indelMinMQ;
  }

  public void setIndelMinMQ(double indelMinMQ) {
    this.indelMinMQ = indelMinMQ;
  }

  public double getSnpMinMQ() {
    return snpMinMQ;
  }

  public void setSnpMinMQ(double snpMinMQ) {
    this.snpMinMQ = snpMinMQ;
  }

  public double getIndelMinRPRS() {
    return indelMinRPRS;
  }

  public void setIndelMinRPRS(double indelMinRPRS) {
    this.indelMinRPRS = indelMinRPRS;
  }

  public double getSnpMinRPRS() {
    return snpMinRPRS;
  }

  public void setSnpMinRPRS(double snpMinRPRS) {
    this.snpMinRPRS = snpMinRPRS;
  }

  public double getMinGQ() {
    return minGQ;
  }

  public void setMinGQ(double minGQ) {
    this.minGQ = minGQ;
  }

  public double getMinDP() {
    return minDP;
  }

  public void setMinDP(double minDP) {
    this.minDP = minDP;
  }

  public double getMaxDP() {
    return maxDP;
  }

  public void setMaxDP(double maxDP) {
    this.maxDP = maxDP;
  }

  public double getMinCallrate() {
    return minCallrate;
  }

  public void setMinCallrate(double minCallrate) {
    this.minCallrate = minCallrate;
  }

  //public double getMinHQRatio() {
  //  return minHQRatio;
  //}

  //public void setMinHQRatio(double minHQRatio) {
  //  this.minHQRatio = minHQRatio;
  //}

  //public double getMinAltHQ() {
  //  return minAltHQ;
  //}

  //public void setMinAltHQ(double minAltHQ) {
  //  this.minAltHQ = minAltHQ;
  //}

  public double getMinFisherCallrate() {
    return minFisherCallrate;
  }

  public void setMinFisherCallrate(double minFisherCallrate) {
    this.minFisherCallrate = minFisherCallrate;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
            minQD,
            maxABHetDev,
            maxABGenoDev,
            minInbreeding,
            minMQRanksum,
            indelMaxFS,
            snpMaxFS,
            indelMaxSOR,
            snpMaxSOR,
            indelMinMQ,
            snpMinMQ,
            indelMinRPRS,
            snpMinRPRS,
            minGQ,
            minDP,
            maxDP,
            minCallrate,
            //minHQRatio,
            //minAltHQ,
            minFisherCallrate
    );
  }

  @Override
  public boolean equals(Object o) {
    if(o == this)
      return true;

    if(!(o instanceof QCParam))
      return false;

    QCParam qcParam = (QCParam)o;

    if(this.minQD != qcParam.minQD) return false;
    if(this.maxABHetDev != qcParam.maxABHetDev) return false;
    if(this.maxABGenoDev != qcParam.maxABGenoDev) return false;
    if(this.minInbreeding != qcParam.minInbreeding) return false;
    if(this.minMQRanksum != qcParam.minMQRanksum) return false;
    if(this.indelMaxFS != qcParam.indelMaxFS) return false;
    if(this.snpMaxFS != qcParam.snpMaxFS) return false;
    if(this.indelMaxSOR != qcParam.indelMaxSOR) return false;
    if(this.snpMaxSOR != qcParam.snpMaxSOR) return false;
    if(this.indelMinMQ != qcParam.indelMinMQ) return false;
    if(this.snpMinMQ != qcParam.snpMinMQ) return false;
    if(this.indelMinRPRS != qcParam.indelMinRPRS) return false;
    if(this.snpMinRPRS != qcParam.snpMinRPRS) return false;
    if(this.minGQ != qcParam.minGQ) return false;
    if(this.minDP != qcParam.minDP) return false;
    if(this.maxDP != qcParam.maxDP) return false;
    if(this.minCallrate != qcParam.minCallrate) return false;
    //if(this.minHQRatio != qcParam.minHQRatio) return false;
    //if(this.minAltHQ != qcParam.minAltHQ) return false;
    if(this.minFisherCallrate != qcParam.minFisherCallrate) return false;

    return true;
  }
}
