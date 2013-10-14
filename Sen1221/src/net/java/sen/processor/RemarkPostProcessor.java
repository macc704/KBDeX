/*
 * RemarkPreProcessor.java - remark postprocessor for tagger.
 * 
 * Copyright (C) 2004 Tsuyoshi Fukui
 * Tsuyoshi Fukui <fukui556@oki.com>
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

package net.java.sen.processor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.java.sen.Token;

public class RemarkPostProcessor implements PostProcessor {

    public Token[] process(Token[] tokens, Map postProcessInfo) {
        List tokenList = (List) postProcessInfo.get("remark");
        if (tokenList.size() == 0) {
            return tokens;
        }

        Token[] newTokens = new Token[tokens.length + tokenList.size()];
        if (tokens.length == 0) {
            tokenList.toArray(newTokens);
            return newTokens;
        }
        Iterator itr = tokenList.iterator();
        Token addToken = (Token) itr.next();
        int newTokenCount = 0;
        for (int i = 0; i < tokens.length; i++) {
            while (addToken != null && tokens[i].start() >= addToken.start()) {
                newTokens[newTokenCount++] = addToken;
                if (!itr.hasNext()) {
                    addToken = null;
                    break;
                }
                addToken = (Token) itr.next();
            }
            newTokens[newTokenCount++] = tokens[i];
        }
        if (addToken != null) {
            newTokens[newTokenCount++] = addToken;
            while (itr.hasNext()) {
                newTokens[newTokenCount++] = (Token) itr.next();
            }
        }
        return newTokens;
    }
}