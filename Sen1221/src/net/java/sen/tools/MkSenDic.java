/*
 * MkChaDic.java - MkChaDic utility to make dictionary.
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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Vector;

import net.java.sen.CToken;
import net.java.sen.util.CSVData;
import net.java.sen.util.CSVParser;
import net.java.sen.util.DoubleArrayTrie;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MkSenDic {
	private static Log log = LogFactory.getLog(MkSenDic.class);

	/**
	 * Build sen dictionary.
	 * 
	 * @param args
	 *            custom dictionary files. see dic/build.xml.
	 */
	public static void main(String args[]) {
		ResourceBundle rb = ResourceBundle.getBundle("dictionary");
		DictionaryMaker dm1 = new DictionaryMaker();
		DictionaryMaker dm2 = new DictionaryMaker();
		DictionaryMaker dm3 = new DictionaryMaker();

		// 1st field information of connect file.
		Vector rule1 = new Vector();

		// 2nd field information of connect file.
		Vector rule2 = new Vector();

		// 3rd field information of connect file.
		Vector rule3 = new Vector();

		// 4th field information of connect file.
		// this field shows cost of morpheme connection
		// [size3*(x3*size2+x2)+x1]
		// [size3*(Attr1*size2+Attr2)+Attl]
		short score[] = new short[20131];

		long start = System.currentTimeMillis();

		// /////////////////////////////////////////
		//
		// Step1. Loading connetion file.
		//
		log.info("(1/7): reading connection matrix ... ");
		try {
			log.info("connection file = "
					+ rb.getString("text_connection_file"));
			log.info("charset = " + rb.getString("dic.charset"));
			CSVParser csvparser = new CSVParser(new FileInputStream(
					rb.getString("text_connection_file")),
					rb.getString("dic.charset"));
			String t[];
			int line = 0;
			while ((t = csvparser.nextTokens()) != null) {
				if (t.length < 4) {
					log.warn("invalid line in "
							+ rb.getString("text_connection_file") + ":" + line);
					log.warn(rb.getString("text_connection_file")
							+ "may be broken.");
					break;
				}
				dm1.add(t[0]);
				rule1.add(t[0]);

				dm2.add(t[1]);
				rule2.add(t[1]);

				dm3.add(t[2]);
				rule3.add(t[2]);

				if (line == score.length) {
					score = resize(score);
				}

				score[line++] = (short) Integer.parseInt(t[3]);
			}

			// /////////////////////////////////////////
			//
			// Step2. Building internal dictionary
			//
			log.info("(2/7): building type dictionary ... ");
			dm1.build();
			dm2.build();
			dm3.build();

			// if you want check specified morpheme, you uncomment and modify
			// following line:
			/*
			 * System.out.print("22="); dm3.getById(22);
			 * System.out.print("368="); dm3.getById(368);
			 * 
			 * System.out.println(dm3.getDicId("?????*,*,*,*,?"));
			 * DictionaryMaker.debug = true;
			 * System.out.println(dm3.getDicId("?????*,*,*,*,?"));
			 * System.out.println(dm3.getDicIdNoCache("?????*,*,*,*,?"));
			 */

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		// -------------------------------------------------

		int size1 = dm1.size();
		int size2 = dm2.size();
		int size3 = dm3.size();
		int ruleSize = rule1.size();
		short matrix[] = new short[size1 * size2 * size3];
		short default_cost = (short) Integer.parseInt(rb
				.getString("default_connection_cost"));

		// /////////////////////////////////////////
		//
		// Step3. Writing Connection Matrix
		//
		log.info("(3/7): writing conection matrix (" + size1 + " x " + size2
				+ " x " + size3 + " = " + size1 * size2 * size3 + ") ...");

		for (int i = 0; i < (int) (size1 * size2 * size3); i++)
			matrix[i] = default_cost;

		for (int i = 0; i < ruleSize; i++) {
			Vector r1 = dm1.getRuleIdList((String) rule1.get(i));
			Vector r2 = dm2.getRuleIdList((String) rule2.get(i));
			Vector r3 = dm3.getRuleIdList((String) rule3.get(i));

			for (Iterator i1 = r1.iterator(); i1.hasNext();) {
				int ii1 = ((Integer) i1.next()).intValue();
				for (Iterator i2 = r2.iterator(); i2.hasNext();) {
					int ii2 = ((Integer) i2.next()).intValue();
					for (Iterator i3 = r3.iterator(); i3.hasNext();) {
						int ii3 = ((Integer) i3.next()).intValue();
						int pos = size3 * (size2 * ii1 + ii2) + ii3;
						matrix[pos] = score[i];
					}
				}
			}
		}

		try {
			DataOutputStream out = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(
							rb.getString("matrix_file"))));
			out.writeShort(size1);
			out.writeShort(size2);
			out.writeShort(size3);
			for (int i1 = 0; i1 < size1; i1++)
				for (int i2 = 0; i2 < size2; i2++)
					for (int i3 = 0; i3 < size3; i3++) {
						out.writeShort(matrix[size3 * (size2 * i1 + i2) + i3]);
						// if (matrix[size3 * (size2 * i1 + i2) + i3] !=
						// default_cost) {
						// }
					}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		matrix = null;
		score = null;

		// -------------------------------------------------

		int pos_start = Integer.parseInt(rb.getString("pos_start"));
		int pos_size = Integer.parseInt(rb.getString("pos_size"));

		int di = 0;
		int offset = 0;
		ArrayList dicList = new ArrayList();

		// /////////////////////////////////////////
		//
		// Step4. Reading Morpheme Information
		//
		log.info("(4/7): reading morpheme information ... ");
		String t = null;
		String[] csv = null;
		try {
			// writer for feature file.
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(rb.getString("pos_file")),
					rb.getString("sen.charset")));

			log.info("load dic: " + rb.getString("text_dic_file"));
			BufferedReader dicStream = null;
			int custom_dic = -1;
			if (args.length == 0) {
				dicStream = new BufferedReader(new InputStreamReader(
						new FileInputStream(rb.getString("text_dic_file")),
						rb.getString("dic.charset")));
			} else {
				custom_dic = 0;
				dicStream = new BufferedReader(new InputStreamReader(
						new FileInputStream(args[custom_dic]),
						rb.getString("dic.charset")));
			}

			int line = 0;

			CSVData key_b = new CSVData();
			CSVData pos_b = new CSVData();

			while (true) {
				t = dicStream.readLine();
				if (t == null) {
					dicStream.close();
					custom_dic++;
					if (args.length == custom_dic) {
						break;
					} else {
						// read custum dictionary
						log.info("load dic: " + "args[custum_dic]");
						dicStream = new BufferedReader(new InputStreamReader(
								new FileInputStream(args[custom_dic]),
								rb.getString("dic.charset")));
					}
					continue;
				}

				CSVParser parser = new CSVParser(t);
				csv = parser.nextTokens();
				if (csv.length < (pos_size + pos_start)) {
					throw new RuntimeException("format error:" + t);
				}

				key_b.clear();
				pos_b.clear();
				for (int i = pos_start; i < (pos_start + pos_size - 1); i++) {
					key_b.append(csv[i]);
					pos_b.append(csv[i]);
				}

				key_b.append(csv[pos_start + pos_size - 1]);
				pos_b.append(csv[pos_start + pos_size - 1]);

				for (int i = pos_start + pos_size; i < (csv.length - 1); i++) {
					pos_b.append(csv[i]);
				}
				pos_b.append(csv[csv.length - 1]);

				CToken token = new CToken();

				token.rcAttr2 = (short) dm1.getDicId(key_b.toString());
				token.rcAttr1 = (short) dm2.getDicId(key_b.toString());
				token.lcAttr = (short) dm3.getDicId(key_b.toString());
				token.posid = 0;
				token.posID = offset;
				token.length = (short) csv[0].length();
				token.cost = (short) Integer.parseInt(csv[1]);

				dicList.add(new PairObject(csv[0], token));

				byte b[] = pos_b.toString().getBytes(
						rb.getString("sen.charset"));
				offset += (b.length + 1);
				String pos_b_str = pos_b.toString();
				bw.write(pos_b_str, 0, pos_b_str.length());
				// bw.write(b, 0, b.length);
				bw.write(0);
				if (++di % 50000 == 0)
					log.info("" + di + "... ");
			}
			bw.close();
			// ----end of writing feature.cha ----
		} catch (Exception e) {
			log.error("Error: " + t);
			e.printStackTrace();
			System.exit(1);
		}

		rule1 = null;
		rule2 = null;
		rule3 = null;

		// /////////////////////////////////////////
		//
		// Step5. Sort lexs and write to file
		//
		log.info("(5/7): sorting lex... ");

		int value[] = new int[dicList.size()];
		char key[][] = new char[dicList.size()][];
		int spos = 0;
		int dsize = 0;
		int bsize = 0;
		String prev = "";
		Collections.sort(dicList);

		// /////////////////////////////////////////
		//
		// Step6. Writing Token Information
		//
		log.info("(6/7): writing token... ");
		try {
			// writer for token file.
			DataOutputStream out = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(
							rb.getString("token_file"))));

			// writing 'bos' and 'eos' and 'unknown' token.
			CToken token = new CToken();
			token.rcAttr2 = (short) dm1.getDicId(rb.getString("bos_pos"));
			token.rcAttr1 = (short) dm2.getDicId(rb.getString("bos_pos"));
			token.lcAttr = (short) dm3.getDicId(rb.getString("bos_pos"));
			token.write(out);

			token.rcAttr2 = (short) dm1.getDicId(rb.getString("eos_pos"));
			token.rcAttr1 = (short) dm2.getDicId(rb.getString("eos_pos"));
			token.lcAttr = (short) dm3.getDicId(rb.getString("eos_pos"));
			token.write(out);

			token.rcAttr2 = (short) dm1.getDicId(rb.getString("unknown_pos"));
			token.rcAttr1 = (short) dm2.getDicId(rb.getString("unknown_pos"));
			token.lcAttr = (short) dm3.getDicId(rb.getString("unknown_pos"));
			token.posID = -1;
			token.write(out);
			log.info("key size = " + key.length);
			for (int i = 0; i < key.length; i++) {
				String k = (String) ((PairObject) dicList.get(i)).key;
				if (!prev.equals(k) && i != 0) {
					key[dsize] = ((String) ((PairObject) dicList.get(spos)).key)
							.toCharArray();
					value[dsize] = bsize + (spos << 8);
					dsize++;
					bsize = 1;
					spos = i;
				} else {
					bsize++;
				}
				prev = (String) ((PairObject) dicList.get(i)).key;
				((CToken) (((PairObject) dicList.get(i)).value)).write(out);
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		key[dsize] = ((String) ((PairObject) dicList.get(spos)).key)
				.toCharArray();

		value[dsize] = bsize + (spos << 8);
		dsize++;

		dm1 = null;
		dm2 = null;
		dm3 = null;
		dicList = null;

		// /////////////////////////////////////////
		//
		// Step7. Build Double Array
		//
		log.info("(7/7): building Double-Array (size = " + dsize + ") ...");

		DoubleArrayTrie da = new DoubleArrayTrie();

		da.build(key, null, value, dsize);
		try {
			da.save(rb.getString("double_array_file"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("total time = " + (System.currentTimeMillis() - start) / 1000
				+ "[ms]");
	}

	private static short[] resize(short s[]) {
		short newbuf[] = new short[(int) (s.length * 1.5)];
		for (int i = 0; i < s.length; i++) {
			newbuf[i] = s[i];
		}
		return newbuf;
	}
}
