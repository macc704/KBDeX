/*
 * KBRelation.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.kbmodel;

import java.util.ArrayList;
import java.util.List;

import clib.common.model.CAbstractModelObject;

public class KBRelation extends AbstractKBRelation<KBElement, IKBReason> {

	private static final long serialVersionUID = 1L;

	public KBRelation(KBElement to, KBElement from) {
		super(to, from);
	}

}

class AbstractKBRelation<V, R> extends CAbstractModelObject {

	private static final long serialVersionUID = 1L;

	private V to;
	private V from;

	private List<R> reasons = new ArrayList<R>();

	public AbstractKBRelation(V to, V from) {
		this.to = to;
		this.from = from;
	}

	public void initialize() {
		reasons.clear();
	}

	public void addReason(R reason) {
		if (!reasons.contains(reason)) {
			reasons.add(reason);
			fireModelUpdated(reason);
		}
	}

	public void removeReason(R reason) {
		if (reasons.contains(reason)) {
			reasons.remove(reason);
			fireModelUpdated(reason);
		}
	}

	public List<R> getReasons() {
		return reasons;
	}

	public boolean isEmpty() {
		return reasons.isEmpty();
	}

	public int getReasonCount() {
		return reasons.size();
	}

	/**
	 * @return the to
	 */
	public V getTo() {
		return to;
	}

	/**
	 * @return the from
	 */
	public V getFrom() {
		return from;
	}

	public String toParamString() {
		return "Relation from:" + getFrom() + " to:" + getTo();
	}
}
