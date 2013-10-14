/*
 * KSenVocaburaryParser.java
 * Created on Apr 12, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse.wordprocessing;

import kbdex.utils.KDictionary;
import clib.common.collections.CVocaburary;

/**
 * @author macchan
 *
 */
public class KEnglishVocaburaryParser implements IKVocaburaryParser {

	public CVocaburary parse(String text, KDictionary<String> keywords) {
		CVocaburary vocaburary = new CVocaburary();
		try {
			text = text.replaceAll("[.]", " ");
			text = text.replaceAll(",", " ");
			String[] tokens = text.split(" ");
			for (int i = 0; i < tokens.length; i++) {
				if (tokens[i].length() > 0) {
					vocaburary.add(tokens[i]);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return vocaburary;
	}
}
