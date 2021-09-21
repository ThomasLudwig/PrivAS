package fr.inserm.u1078.tludwig.privas.utils.binary;

import fr.inserm.u1078.tludwig.privas.utils.CanonicalVariant;

import java.io.IOException;


/**
 * Reader for binary version of GnomAD Files
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-08-09
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class GnomADFileReader extends BinaryFileReader {

  public static final int[] FLAGS = {
          1,2,4,8,
          16,32,64,128,
          256,512,1024,2048};

  public GnomADFileReader(String filename) throws IOException {
    super(filename);
  }

  public GnomADFileReader(String filename, long offset) throws IOException {
    super(filename, offset);
  }

  public double[] readFrequencies() throws IOException {
    int flag = readInt1or2();

    double[] frequencies = new double[12];
    for(int i = 0; i < 12; i++)
      if((flag & FLAGS[i]) == FLAGS[i])
        frequencies[i] = readDouble3();
    return frequencies;
  }

  public String readSequence() throws IOException {
    int len = readInt1or2();
    StringBuilder sb = new StringBuilder();
    for(int i = 0 ; i < len; i+=4)
      sb.append(read4Char1());
    return sb.substring(0, len);
  }

  public static final String[] ACGT ={"A", "C", "G", "T"};

  public String read4Char1() throws IOException {
    int val = readInt1();
    return ACGT[val & 3] +
           ACGT[(val & 12) / 4] +
           ACGT[(val & 48) / 16] +
           ACGT[(val & 192) / 64];
  }

  public GnomADFileHeader readGnomADFileHeader() throws IOException {
    return GnomADFileHeader.parseHeader(this.readString());
  }

  public GnomADLine readGnomADLine() throws IOException {
    int chrom = readInt1();
    int pos = readInt4();
    int flag = readInt1();

    int length = GnomADLine.isLength1(flag) ? 1 : readInt1or2();
    String alt = GnomADLine.getSeq(flag);
    if(alt.isEmpty())
      alt = readSequence();
    double[] frequencies = GnomADLine.isNullFrequencies(flag) ? new double[12] : readFrequencies();
    return new GnomADLine(new CanonicalVariant(chrom, pos, length, alt), frequencies);
  }
}
