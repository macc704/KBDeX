/*
 * KBWord.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.kbmodel;

import java.util.ArrayList;
import java.util.List;

public class KBWord extends KBElement {

	private List<KBDiscourseUnit> units = new ArrayList<KBDiscourseUnit>();

	protected KBWord(String text) {
		super(text);
	}

	public void addUnit(KBDiscourseUnit unit) {
		units.add(unit);
	}

	public void removeUnit(KBDiscourseUnit unit) {
		units.remove(unit);
	}

	public List<KBDiscourseUnit> getUnits() {
		return units;
	}

}
