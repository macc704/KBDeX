/*
 * DoubleArrayTrie.java - DoubleArrayTrie implementation in Java.
 * 
 * Copyright (C) 2001, 2002 Taku Kudoh, Takashi Okamoto
 * Taku Kudoh <taku-ku@is.aist-nara.ac.jp>
 * Takashi Okamoto <tora@debian.org>
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
/**** macro for cpp ****/

#define DartsInt	int
#define NodeType	char
#define ArrayType	int

#define _max(x,y) (((x)>(y))?(x):(y))
#define _check_size(x) {\
  int t = (int)(x);\
  if(t>alloc_size) {\
    resize((int)(t*1.05));\
  }\
}

// even is base array for Double Array Trie
#define base(x) array[((int)x) << 1]

// odd is check array for Double Array Trie
#define check(x) array[(((int)x) << 1) + 1]

/**** end for cpp ****/

/**** begin Java code ****/

package net.java.sen.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Vector;

import java.io.*;

/**
 * Double Array Trie implementation 
 */

public  class DoubleArrayTrie {
  private final static int BUF_SIZE=500000;
  private static Log log = LogFactory.getLog(DoubleArrayTrie.class);

  // base and check arrays for double array trie
  private int       array[];  
  private int       used[];
  private int       size;
  // buffe size for check and base arrays.
  private int       alloc_size;
  private NodeType  str[][];
  private int       str_size;
  private int       len[];
  private ArrayType val[];

  private int       next_check_pos;
  private int       no_delete;

  private class Node
  {
    ArrayType  code;
    int        depth;
    int        left;
    int        right;
  };


  public DoubleArrayTrie() {
    array = null;
    used = null;
    size = 0;
    alloc_size = 0;
    no_delete = 0;
  }

  /**
   * load dictionary for double array trie.
   * dictionary is compatible with ChaSenTNG.
   */
  /**
   * load dictionary for double array trie.
   * dictionary is compatible with ChaSenTNG.
   */

  public void load(String fileName) throws IOException {
    log.info("loading double array trie dict = " + fileName);
    long start = System.currentTimeMillis();
    File file= new File(fileName);
    array = new int[(int)(file.length() / 4)];
    DataInputStream is = new DataInputStream
      (new BufferedInputStream(new FileInputStream(file), BUF_SIZE));
    for (int i = 0 ; i < array.length ; i++) {
      array[i] =  is.readInt();
    }
    log.info("loaded time = " + (((double)(System.currentTimeMillis()- start))/1000) + "[ms]");
  }

  /**
   * reallocate buffer.
   */
  int[] _resize (int ptr[], int n, int l, int v)
  {
    int tmp[] = new int [l]; // realloc
    if (ptr != null) {
      l = ptr.length;
    } else {
      l = 0;
    }

    for (int i = 0; i < l; i++) tmp[i] = ptr[i]; // copy
    for (int i = l; i < l; i++) tmp[i] = v;
    ptr = null;
    return tmp;
  }

  /**
   * reallocate buffer.
   */
  int resize (int new_size)
  {
    array = _resize (array, alloc_size << 1, new_size << 1, (int)0);
    used  = _resize (used,  alloc_size, new_size, (int)0);

    alloc_size = new_size;

    return new_size;
  }

  int fetch (Node parent, Vector siblings)
  {
    ArrayType prev = 0;
    if(log.isTraceEnabled()) {
      log.trace("parent.left=" + parent.left);
      log.trace("parent.right=" + parent.right);    
      log.trace("parent.depth=" + parent.depth);    
    }

    for (int i = parent.left; i < parent.right; i++) {
      if (((len != null) ? 
         len[i] : str[i].length) < parent.depth) continue;

      NodeType tmp[] = str[i];

      ArrayType cur = 0;
      if (((len != null) ? len[i] : str[i].length) != parent.depth) {
        if(log.isTraceEnabled())
          log.trace("tmp["+parent.depth+"]="+tmp[parent.depth]);
        cur = (ArrayType)tmp[parent.depth] + 1;
      }
	  
      if (prev > cur) {
        log.error("given strings are not sorted.\n");
        throw new RuntimeException("Fatal: given strings are not sorted.\n");
      }

      if (cur != prev || siblings.size() == 0) {
        Node tmp_node = new Node();
        tmp_node.depth = parent.depth + 1;
        tmp_node.code  = cur;
        tmp_node.left  = i;
        if (siblings.size() != 0) 
          ((Node)siblings.get(siblings.size()-1)).right = i;
	  
        siblings.add(tmp_node);
      }

      prev = cur;
    }

    if (siblings.size() != 0)
      ((Node)siblings.get(siblings.size()-1)).right = parent.right;

    return siblings.size();
  }

  int insert (Vector siblings)
  {
    int begin       = 0;
    int pos         = _max (((Node)siblings.get(0)).code + 1, 
                            (ArrayType)next_check_pos) - 1;
    int nonzero_num = 0;
    int first       = 0;

    while (true) {
      pos++;
      _check_size(pos);

      if (check(pos)!=0) {
        nonzero_num++;
        continue;
      } else if (first==0) {
        next_check_pos = pos;
        first = 1;
      }

      begin = pos - ((Node)siblings.get(0)).code;

      _check_size(begin + ((Node)siblings.get(siblings.size()-1)).code);

      if (used[begin]!=0) continue;

      boolean flag = false;

      for (int i = 1; i < siblings.size(); i++) {
        if (check(begin + ((Node)siblings.get(i)).code) != 0) {
          flag = true;
          break;
        }
      }
      if(!flag)break;
    }

    // -- Simple heuristics --
    // if the percentage of non-empty contents in check between the index
    // 'next_check_pos' and 'check' is grater than some constant value (e.g. 0.9),
    // new 'next_check_pos' index is written by 'check'.
    if (1.0 * nonzero_num/(pos - next_check_pos + 1) >= 0.95) next_check_pos = pos;
    used[begin] = 1;
    size = _max (size, 
           (int)begin + 
           ((Node)siblings.get(siblings.size()-1)).code + 1);
    for (int i = 0; i < siblings.size(); i++) {
      check (begin + ((Node)siblings.get(i)).code) = begin;
    }

    for (int i = 0; i < siblings.size(); i++) {
      Vector new_siblings = new Vector();
  
      if (fetch(((Node)siblings.get(i)), new_siblings)==0) {
        base (begin + (int)((Node)siblings.get(i)).code) = 
          (val!=null) ? 
          (DartsInt)(-val[((Node)siblings.get(i)).left]-1) 
          : (DartsInt)(-((Node)siblings.get(i)).left-1);
		
        if ((val != null) && 
          ((DartsInt)(-val[((Node)siblings.get(i)).left]-1) >= 0)) {
          log.error("negative value is assgined.");
          throw new RuntimeException("Fatal: negative value is assgined.");
        }

      } else {
        int ins = (int)insert(new_siblings);
        base(begin + ((Node)siblings.get(i)).code) = ins;
      }
    }

    return begin;
  }


  void clear ()
  {
    array      = null;
    used       = null;
    alloc_size = 0;
    size       = 0;
    no_delete  = 0;
  }
  
  int get_unit_size() { return 8; };
  
  int get_size() { return size; };

  int get_nonzero_size ()
  {
    int result = 0;
    for (int i = 0; i < size; i++)
      if (check(i)!=0) result++;
    return result;
  }

  /**
   *
   */
  public int build (NodeType    _str[][],
                    int         _len[],
                    ArrayType   _val[])

  {
    return build(_str, _len, _val, _str.length);
  }

  /**
   *
   */
  public int build (NodeType   _str[][],
                    int        _len[],
                    ArrayType  _val[],
                    int        size)
  {
    if (_str == null) return 0;
    if (_str.length != _val.length) {
      log.warn("index and text should be same size.");
      return 0;
    }

    str      = _str;
    len      = _len;
    str_size     = size;
    val      = _val;

    resize (1024 * 10);

    base(0) = 1;
    next_check_pos = 0;

    Node root_node = new Node();
    root_node.left  = 0;
    root_node.right = str_size;
    root_node.depth = 0;

    Vector siblings = new Vector();
    log.trace("---fetch---");
    fetch (root_node, siblings);
    log.trace("---insert---");
    insert (siblings);

    used  = null;

    return size;
  }

  public DartsInt search (NodeType key[], 
                          int pos,
                          int len)
  {
    if (len==0) len = key.length;

    ArrayType  b = base(0);
    ArrayType p;
    for ( int i = pos; i < len; i++) {
      p = b + (NodeType)(key[i]) + 1;
      if ((ArrayType)b == check(p)) b = base(p);
      else return -1;
    }

    p = b;
    ArrayType n = base(p);

    if ((ArrayType)b == check(p) && n < 0) return (int)(-n-1);
    return -1;
  }

  public int commonPrefixSearch (NodeType key[],
                                 ArrayType result[],
                                 int pos,
                                 int len)
  {
    if (len==0) len = key.length;

    ArrayType  b   = base(0);
    int        num = 0;
    ArrayType  n;
    ArrayType  p;

    for ( int i = pos; i < len; i++) {

      // ignore new line and line feed.
      // this may causes error when use latain character.
      //      if(key[i]=='\n'||key[i]=='\r')continue;

      p = b; // + 0;
      n = base(p);
      if ((ArrayType) b == check(p) && n < 0) {
        if(log.isTraceEnabled())
          log.trace("result["+num+"]="+(-n-1));
          if (num < result.length) {
        	result[num] = -n-1;
          } else {
           log.warn("result array size may not enough");
        }
        num++;
      }

      p = b + (NodeType)(key[i]) + 1;

      // following lines are temporary code to resolve OutOfArrayException.
      // TODO:fixme
      if ( (p<<1) > array.length) {
        log.warn("p range is over.");
        log.warn("(p<<1,array.length)=("+(p<<1)+","+array.length+")");
        return num;
      }
      // end of temporary code.
      
      if ((ArrayType) b == check(p)) {
        b = base(p);
      } else {
        return num;
      }
    }

    p = b;
    n = base(p);
    if ((ArrayType)b == check(p) && n < 0) {
      if(log.isTraceEnabled())
        log.trace("result["+num+"]="+(-n-1));
      if (num < result.length) {
        result[num] = -n-1;
      } else {
        log.warn("result array size may not enough");
      }
      num++;
    }

    return num;
  }

  public void save(String file) throws IOException {
    long start = System.currentTimeMillis();
    DataOutputStream out = new DataOutputStream
      (new BufferedOutputStream(new FileOutputStream(file)));
    int dsize = alloc_size << 1;
    for (int i=0; i < dsize; i++) {
      out.writeInt(array[i]);
    }
    out.close();
    log.info("save time = " + 
         (((double)(System.currentTimeMillis()- start))/1000) + "[s]");
  }


  // for debug 
  public static void dumpChar(char c[],String message) {
    System.err.println("message="+message);
    for (int i = 0;i<c.length;i++) {
      System.err.print(c[i]+",");
    }
    System.err.println();
  }

  public static void main(String args[]) {
    //    DoubleArrayTrie da = new DoubleArrayTrie();
    /*
    try {
      //    da.load("da.cha");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
    
    }
    try{
      byte b[] = args[0].getBytes("EUC_JP");
      char c[] = new char[b.length];
      for (int i=0; i<b.length; i++) {

        // cast byte to char
        c[i] = ((char)(((b[i])>=0)?(b[i]):(256+(char)b[i])));
      }
      int result[] = new int[5];
      System.out.println(da.commonPrefixSearch(c,result,0,0));
      for (int i=0; i<2; i++) {
        System.out.println(result[i]);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
    }
    */

  }
}



