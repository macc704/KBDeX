/*
 * KMetricsScorerSuite.java
 * Created on 2011/11/26
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.controller.metrics;

import java.util.ArrayList;
import java.util.List;

import kbdex.adapters.jung.graphscorer.KAverageStatisticScorer;
import kbdex.adapters.jung.graphscorer.KCentralizationStatisticScorer;
import kbdex.adapters.jung.graphscorer.KTotalStatisticScorer;
import kbdex.controller.IKDiscourseControllerListener;
import kbdex.model.kbmodel.KBElement;
import kbdex.model.network.KNNetwork;
import kbdex.model.network.metrics.IKCentralizationCalculatableScorer;
import kbdex.model.network.metrics.IKGraphMetricsScorer;
import kbdex.model.network.metrics.IKVertexMetricsScorer;
import kbdex.model.network.metrics.KLazyGraphMetricsScroerDecorator;
import kbdex.model.network.metrics.KLazyVertexMetricsScorerDecorator;

/**
 * @author macchan
 */
public class KMetricsScorerSuite<V extends KBElement> implements
		IKDiscourseControllerListener {

	private boolean active = false;

	private String name;
	private KNNetwork<V> network;
	private KVertexTemporalMetricsScorer<V> vertexScorer;
	private List<KGraphTemporalMetricsScorer> graphScorers = new ArrayList<KGraphTemporalMetricsScorer>();
	private List<KMetricsScorerController> controllers = new ArrayList<KMetricsScorerController>();

	public KMetricsScorerSuite(String name, KNNetwork<V> network,
			IKVertexMetricsScorer<V, Number> vertexScorer) {
		this.name = name;
		this.network = network;
		installVertexScorer(vertexScorer);
	}

	@SuppressWarnings("unchecked")
	private void installVertexScorer(IKVertexMetricsScorer<V, Number> scorer) {
		KLazyVertexMetricsScorerDecorator lazyVertexScorer = new KLazyVertexMetricsScorerDecorator(
				scorer);
		lazyVertexScorer.setGraph(network.getGraph());//initialize
		this.vertexScorer = new KVertexTemporalMetricsScorer<V>(
				lazyVertexScorer);
		controllers.add(new KMetricsScorerController(
				new KTemporalMetricsCasher(vertexScorer)));

		installGraphScorer(new KAverageStatisticScorer<V>(lazyVertexScorer));
		installGraphScorer(new KTotalStatisticScorer<V>(lazyVertexScorer));
		if (scorer instanceof IKCentralizationCalculatableScorer) {
			installGraphScorer(new KCentralizationStatisticScorer<V>(
					lazyVertexScorer));
		}
	}

	private void installGraphScorer(IKGraphMetricsScorer<?> scorer) {
		KLazyGraphMetricsScroerDecorator lazyGraphScorer = new KLazyGraphMetricsScroerDecorator(
				scorer);
		KGraphTemporalMetricsScorer temp = new KGraphTemporalMetricsScorer(
				lazyGraphScorer);
		graphScorers.add(temp);
		controllers.add(new KMetricsScorerController(
				new KTemporalMetricsCasher(temp)));
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the vertexScorer
	 */
	public KVertexTemporalMetricsScorer<V> getVertexScorer() {
		return vertexScorer;
	}

	/**
	 * @return the graphScorers
	 */
	public List<KGraphTemporalMetricsScorer> getGraphScorers() {
		return graphScorers;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		if (this.active != active) {
			this.active = active;
			for (KMetricsScorerController controller : controllers) {
				controller.setActive(active);
			}
		}
	}

	/* (non-Javadoc)
	 * @see kbdex.controller.IKDiscourseControllerListener#hardReset()
	 */
	@Override
	public void hardReset() {
		for (KMetricsScorerController controller : controllers) {
			controller.hardReset();
		}
	}

	/* (non-Javadoc)
	 * @see kbdex.controller.IKDiscourseControllerListener#reset()
	 */
	@Override
	public void reset() {
		vertexScorer.getVertexScorer().setGraph(network.getGraph());
		for (KMetricsScorerController controller : controllers) {
			controller.reset();
		}
	}

	/* (non-Javadoc)
	 * @see kbdex.controller.IKDiscourseControllerListener#tick()
	 */
	@Override
	public void tick() {
		for (KMetricsScorerController controller : controllers) {
			controller.tick();
		}
	}

	/* (non-Javadoc)
	 * @see kbdex.controller.IKDiscourseControllerListener#isAnimationFinished()
	 */
	@Override
	public boolean isAnimationFinished() {
		return true;
	}

	/* (non-Javadoc)
	 * @see kbdex.controller.IKDiscourseControllerListener#refreshWithAnimation()
	 */
	@Override
	public void refreshWithAnimation() {
		for (KMetricsScorerController controller : controllers) {
			controller.refreshWithAnimation();
		}
	}

	/* (non-Javadoc)
	 * @see kbdex.controller.IKDiscourseControllerListener#refreshWithoutAnimation()
	 */
	@Override
	public void refreshWithoutAnimation() {
		for (KMetricsScorerController controller : controllers) {
			controller.refreshWithoutAnimation();
		}
	}
}
