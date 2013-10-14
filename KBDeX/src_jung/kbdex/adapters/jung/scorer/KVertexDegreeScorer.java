/*
 * KVertexFormalizedDegreeScorer.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.adapters.jung.scorer;

/**
 * Assigns a score to each vertex equal to its degree.
 * 
 * @param <V>
 *            the vertex type
 */
public class KVertexDegreeScorer<V> extends KAbstractVertexMetricsScorer<V> {

	public static String NAME = "Degree";

	public String getName() {
		return NAME;
	}

	public Integer calculateVertexScore(V v) {
		return getGraph().degree(v);
	}
}
