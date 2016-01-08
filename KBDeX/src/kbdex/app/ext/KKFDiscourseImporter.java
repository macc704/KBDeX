/*
 * KnowledgeForumBridge.java
 * Created on Jul 16, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.app.ext;

import java.awt.Frame;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.zoolib.tuplebase.ZTB;

import clib.common.filesystem.CFile;
import clib.common.thread.ICTask;
import clib.common.utils.ICProgressMonitor;
import clib.view.progress.CPanelProcessingMonitor;
import clib.view.windowmanager.CWindowCentraizer;
import kbdex.app.KBDeX;
import kbdex.app.manager.KDDiscourseManager;
import kbdex.model.discourse.KDDiscourseFile;
import kbdex.model.discourse.KDDiscourseRecord;
import kbdex.model.discourse.KRecordFileIO;
import kfl.app.kfn.KFViewChooser;
import kfl.builder.KFNoteBuilder;
import kfl.connector.KFConnector;
import kfl.connector.KFLoginModel;
import kfl.connector.KFLoginPanel;
import kfl.model.KFNote;
import kfl.model.KFView;

/**
 * @author macchan
 */
public class KKFDiscourseImporter {

	//private static final String SERVER = "kai.cs.inf.shizuoka.ac.jp";
	//private static final int PORT = 8080;
	//private static final String DATABASE = "LM2011_3";
	private static final String SERVER = "builder.ikit.org";
	private static final int PORT = 80;
	private static final String DATABASE = "KSN";
	private static final String USER = "";
	private static final String PASSWORD = "";

	private static DateFormat format = new SimpleDateFormat("yyyyMMddHHmm");

	public KKFDiscourseImporter() {
	}

	public void doLoad() {
		KFLoginModel model = createLoginModel();

		ZTB conn = connectWithDialog(model);
		if (conn == null) {
			return;
		}

		doImport(conn, model.getDBName());
	}

	private KFLoginModel createLoginModel() {
		KFLoginModel model = new KFLoginModel();
		model.setHost(SERVER);
		model.setPort(PORT);
		model.setDBName(DATABASE);
		model.setUser(USER);
		model.setPassword(PASSWORD);
		return model;
	}

	private ZTB connectWithDialog(KFLoginModel model) {
		KFLoginPanel panel = new KFLoginPanel("Login to KF4:");
		panel.setModel(model);
		ZTB conn = null;

		while (conn == null) {
			panel.openDialog();
			if (!panel.isOk()) {// cancel
				break;
			}

			conn = KFConnector.connect(model);
			if (conn == null) {
				panel.setFailiureMessage("Login Failed - Try Again");
			}
		}
		return conn;
	}

	private void doImport(final ZTB conn, String dbName) {
		KDDiscourseManager manager = KBDeX.getInstance().getDiscourseManager();
		String name = dbName + "-" + format.format(new Date());
		KDDiscourseFile dFile = manager.createNewDiscourse(name);
		final CFile file = dFile.getRecordFile();
		final CPanelProcessingMonitor monitor = new CPanelProcessingMonitor(
				false);
		monitor.doTaskWithDialog(new ICTask() {
			public void doTask() {
				build(conn, file, monitor);
			}
		});
	}

	private void build(ZTB conn, CFile out, ICProgressMonitor monitor) {
		KFNoteBuilder builder = new KFNoteBuilder();
		List<KFView> views = selectView(builder, conn);
		builder.build(conn, monitor);
		conn.close();

		List<KFNote> notes = builder.getNotes(views);
		List<KDDiscourseRecord> records = new ArrayList<KDDiscourseRecord>();
		for (KFNote note : notes) {
			KDDiscourseRecord record = new KDDiscourseRecord(note.getIdAsLong(),
					note.getAuthor().getName(), note.getText());
			record.setGroupName(note.getAuthor().getGroup().getName());
			//record.setTime(note.getModified().getTime()); //#bugfix 1.5.7
			record.setTime(note.getCreated().getTime());//#bugfix 1.5.7
			records.add(record);
		}

		KRecordFileIO.save(records, out);
	}

	private List<KFView> selectView(KFNoteBuilder builder, ZTB conn) {
		JDialog dialog = new JDialog((Frame) null, true);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setSize(500, 500);
		CWindowCentraizer.centerWindow(dialog);
		KFViewChooser chooser = new KFViewChooser(builder.prefetchViews(conn),
				dialog);
		dialog.getContentPane().add(chooser);
		dialog.setVisible(true);
		return chooser.getSelectedViews();
	}
}
