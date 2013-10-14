/*
 * KAverageStatisticScorer.java
 * Created on May 2, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.adapters.jung.graphscorer;

import kbdex.model.network.metrics.IKCentralizationCalculatableScorer;
import kbdex.model.network.metrics.IKGraphMetricsScorer;
import kbdex.model.network.metrics.IKVertexMetricsScorer;

/**
 * @author macchan
 *
 */
public class KCentralizationStatisticScorer<V> implements
		IKGraphMetricsScorer<Double> {

	private IKVertexMetricsScorer<V, Double> scorer;

	private double cash;

	public KCentralizationStatisticScorer(
			IKVertexMetricsScorer<V, Double> scorer) {
		this.scorer = scorer;
	}

	public String getName() {
		return "Centralization of " + scorer.getName();
	}

	public void calculate() {
		scorer.calculate();

		double vertexMax = ((IKCentralizationCalculatableScorer) scorer)
				.getMaxValueForVertex();
		double totalDiffMax = ((IKCentralizationCalculatableScorer) scorer)
				.getTheoreticalMaxDifferenceOfTotalCentrality();
		double totalDiff = 0d;
		for (V v : scorer.getGraph().getVertices()) {
			totalDiff += (vertexMax - scorer.getVertexScore(v).doubleValue());
		}

		cash = totalDiff / totalDiffMax;
	}

	public Double getScore() {
		return cash;
	}

}
