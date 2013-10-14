/*
 * FileAccessor - interface to read file.
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

import java.io.IOException;

public interface FileAccessor {
  //    public FileAccessor(String name);
  //    public FileAccessor(File file);
  public void seek(int pos) throws IOException;
  public short readShort() throws IOException;
  public int readInt() throws IOException;
  public int read() throws IOException;
  public int read(byte b[], int start, int length) throws IOException;
  public void close() throws IOException;
}