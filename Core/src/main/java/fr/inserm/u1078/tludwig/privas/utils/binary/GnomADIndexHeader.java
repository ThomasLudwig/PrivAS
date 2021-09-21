package fr.inserm.u1078.tludwig.privas.utils.binary;

import fr.inserm.u1078.tludwig.privas.constants.Constants;
import fr.inserm.u1078.tludwig.privas.constants.FileFormat;
import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.instances.Instance;

import java.util.Date;

/**
 * Header for GnomAD Index File
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-08-24
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class GnomADIndexHeader {

  private final String version;
  private final String[] exomePath;
  private final String[] genomePath;
  private final Date date;
  private final long exomeVariants;
  private final long genomeVariants;
  private final long exomeBytes;
  private final long genomeBytes;
  private final int bufferSize;

  public GnomADIndexHeader(String version, String[] exomePath, String[] genomePath, Date date, long exomeVariants, long genomeVariants, long exomeBytes, long genomeBytes, int bufferSize) {
    this.version = version;
    this.exomePath = exomePath;
    this.genomePath = genomePath;
    this.date = date;
    this.exomeVariants = exomeVariants;
    this.genomeVariants = genomeVariants;
    this.exomeBytes = exomeBytes;
    this.genomeBytes = genomeBytes;
    this.bufferSize = bufferSize;
  }

  public static GnomADIndexHeader parseHeader(String s, Instance log) {
    String v = null;
    String[] eP = null;
    String[] gP = null;
    Date d = null;
    long eV = -1;
    long gV = -1;
    long eB = -1;
    long gB = -1;
    int b = -1;
    for(String line : s.split("\n")){
      String[] f = line.split("\t");
      switch(f[0]){
        case FileFormat.TAG_VERSION:
          v = f[1];
          break;
        case FileFormat.TAG_EXOME_PATH:
          eP = f[1].split(",");
          break;
        case FileFormat.TAG_GENOME_PATH:
          gP = f[1].split(",");
          break;
        case FileFormat.TAG_DATE:
          try {
            d = Constants.DF_DAY_TIME.parse(f[1]);
          } catch(Exception e){
            d = new Date(0);
          }
          break;
        case FileFormat.TAG_EXOME_VARIANTS:
          try{
            eV = Long.parseLong(f[1]);
          } catch(NumberFormatException e) {
            log.logWarning(MSG.cat(MSG.KO_PARSE_LONG, line));
          }
          break;
        case FileFormat.TAG_GENOME_VARIANTS:
          try{
            gV = Long.parseLong(f[1]);
          } catch(NumberFormatException e) {
            log.logWarning(MSG.cat(MSG.KO_PARSE_LONG, line));
          }
          break;
        case FileFormat.TAG_EXOME_SIZE:
          try{
            eB = Long.parseLong(f[1]);
          } catch(NumberFormatException e) {
            log.logWarning(MSG.cat(MSG.KO_PARSE_LONG, line));
          }
          break;
        case FileFormat.TAG_GENOME_SIZE:
          try{
            gB = Long.parseLong(f[1]);
          } catch(NumberFormatException e) {
            log.logWarning(MSG.cat(MSG.KO_PARSE_LONG, line));
          }
          break;
        case FileFormat.TAG_BUFFER_SIZE:
          try{
            b = Integer.parseInt(f[1]);
          } catch(NumberFormatException e) {
            log.logWarning(MSG.cat(MSG.KO_PARSE_INT, line));
          }
          break;
      }
    }

    return new GnomADIndexHeader(v, eP, gP, d, eV, gV, eB, gB, b);
  }

  @Override
  public String toString() {
    return FileFormat.TAG_VERSION + "\t" + version + "\n" +
            FileFormat.TAG_EXOME_PATH + "\t" + String.join(",", exomePath) + "\n" +
            FileFormat.TAG_GENOME_PATH + "\t" + String.join(",", genomePath) + "\n" +
            FileFormat.TAG_DATE + "\t" + getDateAsString() + "\n" +
            FileFormat.TAG_EXOME_VARIANTS + "\t" + exomeVariants + "\n" +
            FileFormat.TAG_GENOME_VARIANTS + "\t" + genomeVariants + "\n" +
            FileFormat.TAG_EXOME_SIZE + "\t" + exomeBytes + "\t" + getExomeSize() + "\n" +
            FileFormat.TAG_GENOME_SIZE + "\t" + genomeBytes + "\t" + getGenomeSize() + "\n" +
            FileFormat.TAG_BUFFER_SIZE + "\t" + bufferSize + "\t";
  }

  public String getVersion() {
    return version;
  }

  @SuppressWarnings("unused")
  public String[] getExomePath() { return exomePath; }

  @SuppressWarnings("unused")
  public String[] getGenomePath() {
    return genomePath;
  }

  @SuppressWarnings("unused")
  public Date getDate() {
    return date;
  }

  @SuppressWarnings("unused")
  public long getExomeVariants() {
    return exomeVariants;
  }

  @SuppressWarnings("unused")
  public long getGenomeVariants() {
    return genomeVariants;
  }

  @SuppressWarnings("unused")
  public long getExomeBytes() {
    return exomeBytes;
  }

  @SuppressWarnings("unused")
  public long getGenomeBytes() {
    return genomeBytes;
  }

  public String getDateAsString() {
    return Constants.DF_DAY_TIME.format(date);
  }

  public String getExomeSize() {
    return Constants.humanReadableByteCountBin(exomeBytes);
  }

  public String getGenomeSize() {
    return Constants.humanReadableByteCountBin(genomeBytes);
  }

  @SuppressWarnings("unused")
  public int getBufferSize() {
    return bufferSize;
  }
}
