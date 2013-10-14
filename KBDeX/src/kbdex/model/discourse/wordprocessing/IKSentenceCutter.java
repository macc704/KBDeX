/*
 * IKSentenceCutter.java
 * Created on Apr 12, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse.wordprocessing;

import java.util.List;

/**
 * @author macchan
 */
public interface IKSentenceCutter {

	public List<String> parse(String text);

}
