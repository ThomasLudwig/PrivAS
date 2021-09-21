package fr.inserm.u1078.tludwig.privas.utils.binary;

import fr.inserm.u1078.tludwig.privas.constants.Constants;
import fr.inserm.u1078.tludwig.privas.constants.FileFormat;

import java.util.Date;

/**
 * Header of a GnomAD File
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-08-24
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class GnomADFileHeader {
  private final String version;
  private final String[] exomePath;
  private final String[] genomePath;
  private final Date date;

  public GnomADFileHeader(String version, String[] exomePath, String[] genomePath, Date date) {
    this.version = version;
    this.exomePath = exomePath;
    this.genomePath = genomePath;
    this.date = date;
  }

  public static GnomADFileHeader parseHeader(String s) {
    String v = null;
    String[] eP = null;
    String[] gP = null;
    Date d = null;
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
      }
    }

    return new GnomADFileHeader(v, eP, gP, d);
  }

  @Override
  public String toString() {
    return FileFormat.TAG_VERSION + "\t" + version + "\n" +
           FileFormat.TAG_EXOME_PATH + "\t" + String.join(",", exomePath) + "\n" +
           FileFormat.TAG_GENOME_PATH + "\t" + String.join(",", genomePath) + "\n" +
           FileFormat.TAG_DATE + "\t" + getDateAsString() + "\n";
  }

  public String getDateAsString() {
    return Constants.DF_DAY_TIME.format(date);
  }

  public String getVersion() {
    return version;
  }

  public String[] getExomePath() {
    return exomePath;
  }

  public String[] getGenomePath() {
    return genomePath;
  }

  public Date getDate() {
    return date;
  }
}
