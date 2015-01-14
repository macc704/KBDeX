package kfl.app.csvexporter.model;

import java.util.ArrayList;
import java.util.List;

public class K4RiseAbove extends K4Element {

	private static final long serialVersionUID = 1L;
	
	private List<K4Note> notes = new ArrayList<K4Note>();

	@Override
	public String getType() {
		return "riseabove";
	}

	public void addNote(K4Note note) {
		notes.add(note);
	}

	public List<K4Note> getNotes() {
		return notes;
	}
	
	@Override
	public String getShortDescrption() {
		return listToString("", notes);
	}
	

}
