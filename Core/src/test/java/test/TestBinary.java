package test;

import fr.inserm.u1078.tludwig.privas.constants.Constants;
import fr.inserm.u1078.tludwig.privas.utils.UniversalReader;
import fr.inserm.u1078.tludwig.privas.utils.binary.*;

import java.io.*;
import java.util.Date;

/**
 * XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-08-04
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
@SuppressWarnings("unused")
public class TestBinary {

  public static void main(String[] args) {
    //testDouble();
    //testBinary();
    //testString();
    testDouble2();
  }

  private static void testDouble2() {
    double d = Double.NaN;

    boolean neg = Double.NEGATIVE_INFINITY >= d;
    boolean pos = d >= Double.NEGATIVE_INFINITY;

    System.err.println("Neg ["+neg+"] Pos ["+pos+"]");
  }

  private static void testString() {
    String[] original = "This is the original String to test".split("\\s+");
    String[] a = original.clone();
    for(int i = 0; i < a.length; i++)
      a[i] = ""+i;

    String[] b = original.clone();
    System.err.println(String.join(" ", a));
    System.err.println(String.join(" ", b));
    System.err.println(String.join(" ", original));
  }

  private static void testDouble() {
/*
    int s = 121951;
    int exp = 2;
*/

    double[] ds = {6.22743E-5, 6.41849E-5};
    for(double d : ds){
      System.err.println("Initialize "+d);
      byte[] b = GnomADFileWriter.encodeDouble0To1(d);
      System.err.println("Encoded ["+(b[0]&0xFF)+"|"+(b[1]&0xFF)+"|"+(b[2]&0xFF)+"]");
      System.err.println("Decoded "+ GnomADFileReader.decodeDouble0to1(b));
    }



    /*
    int s = 986842;
    int exp = 3;

    double d = s * Math.pow(10, -(5+exp));
    double f = s * 0.00000001;
    double h = s / 10000000D;
    double g = Double.parseDouble("0.00986842");
    double m = h;
    double n = h;
    double o = s;
    for(int i = 0; i < exp; i++) {
      m *= 0.1;
      n = n/10;
    }
    for(int i = 0; i < 5+exp; i++)
      o = o / 10;
    System.err.println("d["+d+"] g["+g+"] f["+f+"] h["+h+"] m["+m+"] n["+n+"] o["+o+"]");*/
  }

  private static void testBinary() throws IOException {
    UniversalReader ins = new UniversalReader("test.txt");
    GnomADFileWriter out = new GnomADFileWriter("test.bin");
    String line;
    int n = 0;
    while((line = ins.readLine()) != null) {
      out.writeGnomADLine(new GnomADLine(line));
      n++;
    }
    out.close();
    ins.close();

    GnomADFileReader in = new GnomADFileReader("test.bin");
    PrintWriter o = new PrintWriter(new FileWriter("check.txt"));
    for(int i = 0 ; i < n ; i++)
      o.println(in.readGnomADLine().toString());
    in.close();
    o.close();
  }
/*
  public static void testSeek(String inputBinary, String listFiles) throws IOException {
    long start = new Date().getTime();
    GnomADIndexReader reader = new GnomADIndexReader(inputBinary);
    UniversalReader in = new UniversalReader(listFiles);
    String canonical;
    while((canonical = in.readLine()) != null){
      GnomADLine[] gnomADLines = reader.fetch(new CanonicalVariant(canonical));
      System.err.println("Looking for : "+canonical+" exome["+gnomADLines[0]+"] genome["+gnomADLines[1]+"]");
    }
    in.close();
    long end = new Date().getTime();
    double dur = (end-start)/1000D;
    System.err.println("Duration : "+dur+"s");
  }
*/
  public static void testImportTxt(String inputFile) throws IOException {
    long nbRead=0;

    UniversalReader in = new UniversalReader(inputFile);

    Date prev = new Date();

    String line;
    in.readLine();
    while((line = in.readLine()) != null){
      GnomADLine gn = new GnomADLine(line);
      nbRead++;
    }
    Date cur = new Date();
    System.err.println("Read ["+nbRead+"] in "+Constants.durationInSeconds(prev, cur));

    in.close();
  }

  public static void testImportBinary(String binaryFile) throws IOException {
    long[] nbRead = new long[2];
    int file = 0;
    GnomADFileReader in = new GnomADFileReader(binaryFile);
    GnomADLine line;
    Date prev = new Date();
    Date cur;
    while(true){
      try {
        line = in.readGnomADLine();
        if (line.isNull()) {
          cur = new Date();
          System.err.println("Read [" + nbRead[file] + "] in " + Constants.durationInSeconds(prev, cur));
          prev = cur;
          file++;
        } else
          nbRead[file]++;
      } catch(EOFException e) {
        break;
      }
    }
    cur = new Date();
    System.err.println("Read ["+nbRead[file]+"] in "+Constants.durationInSeconds(prev, cur));

    in.close();
  }

  public static void testClearTxtIndex(String inputFile, String outputFile) throws IOException {
    BinaryFileReader in = new BinaryFileReader(inputFile);
    PrintWriter out = new PrintWriter(new FileWriter(outputFile));

    out.println(in.readString());

    while(true)
      try {
        out.println(in.readInt1() + "\t" + in.readInt4() + "\t" + in.readLong8());
      } catch(IOException e){
        break;
      }

    in.close();
    out.close();
  }
/*
  public static void testBenchFetch(String inputVCF, String gnomadFile) throws IOException {
    Date start = new Date();
    UniversalReader in = new UniversalReader(inputVCF);
    GnomADIndexReader gnomad = new GnomADIndexReader(gnomadFile);
    String line;
    while((line = in.readLine()) != null) {
      if(!line.startsWith("#")){
        String[] f= line.split("\t");

        for(String alt : f[4].split(",")) {
          CanonicalVariant canonical = new CanonicalVariant(f[0], f[1], f[3], alt);
          String outline = String.join("\t", f[0], f[1], f[2], f[3], alt);
          GnomADLine[] gl = gnomad.fetch(canonical);
          if(gl[0] == null)
            outline += "\tnull";
          else
            outline += "\t"+gl[0].getFrequencies()[0];
          if(gl[1] == null)
            outline += "\tnull";
          else
            outline += "\t"+gl[1].getFrequencies()[0];
          System.out.println(outline);
        }
      }
    }
    in.close();
    Date end = new Date();
    System.err.println("Process time "+Constants.durationInSeconds(start, end)+"s");
  }*/
}
