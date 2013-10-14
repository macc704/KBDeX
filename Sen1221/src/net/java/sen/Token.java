/*
 * Token.java - Token which is used at Viterbi
 *
 * Copyright (C) 2001, 2002 Taku Kudoh, Takashi Okamoto
 * Taku Kudoh <taku-ku@is.aist-nara.ac.jp>
 * Takashi Okamoto <tora@debian.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 */

package net.java.sen;

/**
 * This class represent morpheme information.
 */
final public class Token {
  private Node node;
  // cache for each value.
  private String pos = null;
  private String pronunciation = null;
  private String basic = null;
  private String cform = null;
  private String read = null;
  private String nodeStr = null;
  private String termInfo = null;

  private String addInfo = null;
  private int cost = -1;
  private int start = -1;
  private int length = -1;

  /**
   * constructor.
   */
  protected Token(Node n) {
    node = n;
    pos = node.getPos();
  }

  /**
   * constructor.
   */
  public Token() {}

  /**
   * get start index of this token.
   */
  public int start() {
  	if (start == -1) {
  		start = node.begin;
  	}
    return start;
  }

  /**
   * set start position. This method is threa unsafe.
   */
  public void setStart(int s) {
  	this.start = s;
  }

  /**
   * get end index of this token.
   */
  public int end() {
    return start() + length();
  }

  /**
   * get length of this token.
   */
  public int length() {
    if (length == -1) {
    	length = node.length;
    }
    return length;
  }

  /**
   * set length of token. This method is threa unsafe.
   */
  public void setLength(int l) {
  	this.length = l;
  	if (node != null) {
  		this.node.length = l;
  	}
  }

  /**
   * get part of speech.
   *
   * @return part of speech which represents this token.
   */
  public String getPos() {
    return pos;
  }

  /**
   * set part of speech.
   *
   * @pos part of speech.
   */
  public void setPos(String pos) {
    this.pos = pos;
  }

  /**
   * get un-conjugate string. This method is thread unsafe.
   *
   * @return un-conjugate representation for morpheme.
   */
  public String getBasicString() {
    if (basic == null)
      basic = node.getBasicString();
    return basic;
  }

  /**
   * set un-conjugate string. This method is thread unsafe.
   */
  public void setBasicString(String basic) {
  	this.basic = basic;
  }

  /**
   * get conjugational form. This method is thread unsafe.
   */
  public String getCform() {
    if (cform == null)
      cform = node.getCform();
    return cform;
  }

  /**
   * set conjugational form. This method is thread unsafe.
   */
  public void setCform(String cform) {
  	this.cform = cform;
  }

  /**
   * get reading. This method is thread unsafe.
   */
  public String getReading() {
    if (read == null)
      read = node.getReading();
    return read;
  }

  /**
   * set reading. This method is thread unsafe.
   */
  public void setReading(String read) {
  	this.read = read;
  }

  /**
   * get pronunciation. This method is thread unsafe.
   */
  public String getPronunciation() {
    if (pronunciation == null)
      pronunciation = node.getPronunciation();
    return pronunciation;
  }

  /**
   * set pronunciation. This method is thread unsafe.
   */
  public void setPronunciation(String pronunciation) {
  	this.pronunciation = pronunciation;
  }

  /**
   * get surface. This method is thread unsafe.
   */
  public String getSurface() {
  	if (nodeStr == null)
  		nodeStr = node.toString();
  	return nodeStr;
  }

  /**
   * set surface. This method is thread unsafe.
   */
  public void setSurface(String surface) {
  	nodeStr = surface;
  }

  /**
   * get cost. This method is thread unsafe.
   */
  public int getCost() {
  	if (cost == -1) {
  		cost = node.getCost();
  	}
  	return cost;
  }

  /**
   * set cost. This method is thread unsafe.
   */
  public void setCost(int cost) {
  	this.cost = cost;
  }

  /**
   * get additional information. This method is thread unsafe.
   */
  public String getAddInfo() {
  	if (addInfo == null) {
  		addInfo = node.getAddInfo();
  	}
  	return addInfo;
  }

  /**
   * set additional information. This method is thread unsafe.
   */
  public void setAddInfo(String addInfo) {
    this.addInfo = addInfo;
  }

  /**
   * get termInfo. This method is thread unsafe.
   */
  public String getTermInfo() {
    if (termInfo == null) {
        termInfo = node.termInfo;
    }
  	return termInfo;
  }

  /**
   * set termInfo. This method is thread unsafe.
   */
  public void setTermInfo(String termInfo) {
    this.termInfo = termInfo;
  }

  /**
   * get string representation. This method is thread unsafe.
   */
  public String toString() {
  	return getSurface();
  }
}