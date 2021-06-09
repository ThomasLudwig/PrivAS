package fr.inserm.u1078.tludwig.privas.utils;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * Input stream that decompresses data.
 * <p>
 * Copyright 2005 - Philip Isenhour - http://javatechniques.com/
 * <p>
 * This software is provided 'as-is', without any express or implied warranty. In no event will the authors be held liable for any damages arising from the use
 * of this software.
 * <p>
 * Permission is granted to anyone to use this software for any purpose, including commercial applications, and to alter it and redistribute it freely, subject
 * to the following restrictions:
 * <p>
 * 1. The origin of this software must not be misrepresented; you must not claim that you wrote the original software. If you use this software in a product, an
 * acknowledgment in the product documentation would be appreciated but is not required.
 * <p>
 * 2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the original software.
 * <p>
 * 3. This notice may not be removed or altered from any source distribution.
 *
 * @author Adaptation by Thomas E. Ludwig (INSERM - U1078) 2019
 *
 * Javadoc complete on 2019-08-07
 */
public class CompressedBlockInputStream extends FilterInputStream {

  /**
   * Buffer of compressed data read from the stream
   */
  private byte[] inBuf = null;

  /**
   * Buffer of uncompressed data
   */
  private byte[] outBuf = null;

  /**
   * Offset and length of uncompressed data
   */
  private int outOffs = 0;

  private int outLength = 0;

  /**
   * Inflater for decompressing
   */
  private final Inflater inflater;

  /**
   * Wrap an input stream and decompress the data from it.
   *
   * @param is the input stream.
   */
  public CompressedBlockInputStream(final InputStream is) {
    super(is);
    this.inflater = new Inflater();
  }

  private void readAndDecompress() throws IOException {
    // Read the length of the compressed block
    int ch1 = this.in.read();
    int ch2 = this.in.read();
    int ch3 = this.in.read();
    int ch4 = this.in.read();
    if ((ch1 | ch2 | ch3 | ch4) < 0)
      throw new EOFException();
    int inLength = (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4;

    ch1 = this.in.read();
    ch2 = this.in.read();
    ch3 = this.in.read();
    ch4 = this.in.read();
    if ((ch1 | ch2 | ch3 | ch4) < 0)
      throw new EOFException();
    this.outLength = (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4;

    // Make sure we've got enough space to read the block
    if (this.inBuf == null || inLength > this.inBuf.length)
      this.inBuf = new byte[inLength];

    if (this.outBuf == null || this.outLength > this.outBuf.length)
      this.outBuf = new byte[this.outLength];

    // Read until we're got the entire compressed buffer.
    // read(...) will not necessarily block until all
    // requested data has been read, so we loop until
    // we're done.
    int inOffs = 0;
    while (inOffs < inLength) {
      final int inCount = this.in.read(this.inBuf, inOffs, inLength - inOffs);
      if (inCount == -1)
        throw new EOFException();
      inOffs += inCount;
    }

    this.inflater.setInput(this.inBuf, 0, inLength);
    try {
      this.inflater.inflate(this.outBuf);
    } catch (final DataFormatException dfe) {
      throw new IOException("Data format exception - " + dfe.getMessage());
    }

    // Reset the inflater so we can re-use it for the
    // next block
    this.inflater.reset();

    this.outOffs = 0;
  }

  @Override
  public int read() throws IOException {
    if (this.outOffs >= this.outLength)
      try {
        this.readAndDecompress();
      } catch (final EOFException eof) {
        return -1;
      }

    return this.outBuf[this.outOffs++] & 0xff;
  }

  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    int count = 0;
    while (count < len) {
      if (this.outOffs >= this.outLength)
        try {
          // If we've read at least one decompressed
          // byte and further decompression would
          // require blocking, return the count.
          if (count > 0 && this.in.available() == 0)
            return count;
          else
            this.readAndDecompress();
        } catch (final EOFException eof) {
          if (count == 0)
            count = -1;
          return count;
        }

      final int toCopy = Math.min(this.outLength - this.outOffs, len - count);
      System.arraycopy(this.outBuf, this.outOffs, b, off + count, toCopy);
      this.outOffs += toCopy;
      count += toCopy;
    }

    return count;
  }

  @Override
  public int available() throws IOException {
    // This isn't precise, but should be an adequate
    // lower bound on the actual amount of available data
    return this.outLength - this.outOffs + this.in.available();
  }

}
