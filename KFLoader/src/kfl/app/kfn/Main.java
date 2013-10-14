/*
 * Main.java
 * Created on Oct 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kfl.app.kfn;

import java.awt.Frame;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;

import kfl.builder.KFNoteBuilder;
import kfl.connector.KFConnector;
import kfl.connector.KFLoginModel;
import kfl.model.KFNote;
import kfl.model.KFView;

import org.zoolib.tuplebase.ZTB;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.io.CStreamWriter;
import clib.common.system.CEncoding;
import clib.common.thread.ICTask;
import clib.common.utils.ICProgressMonitor;
import clib.view.progress.CPanelProcessingMonitor;
import clib.view.windowmanager.CWindowCentraizer;

/*
 * @author macchan 
 * http://analysis.ikit.org/kfcoders/index.php/Main_Page
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

		model.setPort(80);
		// model.setHost("joshimalab.cs.inf.shizuoka.ac.jp");
		// model.setDBName("LM2010_2");
		// model.setUser("ymatsuzawa");
		// model.setPassword("yoshiaki");

		model.setHost("builder.ikit.org");
		model.setDBName("KSN");
		model.setUser("yoshiaki");
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
				System.exit(0);
			}
		});
	}

	private void build(ZTB conn, CDirectory dir, ICProgressMonitor monitor) {
		KFNoteBuilder builder = new KFNoteBuilder();
		List<KFView> views = selectView(builder, conn);
		builder.build(conn, monitor);
		conn.close();

		List<KFNote> allnotes = builder.getNotes();
		CFile allfile = dir.findOrCreateFile("all_data.csv");
		printToCSV(allfile, allnotes);

		List<KFNote> notes = builder.getNotes(views);
		CFile file = dir.findOrCreateFile("data.csv");
		printToCSV(file, notes);
	}

	public List<KFView> selectView(KFNoteBuilder builder, ZTB conn) {
		JDialog dialog = new JDialog((Frame) null, true);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setSize(300, 300);
		CWindowCentraizer.centerWindow(dialog);
		KFViewChooser chooser = new KFViewChooser(builder.prefetchViews(conn),
				dialog);
		dialog.getContentPane().add(chooser);
		dialog.setVisible(true);
		return chooser.getSelectedViews();
	}

	private void printToCSV(CFile file, List<KFNote> notes) {
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
	}
}
