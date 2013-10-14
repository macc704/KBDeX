/*
 * IKDiscourseFilter.java
 * Created on Mar 7, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse.filters;

import kbdex.model.discourse.KDDiscourseRecord;

/**
 * @author macchan
 * 
 */
public interface IKDiscourseFilter {
	public boolean accept(KDDiscourseRecord record);
}
