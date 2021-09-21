package fr.inserm.u1078.tludwig.privas.utils.binary;

import fr.inserm.u1078.tludwig.privas.utils.ExtractAnnotations;

import java.io.IOException;
import java.util.TreeMap;

/**
 * Class to Write in GnomAD Index Files
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-08-24
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class GnomADIndexWriter extends BinaryFileWriter {
  public static final int DEFAULT_BUFFER_SIZE = 512;

  public GnomADIndexWriter(String filename) throws IOException {
    super(filename);
  }

  public void writeIndex(int chrom, int position, long offset) throws IOException {
    writeInt1(chrom);
    writeInt4(position);
    writeLong8(offset);
  }

  public void writeBlankIndex() throws IOException {
    this.writeIndex(0, 0, 0);
  }

  public void writeGnomADIndexHeader(GnomADIndexHeader header) throws IOException {
    this.writeString(header.toString());
  }

  public void writeIndices(ExtractAnnotations.IndexData indexData) throws IOException {
    for (int chrom : indexData.getIndices().navigableKeySet()) {
      TreeMap<Integer, Long> index = indexData.getIndices().get(chrom);
      for (int pos : index.navigableKeySet())
        writeIndex(chrom, pos, index.get(pos));
    }
    writeBlankIndex();
  }
}
