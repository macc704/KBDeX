/*
 * A.java
 * Created on 2011/11/26
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.controller.metrics;

import kbdex.model.network.metrics.KAbstractLazyMetricsScorerDecorator;

/**
 * @author macchan
 */
public class KTemporalMetricsCasher {

	private int frame = 0;
	private int cashedFrameCount = 0;

	private IKTemporalMetricsScorer temporalScorer;

	public KTemporalMetricsCasher(IKTemporalMetricsScorer temporalScorer) {
		this.temporalScorer = temporalScorer;
	}

	public void hardReset() {
		frame = 0;
		cashedFrameCount = 0;
		temporalScorer.clear();
		tick();//initial tick
	}

	public void reset() {
		frame = 0;
	}

	public void tick() {
		frame++;

		while (cashedFrameCount < frame) {
			if (temporalScorer.getScorer() instanceof KAbstractLazyMetricsScorerDecorator) {
				((KAbstractLazyMetricsScorerDecorator) temporalScorer
						.getScorer()).tick();
				temporalScorer.getScorer().calculate();
			}
			temporalScorer.tick();
			cashedFrameCount++;
		}
	}
}
