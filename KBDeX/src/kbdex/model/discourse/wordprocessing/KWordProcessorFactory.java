/*
 * KWordFinderRactory.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse.wordprocessing;

import kbdex.model.discourse.KDDiscourse;
import kbdex.model.discourse.KDDiscourse.Language;

public abstract class KWordProcessorFactory {

	public static KWordProcessorFactory createFactory(
			KDDiscourse.Language language) {
		if (language == Language.ENGLISH) {
			return new KEnglishWordProcessorFactory();
		} else if (language == Language.JAPANESE) {
			return new KJapaneseWordProcessorFactory();
		} else {
			throw new RuntimeException();
		}
	}

	public abstract IKWordFinder createWordFinder();

	public abstract IKVocaburaryParser createVocaburaryParser();

	public abstract IKSentenceCutter createSentenceCutter();

}

class KEnglishWordProcessorFactory extends KWordProcessorFactory {

	public IKWordFinder createWordFinder() {
		return new KSimpleWordFinder(true, true);
	}

	public IKVocaburaryParser createVocaburaryParser() {
		return new KEnglishVocaburaryParser();
	}

	public IKSentenceCutter createSentenceCutter() {
		return new DefaultSentenceCutter();
	}
}

class KJapaneseWordProcessorFactory extends KWordProcessorFactory {

	public IKWordFinder createWordFinder() {
		return new KSimpleWordFinder(false, false);
	}

	public IKVocaburaryParser createVocaburaryParser() {
		return new KSenVocaburaryParser();
	}

	public IKSentenceCutter createSentenceCutter() {
		return new DefaultSentenceCutter();
	}
}
