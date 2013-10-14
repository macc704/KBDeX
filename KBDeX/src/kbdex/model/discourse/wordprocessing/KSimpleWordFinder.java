/*
 * KSimpleWordFinder.java
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

/*
 * ・テスト用．Pajekとの比較によるverifyをするために使う
 */
public class KSimpleWordFinder implements IKWordFinder {

	private boolean lowercaseCheck = false;
	private boolean wordIncludingCheck = true;

	private SortedMap<Integer, String> founds = new TreeMap<Integer, String>();

	protected KSimpleWordFinder(boolean lowercaseCheck,
			boolean wordIncludingCheck) {
		this.lowercaseCheck = lowercaseCheck;
		this.wordIncludingCheck = wordIncludingCheck;
	}

	public void parse(String text, KDictionary<String> keywords) {
		if (lowercaseCheck) {
			text = text.toLowerCase();
		}

		for (String word : keywords.getElements()) {
			String currentText = text;
			while (true) {// 後ろから切っていくこと．前から切ると，indexがずれる．
				int index = currentText.lastIndexOf(word);
				if (index < 0) {
					break;
				}

				// 包含しているwordでないか調べる(アルゴリズムがださいので変更すること)
				if (wordIncludingCheck) {
					if (index > 0) {// 最初でない
						char c = text.charAt(index - 1);
						if (!(c == ' ' /* || c == '.' */|| c == ',' || c == '?')) {// かつ，直前の文字がスペース,.じゃない->NG
							// 7.29問題回避するため，ひとまず直前ピリオドは×
							currentText = currentText.substring(0, index);
							continue;
						}
					}
					if (!text.endsWith(word)) {
						char c = text.charAt(index + word.length());
						if (!(c == ' ' || c == '.' || c == ',' || c == '?')) {
							currentText = currentText.substring(0, index);
							continue;
						}
					}
				}

				founds.put(index, word);
				currentText = currentText.substring(0, index);
			}
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

	public String toString() {
		return founds.toString();
	}

}
