/*
 * IKVocaburaryParser.java
 * Created on Apr 12, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse.wordprocessing;

import kbdex.utils.KDictionary;
import clib.common.collections.CVocaburary;

/**
 * @author macchan
 */
public interface IKVocaburaryParser {

	public CVocaburary parse(String text, KDictionary<String> keywords);

}
