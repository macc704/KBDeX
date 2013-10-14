/*
 * KBElementPair.java
 * Created on Apr 23, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.kbmodel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author macchan
 */
public class KBElementPair {

	private Object[] objects;
	private String cashString;
	private int cashHashCode;

	public KBElementPair(Object... objects) {
		this.objects = createSorted(objects);
		this.cashString = createString();
		this.cashHashCode = cashString.hashCode();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object[] createSorted(Object[] objects) {
		List array = Arrays.asList(objects);
		Collections.sort(array);
		return array.toArray();
	}

	/**
	 * @return
	 */
	private String createString() {
		StringBuffer buf = new StringBuffer();
		for (Object o : objects) {
			buf.append(o);
			buf.append("+");
		}
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return cashHashCode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return cashString;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || (!(obj instanceof KBElementPair))) {
			return false;
		}
		KBElementPair another = (KBElementPair) obj;
		if (objects.length != another.objects.length) {
			return false;
		}
		return cashString.equals(another.cashString);
	}
}
