package kbdex.app.ext;

import info.matsuzawalab.kf.kf5loader.KF5Service;
import info.matsuzawalab.kf.kf5loader.KF5ServiceException;

import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import kbdex.app.KBDeX;
import kbdex.app.manager.KDDiscourseManager;
import kbdex.model.discourse.KDDiscourseFile;
import kbdex.model.discourse.KDDiscourseRecord;
import kbdex.model.discourse.KRecordFileIO;
import kfl.connector.KFLoginModel;
import kfl.connector.KFLoginPanel;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.TextExtractor;

import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONArray;
import org.json.JSONObject;

import clib.common.filesystem.CFile;
import clib.common.thread.ICTask;
import clib.common.utils.ICProgressMonitor;
import clib.view.progress.CPanelProcessingMonitor;

public class KKF5Importer {

	//private static final String SERVER = "132.203.154.41";
	private static final String SERVER = "128.100.72.137";
	private static final int PORT = 8080;
	private static final String DATABASE = "-- not nessary to choose -- ";
	private static final String USER = "";
	private static final String PASSWORD = "";

	private static DateFormat format = new SimpleDateFormat(
			"MMM d, yyyy HH:mm:ss a");
	private static DateFormat fnameFormat = new SimpleDateFormat("yyyyMMddHHmm");

	private final KF5Service service = new KF5Service("");
	private final KFLoginModel model = new KFLoginModel();

	public KKF5Importer() {
		service.setKf51(true);

		model.setHost(SERVER);
		model.setPort(PORT);
		model.setDBName(DATABASE);
		model.setUser(USER);
		model.setPassword(PASSWORD);
	}

	public void doLoad() {
		final CPanelProcessingMonitor monitor = new CPanelProcessingMonitor(
				true);
		monitor.doTaskWithDialog(new ICTask() {
			public void doTask() {
				try {
					doImport(monitor);
				} catch (Exception ex) {
					KBDeX.getInstance().handleException(null, ex);
				}
			}
		});
	}

	private void doImport(ICProgressMonitor monitor) throws Exception {
		monitor.setMax(6);

		monitor.setWorkTitle("login ...");
		KFLoginPanel panel = new KFLoginPanel();
		panel.setShowDatabase(false);
		panel.setModel(model);

		//retry loop
		while (true) {
			try {
				panel.openDialog();
				if (!panel.isOk()) {// cancel
					return;
				}
				service.setHost(model.getBaseAddress());
				service.login(model.getUser(), model.getPassword());
				break;
			} catch (HttpHostConnectException ex) {
				panel.setFailiureMessage("failed to connect internet");
			} catch (SocketException ex) {
				panel.setFailiureMessage("failed to connect server and port");
			} catch (KF5ServiceException ex) {
				if (ex.getHttpCode() == 404) {
					panel.setFailiureMessage("there is no service");
				} else if (ex.getHttpCode() == 401) {
					panel.setFailiureMessage("login failed");
				} else {
					panel.setFailiureMessage("error code=" + ex.getHttpCode());
				}
			} catch (Exception ex) {
				panel.setFailiureMessage("error " + ex.getMessage());
			}

			//retry
		}
		monitor.progress(1);

		monitor.setWorkTitle("selecting community ...");
		KF5Registration selectedReg = null;
		{
			JSONArray regs = service.getRegistrations();
			int len = regs.length();
			if (len <= 0) {
				throw new RuntimeException("There is no registred community.");
			}
			JComboBox<KF5Registration> combobox = new JComboBox<KF5Registration>();
			for (int i = 0; i < len; i++) {
				KF5Registration reg = new KF5Registration(regs.getJSONObject(i));
				combobox.addItem(reg);
			}
			JOptionPane.showConfirmDialog(null, combobox, "Registration?",
					JOptionPane.OK_OPTION);
			selectedReg = (KF5Registration) combobox.getSelectedItem();
		}
		service.enterCommunity(selectedReg.guid);
		monitor.progress(1);

		monitor.setWorkTitle("selecting view ...");
		KF5View selectedView = null;
		{
			JSONArray views = service.getViews(selectedReg.communityId);
			int len = views.length();
			if (len <= 0) {
				throw new RuntimeException("There is no view.");
			}
			JComboBox<KF5View> combobox = new JComboBox<KF5View>();
			for (int i = 0; i < len; i++) {
				KF5View view = new KF5View(views.getJSONObject(i));
				combobox.addItem(view);
			}
			JOptionPane.showConfirmDialog(null, combobox, "View?",
					JOptionPane.OK_OPTION);
			selectedView = (KF5View) combobox.getSelectedItem();
		}
		monitor.progress(1);

		monitor.setWorkTitle("connect and getting data...");
		List<JSONObject> jsonPosts = retrieveNotesForView(selectedView);
		monitor.progress(1);

		monitor.setWorkTitle("analyzing data...");
		List<KDDiscourseRecord> records = new ArrayList<KDDiscourseRecord>();
		for (JSONObject post : jsonPosts) {
			try {
				if (!post.getString("postType").equals("NOTE")) {
					continue;
				}

				String author = post.getJSONArray("authors").getJSONObject(0)
						.getString("userName"); /*tmp*/
				String body = toText(post.getString("body"));
				KDDiscourseRecord record = new KDDiscourseRecord(0, author,
						body);
				record.setGroupName("default-group"); /*tmp*/
				String created = post.getString("created");
				long createdTime = format.parse(created).getTime();
				//record.setTime(note.getModified().getTime()); //#bugfix 1.5.7
				record.setTime(createdTime);
				records.add(record);
			} catch (Exception ex) {
				ex.printStackTrace();
				System.err.println(post.toString(2));
			}
		}
		Collections.sort(records, new Comparator<KDDiscourseRecord>() {
			public int compare(KDDiscourseRecord r1, KDDiscourseRecord r2) {
				long diff = r1.getTimeAsLong() - r2.getTimeAsLong();
				return diff < 0 ? -1 : 1;
			};
		});
		long counter = 1;
		for (KDDiscourseRecord record : records) {
			record.setId(counter);
			counter++;
		}
		monitor.progress(1);

		monitor.setWorkTitle("writing to your disk...");
		model.setDBName(selectedReg.sectionTitle + "-" + selectedView.title);
		KDDiscourseManager manager = KBDeX.getInstance().getDiscourseManager();
		String name = fnameFormat.format(new Date()) + "-" + model.getDBName();
		KDDiscourseFile dFile = manager.createNewDiscourse(name);
		CFile file = dFile.getRecordFile();
		KRecordFileIO.save(records, file);
		monitor.progress(1);

	}

	private List<JSONObject> retrieveNotesForView(KF5View view)
			throws Exception {
		JSONArray jsonPosts = service.getPostsForView(view.guid);
		List<JSONObject> posts = new ArrayList<JSONObject>();
		JSONArray viewPostRefs = jsonPosts.getJSONObject(0).getJSONArray(
				"viewPostRefs");
		int len = viewPostRefs.length();
		for (int i = 0; i < len; i++) {
			posts.add(viewPostRefs.getJSONObject(i).getJSONObject("postInfo"));
		}
		return posts;
	}

	@SuppressWarnings("unused")
	private List<JSONObject> retrieveAllPosts(KF5Registration selectedReg)
			throws Exception {
		JSONArray jsonPosts = service
				.getPostsForCommunity(selectedReg.communityId);
		List<JSONObject> posts = new ArrayList<JSONObject>();
		int len = jsonPosts.length();
		for (int i = 0; i < len; i++) {
			posts.add(jsonPosts.getJSONObject(i));
		}
		return posts;
	}

	public String toText(String html) {
		Source source = new Source(html);
		TextExtractor extractor = source.getTextExtractor();
		extractor.setExcludeNonHTMLElements(true);
		String text = source.getTextExtractor().toString();
		return text;
	}
}

class KF5Registration {
	String guid;
	String communityId;
	String sectionTitle;
	String roleName;

	KF5Registration(JSONObject json) {
		guid = json.getString("guid");
		communityId = json.getString("sectionId");
		sectionTitle = json.getString("sectionTitle");
		roleName = json.getJSONObject("roleInfo").getString("name");
	}

	public String toString() {
		return sectionTitle + " (as " + roleName + ")";
	}
}

class KF5View {
	String guid;
	String title;

	KF5View(JSONObject json) {
		guid = json.getString("guid");
		title = json.getString("title");
	}

	public String toString() {
		return title;
	}
}
