/*
 * A.java
 * Created on 2011/11/26
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.controller.metrics;

import kbdex.model.network.metrics.IKMetricsScorer;
import clib.common.model.ICModelChangeListener;

/**
 * @author macchan
 */
public interface IKTemporalMetricsScorer {

	public IKMetricsScorer getScorer();

	public void clear();

	public void tick();

	public void addModelListener(ICModelChangeListener l);

	public void removeModelListener(ICModelChangeListener l);

}
