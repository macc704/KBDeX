/*
 * KFNote.java
 * Created on Jul 13, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kfl.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author macchan
 * 
 */
public class KFNote extends KFElement {

	private static final long serialVersionUID = 1L;

	private String title;
	private String text;
	// private KFAuthor author;

	private KFNote buildson;
	private List<KFRiseAbove> riseaboves = new ArrayList<KFRiseAbove>();
	private List<KFSupport> supported = new ArrayList<KFSupport>();
	private List<KFKeyword> keywords = new ArrayList<KFKeyword>();

	public KFNote() {
	}

	@Override
	public String getType() {
		return "note";
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	// public KFAuthor getAuthor() {
	// return author;
	// }
	//
	// public void setAuthor(KFAuthor author) {
	// this.author = author;
	// }

	public void addRiseabove(KFRiseAbove riseabove) {
		riseaboves.add(riseabove);
	}

	public void addSupport(KFSupport support) {
		supported.add(support);
	}

	public void addKeyword(KFKeyword keyword) {
		keywords.add(keyword);
	}

	// public boolean isContained(KFView view) {
	// return views.contains(view);
	// }

	// public boolean isContained(List<KFView> views) {
	// for (KFView view : views) {
	// if (isContained(view)) {
	// return true;
	// }
	// }
	// return false;
	//
	// }

	public void setBuildson(KFNote buildson) {
		this.buildson = buildson;
	}

	public KFNote getBuildson() {
		return buildson;
	}

	@Override
	public String toString() {
		return "((note)" + getTitle() + ")";
	}

	public List<String> getStrings() {
		List<String> strings = new ArrayList<String>();
		addBasicStrings(strings);
		strings.add(getTitle());
		strings.add(getText());
		if (getBuildson() != null) {
			strings.add(getBuildson().getIdAsString());
		} else {
			strings.add("");
		}
		strings.add(listToString("keywords", keywords));
		strings.add(listToString("supported", supported));
		strings.add(listToString("riseaboves", riseaboves));

		return strings;
	}

	

	@Override
	public String getShortDescrption() {
		return getTitle();
	}

}
