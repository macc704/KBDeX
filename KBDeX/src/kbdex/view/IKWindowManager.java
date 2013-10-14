/*
 * IKWindowManager.java
 * Created on Apr 19, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view;

import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JMenuBar;

/**
 * @author macchan
 *
 */
public interface IKWindowManager {
	public Component openFrame(JComponent comp, String title, JMenuBar menuBar,
			Rectangle bounds);
}
