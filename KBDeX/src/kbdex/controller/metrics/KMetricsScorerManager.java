/*
 * KMetricsManager.java
 * Created on Apr 24, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.controller.metrics;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kbdex.adapters.jung.scorer.KVertexBetweennessScorer;
import kbdex.adapters.jung.scorer.KVertexClosenessCentralityScorer;
import kbdex.adapters.jung.scorer.KVertexClusteringScorer;
import kbdex.adapters.jung.scorer.KVertexDegreeScorer;
import kbdex.adapters.jung.scorer.KVertexFormalizedDegreeScorer;
import kbdex.adapters.jung.scorer.KVertexPageRankScorer;
import kbdex.controller.IKDiscourseControllerListener;
import kbdex.model.kbmodel.KBElement;
import kbdex.model.kbmodel.KBRelation;
import kbdex.model.network.KNNetwork;
import kbdex.model.network.metrics.IKGraphMetricsScorer;
import kbdex.model.network.metrics.IKVertexMetricsScorer;

import org.apache.commons.collections15.Transformer;

/**
 * @author macchan
 * @_ TODO 100 DisableにしたときのScorerの値の検証が必要
 */
public class KMetricsScorerManager<V extends KBElement> implements
		IKDiscourseControllerListener {

	private KNNetwork<V> network;
	private Map<String, KMetricsScorerSuite<V>> scorers = new LinkedHashMap<String, KMetricsScorerSuite<V>>();

	public KMetricsScorerManager(KNNetwork<V> network) {
		this.network = network;
		initialize();
	}

	@SuppressWarnings("unused")
	private Transformer<KBRelation, ? extends Number> edgeWeights = new Transformer<KBRelation, Integer>() {
		public Integer transform(KBRelation r) {
			return r.getReasonCount();
		}
	};

	private void initialize() {
		installScorer(new KVertexDegreeScorer<V>());//Rで検証済
		installScorer(new KVertexFormalizedDegreeScorer<V>());//Rで検証済		
		installScorer(new KVertexBetweennessScorer<V>());//Rで検証済
		installScorer(new KVertexClosenessCentralityScorer<V>());//Rで検証済，Pajekとは割り方が異なる
		installScorer(new KVertexClusteringScorer<V>());//Pajekで検証済　TODO 100 Rで検証すること
		installScorer(new KVertexPageRankScorer<V>());//Rでほぼ同様となることを検証済
		//TODO 100 値を検証して，導入すること
		//installVertexScorer(new KVertexBetweennessWeightedScorer<V, KBRelation>(edgeWeights));
		//TODO 100 値を検証して，導入すること
		//installVertexScorer(new KVertexClosenessCentralityWeightedScorer<V, KBRelation>(edgeWeights));
		//TODO 100 値を検証して，導入すること（weightedは値がかなりおかしい）
		//installVertexScorer(new KVertexPageRankWeightedScorer<V, KBRelation>(edgeWeights));
	}

	/**
	 * @param string
	 * @param kVertexDegreeScorer
	 */
	private void installScorer(IKVertexMetricsScorer<V, Number> scorer) {
		scorers.put(scorer.getName(),
				new KMetricsScorerSuite<V>(scorer.getName(), network, scorer));
	}

	public void disableAllScoreres() {
		for (KMetricsScorerSuite<V> scorer : getScorers()) {
			scorer.setActive(false);
		}
	}

	public List<KMetricsScorerSuite<V>> getScorers() {
		return new ArrayList<KMetricsScorerSuite<V>>(scorers.values());
	}

	public KMetricsScorerSuite<V> getScorer(String name) {
		return scorers.get(name);
	}

	public void hardReset() {
		for (KMetricsScorerSuite<V> scorer : getScorers()) {
			scorer.hardReset();
		}
	}

	public void reset() {
		for (KMetricsScorerSuite<V> scorer : getScorers()) {
			scorer.reset();
		}
	}

	public void tick() {
		for (KMetricsScorerSuite<V> scorer : getScorers()) {
			scorer.tick();
		}
	}

	public boolean isAnimationFinished() {
		return true;
	}

	public void refreshWithAnimation() {
		for (KMetricsScorerSuite<V> scorer : getScorers()) {
			scorer.refreshWithAnimation();
		}
	}

	public void refreshWithoutAnimation() {
		for (KMetricsScorerSuite<V> scorer : getScorers()) {
			scorer.refreshWithoutAnimation();
		}
	}

	/**
	 * @return
	 * @deprecated
	 */
	public List<IKVertexMetricsScorer<V, Number>> getVertexScorers() {
		throw new UnsupportedOperationException(
				"This method has already deprecated");
	}

	/**
	 * @return
	 * @deprecated
	 */
	public List<IKGraphMetricsScorer<?>> getGraphScorers() {
		throw new UnsupportedOperationException(
				"This method has already deprecated");
	}

}
