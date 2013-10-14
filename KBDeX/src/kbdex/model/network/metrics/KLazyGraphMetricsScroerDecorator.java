/*
 * KLazyGraphMetricsScroerDecorator.java
 * Created on May 2, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.network.metrics;

/**
 * @author macchan
 */
@SuppressWarnings("rawtypes")
public class KLazyGraphMetricsScroerDecorator extends
		KAbstractLazyMetricsScorerDecorator implements IKGraphMetricsScorer {

	public KLazyGraphMetricsScroerDecorator(IKGraphMetricsScorer scorer) {
		super(scorer);
	}

	public IKMetricsScorer getScorer() {
		return super.getScorer();
	}

	public IKGraphMetricsScorer getGraphScorer() {
		return (IKGraphMetricsScorer) getScorer();
	}

	public Object getScore() {
		return getGraphScorer().getScore();
	}

}
