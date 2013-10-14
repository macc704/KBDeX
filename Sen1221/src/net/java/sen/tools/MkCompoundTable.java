/*
 * MkCompoundTable.java - MkCompoundTable utility to make compound word table.
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

package net.java.sen.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.ResourceBundle;

import net.java.sen.util.CSVParser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MkCompoundTable {
	private static Log log = LogFactory.getLog(MkCompoundTable.class);

	/**
	 * Build compound word table.
	 */
	public static void main(String args[]) {
		ResourceBundle rb = ResourceBundle.getBundle("dictionary");
	    int pos_start = Integer.parseInt(rb.getString("pos_start"));
	    int pos_size = Integer.parseInt(rb.getString("pos_size"));
	    
		try {
			log.info("reading compound word information ... ");
			HashMap compoundTable = new HashMap();
			
			log.info("load dic: " + rb.getString("compound_word_file"));
			BufferedReader dicStream = new BufferedReader(
					new InputStreamReader(new FileInputStream(rb
							.getString("compound_word_file")), rb
							.getString("dic.charset")));

			String t;
			int line = 0;

			StringBuffer pos_b = new StringBuffer();
			while ((t = dicStream.readLine()) != null) {
				CSVParser parser = new CSVParser(t);
				String csv[] = parser.nextTokens();
				if (csv.length < (pos_size + pos_start)) {
					throw new RuntimeException("format error:" + line);
				}

				pos_b.setLength(0);
				for (int i = pos_start; i < (pos_start + pos_size - 1); i++) {
					pos_b.append(csv[i]);
					pos_b.append(',');
				}

				pos_b.append(csv[pos_start + pos_size - 1]);
				pos_b.append(',');

				for (int i = pos_start + pos_size; i < (csv.length - 2); i++) {
					pos_b.append(csv[i]);
					pos_b.append(',');
				}
				pos_b.append(csv[csv.length - 2]);
	        	compoundTable.put(pos_b.toString(), csv[csv.length-1]);
			}
			dicStream.close();
			log.info("done.");
			log.info("writing compound word table ... ");
			ObjectOutputStream os = new ObjectOutputStream(
					new FileOutputStream(rb.getString("compound_word_table")));
			os.writeObject(compoundTable);
			os.close();
			log.info("done.");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}