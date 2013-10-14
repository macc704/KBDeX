/*
 * CompositPostProcessor.java - Composit postprocessor for tagger.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import net.java.sen.Token;

public class CompositPostProcessor implements PostProcessor {
	private List rules = new ArrayList();

	public void readRules(BufferedReader reader) throws IOException {
		String line = null;
		while ((line = reader.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line);
			if (!st.hasMoreTokens()) {
				continue;
			}

			Set ruleSet = new HashSet();
			String first = st.nextToken();
			if (!st.hasMoreTokens()) {
				// if the count of that is one, it is not only
				// renketsu-hinnshimei, but kousei-hinshimei.
				removeFromOtherRules(first);
				ruleSet.add(first);
				rules.add(new Rule(first, ruleSet));
				continue;
			}

			while (st.hasMoreTokens()) {
				String pos = st.nextToken();
				removeFromOtherRules(pos);
				ruleSet.add(pos);
			}
			rules.add(new Rule(first, ruleSet));
		}
	}

	private void removeFromOtherRules(String pos) {
		for (int i = 0; i < rules.size(); i++) {
			Rule rule = (Rule) rules.get(i);
			if (rule.contains(pos)) {
				rule.remove(pos);
				return;
			}
		}
	}

	public List getRules() {
		return rules;
	}

	public Token[] process(Token[] tokens, Map postProcessInfo) {
		if (tokens.length == 0) {
			return tokens;
		}

		List newTokens = new ArrayList();
		Token prevToken = null;
		Rule currentRule = null;
		outer_loop: for (int i = 0; i < tokens.length; i++) {
			if (currentRule != null) {
				if (prevToken.end() != tokens[i].start()
						|| !currentRule.contains(tokens[i].getPos())) {
					currentRule = null;
					newTokens.add(prevToken);
					prevToken = null;
				} else {
					merge(prevToken, tokens[i], currentRule.getPos());
					if (i == tokens.length - 1) {
						newTokens.add(prevToken);
						prevToken = null;
					}
					continue;
				}
			}
			for (int j = 0; j < rules.size(); j++) {
				Rule rule = (Rule) rules.get(j);
				if (rule.contains(tokens[i].getPos())) {
					currentRule = rule;
					prevToken = tokens[i];
					continue outer_loop;
				}
			}
			currentRule = null;
			newTokens.add(tokens[i]);
		}
		if (prevToken != null) {
			newTokens.add(prevToken);
		}
		Token[] newTokenArray = new Token[newTokens.size()];
		newTokens.toArray(newTokenArray);
		return newTokenArray;
	}

	private void merge(Token prev, Token current, String newPos) {
		if (prev == null) {
			return;
		}

		prev.setBasicString(prev.getBasicString() + current.getBasicString());
		prev.setCost(prev.getCost() + current.getCost());
		prev.setPos(newPos);
		prev.setPronunciation(prev.getPronunciation()
				+ current.getPronunciation());
		prev.setReading(prev.getReading() + current.getReading());
		prev.setLength(prev.length() + current.length());
		prev.setSurface(null);
	}

	class Rule {
		private String pos;
		private Set ruleSet;

		public Rule(String pos, Set ruleSet) {
			this.pos = pos;
			this.ruleSet = ruleSet;
		}

		public String getPos() {
			return pos;
		}

		public boolean contains(String pos) {
			return ruleSet.contains(pos);
		}

		public void remove(String pos) {
			ruleSet.remove(pos);
		}

		public String toString() {
			StringBuffer buf = new StringBuffer(pos);
			Iterator itr = ruleSet.iterator();
			while (itr.hasNext()) {
				buf.append(" ").append((String) itr.next());
			}
			return new String(buf);
		}
	}
}