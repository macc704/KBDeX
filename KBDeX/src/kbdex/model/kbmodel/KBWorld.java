/*
 * KBWorld.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.kbmodel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import kbdex.model.discourse.KDContentsText;
import kbdex.model.discourse.KDDiscourseRecord;
import kbdex.utils.KDictionary;
import clib.common.model.CAbstractModelObject;

public class KBWorld extends CAbstractModelObject {

	private static final long serialVersionUID = 1L;

	public enum Event {
		AGENT_ADDED, UNIT_ADDED, WORD_ADDED, PREPARE_AGENT_REMOVE, PREPARE_UNIT_REMOVE, PREPARE_WORD_REMOVE
	};

	private LinkedList<KDDiscourseRecord> records = new LinkedList<KDDiscourseRecord>();
	private int lifetime = Integer.MAX_VALUE;//default
	private boolean sentenceMode = false;//default

	private KDictionary<KBAgent> agents = new KDictionary<KBAgent>() {
		private static final long serialVersionUID = 1L;

		protected KBAgent createInstance(String text) {
			return new KBAgent(text);
		}
	};
	private KDictionary<KBDiscourseUnit> units = new KDictionary<KBDiscourseUnit>() {
		private static final long serialVersionUID = 1L;

		protected KBDiscourseUnit createInstance(String text) {
			return new KBDiscourseUnit(text);
		}
	};
	private KDictionary<KBWord> words = new KDictionary<KBWord>() {
		private static final long serialVersionUID = 1L;

		protected KBWord createInstance(String text) {
			return new KBWord(text);
		}
	};

	private KDictionary<KBAgent> ignoreAgents = new KDictionary<KBAgent>() {
		private static final long serialVersionUID = 1L;

		protected KBAgent createInstance(String text) {
			return new KBAgent(text);
		}
	};

	private KDictionary<KBDiscourseUnit> ignoreUnits = new KDictionary<KBDiscourseUnit>() {
		private static final long serialVersionUID = 1L;

		protected KBDiscourseUnit createInstance(String text) {
			return new KBDiscourseUnit(text);
		}
	};

	private KDictionary<KBWord> ignoreWords = new KDictionary<KBWord>() {
		private static final long serialVersionUID = 1L;

		protected KBWord createInstance(String text) {
			return new KBWord(text);
		}
	};

	private KBSelectionManager selectionManager = new KBSelectionManager(this);

	public KBWorld() {
	}

	public void setLifetime(int lifetime) {
		if (lifetime <= 0) {
			lifetime = Integer.MAX_VALUE;
		}
		this.lifetime = lifetime;
	}

	public boolean isLifeExpired() {
		return records.size() > lifetime;
	}

	public void setSentenceMode(boolean sentenceMode) {
		this.sentenceMode = sentenceMode;
	}

	public boolean isSentenceMode() {
		return sentenceMode;
	}

	public void addRecord(KDDiscourseRecord record) {
		addRecordInternal(record);
		records.addLast(record);

		if (isLifeExpired()) {
			KDDiscourseRecord oldRecord = records.removeFirst();
			removeRecordInternal(oldRecord);
		}
	}

	private void addRecordInternal(KDDiscourseRecord record) {
		if (!isSentenceMode()) {
			KBDiscourseUnit unit = createNewDiscourseUnit(record.getIdAsText(),
					record.getContentsText(), record);
			addDiscourseUnit(unit, record.getAgentName());
		} else {
			int count = 1;
			for (KDContentsText text : record.getSentences()) {
				String id = createDiscourseUnitId(record.getId(), count);
				KBDiscourseUnit unit = createNewDiscourseUnit(id, text, record);
				addDiscourseUnit(unit, record.getAgentName());
				count++;
			}
		}
	}

	private void removeRecordInternal(KDDiscourseRecord record) {
		if (!isSentenceMode()) {
			KBDiscourseUnit unit = units.getElement(record.getIdAsText());
			removeDiscourseUnit(unit);
		} else {
			int len = record.getSentences().size();
			for (int i = 0; i < len; i++) {
				String id = createDiscourseUnitId(record.getId(), i + 1);
				KBDiscourseUnit unit = units.getElement(id);
				removeDiscourseUnit(unit);
			}
		}
	}

	private String createDiscourseUnitId(long base, int count) {
		long id = base * 100 + count;
		return Long.toString(id);
	}

	private KBDiscourseUnit createNewDiscourseUnit(String id,
			KDContentsText text, KDDiscourseRecord record) {
		KBDiscourseUnit unit = units.getElement(id); //Must be new
		unit.setRecord(record);
		unit.setText(text);
		if (ignoreUnits.contains(id)) {
			unit.setValid(false);
		}
		return unit;
	}

	private void addDiscourseUnit(KBDiscourseUnit unit, String agentName) {
		//Create Agent
		KBAgent agent = addAgentIfNone(agentName);

		//Unit RelationShip(Agent)		
		unit.setAgent(agent);
		agent.addUnit(unit);
		if (!agent.isValid()) {//Ignore Unit by Agent
			unit.setValid(false);
		}

		//Unit RelationShip(Word)
		List<String> keywords = unit.getFoundKeywords();
		for (String keyword : keywords) {
			KBWord word = addWordIfNone(keyword);
			unit.addWord(word);
			word.addUnit(unit);
		}

		fireModelUpdated(Event.UNIT_ADDED, unit);
	}

	private void removeDiscourseUnit(KBDiscourseUnit unit) {
		fireModelUpdated(Event.PREPARE_UNIT_REMOVE, unit);

		units.removeElement(unit.toString());
		if (unit.getAgent() != null) {
			unit.getAgent().removeUnit(unit);
		}
		for (KBWord word : unit.getWords()) {
			word.removeUnit(unit);
		}
	}

	private KBAgent addAgentIfNone(String name) {
		if (!agents.contains(name)) {
			KBAgent agent = agents.getElement(name);
			if (ignoreAgents.contains(name)) {
				agent.setValid(false);
			}
			fireModelUpdated(Event.AGENT_ADDED, agent);
			return agent;
		} else {
			return agents.getElement(name);
		}
	}

	private KBWord addWordIfNone(String name) {
		if (!words.contains(name)) {
			KBWord word = words.getElement(name);
			if (ignoreWords.contains(name)) {
				word.setValid(false);
			}
			fireModelUpdated(Event.WORD_ADDED, word);
			return word;
		} else {
			return words.getElement(name);
		}
	}

	/*****************************************************************
	 * Basic Interface
	 *****************************************************************/

	public KDictionary<KBAgent> getAgents() {
		return agents;
	}

	public KDictionary<KBDiscourseUnit> getUnits() {
		return units;
	}

	public KDictionary<KBWord> getWords() {
		return words;
	}

	public void clearData() {
		records.clear();
		agents.clear();
		words.clear();
		units.clear();
	}

	//	protected void clearAll() {
	//		clearData();
	//		clearIgnoreStates();
	//		clearSelection();
	//	}

	/*****************************************************************
	 * Selection Manager
	 *****************************************************************/

	public void selectAgents(List<KBAgent> agents) {
		selectionManager.selectAgents(agents);
	}

	public void selectUnits(List<KBDiscourseUnit> units) {
		selectionManager.selectUnits(units);
	}

	public void selectWords(List<KBWord> words) {
		selectionManager.selectWords(words);
	}

	public List<KBAgent> getSelectedAgents() {
		return selectionManager.getSelectedAgents();
	}

	public List<KBDiscourseUnit> getSelectedUnits() {
		return selectionManager.getSelectedUnits();
	}

	public List<KBWord> getSelectedWords() {
		return selectionManager.getSelectedWords();
	}

	public List<KBAgent> getNonSelectedAgents() {
		return selectionManager.getNonSelectedAgents();
	}

	public List<KBDiscourseUnit> getNonSelectedUnits() {
		return selectionManager.getNonSelectedUnits();
	}

	public List<KBWord> getNonSelectedWords() {
		return selectionManager.getNonSelectedWords();
	}

	public void selectAgentsByPointer(List<String> pointers) {
		selectAgents(getPointedAgents(pointers));
	}

	public void selectUnitsByPointer(List<String> pointers) {
		selectUnits(getPointedUnits(pointers));
	}

	public void selectWordsByPointer(List<String> pointers) {
		selectWords(getPointedWords(pointers));
	}

	public void clearSelection() {
		selectionManager.clearSelection();
	}

	public void redoSelection() {
		selectionManager.redoSelection();
	}

	protected List<String> getPointers(List<? extends KBElement> elements) {
		List<String> pointers = new ArrayList<String>();
		for (KBElement element : elements) {
			pointers.add(element.getName());
		}
		return pointers;
	}

	protected List<KBAgent> getPointedAgents(List<String> pointers) {
		return agents.getElements(pointers);
	}

	protected List<KBDiscourseUnit> getPointedUnits(List<String> pointers) {
		return units.getElements(pointers);
	}

	protected List<KBWord> getPointedWords(List<String> pointers) {
		return words.getElements(pointers);
	}

	/*****************************************************************
	 * Ignoreing Manager
	 *****************************************************************/

	public void addIgnoreAgent(String name) {
		ignoreAgents.addElement(name);
	}

	public void removeIgnoreAgent(String name) {
		ignoreAgents.removeElement(name);
	}

	public void clearIgnoreAgents() {
		ignoreAgents.clear();
	}

	public void addIgnoreNote(String name) {
		ignoreUnits.addElement(name);
	}

	public void removeIgnoreNote(String name) {
		ignoreUnits.removeElement(name);
	}

	public void clearIgnoreNotes() {
		ignoreUnits.clear();
	}

	public void addIgnoreWord(String name) {
		ignoreWords.addElement(name);
	}

	public void removeIgnoreWord(String name) {
		ignoreWords.removeElement(name);
	}

	public void clearIgnoreWords() {
		ignoreWords.clear();
	}

	public void clearAllIgnoreStates() {
		clearIgnoreAgents();
		clearIgnoreNotes();
		clearIgnoreWords();
	}

}
