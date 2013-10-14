/*
 * StringTaggerDemo2.java - StringTaggerDemo2 is demonstration program for Sen.
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

import net.java.sen.StringTagger;
import net.java.sen.Token;

public class StringTaggerDemo2 {
	public static void main(String args[]) throws Exception {
		System.setProperty("sen.home", "../Sen1221/senhome-ipadic");
		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.defaultlog",
				"FATAL");

		StringTagger tagger = StringTagger.getInstance();
		String s = "すもももももももものうち";

		Token[] token = tagger.analyze(s);
		if (token != null) {
			show(token);
		}
	}

	private static void show(Token[] token) {
		for (int i = 0; i < token.length; i++) {
			System.out.println(token[i].toString() + "\t("
					+ token[i].getBasicString() + ")" + "\t"
					+ token[i].getPos() + "(" + token[i].start() + ","
					+ token[i].end() + "," + token[i].length() + ")\t"
					+ token[i].getReading() + "\t"
					+ token[i].getPronunciation()

			);
		}
	}
}
