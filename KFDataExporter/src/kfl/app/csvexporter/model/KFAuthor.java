/*
 * KFAuthor.java
 * Created on Jul 13, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kfl.app.csvexporter.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author macchan
 * 
 */
public class KFAuthor extends KFOwnerObject {

	private static final long serialVersionUID = 1L;

	private String firstName;
	private String lastName;
	private String userName;
	private String role;
	private Date lastLogin;

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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
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

	public String getShortDescrption() {
		return getUserName();
	}

	public static List<String> header() {
		return Arrays.asList("id", "type", "uname", "name", "stat",
				"last_login");
	}

	public List<String> getStrings() {
		List<String> strings = new ArrayList<String>();
		// addBasicStrings(strings);
		strings.add(getId().toString());
		strings.add(getRole());
		strings.add(getUserName());
		strings.add(getName());
		strings.add(getStatus());
		strings.add(getLastLogin().toString());
		return strings;
	}
}
