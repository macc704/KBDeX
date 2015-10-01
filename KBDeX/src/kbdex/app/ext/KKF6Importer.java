package kbdex.app.ext;

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

import clib.common.filesystem.CFile;
import clib.common.thread.ICTask;
import clib.common.utils.ICProgressMonitor;
import clib.view.progress.CPanelProcessingMonitor;
import info.matsuzawalab.kf.kf6connector.KF6Service;
import info.matsuzawalab.kf.kf6connector.model.K6Author;
import info.matsuzawalab.kf.kf6connector.model.KNote;
import kbdex.app.KBDeX;
import kbdex.app.manager.KDDiscourseManager;
import kbdex.model.discourse.KDDiscourseFile;
import kbdex.model.discourse.KDDiscourseRecord;
import kbdex.model.discourse.KRecordFileIO;
import kfl.connector.KFLoginModel;
import kfl.connector.KFLoginPanel;

public class KKF6Importer {

	private static final String SERVER = "localhost";
	private static final int PORT = 9000;
	private static final String DATABASE = "-- not nessary to choose -- ";
	private static final String USER = "";
	private static final String PASSWORD = "";

	private static DateFormat format = new SimpleDateFormat(
			"MMM d, yyyy HH:mm:ss a");
	private static DateFormat fnameFormat = new SimpleDateFormat(
			"yyyyMMddHHmm");

	private final KF6Service service = new KF6Service();
	private final KFLoginModel model = new KFLoginModel();

	public KKF6Importer() {
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
				service.setBaseURI(model.getBaseAddress());
				service.login(model.getUser(), model.getPassword());
				break;
			} catch (SocketException ex) {
				panel.setFailiureMessage("failed to connect the internet");
			} catch (Exception ex) {
				panel.setFailiureMessage("error " + ex.getMessage());
			}

			//retry
		}
		monitor.progress(1);

		monitor.setWorkTitle("selecting community ...");
		K6Author selectedAuthor = null;
		{
			List<K6Author> authors = service.getRegistrations();
			if (authors.isEmpty()) {
				throw new RuntimeException("There is no registred community.");
			}
			JComboBox<K6Author> combobox = new JComboBox<K6Author>();
			for (K6Author author : authors) {
				combobox.addItem(author);
			}
			int res = JOptionPane.showConfirmDialog(null, combobox,
					"Registration?", JOptionPane.OK_OPTION);
			if (res != JOptionPane.OK_OPTION) {
				return;
			}
			selectedAuthor = (K6Author) combobox.getSelectedItem();
		}
		service.setCommunityId(selectedAuthor.communityId);
		monitor.progress(1);

		//		monitor.setWorkTitle("selecting view ...");
		//		List<KF5View> selected = new ArrayList<KF5View>();
		//		{
		//			JSONArray viewJsons = service.getViews(selectedAuthor.communityId);
		//			int len = viewJsons.length();
		//			if (len <= 0) {
		//				throw new RuntimeException("There is no view.");
		//			}
		//			List<KF5View> views = new ArrayList<KF5View>();
		//			for (int i = 0; i < len; i++) {
		//				KF5View view = new KF5View(viewJsons.getJSONObject(i));
		//				views.add(view);
		//			}
		//
		//			JPanel listPanel = new JPanel();
		//			JScrollPane scroll = new JScrollPane(listPanel);
		//			scroll.setPreferredSize(new Dimension(400, 300));
		//
		//			listPanel.setBackground(Color.WHITE);
		//			listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		//
		//			List<JCheckBox> boxes = new ArrayList<JCheckBox>();
		//			for (KF5View view : views) {
		//				JCheckBox box = new JCheckBox(view.title);
		//				listPanel.add(box);
		//				boxes.add(box);
		//			}
		//
		//			int res = JOptionPane.showConfirmDialog(null, scroll, "View?",
		//					JOptionPane.OK_OPTION);
		//			if (res != JOptionPane.OK_OPTION) {
		//				return;
		//			}
		//
		//			for (int i = 0; i < len; i++) {
		//				JCheckBox box = boxes.get(i);
		//				if (box.isSelected()) {
		//					selected.add(views.get(i));
		//				}
		//			}
		//		}
		//		monitor.progress(1);

		monitor.setWorkTitle("connect and getting data...");
		//		List<JSONObject> jsonPosts = new ArrayList<JSONObject>();
		//		List<String> guids = new ArrayList<String>();
		//		for (KF5View view : selected) {
		//			List<JSONObject> jsonpostsview = retrieveNotesForView(view);
		//			for (JSONObject jsonpost : jsonpostsview) {
		//				String guid = jsonpost.getString("guid");
		//				if (!guids.contains(guid)) {
		//					jsonPosts.add(jsonpost);
		//					guids.add(guid);
		//				}
		//			}
		//		}
		List<KNote> notes = service.getAllNotes();
		monitor.progress(1);

		monitor.setWorkTitle("analyzing data...");
		List<KDDiscourseRecord> records = new ArrayList<KDDiscourseRecord>();
		for (KNote note : notes) {
			try {
				if (!note.type.equals("Note")) {
					continue;
				}

				String author = "author-not-implemented";
				String body = note.data.body;
				KDDiscourseRecord record = new KDDiscourseRecord(0, author,
						body);
				record.setGroupName("default-group"); /*tmp*/
				record.setTime(note.created.getTime());

				records.add(record);
			} catch (Exception ex) {
				ex.printStackTrace();
				System.err.println(note.title);
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

		String viewnames = "test";
		//		for (KF5View view : selected) {
		//			viewnames += "-" + view.title;
		//		}
		viewnames = viewnames.replaceAll(":", "");
		viewnames = viewnames.replaceAll(";", "");
		viewnames = viewnames.replaceAll("&", "");
		viewnames = viewnames.replaceAll("!", "");
		if (viewnames.length() > 50) {
			viewnames = viewnames.substring(0, 49);
		}
		model.setDBName(selectedAuthor._community.title + "-" + viewnames);
		KDDiscourseManager manager = KBDeX.getInstance().getDiscourseManager();
		String name = fnameFormat.format(new Date()) + "-" + model.getDBName();
		name = encodeFilename(name);
		KDDiscourseFile dFile = manager.createNewDiscourse(name);
		CFile file = dFile.getRecordFile();
		KRecordFileIO.save(records, file);
		monitor.progress(1);
	}

	//	private List<JSONObject> retrieveNotesForView(KF5View view)
	//			throws Exception {
	//		JSONArray jsonPosts = service.getPostsForView(view.guid);
	//		List<JSONObject> posts = new ArrayList<JSONObject>();
	//		JSONArray viewPostRefs;
	//		try {
	//			viewPostRefs = jsonPosts.getJSONObject(0)
	//					.getJSONArray("viewPostRefs");
	//		} catch (Exception ex) {
	//			return new ArrayList<JSONObject>();
	//		}
	//		int len = viewPostRefs.length();
	//		for (int i = 0; i < len; i++) {
	//			posts.add(viewPostRefs.getJSONObject(i).getJSONObject("postInfo"));
	//		}
	//		return posts;
	//	}
	//
	//	@SuppressWarnings("unused")
	//	private List<JSONObject> retrieveAllPosts(KF5Registration selectedReg)
	//			throws Exception {
	//		JSONArray jsonPosts = service
	//				.getPostsForCommunity(selectedReg.communityId);
	//		List<JSONObject> posts = new ArrayList<JSONObject>();
	//		int len = jsonPosts.length();
	//		for (int i = 0; i < len; i++) {
	//			posts.add(jsonPosts.getJSONObject(i));
	//		}
	//		return posts;
	//	}

	//	public String toText(String html) {
	//		Source source = new Source(html);
	//		TextExtractor extractor = source.getTextExtractor();
	//		extractor.setExcludeNonHTMLElements(true);
	//		String text = source.getTextExtractor().toString();
	//		return text;
	//	}

	private static String encodeFilename(String name) {
		String notAllowedChars = "[( |\\\\|/|:|\\*|?|\"|<|>|\\|)]";
		name = name.replaceAll(notAllowedChars, "_");
		return name;
	}

	public static void main(String[] args) {
		System.out.println(encodeFilename("hoge"));
		System.out.println(encodeFilename("hoge/hoge"));
		System.out.println(encodeFilename("hoge/h::\\?<oge"));
		System.out.println(encodeFilename("hoge/h::\\?<og e"));
	}
}
