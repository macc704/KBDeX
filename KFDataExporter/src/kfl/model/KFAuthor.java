/*
 * KFAuthor.java
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
public class KFAuthor extends KFOwnerObject {

	private static final long serialVersionUID = 1L;
	
	private String firstName;
	private String lastName;
	private List<KFNote> notes = new ArrayList<KFNote>();
	private KFGroup group;

	public KFAuthor() {
	}

	@Override
	public String getType() {
		return "author";
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getName() {
		return firstName + " " + lastName;
	}

	public List<KFNote> getNotes() {
		return notes;
	}

	public void addNotes(KFNote note) {
		this.notes.add(note);
	}

	public KFGroup getGroup() {
		return group;
	}

	public void setGroup(KFGroup group) {
		this.group = group;
	}

	@Override
	public String toString() {
		return "((author)" + getName() + ")";
	}

	public List<String> getStrings() {
		List<String> strings = new ArrayList<String>();		
		addBasicStrings(strings);
		strings.add(getFirstName());
		strings.add(getLastName());
		return strings;
	}
}
