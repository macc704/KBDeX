/*
 * IKVertexMetricsScorer.java
 * Created on Jul 15, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.network.metrics;

import kbdex.adapters.jung.KGraph;

/**
 * @author macchan
 */
public interface IKVertexMetricsScorer<V, S> extends IKMetricsScorer {

	public KGraph<V, ?> getGraph();

	public void setGraph(KGraph<V, ?> graph);

	public S getVertexScore(V model);

}
