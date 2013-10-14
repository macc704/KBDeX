/*
 * KVertexClusteringScorer.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.adapters.jung.scorer;

import java.util.Map;

import edu.uci.ics.jung.algorithms.metrics.Metrics;

/**
 * Class KVertexClusteringScorer
 */
public class KVertexClusteringScorer<V> extends KAbstractVertexMetricsScorer<V> {

	private Map<V, Double> scores;

	public static String NAME = "Clustering Coefficients";

	public String getName() {
		return NAME;
	}

	protected void preCalculate() {
		scores = Metrics.clusteringCoefficients(getGraph());
	}

	protected void postCalcurate() {
		scores = null;
	}

	protected Double calculateVertexScore(V v) {
		if (scores != null) {
			Double d = scores.get(v);
			return d;
		} else {
			return Double.NaN;
		}
	}
}
