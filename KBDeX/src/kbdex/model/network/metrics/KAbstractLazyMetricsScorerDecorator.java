/*
 * KAbstractLazyMetricsScorerDecorator.java
 * Created on May 2, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.network.metrics;

/**
 * @author macchan
 * TODO Lazyの仕組みが，依存先に「先に」tickが送られてくることを前提としている．
 */
public abstract class KAbstractLazyMetricsScorerDecorator implements
		IKMetricsScorer {

	private enum State {
		TICKED, CALCULATED
	};

	private State state = State.TICKED;

	private IKMetricsScorer scorer;

	public KAbstractLazyMetricsScorerDecorator(IKMetricsScorer scorer) {
		this.scorer = scorer;
	}

	public IKMetricsScorer getScorer() {
		return scorer;
	}

	public String getName() {
		return this.scorer.getName();
	}

	public void tick() {
		state = State.TICKED;
	}

	public synchronized void calculate() {
		if (state == State.TICKED) {
			scorer.calculate();
			state = State.CALCULATED;
		}
	}
}
