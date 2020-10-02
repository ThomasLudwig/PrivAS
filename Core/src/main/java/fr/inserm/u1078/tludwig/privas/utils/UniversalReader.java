package fr.inserm.u1078.tludwig.privas.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

/**
 * BufferedReader able to handle plain text files as well as gzipped files
 *
 * @author Thomas E. Ludwig (INSERM - U1078) Started : 21 sept. 2015
 *
 * Javadoc complete on 2019-08-06
 */
public class UniversalReader {

  /**
   * The embedded BufferedReader
   */
  private final BufferedReader in;

  /**
   * Constructor from a file name
   *
   * @param filename the name of the file to read
   * @throws IOException
   */
  public UniversalReader(String filename) throws IOException {
    BufferedReader tmp;
    try {
      tmp = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));
    } catch (IOException e) {
      tmp = new BufferedReader(new FileReader(filename));
    }
    in = tmp;
  }

  /**
   * Reads a line of text. A line is considered to be terminated by any one of a line feed ('\n'), a carriage return ('\r'), or a carriage return followed
   * immediately by a linefeed.
   *
   * @return A String containing the contents of the line, not including any line-termination characters, or null if the end of the stream has been reached
   * @throws IOException If an I/O error occurs
   */
  public String readLine() throws IOException {
    return in.readLine();
  }

  /**
   * Closes the stream and releases any system resources associated with it. Once the stream has been closed, further read(), ready(), mark(), reset(), or
   * skip() invocations will throw an IOException. Closing a previously closed stream has no effect.
   *
   * @throws IOException If an I/O error occurs
   */
  public void close() throws IOException {
    in.close();
  }
}
