/*
 * KHTMLMaker.java
 * Created on Apr 12, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse.wordprocessing;

import java.util.List;

/**
 * @author macchan
 *
 */
public class KHTMLMaker {

	public static String makeHighlightedHtml(String text, IKWordFinder finder) {
		List<Integer> wordIndexes = finder.getFoundLocations();
		StringBuffer buf = new StringBuffer();
		buf.append("<html>");
		int cursor = 0;
		for (int index : wordIndexes) {
			if (cursor < index) {
				buf.append(text.substring(cursor, index));
				cursor = index;
			}

			if (cursor == index) {
				String word = finder.getFoundWord(index);
				buf.append("<font color='red'>");
				buf.append(word);
				buf.append("</font>");
				cursor += word.length();
			} else {
				//TODO 自動車と車を両方指定した時の問題
				// throw new RuntimeException();
			}
		}
		buf.append(text.substring(cursor));
		buf.append("</html>");

		return unEscapeToHTml(buf.toString());
	}

	private static String unEscapeToHTml(String text) {
		if (text == null) {
			return "null";
		}
		text = text.replaceAll("[\\\\]r", "");
		text = text.replaceAll("[\\\\]n", "<BR>");
		text = text.replaceAll("[\\\\]t", "");
		text = text.replaceAll("[\\\\][\\\\]", "\\\\");
		return text;
	}
}
