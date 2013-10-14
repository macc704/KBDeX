/*
 * KSequentialPhase.java
 * Created on Sep 26, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.controller.tools.stepwise;

import java.util.ArrayList;
import java.util.List;

import kbdex.model.kbmodel.KBWorld;
import kbdex.model.kbmodel.KBDiscourseUnit;

/**
 * @author macchan
 */
public class KSequentialPhase implements IKPhase {

	private String name;
	private int start = 0;
	private int end = -1;

	public KSequentialPhase() {
		this("Noname Phase", 0, -1);
	}

	public KSequentialPhase(String name, int start, int end) {
		this.name = name;
		this.start = start;
		this.end = end;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kbdex.controller.IKPhase#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kbdex.controller.IKPhase#setToThisPhase(kbdex.model.kbmodel.KBWorld)
	 */
	@Override
	public void setToThisPhase(KBWorld world) {
		List<KBDiscourseUnit> allNotes = world.getUnits().getElements();

		if (allNotes.size() == 0) {
			return;
		}

		int start = this.start;
		int end = this.end;

		int indexLimit = allNotes.size() - 1;
		if (start < 0) {
			start = 0;
		}
		if (start > indexLimit) {
			start = indexLimit;
		}
		if (end < 0) {
			end = indexLimit;
		}
		// if (end > indexLimit) {
		// end = indexLimit;
		// }

		List<KBDiscourseUnit> activeNotes = new ArrayList<KBDiscourseUnit>();
		for (int i = start; i <= end; i++) {
			activeNotes.add(allNotes.get(i));
		}

		List<KBDiscourseUnit> ignoreNotes = new ArrayList<KBDiscourseUnit>(allNotes);
		ignoreNotes.removeAll(activeNotes);

		world.clearIgnoreNotes();
		for (KBDiscourseUnit note : ignoreNotes) {
			world.addIgnoreNote(note.getName());
		}
	}
}
