/*
 * KBElementViewer.java
 * Created on Sep 30, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.modelviewers;

import javax.swing.JPanel;

/**
 * @author macchan
 * 
 */
public abstract class KBElementViewer extends JPanel {

	private static final long serialVersionUID = 1L;

	public String getTitle() {
		return "Default";
	}
}
