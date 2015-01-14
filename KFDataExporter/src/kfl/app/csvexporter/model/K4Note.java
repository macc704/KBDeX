/*
 * KFNote.java
 * Created on Jul 13, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kfl.app.csvexporter.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author macchan
 * 
 */
public class K4Note extends K4Element {

	private static final long serialVersionUID = 1L;

	private String title;
	private String text;
	// private KFAuthor author;

	private K4Note buildson;
	private List<K4RiseAbove> riseaboves = new ArrayList<K4RiseAbove>();
	private Map<K4Support, K4TextLocator> supporteds = new LinkedHashMap<K4Support, K4TextLocator>();
	private Map<K4Note, K4TextLocator> references = new LinkedHashMap<K4Note, K4TextLocator>();
	private List<K4Keyword> keywords = new ArrayList<K4Keyword>();
	private List<Integer> offsets = new ArrayList<Integer>();

	public K4Note() {
	}

	@Override
	public String getType() {
		return "note";
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	// public KFAuthor getAuthor() {
	// return author;
	// }
	//
	// public void setAuthor(KFAuthor author) {
	// this.author = author;
	// }

	public void addRiseabove(K4RiseAbove riseabove) {
		riseaboves.add(riseabove);
	}

	public void addSupport(K4Support support, K4TextLocator locator) {
		supporteds.put(support, locator);
	}

	public void addReference(K4Note note, K4TextLocator locator) {
		references.put(note, locator);
	}

	public List<Integer> getOffsets() {
		return offsets;
	}

	public void setOffsets(List<Integer> offsets) {
		if (offsets == null) {
			return;
		}
		this.offsets = offsets;
	}

	public void addKeyword(K4Keyword keyword) {
		keywords.add(keyword);
	}

	// public boolean isContained(KFView view) {
	// return views.contains(view);
	// }

	// public boolean isContained(List<KFView> views) {
	// for (KFView view : views) {
	// if (isContained(view)) {
	// return true;
	// }
	// }
	// return false;
	//
	// }

	public void setBuildson(K4Note buildson) {
		this.buildson = buildson;
	}

	public K4Note getBuildson() {
		return buildson;
	}

	@Override
	public String toString() {
		return "((note)" + getTitle() + ")";
	}

	public static List<String> header() {
		return new ArrayList<String>(Arrays.asList("id", "crea", "modi",
				"titl", "text", "decoratedText", "buildons", "keywords",
				"supported", "references", "riseaboves", "views", "authors"));
	}

	public List<String> getStrings() {
		List<String> strings = new ArrayList<String>();
		addBasicStrings(strings);
		strings.add(getTitle());
		strings.add(getText());
		strings.add(getDecoratedText());
		if (getBuildson() != null) {
			strings.add(getBuildson().getIdAsString());
		} else {
			strings.add("");
		}
		strings.add(listToString("keywords", keywords));
		strings.add(listToString("supported", new ArrayList<K4Support>(
				supporteds.keySet())));
		strings.add(listToString("references",
				new ArrayList<K4Note>(references.keySet())));
		strings.add(listToString("riseaboves", riseaboves));
		strings.add(listToString("views", getViews()));
		strings.add(listToString("authors", getAuthors()));

		return strings;
	}

	private String getDecoratedText() {
		ReplacableString replacable = new ReplacableString(getText(), offsets);
		for (K4Support support : supporteds.keySet()) {
			K4TextLocator loca = supporteds.get(support);
			if (checkRange(loca.getOffset1()) == false
					|| checkRange(loca.getOffset2()) == false) {
				replacable.insertLast("{[" + support.getName() + "]:}");
				continue;
			}
			replacable.insert(loca.getOffset1(), "{[" + support.getName()
					+ "]:");
			replacable.insert(loca.getOffset2(), "}");
		}
		for (K4Note note : references.keySet()) {
			K4TextLocator loca = references.get(note);
			String textInsert = "[[" + loca.getText() + "][from "
					+ note.getIdAsString() + "]]";
			if (checkRange(loca.getOffset1()) == false) {
				replacable.insertLast(textInsert);
				continue;
			}
			replacable.insert(loca.getOffset1(), textInsert);
		}
		return replacable.getText();
	}

	private boolean checkRange(int offsetIndex) {
		if (offsetIndex >= offsets.size()) {
			// System.out.println("[offset index error] noteId=" + getId()
			// + ", title=" + getTitle() + ", offsetIndex=" + offsetIndex
			// + ", offsets=" + offsets);
			// System.err.flush();
			return false;
		}
		if (offsets.get(offsetIndex) > text.length()) {// = ok
			// System.out.println("[offset error] noteId=" + getId() +
			// ", title="
			// + getTitle() + ", offsetIndex=" + offsetIndex + ", offset="
			// + offsets.get(offsetIndex) + ", textlength="
			// + text.length());
			// System.err.flush();
			return false;
		}
		return true;
	}

	class ReplacableString {
		private StringBuffer text;
		private List<Offset> offsets = new ArrayList<Offset>();

		public ReplacableString(String text, List<Integer> ioffsets) {
			this.text = new StringBuffer(text);
			for (Integer ioffset : ioffsets) {
				offsets.add(new Offset(ioffset));
			}
		}

		public void insertLast(String textInsert) {
			text.append(textInsert);
		}

		public void insert(int offset, String textInsert) {
			try {
				int loc = offsets.get(offset).value;
				text.replace(loc, loc, textInsert);
				int size = offsets.size();
				int textlen = textInsert.length();
				for (int i = offset + 1; i < size; i++) {
					offsets.get(i).value = offsets.get(i).value + textlen;
				}
			} catch (Exception ex) {
				System.err.println("(Exception in replace())");
				return;
			}
		}

		// public String getText(int offset1, int offset2) {
		// try {
		// int loc1 = offsets.get(offset1).value;
		// int loc2 = offsets.get(offset2).value;
		// if (loc1 >= loc2) {
		// return "";
		// }
		// return text.substring(loc1, loc2);
		// } catch (Exception ex) {
		// return "(Exception in getText())";
		// }
		// }

		public String getText() {
			return text.toString();
		}

		class Offset {
			int value;

			public Offset(int value) {
				this.value = value;
			}

			@Override
			public String toString() {
				return "Offset(" + value + ")";
			}
		}
	}

	@Override
	public String getShortDescrption() {
		return getType() + "-" + getTitle();
	}

}
