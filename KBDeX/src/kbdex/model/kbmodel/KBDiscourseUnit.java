/*
 * KBDiscourseUnit.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.kbmodel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kbdex.model.discourse.KDContentsText;
import kbdex.model.discourse.KDDiscourseRecord;

public class KBDiscourseUnit extends KBElement {

	private static final NumberFormat formatter = new DecimalFormat("0");

	private KDDiscourseRecord record;

	private KDContentsText text;

	private KBAgent agent;
	private List<KBWord> words = new ArrayList<KBWord>();

	public KBDiscourseUnit(String name) {
		super(formatter.format(Long.parseLong(name)));
	}

	public KBDiscourseUnit(KDDiscourseRecord record) {
		super(Long.toString(record.getId()));
		setRecord(record);
		setText(record.getContentsText());
	}

	public void setAgent(KBAgent agent) {
		this.agent = agent;
	}

	public KBAgent getAgent() {
		return agent;
	}

	public String getAgentName() {
		return record.getAgentName();
	}

	public void addWord(KBWord word) {
		words.add(word);
	}

	public List<KBWord> getWords() {
		return words;
	}

	public void setText(KDContentsText text) {
		this.text = text;
	}

	public String getText() {
		return text.getText();
	}

	public String getHighlightedText() {
		return text.getHighlightedHtmlText();
	}

	public List<String> getFoundKeywords() {
		return text.getFoundKeywords();
	}

	public void setRecord(KDDiscourseRecord record) {
		this.record = record;
	}

	public Date getTime() {
		return record.getTimeAsDate();
	}
}
