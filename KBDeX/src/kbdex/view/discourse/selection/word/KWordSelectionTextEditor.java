/*
 * KWordSelectionTextEditor.java
 * Created on Apr 11, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.discourse.selection.word;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import clib.common.string.CStringChopper;
import clib.common.string.CStringCleaner;
import clib.view.editor.CSimpleTextEditor;

/**
 * @author macchan
 *
 */
public class KWordSelectionTextEditor extends CSimpleTextEditor {

	private static final long serialVersionUID = 1L;

	public KWordSelectionTextEditor() {
	}

	/**
	 * @param words
	 */
	public void addWords(List<String> words) {
		List<String> newWords = new ArrayList<String>(words);
		Text text = new Text(getEditorText());

		for (String word : words) {
			for (Line line : text.lines) {
				if (word.equals(line.getText())) {
					line.setComment(false);
					newWords.remove(line.getText());
				}
			}
		}

		for (String word : newWords) {
			text.addLine(word);
		}

		setEditorText(text.toString());
	}

	/**
	 * @param words
	 */
	public void removeWords(List<String> words) {
		Text text = new Text(getEditorText());

		for (String word : words) {
			for (Line line : text.lines) {
				if (word.equals(line.getText())) {
					line.setComment(true);
				}
			}
		}

		setEditorText(text.toString());
	}

	class Text {
		public static final String CR = "\n";

		public List<Line> lines = new ArrayList<Line>();

		public Text(String text) {
			StringTokenizer tokenizer = new StringTokenizer(getEditorText(), CR);
			while (tokenizer.hasMoreTokens()) {
				addLine(tokenizer.nextToken());
			}
		}

		/**
		 * @param word
		 */
		public void addLine(String text) {
			lines.add(new Line(text));
		}

		public String toString() {
			StringBuffer buf = new StringBuffer();
			for (Line line : lines) {
				buf.append(line);
				buf.append(CR);
			}
			String text = buf.toString();
			text = CStringChopper.chopped(text);
			return text;
		}
	}

	class Line {
		private String text;
		private boolean comment = false;

		public Line(String text) {
			if (text.startsWith("#")) {
				comment = true;
				this.text = CStringCleaner.cleaning(text.substring(1));
			} else {
				comment = false;
				this.text = text;
			}
		}

		public String getText() {
			return text;
		}

		public boolean isComment() {
			return comment;
		}

		public void setComment(boolean comment) {
			this.comment = comment;
		}

		public String toString() {
			if (comment) {
				return "#" + text;
			} else {
				return text;
			}
		}
	}
}
