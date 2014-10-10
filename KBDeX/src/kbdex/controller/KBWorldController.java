/*
 * KBWorldController.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import kbdex.model.discourse.KDDiscourseRecord;
import kbdex.model.kbmodel.KBAgent;
import kbdex.model.kbmodel.KBAgentRelationReason;
import kbdex.model.kbmodel.KBDiscourseUnit;
import kbdex.model.kbmodel.KBElement;
import kbdex.model.kbmodel.KBObjectSharingReason;
import kbdex.model.kbmodel.KBWord;
import kbdex.model.kbmodel.KBWorld;
import clib.common.model.ICModelChangeListener;

/**
 * Class KBWorldController
 *
 */
public class KBWorldController implements ICModelChangeListener,
		IKDiscourseControllerListener {

	private KBWorld model = new KBWorld();

	private KNNetworkController<KBAgent> agentNetworkController;
	private KNNetworkController<KBDiscourseUnit> unitNetworkController;
	private KNNetworkController<KBWord> wordNetworkController;

	public KBWorldController() {
		initialize();
	}

	private void initialize() {
		this.agentNetworkController = new KNNetworkController<KBAgent>(
				"Students", false) {
			public void selectModel(List<KBAgent> models) {
				model.selectAgents(models);
				KBWorldController.this.refreshWithoutAnimation();
			}

			public void selectModelByPointer(List<String> pointers) {
				model.selectAgentsByPointer(pointers);
				KBWorldController.this.refreshWithoutAnimation();
			}
		};
		this.unitNetworkController = new KNNetworkController<KBDiscourseUnit>(
				"Discourse Units", true) {
			public void selectModel(List<KBDiscourseUnit> models) {
				model.selectUnits(models);
				KBWorldController.this.refreshWithoutAnimation();
			}

			public void selectModelByPointer(List<String> pointers) {
				model.selectUnitsByPointer(pointers);
				KBWorldController.this.refreshWithoutAnimation();
			}
		};
		this.wordNetworkController = new KNNetworkController<KBWord>("Words",
				false) {
			public void selectModel(List<KBWord> models) {
				model.selectWords(models);
				KBWorldController.this.refreshWithoutAnimation();
			}

			public void selectModelByPointer(List<String> pointers) {
				model.selectWordsByPointer(pointers);
				KBWorldController.this.refreshWithoutAnimation();
			}
		};

		model.addModelListener(this);
	}

	public KBWorld getModel() {
		return model;
	}

	public KNNetworkController<KBAgent> getAgentNetworkController() {
		return agentNetworkController;
	}

	public KNNetworkController<KBDiscourseUnit> getUnitsNetworkController() {
		return unitNetworkController;
	}

	public KNNetworkController<KBWord> getWordNetworkController() {
		return wordNetworkController;
	}

	public List<KNNetworkController<?>> getAllNetworkControllers() {
		List<KNNetworkController<?>> list = new ArrayList<KNNetworkController<?>>();
		list.add(getAgentNetworkController());
		list.add(getUnitsNetworkController());
		list.add(getWordNetworkController());
		return list;
	}

	public void add(KDDiscourseRecord record) {
		model.addRecord(record);
		model.redoSelection(); //TODO 100 ここでよいか検討
		tick();
	}

	public void modelUpdated(Object... args) {
		try {
			KBWorld.Event event = (KBWorld.Event) args[0];
			KBElement element = (KBElement) args[1];
			if (event == KBWorld.Event.AGENT_ADDED) {
				agentNetworkController.getModel().addNode((KBAgent) element);
			} else if (event == KBWorld.Event.UNIT_ADDED) {
				unitNetworkController.getModel().addNode(
						(KBDiscourseUnit) element);
				makeNetwork((KBDiscourseUnit) element);
			} else if (event == KBWorld.Event.PREPARE_UNIT_REMOVE) {
				undoNetwork((KBDiscourseUnit) element);
				unitNetworkController.getModel().removeNode(
						(KBDiscourseUnit) element);
			} else if (event == KBWorld.Event.WORD_ADDED) {
				wordNetworkController.getModel().addNode((KBWord) element);
			} else {
				throw new RuntimeException("unknown update command");
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private void makeNetwork(KBDiscourseUnit unit) {

		if (!unit.isValid()) {
			return;
		}

		// Note Graph
		for (KBWord word : unit.getWords()) {
			for (KBDiscourseUnit relatedUnit : word.getUnits()) {
				if (!relatedUnit.isValid()) {
					continue;
				}
				unitNetworkController.getModel().addRelation(unit, relatedUnit,
						new KBObjectSharingReason<KBWord>(word));
				//TODO 1000 both direction?
				//unitNetworkController.getModel().addEdge(relatedUnit, unit);
			}
		}

		// Word Graph
		for (KBWord word : unit.getWords()) {
			if (!word.isValid()) {
				continue;
			}
			for (KBWord relatedWord : unit.getWords()) {
				if (!relatedWord.isValid()) {
					continue;
				}

				// normally both direction but one ignored by addShare method in addRelation method
				wordNetworkController.getModel().addRelation(word, relatedWord,
						new KBObjectSharingReason<KBDiscourseUnit>(unit));
			}
		}

		// Agent Graph
		//		System.out.println("add" + unit
		//				+ "--------------------------------------");
		Collection<KBDiscourseUnit> neighbors = unitNetworkController
				.getModel().getGraph().getNeighbors(unit);
		for (KBDiscourseUnit neighbor : neighbors) {
			if (!neighbor.isValid()) {
				continue;
			}
			//			System.out.println("add" + unit.getAgent() + ","
			//					+ neighbor.getAgent() + "," + unit + "," + neighbor);
			agentNetworkController.getModel().addRelation(unit.getAgent(),
					neighbor.getAgent(),
					new KBAgentRelationReason(unit, neighbor));
		}
	}

	private void undoNetwork(KBDiscourseUnit unit) {

		// Agent Graph
		Collection<KBDiscourseUnit> neighbors = unitNetworkController
				.getModel().getGraph().getNeighbors(unit);
		if (neighbors != null) {
			for (KBDiscourseUnit neighbor : neighbors) {
				agentNetworkController.getModel().removeRelation(
						unit.getAgent(), neighbor.getAgent(),
						new KBAgentRelationReason(unit, neighbor));
			}
		}

		// Word Graph
		for (KBWord word : unit.getWords()) {
			for (KBWord relatedWord : unit.getWords()) {
				wordNetworkController.getModel().removeRelation(word,
						relatedWord,
						new KBObjectSharingReason<KBDiscourseUnit>(unit));
			}
		}

		// Unit Graph
		for (KBWord word : unit.getWords()) {
			for (KBDiscourseUnit relatedUnit : word.getUnits()) {
				unitNetworkController.getModel().removeRelation(unit,
						relatedUnit, new KBObjectSharingReason<KBWord>(word));
			}
		}
	}

	public void hardReset() {
		model.clearData();
		this.agentNetworkController.hardReset();
		this.unitNetworkController.hardReset();
		this.wordNetworkController.hardReset();
	}

	public void reset() {
		model.clearData();
		this.agentNetworkController.reset();
		this.unitNetworkController.reset();
		this.wordNetworkController.reset();
	}

	public void tick() {
		agentNetworkController.tick();
		unitNetworkController.tick();
		wordNetworkController.tick();
	}

	public void refreshWithoutAnimation() {
		agentNetworkController.refreshWithoutAnimation();
		unitNetworkController.refreshWithoutAnimation();
		wordNetworkController.refreshWithoutAnimation();
	}

	public void refreshWithAnimation() {
		agentNetworkController.refreshWithAnimation();
		unitNetworkController.refreshWithAnimation();
		wordNetworkController.refreshWithAnimation();
	}

	public boolean isAnimationFinished() {
		boolean finished = agentNetworkController.isAnimationFinished()
				&& unitNetworkController.isAnimationFinished()
				&& wordNetworkController.isAnimationFinished();
		return finished;
	}

}
