package kfl.app.csvexporter.model;

import java.util.ArrayList;
import java.util.List;

public class KFRiseAbove extends KFElement {

	private static final long serialVersionUID = 1L;
	
	private List<KFNote> notes = new ArrayList<KFNote>();

	@Override
	public String getType() {
		return "riseabove";
	}

	public void addNote(KFNote note) {
		notes.add(note);
	}

	public List<KFNote> getNotes() {
		return notes;
	}
	
	@Override
	public String getShortDescrption() {
		return listToString("", notes);
	}
	

}
