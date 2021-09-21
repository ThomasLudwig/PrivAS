package fr.inserm.u1078.tludwig.privas.utils.binary;

import fr.inserm.u1078.tludwig.privas.utils.CanonicalVariant;

import java.io.IOException;


/**
 * Writer for the binary version of GnomADFiles
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-08-09
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class GnomADFileWriter extends BinaryFileWriter {
  public static final int[] FLAGS = GnomADFileReader.FLAGS;

  public GnomADFileWriter(String filename) throws IOException {
    super(filename);
  }

  public void writeFrequencies(double[] frequencies) throws IOException {
    int flag = 0;
    for(int i = 0; i < 12; i++)
      if(frequencies[i] != 0)
        flag += FLAGS[i];
    writeInt1or2(flag);
    for(int i = 0; i < 12; i++)
      if(frequencies[i] != 0)
        writeDouble3(frequencies[i]);
  }

  public void writeSequence(String s) throws IOException {
    writeInt1or2(s.length());
    for(int i = 0; i < s.length(); i+=4) {
      StringBuilder chars = new StringBuilder();
      for(int j = 0; j < 4 && i+j < s.length(); j++)
        chars.append(s.charAt(i + j));
      write4Char1(chars.toString());
    }
  }

  public static int indexACGT(char acgt){
    switch (acgt){
      case 'C' : return 1;
      case 'G' : return 2;
      case 'T' : return 3;
    }
    return 0;
  }

  public void write4Char1(String s) throws IOException {
    int v = indexACGT(s.charAt(0));
    if(s.length() > 1)
      v += 4 * indexACGT(s.charAt(1));
    if(s.length() > 2)
      v += 16 * indexACGT(s.charAt(2));
    if(s.length() > 3)
      v += 64 * indexACGT(s.charAt(3));
    writeInt1(v);
  }

  public void writeGnomADLine(GnomADLine gnomADLine) throws IOException {
    CanonicalVariant canonicalVariant = gnomADLine.getCanonicalVariant();
    writeInt1(canonicalVariant.getChrom());
    writeInt4(canonicalVariant.getPos());
    int flag = canonicalVariant.getLength() == 1 ? GnomADLine.L : 0;
    boolean nf = GnomADLine.isNullFrequencies(gnomADLine.getFrequencies());
    if(nf)
      flag += GnomADLine.F;
    if(!"-".equals(canonicalVariant.getAlt()))
      if(canonicalVariant.getAlt().length() > 2)
        flag += 3;
      else{
        flag += canonicalVariant.getAlt().length();
        flag += 4 * indexACGT(canonicalVariant.getAlt().charAt(0));
        if(canonicalVariant.getAlt().length() == 2)
          flag += 16 * indexACGT(canonicalVariant.getAlt().charAt(1));
      }

    writeInt1(flag);
    if(canonicalVariant.getLength() != 1)
      writeInt1or2(canonicalVariant.getLength());
    if(canonicalVariant.getAlt().length() > 2)
      writeSequence(canonicalVariant.getAlt());
    if(!nf)
      writeFrequencies(gnomADLine.getFrequencies());
  }

  public void writeGnomADFileHeader(GnomADFileHeader header) throws IOException {
    this.writeString(header.toString());
  }
}
