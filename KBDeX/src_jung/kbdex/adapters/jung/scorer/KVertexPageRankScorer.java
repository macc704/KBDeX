/*
 * KVertexPageRankScorer.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.adapters.jung.scorer;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;

@SuppressWarnings("unchecked")
public class KVertexPageRankScorer<V> extends KAbstractVertexMetricsScorer<V> {

	public static final double DUMPING = 0.85;
	//public static final double DUMPING = 0.87455;	//TODO 1000 ある条件ではこちらの方がRにはFitする, が．．
	public static final double ALPHA = 1 - DUMPING;

	private PageRankWithPriors<V, ?> pr;

	public static String NAME = "PageRank";

	public String getName() {
		return NAME;
	}

	protected void preCalculate() {
		this.pr = createCalcurator();
		this.pr.evaluate();
	}

	protected void postCalcurate() {
		this.pr = null;
	}

	@SuppressWarnings({ "rawtypes" })
	protected PageRankWithPriors<V, ?> createCalcurator() {
		return new PageRank(getGraph(), ALPHA);
	}

	protected Double calculateVertexScore(V v) {
		if (pr != null) {
			Double d = pr.getVertexScore(v);
			return d;
		} else {
			return 0d;
		}
	}

}
