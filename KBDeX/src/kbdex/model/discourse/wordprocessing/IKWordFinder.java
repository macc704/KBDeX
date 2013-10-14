/*
 * IKWordFinder.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse.wordprocessing;

import java.util.List;

import kbdex.utils.KDictionary;

public interface IKWordFinder {

	public abstract void parse(String text, KDictionary<String> keywords);

	public abstract String getFoundWord(int index);

	public abstract List<Integer> getFoundLocations();

	public abstract List<String> getFoundWords();

}