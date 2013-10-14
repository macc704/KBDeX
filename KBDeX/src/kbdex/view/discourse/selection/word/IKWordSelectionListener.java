/*
 * IKWordSelectionListener.java
 * Created on Oct 28, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.discourse.selection.word;

import java.util.List;

/**
 * @author macchan
 * 
 */
public interface IKWordSelectionListener {

	public void wordSelected(List<String> words);

	public void wordDeselected(List<String> words);

}
