/*
 * KMetricsChooser.java
 * Created on 2011/11/26
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.network.metrics;

import java.util.ArrayList;
import java.util.List;

import kbdex.controller.metrics.IKTemporalMetricsScorer;
import kbdex.controller.metrics.KMetricsScorerManager;
import kbdex.controller.metrics.KMetricsScorerSuite;
import kbdex.model.kbmodel.KBElement;

/**
 * @author macchan
 */
public class KGraphMetricsScorerChooserPanel<V extends KBElement> extends
		KAbstractMetricsScorerChooserPanel<V> {

	private static final long serialVersionUID = 1L;

	public KGraphMetricsScorerChooserPanel(KMetricsScorerManager<V> manager) {
		super(manager);
	}

	protected List<IKTemporalMetricsScorer> getChoosableScorers(
			KMetricsScorerSuite<V> suite) {
		return new ArrayList<IKTemporalMetricsScorer>(suite.getGraphScorers());
	}

}
