/*
 * KSenVocaburaryParser.java
 * Created on Apr 12, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse.wordprocessing;

import kbdex.utils.KDictionary;
import net.java.sen.Token;
import clib.common.collections.CVocaburary;

/**
 * @author macchan
 *
 */
public class KSenVocaburaryParser implements IKVocaburaryParser {

	public CVocaburary parse(String text, KDictionary<String> keywords) {
		CVocaburary vocaburary = new CVocaburary();
		try {
			Token[] tokens = KSenStringTagger.getTagger().analyze(text);
			if (tokens != null) {
				for (int i = 0; i < tokens.length; i++) {
					String word = tokens[i].toString();
					String kind = tokens[i].getPos();
					if (kind.startsWith("名詞")) {
						vocaburary.add(word);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return vocaburary;
	}
}
