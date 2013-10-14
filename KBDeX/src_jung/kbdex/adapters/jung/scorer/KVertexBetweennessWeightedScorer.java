/*
 * KVertexBetweennessWeightedScorer.java
 * Created on Apr 24, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.adapters.jung.scorer;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality;

/**
 * @author macchan
 *
 */
public class KVertexBetweennessWeightedScorer<V, E> extends
		KVertexBetweennessScorer<V> {

	private Transformer<E, ? extends Number> edgeWeights;

	public KVertexBetweennessWeightedScorer(
			Transformer<E, ? extends Number> edgeWeights) {
		this.edgeWeights = edgeWeights;
	}

	public String getName() {
		return "Betweenness Centrality(Weighted)";
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected BetweennessCentrality<V, ?> createBC() {
		return new BetweennessCentrality(getGraph(), edgeWeights);
	}

}
