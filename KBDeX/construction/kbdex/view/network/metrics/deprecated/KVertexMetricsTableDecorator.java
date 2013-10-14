/*
 * KVertexMetricsTableDecorator.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.network.metrics.deprecated;

import kbdex.model.network.metrics.IKVertexMetricsScorer;
import clib.view.table.model.ICTableDataDecorator;

/**
 * @author macchan
 * @deprecated
 */
public class KVertexMetricsTableDecorator<V> implements ICTableDataDecorator<V> {

	private IKVertexMetricsScorer<V, Number> scorer;
	private String name;
	private boolean active = false;

	public KVertexMetricsTableDecorator(
			IKVertexMetricsScorer<V, Number> scorer, String name) {
		this.scorer = scorer;
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Number getValueAt(V model) {
		if (!active) {
			return Double.NaN;//TODO 1000 "NA"にしたい
		}

		Number d = scorer.getVertexScore(model);
		if (d == null) {
			return Double.NaN;
		}
		if (d instanceof Integer) {
			return new Double((Integer) d);
		}
		return d;
	}

	public String getValueName() {
		return this.name;
	}

	public Class<?> getValueType() {
		return Double.class;
	}
}
