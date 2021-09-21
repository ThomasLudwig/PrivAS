package fr.inserm.u1078.tludwig.privas.utils.binary;

import fr.inserm.u1078.tludwig.privas.constants.MSG;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Class to Write in Binary Files
 *
 * @author Thomas E. Ludwig (INSERM - U1078)
 * Started on             2021-08-24
 * Checked for release on XXXX-XX-XX
 * Unit Test defined on   XXXX-XX-XX
 */
public class BinaryFileWriter {
  public static final char DELIMITER = BinaryFileReader.DELIMITER;

  private final String filename;
  private final BufferedOutputStream out;
  private long bytesWritten = 0;

  public BinaryFileWriter(String filename) throws IOException {
    this.filename = filename;
    this.out = new BufferedOutputStream(new FileOutputStream(filename));
  }

  public void write(byte[] bytes) throws IOException {
    out.write(bytes);
    this.bytesWritten += bytes.length;
  }

  public void writeInt1(int i) throws IOException {
    write(new byte[]{(byte)i});
  }

  @SuppressWarnings("unused")
  public void writeInt2(int i) throws IOException {
    write(ByteBuffer.allocate(2).putShort((short)i).array());
  }

  public void writeInt1or2(int i) throws IOException {
    if(i < 128)
      writeInt1(i);
    else{
      writeInt1((i%128) + 128);
      writeInt1(i/128);
    }
  }

  public void writeInt4(int i) throws IOException {
    write(ByteBuffer.allocate(4).putInt(i).array());
  }

  public void writeLong8(long l) throws IOException {
    write(ByteBuffer.allocate(8).putLong(l).array());
  }

  public void writeDouble3(double d) throws IOException {
    write(encodeDouble0To1(d));
  }

  public static byte[] encodeDouble0To1(double d) throws NumberFormatException {
    if(d < 0)
      throw new NumberFormatException(MSG.cat(MSG.ENCODE_NO_NEGATIVE, d+""));
    if(d > 1)
      throw new NumberFormatException(MSG.cat(MSG.ENCODE_NO_LARGE, d+""));
    byte[] bytes = new byte[3];
    if(d == 0)
      return bytes;

    int exp = 0;
    double tmp = d;
    while(tmp < 1){
      exp++;
      tmp *= 10;
    }
    tmp *= 100000;
    int sign = (int)Math.round(tmp);
    if(sign == 1000000){
      sign = sign/10;
      exp++;
    }
    if(exp > 15)
      throw new NumberFormatException(MSG.cat(MSG.ENCODE_NO_PRECISE, d+""));
    int value = exp + 16*sign;
    int a = value%256;
    value = value/256;
    int b = value%256;
    int c = value/256;
    bytes[0] = (byte)a;
    bytes[1] = (byte)b;
    bytes[2] = (byte)c;

    return bytes;
  }

  @SuppressWarnings("unused")
  public void writeFloat4(double d) throws IOException {
    write(ByteBuffer.allocate(4).putFloat((float)d).array());
  }

  @SuppressWarnings("unused")
  public void writeDouble(double d) throws IOException {
    write(ByteBuffer.allocate(8).putDouble(d).array());
  }

  public void writeChar(char c) throws IOException {
    write(ByteBuffer.allocate(2).putChar(c).array());
  }

  public void writeString(String s) throws IOException {
    for(int i = 0 ; i < s.length(); i++) {
      char c = s.charAt(i);
      if(c == DELIMITER)
        throw new NumberFormatException(MSG.cat(MSG.ENCODE_RESERVED_STRING, DELIMITER+""));
      writeChar(c);
    }
    writeChar(DELIMITER);
  }

  public void close() throws IOException {
    out.close();
  }

  @SuppressWarnings("unused")
  public long getBytesWritten() { return bytesWritten; }

  @SuppressWarnings("unused")
  public String getFilename() {
    return filename;
  }
}
