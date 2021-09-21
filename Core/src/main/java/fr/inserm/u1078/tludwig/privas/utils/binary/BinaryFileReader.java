package fr.inserm.u1078.tludwig.privas.utils.binary;

import fr.inserm.u1078.tludwig.privas.constants.MSG;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Class to Read from Binary Files
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-08-24
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class BinaryFileReader {
  public static final char DELIMITER = '*';

  private final String filename;
  private final BufferedInputStream in;
  private long bytesRead = 0;

  public BinaryFileReader(String filename) throws FileNotFoundException {
    this.filename = filename;
    in = new BufferedInputStream(new FileInputStream(filename));
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public BinaryFileReader(String filename, long offset) throws IOException {
    this.filename = filename;
    in = new BufferedInputStream(new FileInputStream(filename));
    in.skip(offset);
  }

  public void read(byte[] b) throws IOException {
    if(in.read(b) == -1) {
      in.close();
      throw new EOFException(MSG.cat(MSG.BIN_EOF, this.getFilename()));
    }
    this.bytesRead += b.length;
  }

  public byte[] read(int length) throws IOException {
    byte[] bytes = new byte[length];
    read(bytes);
    return bytes;
  }

  public int readInt1() throws IOException {
    return read(1)[0] & 0xFF;
  }

  @SuppressWarnings("unused")
  public int readInt2() throws IOException {
    return ByteBuffer.wrap(read(2)).getShort();
  }

  public int readInt1or2() throws IOException {
    int small = readInt1();
    if(small < 128)
      return small;
    return small + 128 * (readInt1()-1);
  }

  public int readInt4() throws IOException {
    return ByteBuffer.wrap(read(4)).getInt();
  }

  public long readLong8() throws IOException {
    return ByteBuffer.wrap(read(8)).getLong();
  }

  public double readDouble3() throws IOException {
    return decodeDouble0to1(read(3));
  }

  @SuppressWarnings("unused")
  public double readFloat4() throws IOException {
    return ByteBuffer.wrap(read(4)).getFloat();
  }

  @SuppressWarnings("unused")
  public double readDouble8() throws IOException {
    return ByteBuffer.wrap(read(8)).getDouble();
  }

  public char readChar() throws IOException {//encoded on 2 bytes and not 1, but string are very short
    return ByteBuffer.wrap(read(2)).getChar();
  }

  public String readString() throws IOException {
    StringBuilder sb = new StringBuilder();
    char c;
    while((c = readChar()) != DELIMITER)
      sb.append(c);
    return sb.toString();
  }

  public static double decodeDouble0to1(byte[] bytes) {
    if(bytes.length != 3)
      throw new IllegalArgumentException(MSG.BIN_DOUBLE3_LENGTH);
    //double representation as y.yyyyyE-x
    //3 bytes -> int
    //4 bits on the right (%16 = 0-15) -> x
    //rest of int (/16) -> yyyyyy
    int a = bytes[0] & 0xFF;

    int exp = a%16;
    int sign = (a + 256*(bytes[1] & 0xFF) + 256*256*(bytes[2] & 0xFF))/16;

    int div = 1;
    for(int i = 0; i < 5+exp; i++)
      div *= 10;
    return sign / (double)div;
  }

  public void close() throws IOException {
    in.close();
  }

  public long getBytesRead() {
    return bytesRead;
  }

  @SuppressWarnings("unused")
  public String getFilename() {
    return filename;
  }
}
