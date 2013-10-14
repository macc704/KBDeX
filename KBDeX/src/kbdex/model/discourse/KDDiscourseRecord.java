/*
 * KDDiscourseRecord.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kbdex.model.discourse.wordprocessing.IKSentenceCutter;
import kbdex.model.discourse.wordprocessing.KWordProcessorFactory;
import kbdex.utils.KDictionary;
import clib.common.collections.CVocaburary;
import clib.common.time.CTime;

public class KDDiscourseRecord {

	private boolean valid = true;

	private long id;
	private String agentName;
	private String groupName;
	private long time;
	private KDContentsText text;
	private List<KDContentsText> sentences = new ArrayList<KDContentsText>();

	public KDDiscourseRecord(long id, String student, String text) {
		this.id = id;
		this.agentName = student;
		this.text = new KDContentsText(text);
	}

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @param valid
	 *            the valid to set
	 */
	public void setValid(boolean valid) {

		this.valid = valid;
	}

	public long getId() {
		return id;
	}

	public String getIdAsText() {
		return Long.toString(id);
	}

	public String getAgentName() {
		return agentName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public CTime getTime() {
		return new CTime(time);
	}

	public long getTimeAsLong() {
		return time;
	}

	public Date getTimeAsDate() {
		return new Date(time);
	}

	public KDContentsText getContentsText() {
		return text;
	}

	public List<KDContentsText> getSentences() {
		return sentences;
	}

	/*******************************************
	 * Text deligations
	 *******************************************/

	public String getText() {
		return text.getText();
	}

	public CVocaburary getVocaburary() {
		return text.getVocaburary();
	}

	public List<String> getFoundKeywords() {
		return text.getFoundKeywords();
	}

	public String getHighlightedHtmlText() {
		return text.getHighlightedHtmlText();
	}

	public void createSentencesCash(KWordProcessorFactory factory) {
		IKSentenceCutter cutter = factory.createSentenceCutter();
		for (String text : cutter.parse(getText())) {
			sentences.add(new KDContentsText(text));
		}
	}

	public void createVocaburaryCash(KDictionary<String> keywords,
			KWordProcessorFactory factory) {
		this.text.createVocaburaryCash(keywords, factory);
		for (KDContentsText text : sentences) {
			text.createVocaburaryCash(keywords, factory);
		}
	}

	public void createKeywordingCash(KDictionary<String> keywords,
			KWordProcessorFactory factory) {
		this.text.createKeywordingCash(keywords, factory);
		for (KDContentsText text : sentences) {
			text.createKeywordingCash(keywords, factory);
		}
	}

}
