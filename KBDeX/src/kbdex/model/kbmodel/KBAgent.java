/*
 * KBAgent.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.kbmodel;

import java.util.ArrayList;
import java.util.List;

public class KBAgent extends KBElement {

	private List<KBDiscourseUnit> units = new ArrayList<KBDiscourseUnit>();

	public KBAgent(String name) {
		super(name);
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
