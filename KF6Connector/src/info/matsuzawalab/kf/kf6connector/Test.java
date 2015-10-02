package info.matsuzawalab.kf.kf6connector;

import java.util.List;

import info.matsuzawalab.kf.kf6connector.model.K6Author;
import info.matsuzawalab.kf.kf6connector.model.K6Note;

public class Test {

	public static void main(String[] args) throws Exception {
		new Test().run(args);
	}

	void run(String[] args) throws Exception {
		KF6Service service = new KF6Service();
		service.setBaseURI("http://localhost:9000");
		service.login("yoshiaki.matsuzawa@gmail.com", "test");
		List<K6Author> authors = service.getRegistrations();
		service.setCommunityId(authors.get(3).communityId);
		List<K6Note> notes = service.getAllNotes();
		for (K6Note note : notes) {
			System.out.println(note.title);
		}
	}

}
