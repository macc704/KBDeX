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
		// KF5Service service = new KF5Service("http://128.100.72.137:8080/");
		KF5Service service = new KF5Service("http://localhost:8080/");
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
		System.out.println("-view registrations-");
		JSONArray regs = service.getRegistrations();
		System.out.println(regs.toString(2));
		String code = regs.getJSONObject(0).getString("guid");
		JSONArray community = service.enterCommunity(code);

		System.out.println("-view section-");
		String communityId = community.getJSONObject(0)
				.getJSONObject("section").getString("guid");
		JSONArray views = service.getViews(communityId);
		System.out.println(views.toString(2));

		System.out.println("-view posts-");
		String viewId = views.getJSONObject(0).getString("guid");
		// JSONArray notes = service.getPostsForCommunity(communityId);
		// System.out.println(notes.length());
		// System.out.println(notes.toString(2));
		JSONArray notes = service.getPostsForView(viewId);
		System.out.println(notes.toString(2));

		{
			System.out.println("-view post history-");
			// 74a28acb-cd5d-4cd0-abde-359c2c9840d2
			JSONArray history = service.getPostHistoriesForView(viewId);
			// JSONArray history =
			// service.getPostHistoriesForView("74a28acb-cd5d-4cd0-abde-359c2c9840d2");
			System.out.println(history.toString(2));
		}

		{
			JSONArray history = service
					.getPostHistory("5c1295fc-664c-45df-b81b-29ba316a3c80");
			System.out.println("-history-");
			System.out.println(history.toString(2));
			// // {
			// // "guid": "a71aa619-f73e-4214-8922-2f658d948e3b",
			// // "accessTime": "Apr 3, 2014 11:56:01 AM",
			// // "operationType": "UPDATE",
			// // "entityId": "5c1295fc-664c-45df-b81b-29ba316a3c80",
			// // "userName": "cesaraanunes",
			// // "entityType": "POST"
			// // },
		}
	}

}
