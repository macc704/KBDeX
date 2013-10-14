/*
 * KBSelectionManager.java
 * Created on Apr 15, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.kbmodel;

import java.util.ArrayList;
import java.util.List;

import clib.common.thread.ICTask;

/**
 * @author macchan
 * 現在多少冗長なアルゴリズム（前クリアしてから選択し直す）となっている．
 */
public class KBSelectionManager {

	private ICTask selectionTask = null;

	private KBWorld world;

	public KBSelectionManager(KBWorld world) {
		this.world = world;
	}

	public void selectAgents(List<KBAgent> agents) {
		final List<String> pointers = world.getPointers(agents);
		this.selectionTask = new ICTask() {
			public void doTask() {
				List<KBAgent> agents = world.getPointedAgents(pointers);
				List<KBDiscourseUnit> units = getUnitsByAgents(agents);
				List<KBWord> words = getWordsByUnits(units);
				clearSelectionInternal();
				addSelection(agents, units, words, true);
			}
		};
		this.selectionTask.doTask();
	}

	public void selectUnits(List<KBDiscourseUnit> units) {
		final List<String> pointers = world.getPointers(units);
		this.selectionTask = new ICTask() {
			public void doTask() {
				List<KBDiscourseUnit> units = world.getPointedUnits(pointers);
				List<KBAgent> agents = getAgentsByUnits(units);
				List<KBWord> words = getWordsByUnits(units);
				clearSelectionInternal();
				addSelection(agents, units, words, true);
			}
		};
		this.selectionTask.doTask();
	}

	public void selectWords(final List<KBWord> words) {
		final List<String> pointers = world.getPointers(words);
		this.selectionTask = new ICTask() {
			public void doTask() {
				List<KBWord> words = world.getPointedWords(pointers);
				List<KBDiscourseUnit> units = getUnitsByWords(words);
				List<KBAgent> agents = getAgentsByUnits(units);
				clearSelectionInternal();
				addSelection(agents, units, words, true);
			}
		};
		this.selectionTask.doTask();
	}

	private List<KBAgent> getAgentsByUnits(List<KBDiscourseUnit> units) {
		List<KBAgent> agents = new ArrayList<KBAgent>();
		for (KBDiscourseUnit note : units) {
			agents.add(note.getAgent());
		}
		return agents;
	}

	private List<KBDiscourseUnit> getUnitsByAgents(List<KBAgent> agents) {
		List<KBDiscourseUnit> units = new ArrayList<KBDiscourseUnit>();
		for (KBAgent agent : agents) {
			units.addAll(agent.getUnits());
		}
		return units;
	}

	private List<KBDiscourseUnit> getUnitsByWords(List<KBWord> words) {
		List<KBDiscourseUnit> units = new ArrayList<KBDiscourseUnit>();
		for (KBWord word : words) {
			units.addAll(word.getUnits());
		}
		return units;
	}

	private List<KBWord> getWordsByUnits(List<KBDiscourseUnit> units) {
		List<KBWord> words = new ArrayList<KBWord>();
		for (KBDiscourseUnit unit : units) {
			words.addAll(unit.getWords());
		}
		return words;
	}

	private void addSelection(List<KBAgent> agents,
			List<KBDiscourseUnit> units, List<KBWord> words, boolean status) {
		addSelection(agents, status);
		addSelection(units, status);
		addSelection(words, status);
	}

	private void addSelection(List<? extends KBElement> elements, boolean status) {
		for (KBElement element : elements) {
			element.setSelected(status);
		}
	}

	private void clearSelectionInternal() {
		addSelection(world.getAgents().getElements(), world.getUnits()
				.getElements(), world.getWords().getElements(), false);
	}

	public void clearSelection() {
		this.selectionTask = new ICTask() {
			public void doTask() {
				clearSelectionInternal();
			}
		};
		this.selectionTask.doTask();
	}

	public void redoSelection() {
		if (selectionTask != null) {
			selectionTask.doTask();
		}
	}

	public List<KBAgent> getSelectedAgents() {
		return filterElements(world.getAgents().getElements(), true);
	}

	public List<KBDiscourseUnit> getSelectedUnits() {
		return filterElements(world.getUnits().getElements(), true);
	}

	public List<KBWord> getSelectedWords() {
		return filterElements(world.getWords().getElements(), true);
	}

	public List<KBAgent> getNonSelectedAgents() {
		return filterElements(world.getAgents().getElements(), false);
	}

	public List<KBDiscourseUnit> getNonSelectedUnits() {
		return filterElements(world.getUnits().getElements(), false);
	}

	public List<KBWord> getNonSelectedWords() {
		return filterElements(world.getWords().getElements(), false);
	}

	private <T extends KBElement> List<T> filterElements(List<T> all,
			boolean selectionState) {
		List<T> selected = new ArrayList<T>();
		for (T element : all) {
			if (element.isSelected() == selectionState) {
				selected.add(element);
			}
		}
		return selected;
	}
}
