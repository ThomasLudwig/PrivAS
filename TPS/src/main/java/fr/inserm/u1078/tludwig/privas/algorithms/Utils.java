package fr.inserm.u1078.tludwig.privas.algorithms;

import fr.inserm.u1078.tludwig.privas.utils.UniversalReader;

import java.io.IOException;
import java.util.Date;

/**
 * class providing static methods that are independent of the algorithms
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-07-21
 * Checked for release on 2021-07-21
 */
public class Utils {

  /**
   * Authorized and sorted values for the 64 digits code
   */
  public static final String CODE64 = "0123456789" +
          "abcdefghijklmnopqrstuvwxyz" +
          "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
          "+*";

  /**
   * Conversion of int (< 64) to a character
   * @param i
   * @return
   */
  public static char code64(int i){
    return i > CODE64.length() ? '?' : CODE64.charAt(i);
  }

  /**
   * Simplifies an array of boolean to a text in a 64 characters code
   * @param bs
   * @return
   */
  public static String getTextPhenotype(boolean[] bs){
    String code64 = "";
    for(int i = 0 ; i < bs.length; i+=6){
      int fac = 1;
      int l = 0;
      for(int j = i; j < i+6 && j < bs.length; j++) {
        int v = bs[bs.length - (1+j)] ? 1 : 0;
        l += v * fac;
        fac *= 2;
      }
      code64 = code64(l) + code64;
    }
    return code64;
  }

  /**
   * Parses a text file containing phenotypes as boolean (0/false : unaffected, 1/true : affected)
   * @param phenotypeFilename the filename
   * @return array of boolean phenotypes
   * @throws IOException
   */
  public static boolean[] parsePhenotypes(String phenotypeFilename) throws IOException {
    UniversalReader in = new UniversalReader(phenotypeFilename);
    String[] f = in.readLine().split("\t");
    in.close();
    boolean[] phenotypes = new boolean[f.length];
    for(int i = 0 ; i < f.length; i++)
      phenotypes[i] = Boolean.parseBoolean(f[i].replace("0", "false").replace("1", "true"));
    return phenotypes;
  }

  /**
   * Gets the durations in seconds.milliseconds elapses since the given timestamp
   *
   * @param start initial timestamp
   * @return duration in seconds, with milliseconds
   */
  public static double durationInSeconds(long start) {
    long ms = (new Date().getTime() - start);
    return ms * 0.001;
  }

}
