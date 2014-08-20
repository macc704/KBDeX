/*
 * KSenStringTagger.java
 * Created on Apr 12, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse.wordprocessing;

import net.java.sen.StringTagger;
import clib.view.dialogs.CErrorDialog;

/**
 * @author macchan
 *
 */
public class KSenStringTagger {

	private static StringTagger tagger;

	static {
		System.setProperty("sen.home", "../Sen1221/senhome-ipadic");
		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.defaultlog",
				"FATAL");
		try {
			tagger = StringTagger.getInstance();
		} catch (Exception ex) {
			CErrorDialog.show(null, "日本語辞書が見つかりません．日本語辞書付きバージョンをダウンロードしてください．",
					ex);
			ex.printStackTrace();
		}
	}

	public static StringTagger getTagger() {
		return tagger;
	}
}
