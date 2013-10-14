/*
 * KAbstractVertexMetricsScorer.java
 * Created on Apr 24, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.adapters.jung.scorer;

import java.util.HashMap;
import java.util.Map;

import kbdex.adapters.jung.KGraph;
import kbdex.model.network.metrics.IKVertexMetricsScorer;

/**
 * @author macchan
 */
public abstract class KAbstractVertexMetricsScorer<V> implements
		IKVertexMetricsScorer<V, Number> {

	private KGraph<V, ?> graph;

	private Map<V, Number> results = new HashMap<V, Number>();

	//for centralization
	private Number max;

	public KGraph<V, ?> getGraph() {
		return graph;
	}

	public void setGraph(KGraph<V, ?> graph) {
		this.graph = graph;
	}

	public synchronized final void calculate() {
		results.clear();
		double max = 0d;
		preCalculate();
		for (V v : getGraph().getVertices()) {
			Number number = calculateVertexScore(v);
			if (max < number.doubleValue()) {
				max = number.doubleValue();
			}
			results.put(v, number);
		}
		this.max = max;
		postCalcurate();
	}

	public final Number getVertexScore(V vertex) {
		return results.get(vertex);
	}

	public Number getMax() {
		return max;
	}

	protected void preCalculate() {
		// for override
	}

	protected void postCalcurate() {
		// for override
	}

	protected abstract Number calculateVertexScore(V vertex);
}
