/*
 * KVertexClosenessCentralityScorer.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.adapters.jung.scorer;

import java.util.HashMap;
import java.util.Map;

import kbdex.adapters.jung.KGraph;
import kbdex.model.network.metrics.IKCentralizationCalculatableScorer;
import edu.uci.ics.jung.algorithms.shortestpath.Distance;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;

public class KVertexClosenessCentralityScorer<V> extends
		KAbstractVertexMetricsScorer<V> implements
		IKCentralizationCalculatableScorer {

	public static String NAME = "Closeness Centrality";

	public String getName() {
		return NAME;
	}

	/**
	 * The metric to use for specifying the distance between pairs of vertices.
	 */
	protected Distance<V> distance;

	//	/**
	//	 * The cache for the output results. Null encodes "not yet calculated", < 0
	//	 * encodes "no such distance exists".
	//	 */
	//	protected Map<V, Double> output;

	/**
	 * Specifies whether the values returned are the sum of the v-distances or
	 * the mean v-distance.
	 */
	protected boolean averaging;

	/**
	 * Specifies whether, for a vertex <code>v</code> with missing (null)
	 * distances, <code>v</code>'s score should ignore the missing values or be
	 * set to 'null'. Defaults to 'true'.
	 */
	protected boolean ignore_missing;

	/**
	 * Specifies whether the values returned should ignore self-distances
	 * (distances from <code>v</code> to itself). Defaults to 'true'.
	 */
	protected boolean ignore_self_distances;

	/**
	 * Equivalent to <code>this(graph, averaging, true, true)</code>.
	 * 
	 * @param graph
	 *            The graph on which the vertex scores are to be calculated.
	 * @param averaging
	 *            Specifies whether the values returned is the sum of all
	 *            v-distances or the mean v-distance.
	 */
	public KVertexClosenessCentralityScorer() {
		this(true, true, true);
	}

	/**
	 * Creates an instance with the specified graph, distance metric, and
	 * averaging behavior.
	 * 
	 * @param graph
	 *            The graph on which the vertex scores are to be calculated.
	 * @param distance
	 *            The metric to use for specifying the distance between pairs of
	 *            vertices.
	 * @param averaging
	 *            Specifies whether the values returned is the sum of all
	 *            v-distances or the mean v-distance.
	 * @param ignore_missing
	 *            Specifies whether scores for missing distances are to ignore
	 *            missing distances or be set to null.
	 * @param ignore_self_distances
	 *            Specifies whether distances from a vertex to itself should be
	 *            included in its score.
	 */
	public KVertexClosenessCentralityScorer(boolean averaging,
			boolean ignore_missing, boolean ignore_self_distances) {
		this.averaging = averaging;
		this.ignore_missing = ignore_missing;
		this.ignore_self_distances = ignore_self_distances;
	}

	protected void preCalculate() {
		this.distance = createDistance();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Distance<V> createDistance() {
		return new UnweightedShortestPath(getGraph());
	}

	protected Double calculateVertexScore(V v) {
		KGraph<V, ?> graph = getGraph();
		Double value;
		int n = graph.getVertexCount();// mac

		Map<V, Number> v_distances = new HashMap<V, Number>(
				distance.getDistanceMap(v));
		if (ignore_self_distances) {
			v_distances.remove(v);
		}

		// if we don't ignore missing distances and there aren't enough
		// distances, output null (shortcut)
		if (!ignore_missing) {
			int num_dests = graph.getVertexCount()
					- (ignore_self_distances ? 1 : 0);
			if (v_distances.size() != num_dests) {
				return null;
			}
		}

		Double sum = 0.0;
		for (V w : graph.getVertices()) {
			if (w.equals(v) && ignore_self_distances) {
				continue;
			}
			Number w_distance = v_distances.get(w);
			if (w_distance == null)
				if (ignore_missing) {
					sum += n;// mac
					continue;
				} else {
					return null;
				}
			else {
				sum += w_distance.doubleValue();
			}
		}
		value = sum;
		if (averaging) {
			//value /= v_distances.size();// original

			// Mac			
			value /= (n - 1);

			// 近接点をもたないものが，19点...OK なぜpajekは54でわる？
			// int nn = v_distances.size();
			// System.out.println(v_distances.size());
			// value /= (v_distances.size() / 2);/
			// value /= 54d;// mac
			// value /= nn;
			// value /= n;
			// System.out.println(n);// 87
			// System.out.println(nn);// 68
			// value /= (nn - (n - nn));// mac
			// value /= n;// mac
		}

		double score = value == 0 ? Double.POSITIVE_INFINITY : 1.0 / value;

		return score;
	}

	public double getTheoreticalMaxDifferenceOfTotalCentrality() {
		int n = getGraph().getValidVertexCount();
		return (double) (n * n - 3 * n + 2) / (double) (2 * n - 3);
	}

	public double getMaxValueForVertex() {
		return getMax().doubleValue();
	}
}
