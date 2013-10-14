/*
 * KFElement.java
 * Created on Jul 21, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kfl.model;

import org.zoolib.ZID;

/**
 * @author macchan
 * 
 */
public class KFElement {

	private ZID id;

	public long getIdAsLong() {
		return id.longValue();
	}

	public ZID getId() {
		return id;
	}

	public void setId(ZID id) {
		this.id = id;
	}
}
