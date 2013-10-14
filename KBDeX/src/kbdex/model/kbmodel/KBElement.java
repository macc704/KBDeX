/*
 * KBElement.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.kbmodel;

public abstract class KBElement implements Comparable<KBElement> {

	//property
	private String name;

	//state
	private boolean valid = true;
	private boolean selected = false;

	/*************************************
	 * Constructor(s)
	 *************************************/

	protected KBElement(String name) {
		this.name = name;
		initialize();
	}

	protected void initialize() {
		this.hashCash = toString().hashCode();
	}

	/*************************************
	 * public interface
	 *************************************/

	public String getName() {
		return name;
	}

	protected void setValid(boolean valid) {
		this.valid = valid;
	}

	public boolean isValid() {
		return valid;
	}

	protected void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	/*************************************
	 * equals policies
	 *************************************/

	private int hashCash = -1;//optimization 04/21

	public final int hashCode() {
		//		if (hashCash < 0) {
		//			hashCash = toString().hashCode();
		//		}
		return hashCash;
	}

	public final boolean equals(Object another) {
		//optimization 04/21		
		//		if (another == null) {
		//			return false;
		//		}
		assert another != null;
		assert another instanceof KBElement;

		//optimization 04/21
		if (hashCash != ((KBElement) another).hashCash) {
			return false;
		}
		return toString().equals(another.toString());
	}

	public String toString() {
		return getName();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(KBElement o) {
		return toString().compareTo(o.toString());
	}
}
