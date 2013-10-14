/*
 * KLazyVertexMetricsScorerDecorator.java
 * Created on May 2, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.network.metrics;

import kbdex.adapters.jung.KGraph;

/**
 * @author macchan
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class KLazyVertexMetricsScorerDecorator extends
		KAbstractLazyMetricsScorerDecorator implements IKVertexMetricsScorer,
		IKCentralizationCalculatableScorer {

	private IKVertexMetricsScorer scorer;

	public KLazyVertexMetricsScorerDecorator(IKVertexMetricsScorer scorer) {
		super(scorer);
		this.scorer = scorer;
	}

	public KGraph getGraph() {
		return scorer.getGraph();
	}

	public void setGraph(KGraph graph) {
		scorer.setGraph(graph);
	}

	public Object getVertexScore(Object model) {
		return scorer.getVertexScore(model);
	}

	/* (non-Javadoc)
	 * @see kbdex.controller.metrics.IKCentralizationCalculatableScorer#getTheoreticalMaxDifferenceOfTotalCentrality()
	 */
	@Override
	public double getTheoreticalMaxDifferenceOfTotalCentrality() {
		return ((IKCentralizationCalculatableScorer) scorer)
				.getTheoreticalMaxDifferenceOfTotalCentrality();
	}

	/* (non-Javadoc)
	 * @see kbdex.controller.metrics.IKCentralizationCalculatableScorer#getTheoreticalMaxForVertex()
	 */
	@Override
	public double getMaxValueForVertex() {
		return ((IKCentralizationCalculatableScorer) scorer)
				.getMaxValueForVertex();
	}
}
