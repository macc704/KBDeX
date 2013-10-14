/*
 * KNNetworkController.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.controller;

import java.util.ArrayList;
import java.util.List;

import kbdex.controller.metrics.KMetricsScorerManager;
import kbdex.model.kbmodel.KBElement;
import kbdex.model.network.KNNetwork;

public abstract class KNNetworkController<V extends KBElement> implements
		IKDiscourseControllerListener {

	private KNNetwork<V> model = new KNNetwork<V>();
	private KMetricsScorerManager<V> metricsManager = new KMetricsScorerManager<V>(model);

	private boolean idSorting = false;
	private String networkTypeName;

	private List<IKDiscourseControllerListener> listeners = new ArrayList<IKDiscourseControllerListener>();

	public KNNetworkController(String networkTypeName, boolean idSorting) {
		this.networkTypeName = networkTypeName;
		this.idSorting = idSorting;
	}

	public String getNetworkTypeName() {
		return networkTypeName;
	}

	public boolean isIdSorting() {
		return idSorting;
	}

	public KNNetwork<V> getModel() {
		return model;
	}

	public KMetricsScorerManager<V> getMetricsManager() {
		return metricsManager;
	}

	public void hardReset() {
		model.clear();
		metricsManager.hardReset();
		this.fireHardReset();
	}

	public void reset() {
		model.clear();
		metricsManager.reset();
		this.fireReset();
	}

	public void tick() {
		metricsManager.tick();
		this.fireTick();
	}

	public boolean isAnimationFinished() {
		if (metricsManager.isAnimationFinished()) {
			return true;
		}
		return this.fireIsAnimationFinished();
	}

	public void refreshWithoutAnimation() {
		metricsManager.refreshWithoutAnimation();
		this.fireRefreshWithoutAnimation();
	}

	public void refreshWithAnimation() {
		metricsManager.refreshWithAnimation();
		this.fireRefreshWithAnimation();
	}

	/*********************************************
	 * Selecting Strategy
	 *********************************************/

	/**
	 * for overriding
	 */
	public abstract void selectModel(List<V> models);

	/**
	 * for overriding
	 */
	public abstract void selectModelByPointer(List<String> pointers);

	/*********************************************
	 * Listener Strategy
	 *********************************************/

	public void addNetworkControllerListener(
			IKDiscourseControllerListener listener) {
		this.listeners.add(listener);
	}

	public void removeNetworkControllerListener(
			IKDiscourseControllerListener listener) {
		this.listeners.remove(listener);
	}

	protected void fireHardReset() {
		for (IKDiscourseControllerListener listener : listeners) {
			listener.hardReset();
		}
	}

	protected void fireReset() {
		for (IKDiscourseControllerListener listener : listeners) {
			listener.reset();
		}
	}

	protected void fireTick() {
		for (IKDiscourseControllerListener listener : listeners) {
			listener.tick();
		}
	}

	protected void fireRefreshWithoutAnimation() {
		for (IKDiscourseControllerListener listener : listeners) {
			listener.refreshWithoutAnimation();
		}
	}

	protected void fireRefreshWithAnimation() {
		for (IKDiscourseControllerListener listener : listeners) {
			listener.refreshWithAnimation();
		}
	}

	protected boolean fireIsAnimationFinished() {
		for (IKDiscourseControllerListener listener : listeners) {
			if (!listener.isAnimationFinished()) {
				return false;
			}
		}
		return true;
	}

}
