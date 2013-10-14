/*
 * KBDiscourseUnitViewer.java
 * Created on Sep 30, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.modelviewers;

import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.StyledEditorKit;

import kbdex.model.kbmodel.KBWord;

/**
 * @author macchan
 * 
 */
public class KBWordViewer extends KBElementViewer {

	private static final long serialVersionUID = 1L;

	private KBWord word;

	public KBWordViewer(KBWord word) {
		this.word = word;
		initialize();
	}

	private void initialize() {
		JScrollPane scroll = new JScrollPane();
		scroll.setPreferredSize(new Dimension(300, 300));
		// JTextArea area = new JTextArea(record.getHighlightedText());
		// area.setLineWrap(true);
		JEditorPane area = new JEditorPane();
		area.setEditorKit(new StyledEditorKit());// wrapping
		// area.setFont(table.getFont());
		area.setContentType("text/html");
		area.setText(word.getName());
		scroll.setViewportView(area);

		add(scroll);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kbdex.controller.KNodeViewer#getTitle()
	 */
	@Override
	public String getTitle() {
		return word.getName();
	}
}
