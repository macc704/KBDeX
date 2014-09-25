/*
 * Main.java
 * Created on Oct 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kfl.app.kfn;

import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import kfl.builder.KFDataRetriever;
import kfl.connector.KFConnector;
import kfl.connector.KFLoginModel;
import kfl.model.KFAttachment;
import kfl.model.KFAuthor;
import kfl.model.KFGroup;
import kfl.model.KFLog;
import kfl.model.KFNote;
import kfl.model.KFView;
import kfl.model.KFWorld;

import org.zoolib.tuplebase.ZTB;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.table.CCSVFileIO;
import clib.common.thread.ICTask;
import clib.common.utils.ICProgressMonitor;
import clib.view.progress.CPanelProcessingMonitor;

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

	private static DateFormat format = new SimpleDateFormat("-yyyyMMdd-HHmmss");

	// private static DateFormat dformat = new SimpleDateFormat(
	// "yy/MM/dd-HH:mm:ss");

	private void doLoad() {
		// create model
		final KFLoginModel model = new KFLoginModel();

		model.setPort(80);
		// model.setHost("joshimalab.cs.inf.shizuoka.ac.jp");
		// model.setDBName("LM2010_2");
		// model.setUser("ymatsuzawa");
		// model.setPassword("yoshiaki");

		model.setHost("builder.ikit.org");
		// model.setDBName("KSN");
		model.setDBName("Susana_test");
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
				try {
					build(conn, newDir, monitor);
					new DataDump().dump(model.getHost(), model.getPort(),
							model.getDBName(), model.getUser(),
							model.getPassword(),
							newDir.findOrCreateFile("tuples.txt").toJavaFile());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				// finally {
				System.exit(0);
				// }
			}
		});
	}

	private void build(ZTB conn, CDirectory dir, ICProgressMonitor monitor)
			throws Exception {
		// try {
		// System.setOut(new PrintStream(new File("test.out")));
		// } catch (Exception ex) {
		// throw new RuntimeException(ex);
		// }

		KFDataRetriever builder = new KFDataRetriever();
		builder.build(conn, monitor);
		conn.close();

		KFWorld world = builder.getWorld();

		{
			CFile dumpfile = dir.findOrCreateFile("serialized");
			ObjectOutputStream oos = new ObjectOutputStream(
					dumpfile.openOutputStream());
			oos.writeObject(world);
			oos.close();
		}

		{
			CFile file = dir.findOrCreateFile("log.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			table.add(Arrays.asList("crea", "action", "uid", "uname", 
					"obj_id", "obj_type", "obj_info",
					"obj2_id", "obj2_type", "obj2_info"));
			for (KFLog each : world.getLogs()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

		{
			CFile file = dir.findOrCreateFile("notes.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			table.add(Arrays.asList("id", "crea", "modi", "titl", 
					"text", "buildons", "keywords", "supports", "riseaboves"));
			for (KFNote each : world.getNotes()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

		{
			CFile file = dir.findOrCreateFile("authors.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			table.add(Arrays.asList("id", "type", "uname", "name", 
					"stat", "last_login"));
			for (KFAuthor each : world.getAuthors()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

		{
			CFile file = dir.findOrCreateFile("groups.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			table.add(Arrays.asList("id", "name", "members"));
			for (KFGroup each : world.getGroups()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

		{
			CFile file = dir.findOrCreateFile("views.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			table.add(Arrays.asList("id", "crea", "modi", "titl", "elements"));
			for (KFView each : world.getViews()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

		{
			CFile file = dir.findOrCreateFile("attachments.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			table.add(Arrays.asList("id", "crea", "modi", "titl", "path", "mime", "file"));
			for (KFAttachment each : world.getAttachments()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

	}

}
