/*
 * StringTaggerDemo.java - StringTaggerDemo is demonstration program for Sen.
 * 
 * Copyright (C) 2002-2004 Takashi Okamoto Takashi Okamoto <tora@debian.org>
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
import java.io.InputStreamReader;

import net.java.sen.StringTagger;
import net.java.sen.Token;

public class StringTaggerDemo {
	public static void main(String args[]) {
		try {
			System.setProperty("sen.home", "../Sen1221/senhome-ipadic");
			System.setProperty("org.apache.commons.logging.Log",
					"org.apache.commons.logging.impl.SimpleLog");
			System.setProperty(
					"org.apache.commons.logging.simplelog.defaultlog", "FATAL");

			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			System.out.println("Please input Japanese sentence:");

			StringTagger tagger = StringTagger.getInstance();
			// You can also get StringTagger instance by following code:
			//
			// String confPath = System.getProperty("sen.home")
			// + System.getProperty("file.separator") + "conf/sen.xml";
			// tagger = StringTagger.getInstance(confPath);

			String s;
			while ((s = br.readLine()) != null) {
				System.out.println(s);
				Token[] token = tagger.analyze(s);
				if (token != null) {
					for (int i = 0; i < token.length; i++) {
						System.out.println(token[i].toString() + "\t("
								+ token[i].getBasicString() + ")" + "\t"
								+ token[i].getPos() + "(" + token[i].start()
								+ "," + token[i].end() + ","
								+ token[i].length() + ")\t"
								+ token[i].getReading() + "\t"
								+ token[i].getPronunciation());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
