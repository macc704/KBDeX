/*
 * KFNote.java
 * Created on Jul 13, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kfl.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author macchan
 * 
 */
public class KFNote extends KFElement {

	private String title;
	private String text;
	private Date created;
	private Date modified;
	private KFAuthor author;
	private List<KFView> views = new ArrayList<KFView>();

	public KFNote() {
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

	public KFAuthor getAuthor() {
		return author;
	}

	public void setAuthor(KFAuthor author) {
		this.author = author;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created
	 *            the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the modified
	 */
	public Date getModified() {
		return modified;
	}

	/**
	 * @param modified
	 *            the modified to set
	 */
	public void setModified(Date modified) {
		this.modified = modified;
	}

	public void addView(KFView view) {
		views.add(view);
	}

	public boolean isContained(KFView view) {
		return views.contains(view);
	}

	public boolean isContained(List<KFView> views) {
		for (KFView view : views) {
			if (isContained(view)) {
				return true;
			}
		}
		return false;

	}

}
