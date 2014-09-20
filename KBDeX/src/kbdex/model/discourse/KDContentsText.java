/*
 * KDContentsText.java
 * Created on Apr 23, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse;

import java.util.List;
import java.util.Map;

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

	private String originalText;

	//cash
	private String filteredText;
	private CVocaburary vocaburary;
	private List<String> foundKeywords;
	private String highlightedHtmlText;

	//private JLabel highlightedJLabel; //Labelは時間がかかりすぎるのでcashしない

	public KDContentsText(String text) {
		this.originalText = text;
	}
	
	public String getOriginalText(){
		return originalText;
	}

	public String getText() {
		if(filteredText == null){
			return originalText;
		}
		return filteredText;
	}

	/*******************************************
	 * cashing strategy
	 *******************************************/

	public void createFilterTextCash(Map<String, String> textfilter) {
		filteredText = originalText.toLowerCase();
		for (String key : textfilter.keySet()) {
			key = key.toLowerCase();
			filteredText = filteredText.replaceAll(key, textfilter.get(key));
		}
	}

	public void createVocaburaryCash(KDictionary<String> keywords,
			KWordProcessorFactory factory) {
		vocaburary = factory.createVocaburaryParser().parse(getText(),
				keywords);
	}

	public void createKeywordingCash(KDictionary<String> keywords,
			KWordProcessorFactory factory) {
		IKWordFinder finder = factory.createWordFinder();
		finder.parse(getText(), keywords);
		this.foundKeywords = finder.getFoundWords();
		this.highlightedHtmlText = KHTMLMaker.makeHighlightedHtml(getText(),
				finder);
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
