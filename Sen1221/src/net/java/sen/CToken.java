/*
 * Token.java - Token which is used at Viterbi
 * 
 * Copyright (C) 2002 Takashi Okamoto Takashi Okamoto <tora@debian.org>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package net.java.sen;

import java.io.DataOutput;
import java.io.IOException;
import java.io.RandomAccessFile;

import net.java.sen.io.FileAccessor;

final public class CToken {
  final public static long SIZE = 16;
  public short rcAttr2 = 0; // left Attr
  public short rcAttr1 = 0; // left Attr
  public short lcAttr = 0; // right Attr
  public short posid = 0; // POS id
  public short length = 0; // length of this token
  public short cost = 0; // cost of this Token
  public int posID = 0; // ptr for Token information (read, cform .. etc)

  public void read(FileAccessor is) throws IOException {
    rcAttr2 = is.readShort();
    rcAttr1 = is.readShort();
    lcAttr = is.readShort();
    posid = is.readShort();
    length = is.readShort();
    cost = is.readShort();
    posID = is.readInt();
  }

  public void read(RandomAccessFile is) throws IOException {
    rcAttr2 = is.readShort();
    rcAttr1 = is.readShort();
    lcAttr = is.readShort();
    posid = is.readShort();
    length = is.readShort();
    cost = is.readShort();
    posID = is.readInt();
  }

  public void write(DataOutput os) throws IOException {
    os.writeShort(rcAttr2);
    os.writeShort(rcAttr1);
    os.writeShort(lcAttr);
    os.writeShort(posid);
    os.writeShort(length);
    os.writeShort(cost);
    os.writeInt(posID);
  }
}