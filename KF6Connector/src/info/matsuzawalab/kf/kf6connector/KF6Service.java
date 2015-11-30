package info.matsuzawalab.kf.kf6connector;

import java.nio.charset.StandardCharsets;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import info.matsuzawalab.kf.kf6connector.model.K6Author;
import info.matsuzawalab.kf.kf6connector.model.K6Note;
import info.matsuzawalab.kf.kf6connector.model.K6View;

public class KF6Service {

	private static String CHARSET = "UTF-8";

	private HttpClient client;
	private Gson gson;
	private String baseURI;

	private Token token;
	private String communityId;

	public KF6Service() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
		client = HttpClientBuilder.create().build();
	}

	public void setBaseURI(String baseURI) {
		this.baseURI = baseURI;
	}

	public void login(String userName, String password) throws Exception {
		if (baseURI == null) {
			throw new RuntimeException("no baseURI");
		}

		HttpPost req = new HttpPost(baseURI + "/auth/local");
		Login login = new Login();
		login.email = userName;
		login.password = password;
		StringEntity entity = new StringEntity(gson.toJson(login), StandardCharsets.UTF_8);
		req.addHeader("Content-type", "application/json");
		req.setEntity(entity);
		HttpResponse res = client.execute(req);
		int code = res.getStatusLine().getStatusCode();
		if (code != 200) {
			throw new RuntimeException("login failed on code=" + code);
		}
		String content = res2String(res);
		token = gson.fromJson(content, Token.class);
	}

	public List<K6Author> getRegistrations() throws Exception {
		if (token == null) {
			throw new RuntimeException("no token");
		}

		HttpGet req = new HttpGet(baseURI + "/api/users/myRegistrations");
		req.setHeader("authorization", "Bearer " + token.token);
		HttpResponse res = client.execute(req);
		String content = res2String(res);
		// prettyPrint(content);
		List<K6Author> authors = gson.fromJson(content, new TypeToken<List<K6Author>>() {
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

	public List<K6View> getViews() throws Exception {
		checkStatus();

		HttpGet req = new HttpGet(baseURI + "/api/communities/" + communityId + "/views");
		req.setHeader("authorization", "Bearer " + token.token);
		HttpResponse res = client.execute(req);
		String content = res2String(res);
		List<K6View> views = gson.fromJson(content, new TypeToken<List<K6View>>() {
		}.getType());
		return views;
	}

	private String res2String(HttpResponse res) throws Exception {
		String content = EntityUtils.toString(res.getEntity(), CHARSET);
		// prettyPrint(content);
		return content;
	}

	public List<K6Author> getAuthors() throws Exception {
		checkStatus();

		HttpGet req = new HttpGet(baseURI + "/api/communities/" + communityId + "/authors");
		req.setHeader("authorization", "Bearer " + token.token);
		HttpResponse res = client.execute(req);
		String content = res2String(res);
		List<K6Author> authors = gson.fromJson(content, new TypeToken<List<K6Author>>() {
		}.getType());
		return authors;
	}

	public List<K6Note> getAllNotes() throws Exception {
		return getNotes(null);
	}

	public List<K6Note> getNotes(List<String> viewIds) throws Exception {
		checkStatus();

		HttpPost req = new HttpPost(baseURI + "/api/contributions/" + communityId + "/search");
		req.setHeader("authorization", "Bearer " + token.token);
		Search search = new Search();
		search.query.communityId = communityId;
		search.query.pagesize = "10000";
		search.query.viewIds = viewIds;
		StringEntity entity = new StringEntity(gson.toJson(search), StandardCharsets.UTF_8);
		req.addHeader("Content-type", "application/json");
		req.setEntity(entity);
		HttpResponse res = client.execute(req);
		String content = res2String(res);
		List<K6Note> notes = gson.fromJson(content, new TypeToken<List<K6Note>>() {
		}.getType());
		return notes;
	}

	@SuppressWarnings("unused")
	private void prettyPrint(String jsonString) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(jsonString);
		String prettyJsonString = gson.toJson(je);
		System.out.println(prettyJsonString);
	}

	@SuppressWarnings("unused")
	private void prettyPrint(Object obj) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJsonString = gson.toJson(obj);
		System.out.println(prettyJsonString);
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
