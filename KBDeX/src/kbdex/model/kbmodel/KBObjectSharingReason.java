/*
 * KBObjectSharingReason.java
 * Created on Apr 23, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.kbmodel;

/**
 * @author macchan
 *
 */
public class KBObjectSharingReason<X> implements IKBReason {

	private X object;

	public KBObjectSharingReason(X sharedObject) {
		this.object = sharedObject;
	}

	/**
	 * @return the object
	 */
	public X getSharedObject() {
		return object;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return object.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return object.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof KBObjectSharingReason<?>)) {
			return false;
		}
		return object.equals(((KBObjectSharingReason<?>) obj).object);
	}
}
