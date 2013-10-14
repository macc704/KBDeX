/*
 * StringTagger.java - generate tag from string.
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
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 */
package net.java.sen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.java.sen.processor.PostProcessor;
import net.java.sen.processor.PreProcessor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class generate morpheme tags from String. Sample code is here:
 * 
 * <pre>
 * StringTagger tagger = StringTagger.getInstance(&quot;/usr/local/sen/conf/sen.xml&quot;);
 * Token[] token = tagger.analyze(s);
 * for (int i = 0; i &lt; token.length; i++) {
 * 	Token t = token[i];
 * 	String pos = t.getPos(); // part of speech
 * 	String basic = t.getBasic(); // un-conjugate representation
 * 	String reading = t.getReading(); // reading 
 * }
 * </pre>
 */
public class StringTagger {
	private static Log log = LogFactory.getLog(StringTagger.class);
	private static HashMap hash = new HashMap();
	private Viterbi viterbi = null;
	Token[] token = null;
	int cnt = 0;
	@SuppressWarnings("unused")
	private static StringTagger tagger = null;
	private static String DEFAULT_CONFIG = "conf/sen.xml";
	protected String unknownPos = null;

	private List preProcessorList = new ArrayList();
	private List postProcessorList = new ArrayList();

	// configuration file
	String tokenFile = null;
	String doubleArrayFile = null;
	String posInfoFile = null;
	String connectFile = null;

	String charset = null;
	String tokenizerClass = "net.java.sen.ja.JapaneseTokenizer";

	/**
	 * Construct new StringTagger.
	 * 
	 * StringTagger instance is exsisted only one for each configuration file.
	 * 
	 * @param senConfig
	 *            configuration file for sen.
	 */
	private StringTagger(String senConfig) throws IOException,
			IllegalArgumentException {
		this.init(senConfig);
	}

	public static StringTagger getInstance() throws IllegalArgumentException,
			IOException {
		return getInstance(System.getProperty("sen.home")
				+ System.getProperty("file.separator") + DEFAULT_CONFIG);
	}

	/**
	 * Obtain StringTagger instance for specified locale.
	 * 
	 * @deprecated use instead of StringTagger#getinstance(String senConfig)
	 * @param locale
	 *            Locale to generate morphological analyzer.
	 */
	public static StringTagger getInstance(Locale locale) throws IOException,
			IllegalArgumentException {
		if (locale.equals(Locale.JAPANESE)) {
			return getInstance();
		} else {
			throw new IllegalArgumentException("Locale '"
					+ locale.getDisplayName() + "' isn't supported.");
		}
	}

	/**
	 * Obtain StringTagger instance for with specified configuration.
	 * 
	 * @param senConfig
	 *            configuration file for sen.(ex. "SEN_HOME/conf/sen.xml").
	 * @return StringTagger instance. StringTagger is generated for each
	 *         configuration file. If configuration file is same, reutrn same
	 *         instance.
	 */
	public static synchronized StringTagger getInstance(String senConfig)
			throws IOException, IllegalArgumentException {
		Object tagger = hash.get(senConfig);
		if (tagger == null) {
			tagger = (Object) new StringTagger(senConfig);
			hash.put(senConfig, tagger);
			return (StringTagger) tagger;
		} else {
			return (StringTagger) tagger;
		}
	}

	/**
	 * Initialize mophological analyzer.
	 */
	private void init(String confFile) throws IOException {
		readConfig(confFile);
		net.java.sen.Tokenizer tokenizer = null;
		try {
			Class c = Class.forName(tokenizerClass);
			Constructor cons = c.getConstructor(new Class[] { String.class,
					String.class, String.class, String.class, String.class });

			tokenizer = (net.java.sen.Tokenizer) cons.newInstance(new Object[] {
					tokenFile, doubleArrayFile, posInfoFile, connectFile,
					charset });
			// new net.java.sen.ja.JapaneseTokenizer(
			// tokenFile, doubleArrayFile, posInfoFile, connectFile, charset);
		} catch (Exception ce) {
			throw new IllegalArgumentException("Tokenizer Class: "
					+ tokenizerClass + " is invalid.");
		}
		viterbi = new Viterbi(tokenizer);
	}

	/**
	 * Analyze string.
	 * 
	 * @param input
	 *            string to analyze.
	 * @return token array which represents morphemes.
	 */
	public synchronized Token[] analyze(String input) throws IOException {
		if (log.isDebugEnabled()) {
			log.debug("analyzer:" + input);
		}

		Map postProcessInfo = new HashMap();
		input = doPreProcess(input, postProcessInfo);

		int len = 0;
		Node node = viterbi.analyze(input.toCharArray()).next;
		Node iter = node;

		if (node == null)
			return null;

		while (iter.next != null) {
			len++;
			iter = iter.next;
		}

		token = new Token[len];

		int i = 0;
		while (node.next != null) {
			token[i] = new Token(node);

			if (token[i].getPos() == null) {
				token[i].setPos(unknownPos);
			}

			i++;
			node = node.next;
		}
		cnt = 0;

		token = doPostProcess(token, postProcessInfo);

		return token;
	}

	/**
	 * Get next morpheme.
	 * 
	 * @return next token. return null when next token doesn't exist.
	 */
	public Token next() {
		if (token == null && cnt == token.length)
			return null;
		return token[cnt++];
	}

	/**
	 * Check StringTagger have more morphemes or not.
	 * 
	 * @return true if StringTagger has more morphemes.
	 */
	public boolean hasNext() {
		if (token == null && cnt == token.length)
			return false;
		return true;
	}

	/**
	 * Read configuration file.
	 * 
	 * @param confFile
	 *            configuration file
	 */
	private void readConfig(String confFile) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			File cf = new File(confFile);
			String parent = cf.getParentFile().getParent();
			if (parent == null)
				parent = ".";
			Document doc = builder.parse(new InputSource(confFile));
			NodeList nl = doc.getFirstChild().getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				org.w3c.dom.Node n = nl.item(i);
				if (n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					String nn = n.getNodeName();
					String value = n.getFirstChild().getNodeValue();

					if (nn.equals("charset")) {
						charset = value;
					} else if (nn.equals("unknown-pos")) {
						unknownPos = value;
					} else if (nn.equals("tokenizer")) {
						tokenizerClass = value;
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

								if (dnn.equals("connection-cost")) {
									connectFile = SenUtils.getPath(dvalue,
											parent);
								} else if (dnn.equals("double-array-trie")) {
									doubleArrayFile = SenUtils.getPath(dvalue,
											parent);
								} else if (dnn.equals("token")) {
									tokenFile = SenUtils
											.getPath(dvalue, parent);
								} else if (dnn.equals("pos-info")) {
									posInfoFile = SenUtils.getPath(dvalue,
											parent);
								} else if (dnn.equals("compound")) {
									// do nothing
								} else {
									throw new IllegalArgumentException(
											"element '" + dnn + "' is invalid");
								}
							}
						}
					}
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

	/**
	 * Add PostProcessor.
	 * 
	 * @param processor
	 *            PostProcessor
	 */
	public void addPostProcessor(PostProcessor processor) {
		postProcessorList.add(processor);
	}

	/**
	 * Add PreProcessor.
	 * 
	 * @param processor
	 *            PreProcessor
	 */
	public void addPreProcessor(PreProcessor processor) {
		preProcessorList.add(processor);
	}

	/**
	 * Execute all registered preprocess.
	 * 
	 * @param input
	 *            input string
	 * @param postProcessInfo
	 *            information passed to postProcess
	 * @return preprocessed string
	 */
	protected String doPreProcess(String input, Map postProcessInfo) {
		Iterator itr = preProcessorList.iterator();
		String i = input;
		while (itr.hasNext()) {
			PreProcessor p = (PreProcessor) itr.next();
			i = p.process(i, postProcessInfo);
		}
		return i;
	}

	/**
	 * Execute all registered preprocess.
	 * 
	 * @param tokens
	 *            tokens
	 * @param postProcessInfo
	 *            information passed from preprocess
	 * @return postprocessed tokens
	 */
	protected Token[] doPostProcess(Token[] tokens, Map postProcessInfo) {
		Iterator itr = postProcessorList.iterator();
		Token[] newTokens = tokens;
		while (itr.hasNext()) {
			PostProcessor p = (PostProcessor) itr.next();
			newTokens = p.process(newTokens, postProcessInfo);
		}
		return newTokens;
	}
}
