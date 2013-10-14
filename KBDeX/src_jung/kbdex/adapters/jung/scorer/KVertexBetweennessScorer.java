/*
 * KVertexBetweennessScorer.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.adapters.jung.scorer;

import kbdex.model.network.metrics.IKCentralizationCalculatableScorer;
import edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality;
import edu.uci.ics.jung.graph.UndirectedGraph;

/*
 * DefaultのBetweenessCentralityは，  
 * Graphが変わったときに再計算してくれないので，計算し直すように変更する．
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class KVertexBetweennessScorer<V> extends
		KAbstractVertexMetricsScorer<V> implements
		IKCentralizationCalculatableScorer {

	private BetweennessCentrality<V, ?> bc;

	public static String NAME = "Betweenness Centrality";

	public String getName() {
		return NAME;
	}

	protected void preCalculate() {
		this.bc = createBC();
	}

	protected BetweennessCentrality<V, ?> createBC() {
		return new BetweennessCentrality(getGraph());
	}

	protected void postCalcurate() {
		bc = null;
	}

	protected Double calculateVertexScore(V v) {
		if (bc != null) {
			Double d = bc.getVertexScore(v);
			return formalize(d);
		} else {
			return 0d;
		}
	}

	private Double formalize(Double value) {
		try {
			int n = getGraph().getValidVertexCount();
			if (n < 3) {
				return value;
			}

			int power = (n - 1) * (n - 2);
			if (getGraph() instanceof UndirectedGraph) {
				value = value * 2;
			}
			return value / power;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0d;
		}
	}

	public double getTheoreticalMaxDifferenceOfTotalCentrality() {
		int n = getGraph().getValidVertexCount();
		return (double) (n - 1);
	}

	public double getMaxValueForVertex() {
		return getMax().doubleValue();
	}

}
