/*
 * IKCentralizationCalculatableScorer.java
 * Created on May 4, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.network.metrics;

/**
 * @author macchan
 *
 */
public interface IKCentralizationCalculatableScorer {

	//その指標の最大の値
	//Cx(i*) 
	//(ネットワーク分析　p.64)
	public double getMaxValueForVertex();

	//このグラフと同じn個の頂点からなるグラフでの点中心性の差の和の理論上の最大値
	//max E n=1 to n [Cx(i*) - Cx(i)]
	//(ネットワーク分析　p.64)
	public double getTheoreticalMaxDifferenceOfTotalCentrality();
}
