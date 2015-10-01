package info.matsuzawalab.kf.kf6connector;

import java.util.List;

public class Test {

	public static void main(String[] args) throws Exception {
		new Test().run(args);
	}

	void run(String[] args) throws Exception {
		KF6Service service = new KF6Service("localhost:9000");
		service.login("yoshiaki.matsuzawa@gmail.com", "test");
		List<KAuthor> authors = service.getRegistrations();
		for (KAuthor author : authors) {
			System.out.println(author.communityId);
		}
	}

}
