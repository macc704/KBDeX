/*
 * KDefaultWordFinder.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse.wordprocessing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import kbdex.utils.KDictionary;
import net.java.sen.Token;

/**
 * 使われていません
 */
public class KSenWordFinder implements IKWordFinder {

	private SortedMap<Integer, String> founds = new TreeMap<Integer, String>();

	protected KSenWordFinder() {
	}

	public void parse(String text, KDictionary<String> keywords) {

		try {
			Token[] tokens = KSenStringTagger.getTagger().analyze(text);
			if (tokens == null) {
				return;
			}
			for (int i = 0; i < tokens.length; i++) {
				// String word = tokens[i].getBasicString();
				String word = tokens[i].toString();
				String kind = tokens[i].getPos();
				// System.out.println(word + ":" + kind);
				int index = tokens[i].start();
				if (kind.startsWith("名詞")) {
					for (String selectedWord : keywords.getElements()) {
						if (selectedWord.equals(word)) {
							founds.put(index, word);
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kbdex.view.discourse.IKWordFinder#getFoundLocations()
	 */
	public List<Integer> getFoundLocations() {
		return new ArrayList<Integer>(founds.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kbdex.view.discourse.IKWordFinder#getWord(int)
	 */
	public String getFoundWord(int index) {
		if (!founds.containsKey(index)) {
			throw new RuntimeException();
		}
		return founds.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kbdex.view.discourse.IKWordFinder#getFoundWords()
	 */
	public List<String> getFoundWords() {
		return new ArrayList<String>(new HashSet<String>(founds.values()));
	}

	public String toParamString() {
		StringBuffer buf = new StringBuffer();
		//		buf.append("text: \"");
		//		buf.append(text);
		//		buf.append("\" ");
		//		buf.append("dictionary: ");
		//		buf.append(dictionary);
		//		buf.append(" ");
		//      buf.append("result: ");
		buf.append(this.toString());
		return buf.toString();
	}

	public String toString() {
		return founds.toString();
		// StringBuffer buf = new StringBuffer();
		// buf.append("locations: " + getFoundLocations());
		// buf.append(" ");
		// buf.append("words: " + new ArrayList<String>(founds.values()));
		// return buf.toString();
	}

	/**
	 * Test
	 */
	public static void main(String[] args) {
		test("1つマッチ", "{4=もも}", "すもももももも", "もも");
	}

	public static void test(String title, String expected, String text,
			String... keywords) {
		System.out.print("-----" + title + "----->");
		KDictionary<String> dictionary = new KDictionary<String>() {
			private static final long serialVersionUID = 1L;

			protected String createInstance(String text) {
				return text;
			}
		};
		for (int i = 0; i < keywords.length; i++) {
			dictionary.addElement(keywords[i]);
		}

		KSenWordFinder finder = new KSenWordFinder();
		finder.parse(text, dictionary);

		System.out.println(finder.toString().equals(expected));
		System.out.println(finder.toParamString());
	}
}
