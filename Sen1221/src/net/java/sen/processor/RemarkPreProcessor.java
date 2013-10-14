/*
 * RemarkPreProcessor.java - remark preprocessor for tagger.
 * 
 * Copyright (C) 2004 Tsuyoshi
 * Fukui Tsuyoshi Fukui <fukui556@oki.com>
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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.java.sen.Token;

public class RemarkPreProcessor implements PreProcessor {
    protected List ruleList = new ArrayList();

    public String process(String input, Map postProcessInfo) {
        List tokenList = new ArrayList();
        postProcessInfo.put("remark", tokenList);
        Iterator itr = ruleList.iterator();
        
        while (itr.hasNext()) {
            int count = 0;
            Rule rule = (Rule) itr.next();
            
            while (count < input.length()) {
                int start = -1;
                int end = -1;
                String tokenStr;
                
                start = input.indexOf(rule.start, count);
                if (start >= 0) {
                    count = start + rule.start.length();
                    if (rule.end.equals("")) {
                        end = start;
                    } else {
                        end = input.indexOf(rule.end, count);
                    }
                    if (end >= 0) {
                        if (rule.end.equals("")) {
                            end += rule.start.length();
                        } else {
                            end += rule.end.length();
                        }
                        count = end;
                        tokenStr = input.substring(start, end);
                        input = replaceToSpace(input, start, end);
                        Token token = new Token();
                        token.setPos(rule.pos);
                        token.setStart(start);
                        token.setLength(end - start);
                        token.setSurface(tokenStr);
                        token.setBasicString(tokenStr);
                        token.setPronunciation(tokenStr);
                        token.setReading(tokenStr);
                        token.setCost(0);
                        token.setAddInfo("");
                        token.setCform("*");
                        token.setTermInfo("");
                        addToTokenList(tokenList, token);
                    } else {
                        count = input.length();
                    }
                } else {
                    count = input.length();
                }
            }
        }
        return input;
    }

    public void readRules(BufferedReader reader) throws IOException {
        String line = null;
        
        while ((line = reader.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(line);
            if (!st.hasMoreTokens()) {
                continue;
            }

            Rule rule = new Rule();
            
            if (st.countTokens() == 2) {
                rule.start = st.nextToken();
                rule.end = "";
                rule.pos = st.nextToken();
            } else {
                rule.start = st.nextToken();
                rule.end = st.nextToken();
                rule.pos = st.nextToken();
            }
            
            ruleList.add(rule);
        }
    }

    protected String replaceToSpace(String input, int start, int end) {
        char[] c = input.toCharArray();
        for (int i = start; i < end; i++) {
            c[i] = ' ';
        }
        return new String(c);
    }

    protected void addToTokenList(List tokenList, Token token) {
        for (int i = 0; i < tokenList.size(); i++) {
            Token currentToken = (Token) tokenList.get(i);
            if (token.start() < currentToken.start()) {
                tokenList.add(i, token);
                return;
            }
        }
        tokenList.add(token);
    }

    class Rule {
        public String start;
        public String end;
        public String pos;
    }
}