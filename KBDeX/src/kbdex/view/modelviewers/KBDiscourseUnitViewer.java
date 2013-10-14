/*
 * KBDiscourseUnitViewer.java
 * Created on Sep 30, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.modelviewers;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import kbdex.model.kbmodel.KBDiscourseUnit;
import clib.common.string.CStringEscaper;

/**
 * @author macchan
 * 
 */
public class KBDiscourseUnitViewer extends KBElementViewer {

	private static final long serialVersionUID = 1L;

	private KBDiscourseUnit note;

	public KBDiscourseUnitViewer(KBDiscourseUnit note) {
		this.note = note;
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());

		JScrollPane scroll = new JScrollPane();
		scroll.setPreferredSize(new Dimension(600, 400));
		// scroll.setMaximumSize(new Dimension(1000, 1000));
		// JTextArea area = new JTextArea(record.getHighlightedText());
		// area.setLineWrap(true);
		JEditorPane area = new JEditorPane();

		area.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,
				Boolean.TRUE);
		// System.out.println(area.getFont());
		area.setFont(area.getFont().deriveFont(10));
		// System.out.println(area.getFont());
		// area.setEditorKit(new StyledEditorKit());// wrapping
		// area.setFont(table.getFont());
		area.setContentType("text/html");
		String text = note.getHighlightedText();
		text = CStringEscaper.unEscape(text);
		area.setText(text);
		scroll.setViewportView(area);
		add(scroll);

		// JLabel agentName = new JLabel(note.getAgentName());
		// add(agentName, BorderLayout.NORTH);

		JLabel time = new JLabel(note.getTime().toString());
		time.setHorizontalAlignment(JLabel.RIGHT);
		add(time, BorderLayout.SOUTH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kbdex.controller.KNodeViewer#getTitle()
	 */
	@Override
	public String getTitle() {
		return note.getName() + " - " + note.getAgentName();
	}
}
