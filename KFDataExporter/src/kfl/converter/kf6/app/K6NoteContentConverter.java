package kfl.converter.kf6.app;

import kfl.converter.common.KReplacableString;
import kfl.converter.kf4.model.K4TextLocator;
import kfl.converter.kf6.model.K6Contribution;
import kfl.converter.kf6.model.K6Json;
import kfl.converter.kf6.model.K6Link;
import kfl.converter.kf6.model.K6Note;

public class K6NoteContentConverter {

	public void doConvert(K6Json data) {
		for (K6Contribution cont : data.getContributions()) {
			if (cont.type.equals("Note")) {
				processOne((K6Note) cont);
			}
		}
	}

	private void processOne(K6Note note) {
		String org = note.body;
		KReplacableString replacable = new KReplacableString(org, note.offsets);
		for (K6Link link : note.supporteds.keySet()) {
			K4TextLocator loca = note.supporteds.get(link);
			String startTag = "<span id=\"" + link._id + "\" class=\"KFSupportStart\"/>";
			String endTag = "<span id=\"" + link._id + "\" class=\"KFSupportEnd\"/>";
			if (replacable.checkRange(loca.getOffset1()) == false
					|| replacable.checkRange(loca.getOffset2()) == false) {
				replacable.insertLast(startTag + endTag);
				continue;
			}
			replacable.insert(loca.getOffset1(), startTag);
			replacable.insert(loca.getOffset2(), endTag);
		}
		for (K6Link link : note.references.keySet()) {
			K4TextLocator loca = note.references.get(link);
			String textInsert = "<span id=\"" + link._id + "\" class=\"KFReference\"/>";
			if (replacable.checkRange(loca.getOffset1()) == false) {
				replacable.insertLast(textInsert);
				continue;
			}
			replacable.insert(loca.getOffset1(), textInsert);
		}

		String embedded = replacable.getText();
		String html = embedded.replaceAll("\n", "<br>");
		note.body = html;
	}
}
