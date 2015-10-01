package info.matsuzawalab.kf.kf6connector;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class KF6Service {

	private HttpClient client;
	private Gson gson;
	private String baseURL = null;

	private Token token;
	private String communityId;

	public KF6Service(String server) {
		gson = new GsonBuilder().create();
		client = HttpClientBuilder.create().build();
		baseURL = "http://" + server;
	}

	public boolean login(String userName, String password) throws Exception {
		HttpPost req = new HttpPost(baseURL + "/auth/local");
		Login login = new Login();
		login.email = userName;
		login.password = password;
		StringEntity entity = new StringEntity(gson.toJson(login), StandardCharsets.UTF_8);
		req.addHeader("Content-type", "application/json");
		req.setEntity(entity);
		HttpResponse res = client.execute(req);
		int code = res.getStatusLine().getStatusCode();
		if (code != 200) {
			return false;
		}
		String content = EntityUtils.toString(res.getEntity());
		token = gson.fromJson(content, Token.class);
		return true;
	}

	public List<KAuthor> getRegistrations() throws Exception {
		if (token == null) {
			throw new RuntimeException("no toke");
		}

		HttpGet req = new HttpGet(baseURL + "/api/users/myRegistrations");
		req.setHeader("authorization", "Bearer " + token.token);
		HttpResponse res = client.execute(req);
		String content = EntityUtils.toString(res.getEntity());
		List<KAuthor> authors = gson.fromJson(content, new TypeToken<List<KAuthor>>() {
		}.getType());
		return authors;
	}

	public void setCommunityId(String communityId) {
		this.communityId = communityId;
	}

	private void checkStatus() throws Exception {
		if (token == null) {
			throw new RuntimeException("no token");
		}
		if (communityId == null) {
			throw new RuntimeException("no communityId");
		}
	}

	public List<KView> getViews() throws Exception {
		checkStatus();

		HttpGet req = new HttpGet(baseURL + "/api/communities/" + communityId + "/views");
		req.setHeader("authorization", "Bearer " + token.token);
		HttpResponse res = client.execute(req);
		String content = EntityUtils.toString(res.getEntity());
		List<KView> views = gson.fromJson(content, new TypeToken<List<KView>>() {
		}.getType());
		return views;
	}

	public List<KNote> getAllNotes() throws Exception {
		return getNotes(null);
	}

	public List<KNote> getNotes(String viewId) throws Exception {
		checkStatus();

		HttpPost req = new HttpPost(baseURL + "/api/contributions/" + communityId + "/search");
		req.setHeader("authorization", "Bearer " + token.token);
		Search search = new Search();
		search.query.communityId = communityId;
		search.query.pagesize = "10000";
		search.query.viewIds = Arrays.asList(new String[] { viewId });
		StringEntity entity = new StringEntity(gson.toJson(search), StandardCharsets.UTF_8);
		req.addHeader("Content-type", "application/json");
		req.setEntity(entity);
		HttpResponse res = client.execute(req);
		String content = EntityUtils.toString(res.getEntity());
		List<KNote> notes = gson.fromJson(content, new TypeToken<List<KNote>>() {
		}.getType());
		return notes;
	}

}

class Login {
	String email;
	String password;
}

class Token {
	String token;
}

class Search {
	Query query = new Query();
}

class Query {
	String communityId;
	List<String> viewIds;
	String pagesize;
}

class KObject {
	String _id;
}

class KContribution extends KObject {
	String title;
	String communityId;
}

class KView extends KContribution {

}

class KNote extends KContribution {

}

class KAuthor extends KObject {
	String userId;
	String type;
	String role;
	String firstName;
	String lastName;
	KCommunity _community;
}

class KCommunity {
	String _id;
	String title;
	List<String> scaffolds;
	List<String> views;
}
