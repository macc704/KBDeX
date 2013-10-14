/*
 * JapaneseTokenizer.java - detect japanese character
 * 
 * Copyright (C) 2001, 2002 Takashi Okamoto Takashi Okamoto <tora@debian.org>
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

package net.java.sen.ja;

import java.io.IOException;

import net.java.sen.CToken;
import net.java.sen.Node;
import net.java.sen.Tokenizer;

public class JapaneseTokenizer extends Tokenizer {
  static final int OTHER = 0x80;
  static final int SPACE = 0x81;
  static final int KANJI = 0x82;
  static final int KATAKANA = 0x83;
  static final int HIRAGANA = 0x84;
  static final int HALF_WIDTH = 0x85;

  public JapaneseTokenizer(String tokenFile, String doubleArrayFile,
      String posInfoFile, String connectFile, String charset)
      throws IOException {
    super(tokenFile, doubleArrayFile, posInfoFile, connectFile, charset);
  }

  public int getCharClass(char c) {
    Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
    int type = Character.getType(Character.toLowerCase(c));

    /*
     * if (type == Character.CONNECTOR_PUNCTUATION || type ==
     * Character.DASH_PUNCTUATION || type == Character.OTHER_PUNCTUATION || type ==
     * Character.INITIAL_QUOTE_PUNCTUATION || type ==
     * Character.FINAL_QUOTE_PUNCTUATION || type == Character.START_PUNCTUATION ||
     * type == Character.END_PUNCTUATION) { System.out.println("punctuation char =
     * "+c); System.out.println("type = " + type); return OTHER; }
     */

    if (ub == Character.UnicodeBlock.BASIC_LATIN) {
      if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
        return SPACE;
      }
      return type;
    } else if (ub == Character.UnicodeBlock.HIRAGANA) {
      return HIRAGANA;
    } else if (ub == Character.UnicodeBlock.KATAKANA
        && type != Character.CONNECTOR_PUNCTUATION) {
      return KATAKANA;
    } else if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
      return KANJI;
    } else if (ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
      return HALF_WIDTH;
    }
    return OTHER;
  }

  public Node lookup(char c[], int begin) throws IOException {
    Node resultNode = null;
    int char_class[] = new int[1];
    int end = c.length;
    // TO: skip space. tab and new line.
    // This function should be moved into Processor.
    int begin2 = skipCharClass(c, begin, end, SPACE, char_class);
//    int begin2 = begin;
    if (begin2 == end) {
      // return null when all of remain characters are skipped
      // characters.
      return null;
    }

    CToken t[] = dic.commonPrefixSearch(c, begin2);
    for (int i = 0; t[i] != null; i++) {
      Node newNode = getNewNode();
      newNode.token = t[i];
      newNode.length = t[i].length;
      newNode.surface = c;
      newNode.begin = begin2;
      newNode.end = begin2 - begin + t[i].length;
      newNode.rnext = resultNode;

      // if you want to see mophem type for all candidate word,
      // remove this comment:

      // newNode.feature = dic.getPosInfo(t[i].feature);

      resultNode = newNode;
    }

    if ((resultNode != null)
        && (char_class[0] == HIRAGANA || char_class[0] == KANJI))
      return resultNode;

    int begin3;
    switch (char_class[0]) {
      case HIRAGANA :
      case KANJI :
      case OTHER :
        begin3 = begin2 + 1; // single char is defined as UNKNOWN
        break;
      default :
        begin3 = skipCharClass(c, begin2 + 1, end, char_class[0]); // group by
                                                                   // same class
        break;
    }

    Node newNode = getNewNode();
    newNode.token = unknownToken;
    newNode.surface = c;
    newNode.begin = begin2;
    newNode.length = begin3 - begin2;
    newNode.end = begin3 - begin;
    newNode.termInfo = null;
    newNode.rnext = resultNode;

    return newNode;
  }
}

