/*
 * DefaultSentenceCutter.java
 * Created on Apr 23, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse.wordprocessing;

import java.util.Arrays;
import java.util.List;

/**
 * @author macchan
 *
 */
public class DefaultSentenceCutter implements IKSentenceCutter {

	/* (non-Javadoc)
	 * @see kbdex.model.discourse.wordprocessing.IKSentenceCutter#parse(java.lang.String)
	 */
	@Override
	public List<String> parse(String text) {
		return Arrays.asList(text.split("[.] |。"));
	}

}
