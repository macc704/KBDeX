/*
 * KSpeedControlSlider.java
 * Created on 2011/11/14
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view;

import java.awt.Dimension;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import kbdex.controller.KDiscourseController;

/**
 * @author macchan
 */
public class KSpeedControlSlider extends JSlider {

	private static final long serialVersionUID = 1L;

	private KDiscourseController controller;

	public KSpeedControlSlider(KDiscourseController controller) {
		super(1, 1000, 500);
		this.controller = controller;
		initialize();
	}

	private void initialize() {
		int h = getPreferredSize().height;
		int w = getPreferredSize().width / 3;
		setPreferredSize(new Dimension(w, h));
		addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				commit();
			}
		});
		commit();
	}

	private void commit() {
		int value = getValue();
		int interval = 1001 - value;//reversed
		controller.setInterval(interval);
	}
}
