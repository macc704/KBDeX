/*
 * KFGroup.java
 * Created on Jul 21, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kfl.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author macchan
 * 
 */
public class KFGroup extends KFOwnerObject {

	private static final long serialVersionUID = 1L;
	
	private String name;

	private List<KFAuthor> authors = new ArrayList<KFAuthor>();

	@Override
	public String getType() {
		return "group";
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the authors
	 */
	public List<KFAuthor> getAuthors() {
		return authors;
	}

	/**
	 * @param authors
	 *            the authors to set
	 */
	public void addAuthor(KFAuthor author) {
		this.authors.add(author);
	}
	
	@Override
	public String toString() {
		return "((group)" + getName() + ")";
	}

	public List<String> getStrings() {
		List<String> strings = new ArrayList<String>();		
		strings.add(getName());
		strings.add(listToString("members", authors));
		return strings;
	}

}
