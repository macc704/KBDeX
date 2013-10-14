/*
 * DictionaryMaker.java - DictionaryMaker utility to make dictionary.
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

package net.java.sen.tools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;
import java.util.Vector;

import net.java.sen.util.CSVParser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DictionaryMaker {
  private static Log log = LogFactory.getLog(DictionaryMaker.class);

  public static boolean debug = false;
  // variation information is stored.
  LinkedHashSet ruleSet = new LinkedHashSet();
  // idList[ID] = ????
  public Vector idList = new Vector();

  // ruleList[ID] = splited valiation information.
  Vector ruleList = new Vector();

  // dic2IdHash('word type')= id for word type
  HashMap dic2IdHash = new HashMap();

  // dic2IdHash(valiation) = id for word type
  HashMap rule2IdHash = new HashMap();

  // set flag when last field of valiation isn't '*'
  HashMap isLexcalized = new HashMap();

  public void add(String rule) {
    ruleSet.add(rule);
  }

  public void build() {
    int size = 0;
    // iterate variation
    for (Iterator i = ruleSet.iterator(); i.hasNext();) {
      ruleList.setSize(size + 1);
      String str = (String) i.next();
      rule2IdHash.put(str, new Integer(size));

      // tokenList: split valiation information.
      StringTokenizer st = new StringTokenizer(str, ",");
      int len = st.countTokens();
      String tokenList[] = new String[len];
      for (int j = 0; j < len; j++) {
        tokenList[j] = st.nextToken();
      }

      ruleList.set(size, tokenList);
      //      System.out.println("tokenList="+tokenList[len-1]);
      if (tokenList[len - 1].charAt(0) != '*')
        isLexcalized.put(tokenList[len - 1], "1");
      size++;
    }
    //    System.out.println("size="+size);
    //    System.out.println("ruleList size="+ruleList.size());
    //    System.out.println("test");

    ruleSet.clear();

    idList.setSize(ruleList.size());
    for (int i = 0; i < ruleList.size(); i++) {
      Vector v = new Vector();
      idList.set(i, v);

      getIdList((String[]) ruleList.get(i), (Vector) idList.get(i), 0);
    }
  }

  int getIdList(String csv[], Vector result, int parent) {
    result.setSize(ruleList.size());

    for (int j = 0; j < ruleList.size(); j++)
      result.set(j, new Integer(j));
    //    System.out.println("in:ruleList.size()=" + ruleList.size());
    //    System.out.println("ruleList size="+ruleList.size());
    //    System.out.println("result size="+result.size());
    //    pass

    for (int j = 0; j < csv.length; j++) {
      int k = 0;
      for (int n = 0; n < result.size(); n++) {
        int i = ((Integer) result.get(n)).intValue();
        String rl_ij = ((String[]) ruleList.get(i))[j];
        if ((parent == 0 && csv[j].charAt(0) == '*')
            || (parent == 1 && rl_ij.charAt(0) == '*') || rl_ij.equals(csv[j])) {

          result.set(k++, result.get(n));
        }
      }
      result.setSize(k);
    }
    return result.size();
  }

  private int getDicIdNoCache(String csv[]) {
    Vector result = new Vector();

    getIdList(csv, result, 1);

    if (result.size() == 0) {
      log.error("can't find morpheme type");
      log.error("input string is here:");
      log.error("ruleList size=" + ruleList.size());

      StringBuffer buf = new StringBuffer();
      for (int i = 0; i < csv.length; i++) {
        buf.append(csv[i]);
        buf.append(",");

      }
      log.error(buf);
      return -1;
    }

    int priority[] = new int[result.size()];
    int max = 0;
    for (int i = 0; i < result.size(); i++) {
      String v[] = (String[]) ruleList
          .get(((Integer) result.get(i)).intValue());
      for (int j = 0; j < v.length; j++) {
        if (v[j].charAt(0) != '*')
          priority[i]++;
      }
      if (priority[max] < priority[i])
        max = i;
      log.debug("detect==");
      log.debug(getById(((Integer) result.get(max)).intValue()));
    }
    return ((Integer) result.get(max)).intValue();
  }

  public int size() {
    return ruleList.size();
  }

  public int getDicId(String rule) {
  	CSVParser parser = new CSVParser(rule);
  	String csv[] = null;
  	try {
  		csv = parser.nextTokens();
  	} catch (IOException e) {
  		throw new RuntimeException(e);
  	}
  	String lex = csv[csv.length - 1];
  	if (isLexcalized.get(lex) != null) {
  		int ret = getDicIdNoCache(csv);
      return ret;
  	} else {
      String pos = removeEndField(rule);

      Object r = dic2IdHash.get(pos);
      if (r != null && ((Integer) r).intValue() != 0) {

        int ret = ((Integer) r).intValue() - 1;

        return ret; // 0 if empty
      }

      int rg = getDicIdNoCache(csv);

      log.debug("" + ruleList.size() + ":dic2IdHash(" + pos + ")=" + (rg + 1));

      dic2IdHash.put(pos, new Integer(rg + 1));
      return rg;
    }
  }

  Vector getRuleIdList(String rule) {
    return (Vector) idList.get(((Integer) rule2IdHash.get(rule)).intValue());
  }

  /*
  public static String[] csv2strings(String csv) {
    StringTokenizer st = new StringTokenizer(csv, ",");
    int len = st.countTokens();
    String tokenList[] = new String[len];
    for (int i = 0; i < len; i++) {
      tokenList[i] = st.nextToken();
    }
    return tokenList;
  }
  */

  private static String removeEndField(String str) {
    int field = 0;
    int last = 0;
    for (int i = 0; i < str.length(); i++) {
      if (str.charAt(i) == ',') {
        field++;
        last = i;
      }
    }
    return str.substring(0, last);
  }
  public String getById(int id) {
    String[] r = (String[]) ruleList.get(id);
    StringBuffer buf = new StringBuffer();
    if (r != null) {
      for (int i = 0; i < r.length; i++)
        buf.append(r[i] + ",");
      buf.append("\n");
    } else {
      buf.append("null");
    }
    return null;
  }
}