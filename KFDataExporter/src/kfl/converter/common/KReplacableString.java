package kfl.converter.common;

import java.util.ArrayList;
import java.util.List;

public class KReplacableString {
	private String original;
	private List<Integer> orgOffsets;
	private StringBuffer textBuf;// current
	private List<Offset> offsets = new ArrayList<Offset>();// current

	public KReplacableString(String text, List<Integer> intOffsets) {
		this.original = text;
		this.orgOffsets = intOffsets;
		this.textBuf = new StringBuffer(text);
		for (Integer iOffset : intOffsets) {
			offsets.add(new Offset(iOffset));
		}
	}

	public void insertLast(String textInsert) {
		textBuf.append(textInsert);
	}

	public void insert(int offset, String textInsert) {
		try {
			int loc = offsets.get(offset).value;
			textBuf.replace(loc, loc, textInsert);
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

	public boolean checkRange(int offsetIndex) {
		if (offsetIndex >= orgOffsets.size()) {
			return false;
		}
		if (orgOffsets.get(offsetIndex) > original.length()) {// = ok
			return false;
		}
		return true;
	}

	public String getText() {
		return textBuf.toString();
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
