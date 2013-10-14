/*
 * KDContentsText.java
 * Created on Apr 23, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse;

import java.util.List;

import kbdex.model.discourse.wordprocessing.IKWordFinder;
import kbdex.model.discourse.wordprocessing.KHTMLMaker;
import kbdex.model.discourse.wordprocessing.KWordProcessorFactory;
import kbdex.utils.KDictionary;
import clib.common.collections.CVocaburary;

/**
 * @author macchan
 *
 */
public class KDContentsText {

	private String text;

	//cash
	private CVocaburary vocaburary;
	private List<String> foundKeywords;
	private String highlightedHtmlText;

	//private JLabel highlightedJLabel; //Labelは時間がかかりすぎるのでcashしない

	public KDContentsText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	/*******************************************
	 * cashing strategy
	 *******************************************/

	public void createVocaburaryCash(KDictionary<String> keywords,
			KWordProcessorFactory factory) {
		vocaburary = factory.createVocaburaryParser().parse(text, keywords);
	}

	public void createKeywordingCash(KDictionary<String> keywords,
			KWordProcessorFactory factory) {
		IKWordFinder finder = factory.createWordFinder();
		finder.parse(text, keywords);
		this.foundKeywords = finder.getFoundWords();
		this.highlightedHtmlText = KHTMLMaker.makeHighlightedHtml(text, finder);
	}

	public CVocaburary getVocaburary() {
		if (vocaburary == null) {
			throw new RuntimeException();
		}
		return vocaburary;
	}

	public List<String> getFoundKeywords() {
		if (foundKeywords == null) {
			throw new RuntimeException();
		}
		return foundKeywords;
	}

	public String getHighlightedHtmlText() {
		if (this.highlightedHtmlText == null) {
			throw new RuntimeException();
		}
		return this.highlightedHtmlText;
	}

	//	public JLabel getHighlightedJLabel() {
	//		if (!cashed) {
	//			throw new RuntimeException();
	//		}
	//		return this.highlightedJLabel;
	//	}
}
