/*
 * StreamTaggerDemo.java - StreamTaggerDemo is demonstration program for Sen.
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
 *  
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import net.java.sen.StreamTagger;
import net.java.sen.Token;

public class StreamTaggerDemo {
	public static void main(String args[]) {
		try {
			System.setProperty("sen.home", "../Sen1221/senhome-ipadic");
			System.setProperty("org.apache.commons.logging.Log",
					"org.apache.commons.logging.impl.SimpleLog");
			System.setProperty(
					"org.apache.commons.logging.simplelog.defaultlog", "FATAL");

			if (args.length != 2) {
				System.err
						.println("usage: java StreamTaggerDemo <filename> <encoding>");
				System.exit(1);
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(args[0]), args[1]));
			// String confPath = System.getProperty("sen.home")
			// + System.getProperty("file.separator") + "conf/sen.xml";
			// StreamTagger tagger = new StreamTagger((Reader) br, confPath);
			StreamTagger tagger = new StreamTagger((Reader) br);

			// BufferedReader is = new BufferedReader(System.in);

			while (tagger.hasNext()) {
				Token token = tagger.next();
				System.out.println(token.toString() + "\t("
						+ token.getBasicString() + ")" + "\t" + token.getPos()
						+ "(" + token.start() + "," + token.end() + ","
						+ token.length() + ")\t" + token.getReading() + "\t"
						+ token.getPronunciation());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
