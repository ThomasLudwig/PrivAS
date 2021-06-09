package fr.inserm.u1078.tludwig.privas.utils.qc;

import fr.inserm.u1078.tludwig.privas.utils.UniversalReader;
import javafx.util.Pair;

import java.io.IOException;

/**
 * Reader for the QC
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-05-21
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
class Reader {
  private final UniversalReader in;
  private int read = 0;
  private boolean closed = false;

  Reader(UniversalReader in, int skipHeader) {
    this.in = in;
    for(int i = 0 ; i < skipHeader; i++) {
      try {
        in.readLine();
      } catch (IOException ignore) {
        //ignore
      }
    }
  }

  synchronized Pair<Integer, String> getNext() {
    Pair<Integer, String> r = new Pair<>(read++, null);
    if(closed)
      return r;
    try {
      String line = in.readLine();
      if(line == null) {
        close();
      } else
        r = new Pair<>(read,line);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return r;
  }

  private void close() throws IOException{
    if(!closed){
      closed = true;
      in.close();
    }
  }
}
