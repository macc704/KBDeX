/*
 * FullBufferedReader.java - read entire file into buffer.
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
import java.io.InputStream;

public class FullBufferedReader implements FileAccessor {
	byte buf[];
	int pos = 0;

	public FullBufferedReader(String name) throws IOException {
		this(new File(name));
	}

	public FullBufferedReader(File file) throws IOException {
		buf = new byte[(int) file.length()];
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			is.read(buf);
		} finally {
			if (is != null)
				is.close();
		}
	}

	public void seek(int pos) throws IOException {
		if (pos < 0 || pos >= buf.length)
			throw new IOException("File position is invalid. File size is "
					+ buf.length + " but specified position is " + pos);
		this.pos = (int) pos;
	}

	public void seek(long pos) throws IOException {
		seek((int) pos);
	}

	public short readShort() {

		int i1 = buf[pos++];
		i1 = ((((i1) >= 0) ? (i1) : (256 + i1)));
		int i2 = buf[pos++];
		i2 = ((((i2) >= 0) ? (i2) : (256 + i2)));
		return (short) ((i1 << 8) + (i2 << 0));
	}

	public int readInt() {
		int i1 = buf[pos++];
		i1 = ((((i1) >= 0) ? (i1) : (256 + i1)));
		int i2 = buf[pos++];
		i2 = ((((i2) >= 0) ? (i2) : (256 + i2)));
		int i3 = buf[pos++];
		i3 = ((((i3) >= 0) ? (i3) : (256 + i3)));
		int i4 = buf[pos++];
		i4 = ((((i4) >= 0) ? (i4) : (256 + i4)));

		return ((i1 << 24) + (i2 << 16) + (i3 << 8) + (i4 << 0));
	}

	public int read() {
		byte b = buf[pos++];
		return ((((b) >= 0) ? (b) : (256 + b)));
	}

	public int read(byte b[], int start, int length) {
		int l1 = (start + length);
		for (int i = start; i < (start + length); i++) {
			if (i >= b.length)
				break;
			try {
				b[i] = buf[pos];
				pos++;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("input length = " + b.length);
				System.out.println("pos of input buf = " + i);
				System.out.println("buffer length=" + buf.length);
				System.out.println("current pos=" + pos);
				// System.exit(1);
			}
		}
		return 1;
	}

	public void close() throws IOException {
	}

}