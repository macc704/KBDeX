/*
 * KNetworkViewContextMenuProvider.java
 * Created on Aug 5, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.network;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import kbdex.controller.KDiscourseController;
import kbdex.model.kbmodel.KBWorld;
import clib.common.thread.ICTask;
import clib.view.actions.CActionUtils;

/**
 * @author macchan
 */
public abstract class KNetworkViewContextMenuProvider<V> {

	private KDiscourseController discourseController;
	private KBWorld world;

	public KNetworkViewContextMenuProvider(
			KDiscourseController discourseController, KBWorld world) {
		this.discourseController = discourseController;
		this.world = world;
	}

	public List<Action> getActions() {
		List<Action> actions = new ArrayList<Action>();
		actions.add(createShowAllNodesAction());
		actions.add(createHideSelectedNodesAction());
		actions.add(createHideSelectedAndShowOthersAction());
		actions.add(createShowSelectedNodesAction());
		actions.add(createShowSelectedAndHideOthersAction());
		return actions;
	}

	public Action createHideSelectedNodesAction() {
		return CActionUtils.createAction("Hide Selected Node(s)", new ICTask() {
			public void doTask() {
				for (V node : getSelectedNodes()) {
					addIgnoreNode(node);
				}
				world.clearSelection();
				discourseController.doReloadToThisFrame(true);
			}
		});
	}

	public Action createHideSelectedAndShowOthersAction() {
		return CActionUtils.createAction(
				"Hide Selected Node(s) and Show others", new ICTask() {
					public void doTask() {
						clearIgnoreNode();
						for (V node : getSelectedNodes()) {
							addIgnoreNode(node);
						}
						world.clearSelection();
						discourseController.doReloadToThisFrame(true);
					}
				});
	}

	public Action createShowSelectedNodesAction() {
		return CActionUtils.createAction("Show Selected Node(s)", new ICTask() {
			public void doTask() {
				for (V node : getSelectedNodes()) {
					removeIgnoreNode(node);
				}
				world.clearSelection();
				discourseController.doReloadToThisFrame(true);
			}
		});
	}

	public Action createShowSelectedAndHideOthersAction() {
		return CActionUtils.createAction(
				"Show Selected Node(s) and Hide others", new ICTask() {
					public void doTask() {
						clearIgnoreNode();
						for (V node : getNonSelectedNodes()) {
							addIgnoreNode(node);
						}
						world.clearSelection();
						discourseController.doReloadToThisFrame(true);
					}
				});
	}

	public Action createShowAllNodesAction() {
		return CActionUtils.createAction("Show All Node(s)", new ICTask() {
			public void doTask() {
				clearIgnoreNode();
				world.clearSelection();
				discourseController.doReloadToThisFrame(true);
			}
		});
	}

	protected abstract List<V> getNonSelectedNodes();

	protected abstract List<V> getSelectedNodes();

	protected abstract void addIgnoreNode(V node);

	protected abstract void removeIgnoreNode(V node);

	protected abstract void clearIgnoreNode();

}
