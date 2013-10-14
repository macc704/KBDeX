/*
 * KBAgentRelationReason.java
 * Created on Apr 23, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.kbmodel;


/**
 * @author macchan
 *
 */
public class KBAgentRelationReason extends KBObjectSharingReason<KBElementPair> {

	private KBDiscourseUnit from;
	private KBDiscourseUnit to;

	public KBAgentRelationReason(KBDiscourseUnit from, KBDiscourseUnit to) {
		super(new KBElementPair(from, to));
		this.from = from;
		this.to = to;
	}

	/**
	 * @return the from
	 */
	public KBDiscourseUnit getFrom() {
		return from;
	}

	/**
	 * @return the to
	 */
	public KBDiscourseUnit getTo() {
		return to;
	}

	/* (non-Javadoc)
	 * @see kbdex.model.network.KBObjectSharingReason#toString()
	 */
	@Override
	public String toString() {
		return "Sharing Word(s) (in the unit " + from + " and the unit " + to
				+ ")";
	}
}
