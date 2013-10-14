/*
 * KGraphTemporalMetricsScorer.java
 * Created on 2011/11/26
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.controller.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kbdex.model.network.metrics.IKMetricsScorer;
import kbdex.model.network.metrics.IKVertexMetricsScorer;
import clib.common.model.CAbstractModelObject;

/**
 * @author macchan
 */
public class KVertexTemporalMetricsScorer<V> extends CAbstractModelObject
		implements IKTemporalMetricsScorer {

	private static final long serialVersionUID = 1L;

	private IKVertexMetricsScorer<V, Number> scorer;
	private List<Map<V, Number>> scores = new ArrayList<Map<V, Number>>();
	private Set<V> vertices = new LinkedHashSet<V>();

	public KVertexTemporalMetricsScorer(IKVertexMetricsScorer<V, Number> scorer) {
		this.scorer = scorer;
	}

	/**
	 * @return the scorer
	 */
	public IKMetricsScorer getScorer() {
		return scorer;
	}

	@SuppressWarnings("unchecked")
	public IKVertexMetricsScorer<V, Number> getVertexScorer() {
		return (IKVertexMetricsScorer<V, Number>) getScorer();
	}

	public int size() {
		return scores.size();
	}

	public double getDoubleValue(int index, V v) {
		if (0 <= index && index < size()) {
			Map<V, Number> map = scores.get(index);
			if (map.containsKey(v)) {
				return map.get(v).doubleValue();
			}
			return 0d;
		}
		return 0d;
	}

	public List<V> getVertices() {
		return new ArrayList<V>(vertices);
	}

	/* (non-Javadoc)
	 * @see kbdex.controller.metrics.IKTemporalMetricsScorer#clear()
	 */
	@Override
	public void clear() {
		scores.clear();
		vertices.clear();
		fireModelUpdated();
	}

	/* (non-Javadoc)
	 * @see kbdex.controller.metrics.IKTemporalMetricsScorer#tick()
	 */
	@Override
	public void tick() {
		Collection<V> currentVertices = scorer.getGraph().getVertices();
		vertices.addAll(currentVertices);
		Map<V, Number> currentScores = new HashMap<V, Number>();
		for (V v : currentVertices) {
			Number score = scorer.getVertexScore(v);
			currentScores.put(v, score);
		}
		scores.add(currentScores);
		fireModelUpdated();
	}
}
