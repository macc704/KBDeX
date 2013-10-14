/*
 * KNNetwork.java
 * Created on Apr 15, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import kbdex.adapters.jung.KGraph;
import kbdex.model.kbmodel.IKBReason;
import kbdex.model.kbmodel.KBElement;
import kbdex.model.kbmodel.KBElementPair;
import kbdex.model.kbmodel.KBRelation;
import clib.common.collections.CObservableList;

/**
 * @author macchan
 */
public class KNNetwork<V extends KBElement> {

	private KGraph<V, KBRelation> graph = new KGraph<V, KBRelation>();
	private HashSet<V> nodes = new HashSet<V>();

	private Map<KBElementPair, KBRelation> relationsCash = new HashMap<KBElementPair, KBRelation>();//for reuse

	public KNNetwork() {
	}

	public KGraph<V, KBRelation> getGraph() {
		return graph;
	}

	public void addNode(V node) {
		getGraph().addVertex(node);
		nodes.add(node);
	}

	public void removeNode(V node) {
		getGraph().removeVertex(node);
		nodes.remove(node);
	}

	public void addRelation(V from, V to, IKBReason reason) {
		if (from.equals(to)) {// ignore self relationship
			return;
		}
		KBRelation r = (KBRelation) getGraph().findEdge(from, to);
		if (r == null) {
			r = createNewRelation(from, to);
		}

		r.addReason(reason);
	}

	public void removeRelation(V from, V to, IKBReason reason) {
		KBRelation r = (KBRelation) getGraph().findEdge(from, to);
		if (r == null) {
			return;//do not care.
		}

		r.removeReason(reason);
		if (r.isEmpty()) {
			getGraph().removeEdge(r);
		}
	}

	public KBRelation createNewRelation(V from, V to) {
		KBElementPair key = new KBElementPair(from, to);
		KBRelation r;
		if (relationsCash.containsKey(key)) {
			r = relationsCash.get(key);
			r.initialize();
		} else {
			r = new KBRelation(to, from);
			relationsCash.put(key, r);
		}
		getGraph().addEdge(r, from, to);
		return r;
	}

	public CObservableList<V> getNodes() {
		return new CObservableList<V>(nodes);
	}

	public void clear() {
		this.nodes.clear();
		graph = new KGraph<V, KBRelation>();
	}

	public void tick() {
		//未実装
	}
}
