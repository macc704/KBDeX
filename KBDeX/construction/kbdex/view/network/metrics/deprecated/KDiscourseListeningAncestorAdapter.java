/*
 * KDiscourseListeningAncestorAdapter.java
 * Created on May 7, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.network.metrics.deprecated;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import kbdex.controller.IKDiscourseControllerListener;
import kbdex.controller.KNNetworkController;

/**
 * @author macchan
 * @deprecated
 */
public abstract class KDiscourseListeningAncestorAdapter implements
		AncestorListener, IKDiscourseControllerListener {

	public static final long serialVersionUID = 1L;

	private KNNetworkController<?> controller;

	KDiscourseListeningAncestorAdapter(KNNetworkController<?> controller) {
		this.controller = controller;
	}

	public void ancestorAdded(AncestorEvent event) {
		hookListeners();
	}

	public void ancestorRemoved(AncestorEvent event) {
		unhookListeners();
	}

	public void ancestorMoved(AncestorEvent event) {

	}

	private void hookListeners() {
		controller.addNetworkControllerListener(this);
	}

	private void unhookListeners() {
		controller.removeNetworkControllerListener(this);
	}

	/******************************************************
	 * Implementation of IKDiscourseControllerListener
	 ******************************************************/

	public boolean isAnimationFinished() {
		return true;
	}

	public void refreshWithAnimation() {
	}

	public void refreshWithoutAnimation() {
	}

	public void hardReset() {
	}

	public void reset() {
	}

	public void tick() {
	}
}
