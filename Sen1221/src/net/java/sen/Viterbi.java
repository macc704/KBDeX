/*
 * Viterbi.java - Viterbi algorithm
 * 
 * Copyright (C) 2001, 2002 Taku Kudoh, Takashi Okamoto Taku Kudoh
 * <taku-ku@is.aist-nara.ac.jp> Takashi Okamoto <tora@debian.org>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *  
 */

package net.java.sen;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Viterbi {
  private static Log log = LogFactory.getLog(Viterbi.class);

  private Tokenizer tokenizer;
  private Node eosNode;
  private Node bosNode;
  private char sentence[];
  private Node[] lookupCache;
  private Node[] endNodeList;

  public Viterbi() {
    tokenizer = null;
  }

  public Viterbi(Tokenizer t) {
    tokenizer = t;

    init();
  }

  void init() {
    endNodeList = null;
    lookupCache = null;
    bosNode = tokenizer.getBOSNode();
    eosNode = tokenizer.getEOSNode();
  }

  Node lookup(int pos) throws IOException {
    Node resultNode = null;

    if (lookupCache[pos] != null) {
      for (Node node = lookupCache[pos]; node != null; node = node.rnext) { // copy
                                                                            // contents
        Node newNode = tokenizer.getNewNode();
        int id = newNode.id;
        newNode.copy(node);
        newNode.rnext = resultNode;
        newNode.id = id;
        resultNode = newNode;
      }
    } else {
      resultNode = tokenizer.lookup(sentence, pos);
      lookupCache[pos] = resultNode;
    }

    return resultNode;
  }

  public synchronized Node analyze(char sentence[]) throws IOException {
    int len = sentence.length;

    log.debug("sentence = \"" + new String(sentence) + "\"");
    // initialize Viterbi
    init();

    endNodeList = new Node[len + 1];
    lookupCache = new Node[len + 1];
    endNodeList[0] = bosNode;
    endNodeList[len] = null;
    lookupCache[len] = null;

    this.sentence = sentence;

    // detect the morpheme at each location in character array
    for (int pos = 0; pos < len; pos++) {
      if (endNodeList[pos] != null) {
        Node rNode = lookup(pos);
        if (rNode != null) {
          calcConnectCost(pos, rNode);
          if (log.isTraceEnabled()) {
            log.trace("rNode.token.rcAttr2=" + rNode.token.rcAttr2);
            log.trace("rNode.token.rcAttr1=" + rNode.token.rcAttr1);
            log.trace("rNode.token.lcAttr=" + rNode.token.lcAttr);
          }
        }
      }
    }

    // calc cost for join each node.
    for (int pos = len; pos >= 0; pos--) {
      if (endNodeList[pos] != null) {
        calcConnectCost(pos, eosNode);
        break;
      }
    }

    Node node = eosNode;

    // trace node from end. and reconstruct the morpheme list.
    for (Node prevNode; node.prev != null;) {
      prevNode = node.prev;
      prevNode.next = node;
      node = prevNode;
    }

    log.debug("analized");

    // if you want to see pos for all candidate word,
    // and remove the following comment at JapaneseTokenizer.java:
    //
    // newNode.feature = dic.getPosInfo(t[i].feature);
    //
    // You should comment following 3 lines.

    for (Node it = bosNode.next; it != null && it.surface != null; it = it.next) {
      it.termInfo = tokenizer.dic.getPosInfo(it.token.posID);
    }

    return bosNode;
  }

  final private void calcConnectCost(int pos, Node rNode) throws IOException {

    int len = sentence.length;

    for (; rNode != null; rNode = rNode.rnext) {
      int bestCost = Integer.MAX_VALUE;
      Node bestNode = null;

      for (Node lNode = endNodeList[pos]; lNode != null; lNode = lNode.lnext) {
        if (log.isTraceEnabled()) {
          log.trace("candidate morpheme = " + lNode.toString());
          log.trace("token id = " + lNode.token.posID);
          log.trace("lnode.prev(" + lNode.prev.token.rcAttr2 + ") = "
              + tokenizer.dic.getPosInfo(lNode.prev.token.posID));
          log.trace("lnode(" + lNode.token.rcAttr1 + ") = "
              + tokenizer.dic.getPosInfo(lNode.token.posID));
          log.trace("rnode(" + rNode.token.lcAttr + ") = "
              + tokenizer.dic.getPosInfo(rNode.token.posID));
        }

        int cost = lNode.cost + tokenizer.getCost(lNode.prev, lNode, rNode);
        if (cost <= bestCost) {
          bestNode = lNode;
          bestCost = cost;
        }
      }

      rNode.prev = bestNode;
      rNode.cost = bestCost;
      int x = rNode.end + pos;

      rNode.lnext = endNodeList[x];
      endNodeList[x] = rNode;

      if (rNode.token.rcAttr2 != 0) {
        int pos2 = rNode.end + pos;
        if (pos2 == len)
          continue;
        Node rNode2 = lookup(pos2);
        for (; rNode2 != null; rNode2 = rNode2.rnext) {
          if (log.isTraceEnabled()) {
            log.trace("rnode.prev(" + rNode.prev.token.rcAttr2 + ") = "
                + tokenizer.dic.getPosInfo(rNode.prev.token.posID));
            log.trace("rnode(" + rNode.token.rcAttr1 + ") = "
                + tokenizer.dic.getPosInfo(rNode.token.posID));
            log.trace("rnode2(" + rNode2.token.lcAttr + ") = "
                + tokenizer.dic.getPosInfo(rNode2.token.posID));
          }

          rNode2.cost = rNode.cost
              + tokenizer.getCost(rNode.prev, rNode, rNode2);
          rNode2.prev = rNode;

          int y = rNode2.end + pos2;

          rNode2.lnext = endNodeList[y];
          endNodeList[y] = rNode2;
        }
      }
    }

  }

}

