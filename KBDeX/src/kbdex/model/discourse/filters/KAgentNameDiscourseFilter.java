/*
 * KAgentNameDiscourseFilter.java
 * Created on Mar 7, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse.filters;

import java.util.ArrayList;
import java.util.List;

import kbdex.model.discourse.KDDiscourseRecord;

/**
 * @author macchan
 * @_ KDAgent->Stringへ変更 Filterレベルは文字列の方が扱いやすい
 */
public class KAgentNameDiscourseFilter implements IKDiscourseFilter {

	private List<String> agentNames = new ArrayList<String>();

	public KAgentNameDiscourseFilter() {
	}

	// List<String> or List<KDAgent>を想定している．
	// 二つ作ると，シグニチャがかぶるので一つにしている．
	public KAgentNameDiscourseFilter(List<String> names) {
		this.agentNames = new ArrayList<String>(names);
	}

	public void add(String agent) {
		agentNames.add(agent);
	}

	public List<String> getAgentNames() {
		return new ArrayList<String>(agentNames);
	}

	public boolean accept(KDDiscourseRecord unit) {
		for (String agentName : agentNames) {
			if (agentName.equals(unit.getAgentName())) {
				return true;
			}
		}
		return false;
	}
}
