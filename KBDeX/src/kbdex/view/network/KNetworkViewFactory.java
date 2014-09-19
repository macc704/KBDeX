/*
 * KNetworkViewFactory.java
 * Created on Apr 18, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.network;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import kbdex.adapters.jung.IKNetworkMouseListener;
import kbdex.adapters.jung.IKNetworkViewPickingListener;
import kbdex.adapters.jung.KCircleLayout;
import kbdex.app.KBDeX;
import kbdex.controller.KBWorldController;
import kbdex.controller.KDiscourseController;
import kbdex.controller.KNNetworkController;
import kbdex.model.kbmodel.KBAgent;
import kbdex.model.kbmodel.KBAgentRelationReason;
import kbdex.model.kbmodel.KBDiscourseUnit;
import kbdex.model.kbmodel.KBObjectSharingReason;
import kbdex.model.kbmodel.KBRelation;
import kbdex.model.kbmodel.KBWord;
import kbdex.model.kbmodel.KBWorld;
import kbdex.view.IKWindowManager;
import kbdex.view.modelviewers.KBAgentViewer;
import kbdex.view.modelviewers.KBDiscourseUnitViewer;
import kbdex.view.modelviewers.KBRelationViewer;
import kbdex.view.modelviewers.KBWordViewer;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.FRLayout;

/**
 * @author macchan
 */
public class KNetworkViewFactory {

	private KDiscourseController discourseController;
	private IKWindowManager wManager;

	private static Icon studentIcon = KBDeX.getInstance().getImageIcon(
			"student16x16.jpg");
	private static Icon discourseunitIcon = KBDeX.getInstance().getImageIcon(
			"discourseunit16x16.jpg");
	private static Icon wordIcon = KBDeX.getInstance().getImageIcon(
			"word16x16.jpg");

	public KNetworkViewFactory(KDiscourseController discourseController,
			IKWindowManager wManager) {
		this.discourseController = discourseController;
		this.wManager = wManager;
	}

	public KDiscourseController getDiscourseController() {
		return discourseController;
	}

	private KBWorldController getWorldController() {
		return discourseController.getWorldController();
	}

	private KBWorld getWorld() {
		return getWorldController().getModel();
	}

	public KNetworkViewPanel<KBAgent, KBRelation> createAgentNetworkPanel(
			KNNetworkController<KBAgent> netController) {
		KNetworkViewPanel<KBAgent, KBRelation> panel = new KNetworkViewPanel<KBAgent, KBRelation>(
				netController, studentIcon, discourseController.getDiscourse().getAgentWeightParameter());
		KNetworkViewContextMenuProvider<KBAgent> provider = new KNetworkViewContextMenuProvider<KBAgent>(
				getDiscourseController(), getWorld()) {

			protected List<KBAgent> getNonSelectedNodes() {
				return getWorld().getNonSelectedAgents();
			}

			protected List<KBAgent> getSelectedNodes() {
				return getWorld().getSelectedAgents();
			}

			protected void addIgnoreNode(KBAgent node) {
				getWorld().addIgnoreAgent(node.getName());
			}

			protected void removeIgnoreNode(KBAgent node) {
				getWorld().removeIgnoreAgent(node.getName());
			}

			protected void clearIgnoreNode() {
				getWorld().clearIgnoreAgents();
			}
		};
		panel.getNetworkPanel().addContextMenuActions(provider.getActions());
		panel.getNetworkPanel().addNetworkListener(
				new IKNetworkMouseListener<KBAgent, KBRelation>() {

					@Override
					public void nodeClicked(KBAgent agent, int clickCount) {
						if (clickCount == 2) {
							KBAgentViewer viewer = new KBAgentViewer(agent);
							wManager.openFrame(viewer, viewer.getTitle(), null,
									null);
						}
					}

					@Override
					public void edgeClicked(KBRelation r, int clickCount) {
						if (clickCount == 2) {
							KBRelationViewer<KBAgentRelationReason> viewer = new KBRelationViewer<KBAgentRelationReason>(
									r, wManager) {
								private static final long serialVersionUID = 1L;

								public void doSelect(
										List<KBAgentRelationReason> selected) {
									List<KBDiscourseUnit> units = new ArrayList<KBDiscourseUnit>();
									for (KBAgentRelationReason reason : selected) {
										if (!units.contains(reason.getFrom())) {
											units.add(reason.getFrom());
										}
										if (!units.contains(reason.getTo())) {
											units.add(reason.getTo());
										}
									}
									getWorld().selectUnits(units);
									getWorldController()
											.refreshWithoutAnimation();
								};
							};
							wManager.openFrame(
									viewer,
									"Agents RelationShip (in "
											+ viewer.toParamString()
											+ ") by Words' Shared Discourse Units' Sharing",
									null, null);
						}
					}
				});
		panel.getNetworkPanel().addPickingListener(
				new IKNetworkViewPickingListener<KBAgent, KBRelation>() {
					public void pickingStateChanged(List<KBAgent> pickedNodes,
							List<KBRelation> pickedEdges) {
						getWorldController().getModel().selectAgents(
								pickedNodes);
						getWorldController().refreshWithoutAnimation();
					}
				});
		panel.getGraphLayoutChooser().selectLayout(KCircleLayout.class);
		return panel;
	}

	public KNetworkViewPanel<KBDiscourseUnit, KBRelation> createUnitNetworkPanel(
			KNNetworkController<KBDiscourseUnit> netController) {
		KNetworkViewPanel<KBDiscourseUnit, KBRelation> panel = new KNetworkViewPanel<KBDiscourseUnit, KBRelation>(
				netController, discourseunitIcon, discourseController.getDiscourse().getDiscourseUnitWeightParameter());
		KNetworkViewContextMenuProvider<KBDiscourseUnit> provider = new KNetworkViewContextMenuProvider<KBDiscourseUnit>(
				getDiscourseController(), getWorld()) {

			protected List<KBDiscourseUnit> getNonSelectedNodes() {
				return getWorld().getNonSelectedUnits();
			}

			protected List<KBDiscourseUnit> getSelectedNodes() {
				return getWorld().getSelectedUnits();
			}

			protected void addIgnoreNode(KBDiscourseUnit node) {
				getWorld().addIgnoreNote(node.getName());
			}

			protected void removeIgnoreNode(KBDiscourseUnit node) {
				getWorld().removeIgnoreNote(node.getName());
			}

			protected void clearIgnoreNode() {
				getWorld().clearIgnoreNotes();
			}
		};
		panel.getNetworkPanel().addContextMenuActions(provider.getActions());
		panel.getNetworkPanel().addNetworkListener(
				new IKNetworkMouseListener<KBDiscourseUnit, KBRelation>() {

					@Override
					public void nodeClicked(KBDiscourseUnit note, int clickCount) {
						if (clickCount == 2) {
							KBDiscourseUnitViewer viewer = new KBDiscourseUnitViewer(
									note);
							wManager.openFrame(viewer, viewer.getTitle(), null,
									null);
						}
					}

					@Override
					public void edgeClicked(KBRelation r, int clickCount) {
						if (clickCount == 2) {
							KBRelationViewer<KBObjectSharingReason<KBWord>> viewer = new KBRelationViewer<KBObjectSharingReason<KBWord>>(
									r, wManager) {
								private static final long serialVersionUID = 1L;

								public void doSelect(
										List<KBObjectSharingReason<KBWord>> selected) {
									List<KBWord> words = new ArrayList<KBWord>();
									for (KBObjectSharingReason<KBWord> reason : selected) {
										words.add(reason.getSharedObject());
									}
									getWorld().selectWords(words);
									getWorldController()
											.refreshWithoutAnimation();
								};
							};
							wManager.openFrame(viewer,
									"Discourse Units RelationShip (in "
											+ viewer.toParamString()
											+ ") by Words' Sharing", null, null);
						}
					}
				});
		panel.getNetworkPanel().getViewer().getRenderContext()
				.setVertexLabelTransformer(new KNoteLabelTransformer());
		panel.getNetworkPanel()
				.addPickingListener(
						new IKNetworkViewPickingListener<KBDiscourseUnit, KBRelation>() {
							public void pickingStateChanged(
									List<KBDiscourseUnit> pickedNodes,
									List<KBRelation> pickedEdges) {
								getWorldController().getModel().selectUnits(
										pickedNodes);
								getWorldController().refreshWithoutAnimation();
							}
						});
		panel.getGraphLayoutChooser().selectLayout(KCircleLayout.class);
		return panel;
	}

	class KNoteLabelTransformer implements Transformer<KBDiscourseUnit, String> {
		public String transform(KBDiscourseUnit node) {
			return node.getName();
		}
	}

	public KNetworkViewPanel<KBWord, KBRelation> createWordNetworkPanel(
			KNNetworkController<KBWord> netController) {
		KNetworkViewPanel<KBWord, KBRelation> panel = new KNetworkViewPanel<KBWord, KBRelation>(
				netController, wordIcon, discourseController.getDiscourse().getWordWeightParameter());
		KNetworkViewContextMenuProvider<KBWord> provider = new KNetworkViewContextMenuProvider<KBWord>(
				getDiscourseController(), getWorld()) {

			protected List<KBWord> getSelectedNodes() {
				return getWorld().getSelectedWords();
			}

			protected List<KBWord> getNonSelectedNodes() {
				return getWorld().getNonSelectedWords();
			}

			protected void addIgnoreNode(KBWord node) {
				getWorld().addIgnoreWord(node.getName());
			}

			protected void removeIgnoreNode(KBWord node) {
				getWorld().removeIgnoreWord(node.getName());
			}

			protected void clearIgnoreNode() {
				getWorld().clearIgnoreWords();
			}
		};
		panel.getNetworkPanel().addContextMenuActions(provider.getActions());
		panel.getNetworkPanel().addNetworkListener(
				new IKNetworkMouseListener<KBWord, KBRelation>() {

					@Override
					public void nodeClicked(KBWord word, int clickCount) {
						if (clickCount == 2) {
							KBWordViewer viewer = new KBWordViewer(word);
							wManager.openFrame(viewer, viewer.getTitle(), null,
									null);
						}
					}

					@Override
					public void edgeClicked(KBRelation r, int clickCount) {
						if (clickCount == 2) {
							KBRelationViewer<KBObjectSharingReason<KBDiscourseUnit>> viewer = new KBRelationViewer<KBObjectSharingReason<KBDiscourseUnit>>(
									r, wManager) {
								private static final long serialVersionUID = 1L;

								public void doSelect(
										List<KBObjectSharingReason<KBDiscourseUnit>> selected) {
									List<KBDiscourseUnit> units = new ArrayList<KBDiscourseUnit>();
									for (KBObjectSharingReason<KBDiscourseUnit> reason : selected) {
										units.add(reason.getSharedObject());
									}
									getWorld().selectUnits(units);
									getWorldController()
											.refreshWithoutAnimation();
								};
							};
							wManager.openFrame(
									viewer,
									"Words RelationShip (in "
											+ viewer.toParamString()
											+ ") by Discourse Units' Sharing",
									null, null);
						}
					}
				});
		panel.getNetworkPanel().addPickingListener(
				new IKNetworkViewPickingListener<KBWord, KBRelation>() {
					public void pickingStateChanged(List<KBWord> pickedNodes,
							List<KBRelation> pickedEdges) {
						getWorldController().getModel()
								.selectWords(pickedNodes);
						getWorldController().refreshWithoutAnimation();
					}
				});
		panel.getGraphLayoutChooser().selectLayout(FRLayout.class);
		return panel;
	}
}
