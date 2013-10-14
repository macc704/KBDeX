/*
 * KGraphTemporalMetricsScorer.java
 * Created on 2011/11/26
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.controller.metrics;

import java.util.ArrayList;
import java.util.List;

import kbdex.model.network.metrics.IKGraphMetricsScorer;
import kbdex.model.network.metrics.IKMetricsScorer;
import clib.common.model.CAbstractModelObject;

/**
 * @author macchan
 */
public class KGraphTemporalMetricsScorer extends CAbstractModelObject implements
		IKTemporalMetricsScorer {

	private static final long serialVersionUID = 1L;

	private IKGraphMetricsScorer<?> scorer;
	private List<Number> scores = new ArrayList<Number>();

	public KGraphTemporalMetricsScorer(IKGraphMetricsScorer<?> scorer) {
		this.scorer = scorer;
	}

	public int size() {
		return scores.size();
	}

	public double getDoubleValue(int index) {
		if (0 <= index && index < size()) {
			return scores.get(index).doubleValue();
		}
		return 0d;
	}

	/**
	 * @return the scorer
	 */
	public IKMetricsScorer getScorer() {
		return scorer;
	}

	/* (non-Javadoc)
	 * @see kbdex.controller.metrics.IKTemporalMetricsScorer#clear()
	 */
	@Override
	public void clear() {
		scores.clear();
		fireModelUpdated();
	}

	/* (non-Javadoc)
	 * @see kbdex.controller.metrics.IKTemporalMetricsScorer#tick()
	 */
	@Override
	public void tick() {
		Number score = (Number) scorer.getScore();
		scores.add(score);
		fireModelUpdated();
	}

}
