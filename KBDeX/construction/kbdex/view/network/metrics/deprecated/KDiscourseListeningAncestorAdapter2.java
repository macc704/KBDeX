/*
 * KDiscourseListeningAncestorAdapter2.java
 * Created on May 7, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.network.metrics.deprecated;

import kbdex.controller.KNNetworkController;

/**
 * @author macchan
 * @deprecated
 */
public abstract class KDiscourseListeningAncestorAdapter2 extends
		KDiscourseListeningAncestorAdapter {

	public KDiscourseListeningAncestorAdapter2(KNNetworkController<?> controller) {
		super(controller);
	}

	public final void refreshWithAnimation() {
		refresh();
	}

	public void refreshWithoutAnimation() {
		refresh();
	}

	public void refresh() {
		//for overriding
	}

}
