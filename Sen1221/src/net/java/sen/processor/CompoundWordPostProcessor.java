/*
 * CompoundWordPostProcessor.java - Compound word postprocessor for tagger.
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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.java.sen.Token;

public class CompoundWordPostProcessor implements PostProcessor {
	private HashMap compoundTable;

	public CompoundWordPostProcessor(String compoundFile) {
		try {
			ObjectInputStream is = new ObjectInputStream(new FileInputStream(compoundFile));
			compoundTable = (HashMap)is.readObject();
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public Token[] process(Token[] tokens, Map postProcessInfo) {
		if (tokens.length == 0) {
			return tokens;
		}

		List newTokens = new ArrayList();
		for (int i = 0; i < tokens.length; i++) {
			String compoundInfo = (String)compoundTable.get(tokens[i].getTermInfo());
			if (compoundInfo == null) {
				newTokens.add(tokens[i]);
				continue;
			}
			StringTokenizer st = new StringTokenizer(compoundInfo);
			int start = tokens[i].start();
			while (st.hasMoreTokens()) {
				String termInfo = st.nextToken();
				Token newToken = new Token();
				String surface = getField(termInfo, 0);
				newToken.setSurface(surface);

				StringBuffer pos = new StringBuffer(getField(termInfo, 2));
				String tmp = getField(termInfo, 3);
				if (!tmp.equals("*")) {
					pos.append("-").append(tmp);
				}
				tmp = getField(termInfo, 4);
				if (!tmp.equals("*")) {
					pos.append("-").append(tmp);
				}
				tmp = getField(termInfo, 5);
				if (!tmp.equals("*")) {
					pos.append("-").append(tmp);
				}
				newToken.setPos(new String(pos));
				newToken.setCform(getField(termInfo, 7));
                newToken.setBasicString(getField(termInfo, 8));
				newToken.setReading(getField(termInfo, 9));
                newToken.setPronunciation(getField(termInfo, 10));
                newToken.setTermInfo(termInfo);
				newToken.setCost(tokens[i].getCost());
				if (getField(termInfo, 11).equals("-")) {
					newToken.setAddInfo("p=" + tokens[i].getPos());
				} else {
					newToken.setAddInfo(getField(termInfo, 11));
				}
				newToken.setLength(surface.length());
				newToken.setStart(start);
				start += surface.length();

				newTokens.add(newToken);
			}
		}
		Token[] newTokenArray = new Token[newTokens.size()];
		newTokens.toArray(newTokenArray);
		return newTokenArray;
	}

	private int getFieldBegin(String termInfo, int pos){
		if (pos == 0) {
			return 0;
		}
		int cnt = 0;
		int ptr = 0;

		while(cnt < pos && ptr < termInfo.length()){
			if(termInfo.charAt(ptr++) == ',')cnt++;
		}

		if (cnt != pos) return -1;

		return ptr++;
	}

	private String getField(String termInfo, int pos) {
		int st = getFieldBegin(termInfo, pos);
		int ed = getFieldBegin(termInfo, pos+1);
		if (ed == -1 || ed == termInfo.length()) {
			ed = termInfo.length();
		} else {
			ed--;
		}
		return termInfo.substring(st, ed);
	}
}
