package kfl.converter.common;

import java.util.ArrayList;
import java.util.List;

public class KReplacableString {
	private StringBuffer text;
	private List<Offset> offsets = new ArrayList<Offset>();

	public KReplacableString(String text, List<Integer> ioffsets) {
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
