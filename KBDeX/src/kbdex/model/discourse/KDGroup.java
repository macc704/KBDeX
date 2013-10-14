/*
 * KDGroup.java
 * Created on Mar 8, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author macchan
 * 
 */
public class KDGroup {

	private String name;
	private List<String> memberNames = new ArrayList<String>();

	public KDGroup(String name) {
		this.name = name;
	}

	public KDGroup(String name, List<String> memberNames) {
		this.name = name;
		this.memberNames.addAll(memberNames);
	}

	public String getName() {
		return name;
	}

	public void addMember(String memberName) {
		if (memberName == null) {
			return;
		}
		if (memberNames.contains(memberName)) {
			return;
		}
		memberNames.add(memberName);
	}

	public List<String> getMemberNames() {
		return memberNames;
	}
}
