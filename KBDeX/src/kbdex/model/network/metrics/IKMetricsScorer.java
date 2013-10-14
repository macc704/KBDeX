/*
 * IKMetricsScorer.java
 * Created on May 2, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.network.metrics;

/**
 * @author macchan
 */
public interface IKMetricsScorer {

	public String getName();

	public void calculate();

}
