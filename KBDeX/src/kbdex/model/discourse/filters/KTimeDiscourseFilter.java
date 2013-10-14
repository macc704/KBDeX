/*
 * KTimeDiscourseFilter.java
 * Created on Mar 7, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse.filters;

import kbdex.model.discourse.KDDiscourseRecord;
import clib.common.time.CTime;
import clib.common.time.CTimeRange;

/**
 * @author macchan
 * 
 */
public class KTimeDiscourseFilter implements IKDiscourseFilter {

	private String name;
	private CTimeRange range;

	public KTimeDiscourseFilter(String name, long start, long end) {
		this(name, new CTimeRange(start, end));
	}

	public KTimeDiscourseFilter(String name, CTimeRange range) {
		this.name = name;
		this.range = range;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CTimeRange getRange() {
		return range;
	}

	public void setRange(CTimeRange range) {
		this.range = range;
	}

	public boolean accept(KDDiscourseRecord record) {
		return range.isIncluding(new CTime(record.getTimeAsLong()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		return toString().equals(obj.toString())
				&& range.equals(((KTimeDiscourseFilter) obj).range);
	}

	public String toString() {
		return getName();
	}
}
