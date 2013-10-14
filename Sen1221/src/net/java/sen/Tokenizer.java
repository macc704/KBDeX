/*
 * Tokenizer.java - get character token.
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

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.java.sen.io.FileAccessor;
import net.java.sen.io.FileAccessorFactory;

public abstract class Tokenizer {
  private int id;

  private Node bosNode2 = new Node();
  private Node eosNode = new Node();
  public Dictionary dic = null;
  public CToken bosToken = null;
  public CToken bosToken2 = new CToken();
  public CToken eosToken = null;
  public CToken unknownToken = new CToken();
  public Node bosNode = new Node();

  // matrix for connect cost and matrix size
  private short matrix[];
  private int msize1;
  private int msize2;
  private int msize3;
  private static Log log = LogFactory.getLog(Tokenizer.class);

  public Tokenizer(String tokenFile, String doubleArrayFile,
      String posInfoFile, String connectFile, String charset)
      throws IOException {
    dic = new Dictionary(tokenFile, doubleArrayFile, posInfoFile, charset);
    bosToken = dic.getBOSToken();
    bosToken2 = dic.getBOSToken();
    eosToken = dic.getEOSToken();
    unknownToken = dic.getUnknownToken();
    unknownToken.cost = 30000;
    loadConnectCost(connectFile);
  }

  /**
   * load connect cost file (matrix.cha)
   */
  private void loadConnectCost(String connectFile) throws IOException {
    FileAccessor fd = null;
    long start;

    File f = new File(connectFile);

    log.info("connection file = " + f.toString());
    start = System.currentTimeMillis();
    fd = FileAccessorFactory.getInstance(f);
    msize1 = fd.readShort();
    msize2 = fd.readShort();
    msize3 = fd.readShort();

    // first 3 value means matrix information:
    // *2 means information is short.
    // each matrix value is short, so / 2

    int len = ((int) f.length() - (3 * 2)) / 2;

    log.debug("msize1=" + msize1);
    log.debug("msize2=" + msize2);
    log.debug("msize3=" + msize3);
    log.debug("matrix size = " + len);

    matrix = new short[len];
    for (int i = 0; i < len; i++) {
      matrix[i] = fd.readShort();
    }
    log.info("time to load connect cost file = "
        + (System.currentTimeMillis() - start) + "[ms]");
    fd.close();
  }

  public int skipCharClass(char[] c, int begin, int end, int char_class,
      int fail[]) {
    int p = begin;

    while (p != end && (fail[0] = getCharClass(c[p])) == char_class)
      p++;

    // -- fixme --
    if (p == end)
      fail[0] = 0;

    return p;
  }

  public int skipCharClass(char c[], int begin, int end, int char_class) {
    int p = begin;

    while (p != end && getCharClass(c[p]) == char_class)
      p++;
    return p;
  }

  public abstract int getCharClass(char c);

  abstract public Node lookup(char c[], int begin) throws IOException;

  public void clear() {
    id = 0;
  }

  public Node getNewNode() {
    Node node = new Node();
    node.id = id++;
    return node;
  }

  Node getBOSNode() {
    bosNode.clear();
    bosNode2.clear();

    bosNode.prev = bosNode2;
    bosNode.surface = bosNode2.surface = null;
    bosNode.length = bosNode2.length = 0;
    bosNode.token = bosToken;
    bosNode2.token = bosToken2;

    return bosNode;
  }

  Node getEOSNode() {
    eosNode.clear();

    eosNode.surface = null;
    eosNode.length = 1;
    eosNode.token = eosToken;
    return eosNode;
  }

  /**
   * get cost from three Node.
   * 
   * @param lNode2
   * @param lNode
   * @param rNode
   */
  int getCost(Node lNode2, Node lNode, Node rNode) {
    int pos = msize3 * (msize2 * lNode2.token.rcAttr2 + lNode.token.rcAttr1)
        + rNode.token.lcAttr;
    if (pos >= matrix.length) {
      System.out.println("error pos=" + pos);
      System.out.println("matrix=" + matrix.length);
    }
    return matrix[pos] + rNode.token.cost;

    // above code means matrix is in memory.
    // if you hesitate consuming a lot of memory, you can use
    // following code.
    /*
     * try { fd.seek(pos*2+(3*2)); short val = fd.readShort();
     * if(log.isTraceEnabled()){ log.trace("cost = " + val); } return val; }
     * catch (IOException e){ throw new RuntimeException(e.toString()); }
     */
  }

  public boolean close() {
    return dic.close();
  }

}

