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

import kbdex.model.kbmodel.KBAgent;

/**
 * @author macchan
 * 
 */
public class KBAgentViewer extends KBElementViewer {

	private static final long serialVersionUID = 1L;

	private KBAgent agent;

	public KBAgentViewer(KBAgent agent) {
		this.agent = agent;
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
		area.setText(agent.getName());
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
		return agent.getName();
	}
}
