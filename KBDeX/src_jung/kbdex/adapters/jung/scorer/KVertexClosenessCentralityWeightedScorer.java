/*
 * KVertexClosenessCentralityScorer.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.adapters.jung.scorer;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.algorithms.shortestpath.Distance;

public class KVertexClosenessCentralityWeightedScorer<V, E> extends
		KVertexClosenessCentralityScorer<V> {

	private Transformer<E, ? extends Number> edgeWeights;

	public KVertexClosenessCentralityWeightedScorer(
			Transformer<E, ? extends Number> edgeWeights) {
		this.edgeWeights = edgeWeights;
	}

	public String getName() {
		return "Closeness Centrality(weighted)";
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Distance<V> createDistance() {
		return new DijkstraDistance(getGraph(), edgeWeights);
	}
}
