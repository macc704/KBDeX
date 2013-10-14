/*
 * CSVParser.java - CSVParser implementation in Java.
 * 
 * Copyright (C) 2001, 2002 Takashi Okamoto
 * Takashi Okamoto <tora@debian.org>
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
 * 
 */

package net.java.sen.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * parse CSV file and extract token.
 */
public class CSVParser {
	private BufferedReader reader = null;
	private String line = null;
	private int pos = 0;

	/**
	 * Constructor for CSVParser
	 * 
	 * @param is
	 *            input stream to parse.
	 */
	public CSVParser(InputStream is) throws IOException {
		reader = new BufferedReader(new InputStreamReader(is));
	}

	/**
	 * Constructor for CSVParser
	 * 
	 * @param str
	 *            input string to parse.
	 */
	public CSVParser(String str) {
		reader = new BufferedReader(new StringReader(str));
	}

	/**
	 * Constructor for CSVParser
	 * 
	 * @param is
	 *            input stream to parse.
	 * @param charset
	 *            charset for stream.
	 */
	public CSVParser(InputStream is, String charset) throws IOException {
		reader = new BufferedReader(new InputStreamReader(is, charset));
	}

	public boolean nextRow() throws IOException {
		line = reader.readLine();

		if (line == null || line.length() == 0)
			return false;
		// System.out.println("line("+line.length()+")="+line);

		pos = 0;
		return true;
	}

	/**
	 * extract next token.
	 * 
	 * @return token
	 */
	public String nextToken() {
		int start;
		boolean quote = false;
		boolean escape = false;

		if (line == null || pos >= line.length())
			return null;

		if (line.charAt(pos) == '\"') {
			quote = true;
			pos++;
		}
		start = pos;

		while (pos < line.length()) {
			if (line.charAt(pos) == ',' && !quote) {
				return line.substring(start, pos++);
			} else if (line.charAt(pos) == '\"' && quote) {
				if (pos + 1 < line.length() && line.charAt(pos + 1) == '\"') {
					pos += 2;
					continue;
				}
				String ret = line.substring(start, pos)
						.replaceAll("\"\"", "\"");
				pos += 2;
				return ret;
			}
			pos++;
		}
		return line.substring(start, pos);
	}

	/**
	 * extract all tokens in line.
	 * 
	 * @return tokens
	 */
	public String[] nextTokens() throws IOException {
		ArrayList list = new ArrayList();
		String input;

		if (nextRow() == false)
			return null;

		while ((input = nextToken()) != null) {
			list.add(input);
		}
		String r[] = new String[list.size()];
		for (int i = 0; i < r.length; i++) {
			r[i] = (String) list.get(i);
		}
		return r;
	}

	public static void main(String args[]) {
		try {
			InputStream is = new FileInputStream(args[0]);
			CSVParser csv = new CSVParser(is, "EUC_JP");
			int row = 0, col = 0;
			String str[];
			while ((str = csv.nextTokens()) != null) {
				row++;
				for (int i = 0; i < str.length; i++) {
					System.out.println("(" + i + "," + row + ")=" + str[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
