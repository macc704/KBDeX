package kfl.app.csvexporter.model;

import java.io.Serializable;

public class K4TextLocator implements Serializable {

	private static final long serialVersionUID = 1L;

	private String text;
	private int offset1;
	private int offset2;

	public K4TextLocator() {

	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getOffset1() {
		return offset1;
	}

	public void setOffset(int offset) {
		this.offset1 = offset & 0x0000FFFF;
		this.offset2 = (offset & 0xFFFF0000) >> 16;
	}

	public int getOffset2() {
		return offset2;
	}

	@Override
	public String toString() {
		return "Locator(offset1:" + offset1 + ", offset2:" + offset2 + ")";
	}

}
