/*
 * KAverageStatisticScorer.java
 * Created on May 2, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.adapters.jung.graphscorer;

import kbdex.model.network.metrics.IKGraphMetricsScorer;
import kbdex.model.network.metrics.IKVertexMetricsScorer;

/**
 * @author macchan
 *
 */
public class KTotalStatisticScorer<V> implements IKGraphMetricsScorer<Double> {

	private IKVertexMetricsScorer<V, Number> scorer;

	private double cash;

	public KTotalStatisticScorer(IKVertexMetricsScorer<V, Number> scorer) {
		this.scorer = scorer;
	}

	public String getName() {
		return "Total of " + scorer.getName();
	}

	public void calculate() {
		scorer.calculate();

		double total = 0d;
		for (V v : scorer.getGraph().getVertices()) {
			total += scorer.getVertexScore(v).doubleValue();
		}
		cash = total;
	}

	public Double getScore() {
		return cash;
	}

}
