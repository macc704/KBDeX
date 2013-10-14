/*
 * KVertexFormalizedDegreeScorer.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.adapters.jung.scorer;

import kbdex.model.network.metrics.IKCentralizationCalculatableScorer;

/**
 * Assigns a score to each vertex equal to its degree.
 * 
 * @param <V>
 *            the vertex type
 */
public class KVertexFormalizedDegreeScorer<V> extends
		KAbstractVertexMetricsScorer<V> implements
		IKCentralizationCalculatableScorer {

	public static String NAME = "Degree Centrality";

	public String getName() {
		return NAME;
	}

	protected Double calculateVertexScore(V v) {
		double n = getGraph().getValidVertexCount();
		if (n >= 2) {
			return getGraph().degree(v) / (n - 1d);
		} else {
			return new Double(getGraph().degree(v));
		}
	}

	public double getTheoreticalMaxDifferenceOfTotalCentrality() {
		return getGraph().getValidVertexCount() - 2d;
	}

	public double getMaxValueForVertex() {
		return getMax().doubleValue();
	}
}
