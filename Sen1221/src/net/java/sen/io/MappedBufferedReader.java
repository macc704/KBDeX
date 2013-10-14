/*
 * MappedBufferedReader.java - file reader for MappedByteBuffer
 * 
 * Copyright (C) 2002 Takashi Okamoto Takashi Okamoto <tora@debian.org>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Sen; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 *  
 */

package net.java.sen.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MappedBufferedReader implements FileAccessor {
  long length = 0;
  MappedByteBuffer map = null;
  FileInputStream fis = null;
  public MappedBufferedReader(String name) throws IOException {
    this(new File(name));
  }

  public MappedBufferedReader(File file) throws IOException {
    fis = new FileInputStream(file);
    FileChannel fc = fis.getChannel();
    // Get the file's size and then map it into memory
    length = fc.size();
    map = fc.map(FileChannel.MapMode.READ_ONLY, 0, length);
  }

  public void seek(int pos) throws IOException {
    if (pos < 0 || pos >= length)
      throw new IOException("File position is invalid. File size is " + length
          + " but specified position is " + pos);
    map.position(pos);
  }

  public void seek(long pos) throws IOException {
    if (pos < 0 || pos >= length)
      throw new IOException("File position is invalid. File size is " + length
          + " but specified position is " + pos);
    map.position((int) pos);
  }

  public short readShort() throws IOException {
    return map.getShort();
  }

  public int readInt() {
    return map.getInt();
  }

  public int read() {
    byte b = map.get();
    return ((((b) >= 0) ? (b) : (256 + b)));
  }

  public int read(byte b[], int start, int length) {
    map.get(b, start, length);
    return length;
  }

  public void close() throws IOException {
    fis.close();
  }
}