package info.matsuzawalab.kf.kf5loader;

import java.net.SocketException;

import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONArray;

public class Test {

	// String host = "http://rooibos.cs.inf.shizuoka.ac.jp/";

	public static void main(String[] args) throws Exception {
		new Test().run();
	}

	private void run() throws Exception {
		// KF5Service service = new KF5Service("http://132.203.154.41:8080/");

		KF5Service service = new KF5Service("http://128.100.72.137:8080/");
		service.setKf51(true);

		try {
			/* JSONArray user = */service.login("ikit", "pass");
			// String username = user.getJSONObject(0).getString("userName");
			// System.out.println(username);
		} catch (HttpHostConnectException ex) {
			System.out.println(ex.getMessage()); // host refusing and port
													// refusing
			System.out.println("host error");
			return;
		} catch (SocketException ex) {
			System.out.println(ex.getMessage()); // host refusing and port
			// network is unreachable
		} catch (KF5ServiceException ex) {
			System.out.println(ex.getMessage()); // 404 port difference, 401
													// authorization
			System.out.println("kf5 error " + ex.getHttpCode());
			return;
		}
		JSONArray regs = service.getRegistrations();
		System.out.println(regs.toString(2));
		String code = regs.getJSONObject(0).getString("guid");
		JSONArray community = service.enterCommunity(code);
		String communityId = community.getJSONObject(0)
				.getJSONObject("section").getString("guid");
		JSONArray views = service.getViews(communityId);
		System.out.println(views.toString(2));
		String viewId = views.getJSONObject(0).getString("guid");
		// JSONArray notes = service.getPostsForCommunity(communityId);
		// System.out.println(notes.length());
		// System.out.println(notes.toString(2));
		JSONArray notes = service.getPostsForView(viewId);
		System.out.println("-view posts-");
		System.out.println(notes.toString(2));
	}

}
