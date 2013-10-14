/*
 * Main.java
 * Created on Oct 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kfn;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import kfl.builder.KFNoteBuilder;
import kfl.connector.KFConnector;
import kfl.connector.KFLoginModel;
import kfl.model.KFNote;

import org.zoolib.tuplebase.ZTB;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.io.CStreamWriter;
import clib.common.system.CEncoding;
import clib.common.thread.ICTask;
import clib.common.utils.ICProgressMonitor;
import clib.view.progress.CPanelProcessingMonitor;

/**
 * @author macchan
 * 
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Main().doLoad();
	}

	private static DateFormat format = new SimpleDateFormat("-yyyyMMdd-HHmm");

	private static DateFormat dformat = new SimpleDateFormat(
			"yy/MM/dd-HH:mm:ss");

	private void doLoad() {
		// create model
		KFLoginModel model = new KFLoginModel();
		model.setHost("joshimalab.cs.inf.shizuoka.ac.jp");
		model.setPort(80);
		model.setDBName("LM2010_2");
		model.setUser("ymatsuzawa");
		model.setPassword("yoshiaki");

		// connect
		final ZTB conn = KFConnector.connectWithDialog(model);
		if (conn == null) {
			return;
		}

		CDirectory baseDir = CFileSystem.getExecuteDirectory()
				.findOrCreateDirectory("kf.out");
		final CDirectory newDir = baseDir.findOrCreateDirectory(model
				.getDBName() + format.format(new Date()));

		// do task (build)
		final CPanelProcessingMonitor monitor = new CPanelProcessingMonitor();
		monitor.doTaskWithDialog(new ICTask() {
			public void doTask() {
				build(conn, newDir, monitor);
			}
		});
	}

	private void build(ZTB conn, CDirectory dir, ICProgressMonitor monitor) {
		KFNoteBuilder builder = new KFNoteBuilder();
		builder.build(conn, monitor);
		List<KFNote> notes = builder.getNotes();

		CFile file = dir.findOrCreateFile("data.csv");
		file.setEncodingOut(CEncoding.Shift_JIS);
		CStreamWriter writer = file.openWriter();
		writer.writeLineFeed("\"ID\",\"Created\",\"Modifiled\",\"Group\",\"Name\",\"Title\",\"Text\"");
		for (KFNote note : notes) {
			StringBuffer buf = new StringBuffer();
			buf.append(note.getId());
			buf.append(",");
			buf.append(dformat.format(note.getCreated()));
			buf.append(",");
			buf.append(dformat.format(note.getModified()));
			buf.append(",");
			buf.append(note.getAuthor().getGroup().getName());
			buf.append(",");
			buf.append(note.getAuthor().getName());
			buf.append(",");
			buf.append(note.getTitle());
			buf.append(",");
			String text = note.getText();
			// text = CStringEscaper.escape(note.getText());
			text = text.replaceAll("\"", "\"\"");
			buf.append("\"" + text + "\"");
			String line = buf.toString();
			// System.out.println(line);
			writer.writeLineFeed(line);
		}
		writer.close();
		conn.close();
	}
}
