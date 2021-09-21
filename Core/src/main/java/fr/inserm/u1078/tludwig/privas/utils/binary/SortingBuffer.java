package fr.inserm.u1078.tludwig.privas.utils.binary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Buffer for a GnomADFileWriter
 *
 * The order in canonical representation of variants is not automatically the same as in VCF Files.
 * This buffer also to sort the values before writing
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-08-30
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class SortingBuffer {
  private final GnomADFileWriter out;
  private final ArrayList<GnomADLine> lines;
  public static final int MAX = 4000;
  public static final int THRESHOLD = 1000;

  public SortingBuffer(GnomADFileWriter out) {
    this.out = out;
    lines = new ArrayList<>();
  }

  public void add(GnomADLine line) throws IOException {
    lines.add(line);
    if(lines.size() >= MAX){
      Collections.sort(lines);
      while(lines.size() > THRESHOLD)
        out.writeGnomADLine(lines.remove(0));
    }
  }

  public void flush() throws IOException {
    Collections.sort(lines);
    while(!lines.isEmpty())
      out.writeGnomADLine(lines.remove(0));
  }
}
