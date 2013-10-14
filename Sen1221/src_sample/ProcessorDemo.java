/*
 * StreamTaggerDemo2.java - StreamTaggerDemo2 is demonstration program for Sen.
 * 
 * Copyright (C) 2002 Takashi Okamoto, Tsuyoshi Fukui Takashi Okamoto
 * <tora@debian.org> Tsuyosh Fukui <fukui556@oki.com>
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.java.sen.SenUtils;
import net.java.sen.StreamTagger;
import net.java.sen.Token;
import net.java.sen.processor.CompositPostProcessor;
import net.java.sen.processor.CompoundWordPostProcessor;
import net.java.sen.processor.RemarkPostProcessor;
import net.java.sen.processor.RemarkPreProcessor;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ProcessorDemo {
	private static String compositRule = "";

	private static boolean isCompound = true;

	private static String compoundFile = null;

	private static String remarkRule = "";

	public static void main(String args[]) {
		try {
			System.setProperty("sen.home", "../Sen1221/senhome-ipadic");
			System.setProperty("org.apache.commons.logging.Log",
					"org.apache.commons.logging.impl.SimpleLog");
			System.setProperty(
					"org.apache.commons.logging.simplelog.defaultlog", "FATAL");

			if (args.length != 2) {
				System.err
						.println("usage: java ProcessorDemo <filename> <encoding>");
				System.exit(1);
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(args[0]), args[1]));
			String confPath = System.getProperty("sen.home")
					+ System.getProperty("file.separator")
					+ "conf/sen-processor.xml";
			StreamTagger tagger = new StreamTagger((Reader) br, confPath);
			readConfig(confPath);

			if (!isCompound) {
				CompoundWordPostProcessor cwProcessor = new CompoundWordPostProcessor(
						compoundFile);
				tagger.addPostProcessor(cwProcessor);
			}

			if (compositRule != null && !compositRule.equals("")) {
				CompositPostProcessor processor = new CompositPostProcessor();
				processor.readRules(new BufferedReader(new StringReader(
						compositRule)));
				tagger.addPostProcessor(processor);
			}

			if (remarkRule != null && !remarkRule.equals("")) {
				RemarkPreProcessor processor = new RemarkPreProcessor();
				processor.readRules(new BufferedReader(new StringReader(
						remarkRule)));
				tagger.addPreProcessor(processor);
				RemarkPostProcessor p2 = new RemarkPostProcessor();
				tagger.addPostProcessor(p2);
			}

			// BufferedReader is = new BufferedReader(System.in);

			while (tagger.hasNext()) {
				Token token = tagger.next();
				System.out.println(token.getSurface() + "\t" + token.getPos()
						+ "\t" + token.start() + "\t" + token.end() + "\t"
						+ token.getCost() + "\t" + token.getAddInfo());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void readConfig(String confFile) {
		String parent = new File(confFile).getParentFile().getParent();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(confFile));
			NodeList nl = doc.getFirstChild().getChildNodes();

			for (int i = 0; i < nl.getLength(); i++) {
				org.w3c.dom.Node n = nl.item(i);
				if (n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					String nn = n.getNodeName();
					String value = n.getFirstChild().getNodeValue();

					if (nn.equals("composit")) {
						compositRule += value + "\n";
					}
					if (nn.equals("compound")) {
						if (value.equals("\u69cb\u6210\u8a9e")) {
							isCompound = false;
						}
					}
					if (nn.equals("remark")) {
						remarkRule += value + "\n";
					}
					if (nn.equals("dictionary")) {
						// read nested tag in <dictinary>
						NodeList dnl = n.getChildNodes();
						for (int j = 0; j < dnl.getLength(); j++) {
							org.w3c.dom.Node dn = dnl.item(j);
							if (dn.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

								String dnn = dn.getNodeName();
								if (dn.getFirstChild() == null) {
									throw new IllegalArgumentException(
											"element '" + dnn + "' is empty");
								}
								String dvalue = dn.getFirstChild()
										.getNodeValue();

								if (dnn.equals("compound")) {
									compoundFile = SenUtils.getPath(dvalue,
											parent);
								}
							}
						}
					}
				}
			}
			if (!isCompound) {
				try {
					ObjectInputStream is = new ObjectInputStream(
							new FileInputStream(compoundFile));
					HashMap hashmap = (HashMap) is.readObject();
				} catch (ClassNotFoundException e1) {
					throw new RuntimeException(e1);
				}
			}
		} catch (ParserConfigurationException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (SAXException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

}