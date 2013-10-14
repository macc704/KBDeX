package kfl.model;

import java.util.ArrayList;
import java.util.List;

public class KFView extends KFElement {

	private String name;

	private List<KFNote> notes = new ArrayList<KFNote>();

	public KFView() {
		this("");
	}

	public KFView(String name) {
		this.name = name;
	}

	public void addNote(KFNote note) {
		notes.add(note);
	}

	public List<KFNote> getNotes() {
		return notes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof KFView)) {
			return false;
		}

		return toString().equals(((KFView) obj).toString());
	}

	@Override
	public String toString() {
		return getName();
	}

}
