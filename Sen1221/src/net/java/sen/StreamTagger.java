/*
 * StreamTagger.java - generate tag from Stream.
 * 
 * Copyright (C) 2002 Takashi Okamoto
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
 * License along with Sen; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */
package net.java.sen;

import java.io.IOException;
import java.io.Reader;
import java.util.Locale;

import net.java.sen.processor.PostProcessor;
import net.java.sen.processor.PreProcessor;

/**
 * This class generate morpheme tags from Reader.
 * <pre>
 * BufferedReader br = 
 *    new BufferedReader(
 *        new InputStreamReader(
 *            new FileInputStream("input.txt"), "Windows-31J"));
 * 
 * StreamTagger tagger = new StreamTagger((Reader) br);
 * 
 * while (tagger.hasNext()) {
 *   Token token = tagger.next();
 *   System.out.println(token.toString()
 * 	                  + "\t(" +
 *                    + token.getBasicString()
 *                    + ")\t"
 *                    + token.getPos());
 * }
 * </pre>
 */
public class StreamTagger {
  private StringTagger stringTagger = null;
  private static final int BUFFER_SIZE = 256;
  private final char[] buffer = new char[BUFFER_SIZE];
  private int cnt = 0;
  private Token[] token;
  private boolean complete = false;
  private Reader reader;
  private int lastReadOffset;

  /**
   * Construct new StreamTagger. Currently only support Locale.JAPANESE.
   * 
   * @deprecated 
   * @param reader
   *          Reader to add tag.
   * @param locale
   *          Locale to generate morphological analyzer.
   */
  public StreamTagger(Reader reader, Locale locale) throws IOException,
  IllegalArgumentException {
      stringTagger = StringTagger.getInstance(locale);
      this.reader = reader;
      this.lastReadOffset = 0;
  }

  /**
   * Construct new StreamTagger.
   */
  public StreamTagger(Reader reader) throws IOException,
     IllegalArgumentException {
      stringTagger = StringTagger.getInstance();
      this.reader = reader;
      this.lastReadOffset = 0;
  }

  /**
   * Construct new StreamTagger.
   */
  public StreamTagger(Reader reader, String senConfig) throws IOException,
  	IllegalArgumentException {
      stringTagger = StringTagger.getInstance(senConfig);
      this.reader = reader;
      this.lastReadOffset = 0;
  }

  /**
   * Check have more morphemes or not.
   * 
   * @return true if StreamTagger has more morphemes.
   */
  public boolean hasNext() throws IOException {
    if (token == null || token.length == cnt) {
      int i;

      do {
        if ((i = readToBuffer()) == -1)
          return false;
        token = stringTagger.analyze(new String(buffer, 0, i));
      } while (token == null);
      cnt = 0;

      // Update the start offsets
      if (token != null) {
        for (int n = 0; n < token.length; n++) {
          token[n].setStart(token[n].start() + this.lastReadOffset);
        }
      }
      this.lastReadOffset += i;
    }

    // when this is happend?
    if (token.length == 0)
      return false;

    return true;
  }

  private int readToBuffer() throws IOException {
    int pos = 0;
    int res = 0;

    while ((pos < BUFFER_SIZE) && (!complete)
        && ((res = reader.read(buffer, pos, 1)) != -1)) {
	if ((Character.getType(buffer[pos]) == Character.OTHER_PUNCTUATION) &&
	    (pos > 0)) {
	    return pos + 1;
	} else {
	    pos++;
      }
    }

    if (res == -1) {
      complete = true;
    }

    if (complete && pos == 0) {
      return -1;
    }

    return pos;
  }

  /**
   * Get next morpheme.
   * 
   * @return morpheme
   * @throws IOException
   */
  public Token next() throws IOException {
    if (token == null || token.length == cnt) {
      int i;

      do {
        if ((i = readToBuffer()) == -1)
          return null;
        token = stringTagger.analyze(new String(buffer, 0, i));
      } while (token == null);
      cnt = 0;

      // Update the start offsets
      if (token != null) {
        for (int n = 0; n < token.length; n++) {
          token[n].setStart(token[n].start() + this.lastReadOffset);
        }
      }
      this.lastReadOffset += i;

    }
    return token[cnt++];
  }
  
  /**
   * Add PostProcessor.
   * @param processor PostProcessor
   */
  public void addPostProcessor(PostProcessor processor) {
  	stringTagger.addPostProcessor(processor);
  }
  
  /**
   * Add PreProcessor.
   * @param processor PreProcessor
   */
  public void addPreProcessor(PreProcessor processor) {
  	stringTagger.addPreProcessor(processor);
  }
}
