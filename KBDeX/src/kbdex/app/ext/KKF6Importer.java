package kbdex.app.ext;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import clib.common.filesystem.CFile;
import clib.common.thread.ICTask;
import clib.common.utils.ICProgressMonitor;
import clib.view.progress.CPanelProcessingMonitor;
import info.matsuzawalab.kf.kf6connector.KF6Service;
import info.matsuzawalab.kf.kf6connector.model.K6Author;
import info.matsuzawalab.kf.kf6connector.model.K6Note;
import info.matsuzawalab.kf.kf6connector.model.K6View;
import kbdex.app.KBDeX;
import kbdex.app.manager.KDDiscourseManager;
import kbdex.model.discourse.KDDiscourseFile;
import kbdex.model.discourse.KDDiscourseRecord;
import kbdex.model.discourse.KRecordFileIO;
import kfl.connector.KFLoginModel;
import kfl.connector.KFLoginPanel;

public class KKF6Importer {

	private static final String PROPFILE = "kf6.properties";

	private static DateFormat fnameFormat = new SimpleDateFormat(
			"yyyyMMddHHmm");

	private final KF6Service service = new KF6Service();
	private final KFLoginModel model = new KFLoginModel();

	public KKF6Importer() {
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
		Properties prop = new Properties();
		{//read from properties			
			File propFile = new File(PROPFILE);
			if (!propFile.exists()) {
				propFile.createNewFile();
			}
			prop.load(new FileReader(propFile));

			model.setProtocol(prop.getProperty("protocol", "http"));
			model.setHost(prop.getProperty("host", "localhost"));
			model.setPort(Integer.parseInt(prop.getProperty("port", "80")));
			model.setDBName(prop.getProperty("db", "db-unnecessary"));
			model.setUser(prop.getProperty("user", ""));
			model.setPassword(prop.getProperty("password", ""));
		}

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
					"Community?", JOptionPane.OK_CANCEL_OPTION);
			if (res != JOptionPane.OK_OPTION) {
				return;
			}
			selectedAuthor = (K6Author) combobox.getSelectedItem();
		}
		service.setCommunityId(selectedAuthor.communityId);
		monitor.progress(1);

		{//write properties
			prop.setProperty("protocol", model.getProtocol());
			prop.setProperty("host", model.getHost());
			prop.setProperty("port", Integer.toString(model.getPort()));
			prop.setProperty("user", model.getUser());
			prop.store(new FileWriter(PROPFILE),
					"This is property file for kf6 connection setting.");
		}

		monitor.setWorkTitle("selecting view ...");
		List<K6View> selectedViews = new ArrayList<K6View>();
		{
			List<K6View> views = service.getViews();
			if (views.isEmpty()) {
				throw new RuntimeException("There is no view.");
			}

			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.setPreferredSize(new Dimension(400, 300));

			JPanel listPanel = new JPanel();
			JScrollPane scroll = new JScrollPane(listPanel);
			scroll.setPreferredSize(new Dimension(400, 300));

			listPanel.setBackground(Color.WHITE);
			listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

			final List<JCheckBox> boxes = new ArrayList<JCheckBox>();
			for (K6View view : views) {
				JCheckBox box = new JCheckBox(view.title);
				listPanel.add(box);
				boxes.add(box);
			}

			final JCheckBox allBox = new JCheckBox("Select All");
			allBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					boolean select = allBox.isSelected();
					for (JCheckBox box : boxes) {
						box.setSelected(select);
					}
				}
			});

			mainPanel.add(scroll, BorderLayout.CENTER);
			mainPanel.add(allBox, BorderLayout.SOUTH);

			int res = JOptionPane.showConfirmDialog(null, mainPanel, "View?",
					JOptionPane.OK_OPTION);
			if (res != JOptionPane.OK_OPTION) {
				return;
			}

			int len = boxes.size();
			for (int i = 0; i < len; i++) {
				JCheckBox box = boxes.get(i);
				if (box.isSelected()) {
					selectedViews.add(views.get(i));
				}
			}
		}
		monitor.progress(1);

		if (selectedViews.isEmpty()) {
			JOptionPane.showConfirmDialog(null, "No view was selected.",
					"Operation Stopped", JOptionPane.DEFAULT_OPTION);
			return;
		}

		monitor.setWorkTitle("connect and getting data...");
		List<String> viewIds = new ArrayList<String>();
		for (K6View view : selectedViews) {
			viewIds.add(view._id);
		}
		List<K6Note> notes = service.getNotes(viewIds);
		List<K6Author> authors = service.getAuthors();
		monitor.progress(1);

		monitor.setWorkTitle("analyzing data...");
		Map<String, K6Author> authorsMap = new HashMap<String, K6Author>();
		for (K6Author author : authors) {
			authorsMap.put(author._id, author);
		}
		List<KDDiscourseRecord> records = new ArrayList<KDDiscourseRecord>();
		for (K6Note note : notes) {
			try {
				if (!note.type.equals("Note")) {
					continue;
				}

				String authorStr = "";
				for (String authorId : note.authors) {
					K6Author author = authorsMap.get(authorId);
					if (author == null) {
						continue;
					}
					if (authorStr.length() != 0) {
						authorStr += ", ";
					}
					authorStr += author.userName;
				}
				if (authorStr.length() == 0) {
					authorStr = "author-not-detected";
				}
				String body = note.text4search;
				KDDiscourseRecord record = new KDDiscourseRecord(0, authorStr,
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

		monitor.setWorkTitle("writing data into your disk...");

		String viewnames = "";
		for (K6View view : selectedViews) {
			viewnames += "-" + view.title;
		}
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
