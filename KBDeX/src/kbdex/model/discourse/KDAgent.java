/*
 * KDAgent.java
 * Created on Apr 5, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author macchan
 * 
 */
public class KDAgent {

	private String name;

	private List<KDDiscourseRecord> records = new ArrayList<KDDiscourseRecord>();

	public KDAgent(String name) {
		this.name = name;
	}

	public void add(KDDiscourseRecord r) {
		records.add(r);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the records
	 */
	public List<KDDiscourseRecord> getRecords() {
		return records;
	}

	public int hashCode() {
		return name.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		return toString().equals(obj.toString());
	}

	public String toString() {
		return name;
	}
}
