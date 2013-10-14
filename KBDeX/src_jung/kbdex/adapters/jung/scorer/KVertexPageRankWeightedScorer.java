/*
 * KVertexBetweennessWeightedScorer.java
 * Created on Apr 24, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.adapters.jung.scorer;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;

/**
 * @author macchan
 */
public class KVertexPageRankWeightedScorer<V, E> extends
		KVertexPageRankScorer<V> {

	private Transformer<E, ? extends Number> edgeWeights;

	public KVertexPageRankWeightedScorer(
			Transformer<E, ? extends Number> edgeWeights) {
		this.edgeWeights = edgeWeights;
	}

	public String getName() {
		return "Page Rank(Weighted)";
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected PageRankWithPriors<V, ?> createCalcurator() {
		return new PageRank(getGraph(), edgeWeights, ALPHA);
	}

}
