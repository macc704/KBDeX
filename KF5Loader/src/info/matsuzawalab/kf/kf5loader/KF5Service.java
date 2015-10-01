package info.matsuzawalab.kf.kf5loader;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

public class KF5Service {

	// Trusting SelfSigned Strategy
	private static SSLConnectionSocketFactory sslsf;

	static {
		try {
			SSLContextBuilder builder = new SSLContextBuilder();
			builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
			sslsf = new SSLConnectionSocketFactory(builder.build());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

	// private CloseableHttpClient httpClient = HttpClients.createDefault();

	// handshake alert: unrecognized_name Problem and Solution
	// http://d.hatena.ne.jp/ttshiko/20121103/1351927402
	// Solution: java option to invalidate => -Djsse.enableSNIExtension=false

	private String baseURI;

	// kf5.1
	private boolean kf51 = false;
	private JSONArray registrations;

	public KF5Service(String baseURI) {
		setBaseURI(baseURI);
	}

	public void setBaseURI(String baseURI) {
		if (!baseURI.endsWith("/")) {
			baseURI = baseURI + "/";
		}
		this.baseURI = baseURI;
	}

	public void setKf51(boolean kf51) {
		this.kf51 = kf51;
	}

	private String getServiceURI(String path) {
		return baseURI + "kforum/rest/" + path;
	}

	private JSONArray getJSON(HttpUriRequest request) throws Exception {
		CloseableHttpResponse response = httpClient.execute(request);
		int status = response.getStatusLine().getStatusCode();
		if (status != 200) {
			throw new KF5ServiceException(request.getURI() + " failed.", status);
		}
		String contentStr = EntityUtils.toString(response.getEntity(), "UTF-8");
		if (!contentStr.startsWith("[")) {
			contentStr = "[" + contentStr + "]";
		}
		JSONArray json = new JSONArray(contentStr);
		return json;
	}

	public boolean login(String username, String password) throws Exception {
		HttpPost method = new HttpPost(getServiceURI("account/userLogin"));
		method.setEntity(
				new UrlEncodedFormEntity(Form.form().add("userName", username).add("password", password).build()));
		registrations = getJSON(method);
		return true;
	}

	public JSONArray currentUser() throws Exception {
		HttpGet method = new HttpGet(getServiceURI("account/currentUser"));
		return getJSON(method);
	}

	public JSONArray logout() throws Exception {
		HttpGet method = new HttpGet(getServiceURI("account/userLogout"));
		return getJSON(method);
	}

	public JSONArray getRegistrations() throws Exception {
		if (kf51) {
			return registrations;
		}
		HttpGet method = new HttpGet(getServiceURI("account/registrations"));
		return getJSON(method);
	}

	public JSONArray enterCommunity(String registrationCode) throws Exception {
		HttpGet method = new HttpGet(getServiceURI("account/selectSection/" + registrationCode));
		return getJSON(method);
	}

	public JSONArray getViews(String communityId) throws Exception {
		HttpGet method = new HttpGet(getServiceURI("content/getSectionViews/" + communityId));
		return getJSON(method);
	}

	public JSONArray getPostsForView(String viewId) throws Exception {
		HttpGet method = new HttpGet(getServiceURI("content/getView/" + viewId));
		return getJSON(method);
	}

	public JSONArray getPostsForCommunity(String communityId) throws Exception {
		HttpGet method = new HttpGet(getServiceURI("content/getSectionPosts/" + communityId));
		return getJSON(method);
	}

	public JSONArray getPostHistory(String postId) throws Exception {
		HttpGet method = new HttpGet(getServiceURI("mobile/getPostHistory/" + postId));
		return getJSON(method);
	}

	public JSONArray getPostHistoriesForView(String viewId) throws Exception {
		HttpGet method = new HttpGet(getServiceURI("mobile/getPostHistoriesForView/" + viewId));
		return getJSON(method);
	}

}
