/*
 * Main.java
 * Created on Oct 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kfl.app.kfn;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import kfl.builder.KFDataRetriever;
import kfl.connector.KFConnector;
import kfl.connector.KFLoginModel;
import kfl.model.KFAttachment;
import kfl.model.KFAuthor;
import kfl.model.KFGroup;
import kfl.model.KFLog;
import kfl.model.KFNote;
import kfl.model.KFOwnerObject;
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
	public static void main(String[] args) throws Exception {
		new Main().doLoad();
	}

	@SuppressWarnings("unused")
	private static DateFormat format = new SimpleDateFormat("-yyyyMMdd-HHmmss");

	// private static DateFormat dformat = new SimpleDateFormat(
	// "yy/MM/dd-HH:mm:ss");

	private void doLoad() throws Exception {
		// create model
		final KFLoginModel model = new KFLoginModel();

		model.setPort(80);

		File propfile = new File("kfloader.ini");
		if (!propfile.exists()) {
			propfile.createNewFile();
		}
		Properties prop = new Properties();
		prop.load(new FileReader(propfile));

		model.setHost(prop.getProperty("host", "builder.ikit.org"));
		model.setDBName(prop.getProperty("db", "Susana_test"));
		model.setUser(prop.getProperty("user", ""));
		model.setPassword(prop.getProperty("pass", ""));

		// connect
		final ZTB conn = KFConnector.connectWithDialog(model);
		if (conn == null) {
			return;
		}

		CDirectory baseDir = CFileSystem.getExecuteDirectory()
				.findOrCreateDirectory("kf.out");
		// final CDirectory newDir = baseDir.findOrCreateDirectory(model
		// .getDBName() + format.format(new Date()));
		final CDirectory newDir = baseDir.findOrCreateDirectory("test");

		prop.setProperty("host", model.getHost());
		prop.setProperty("db", model.getDBName());
		prop.setProperty("user", model.getUser());
		prop.setProperty("pass", model.getPassword());
		prop.store(new FileWriter(propfile), "");

		// do task (build)
		final CPanelProcessingMonitor monitor = new CPanelProcessingMonitor();
		monitor.doTaskWithDialog(new ICTask() {
			public void doTask() {
				try {
					// build(conn, newDir, monitor);
					// new DataDump().dump(
					// model.getHost(),
					// model.getPort(),
					// model.getDBName(),
					// model.getUser(),
					// model.getPassword(),
					// newDir.findOrCreateFile("objects.txt").toJavaFile(),
					// newDir.findOrCreateFile("links.txt").toJavaFile());
					new DataSerialize().dump(model.getHost(), model.getPort(),
							model.getDBName(), model.getUser(),
							model.getPassword(), newDir);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				// finally {
				System.exit(0);
				// }
			}
		});

	}

	@SuppressWarnings("unused")
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
			table.add(KFLog.header());
			for (KFLog each : world.getLogs()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

		{
			CFile file = dir.findOrCreateFile("notes.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			List<String> header = KFNote.header();
			header.add("viewId");
			header.add("authorId");
			table.add(header);
			for (KFNote each : world.getNotes()) {
				for (KFView eachView : each.getViews()) {
					for (KFOwnerObject eachAuthor : each.getAuthors()) {
						List<String> row = each.getStrings();
						row.add(eachView.getIdAsString());
						row.add(eachAuthor.getIdAsString());
						table.add(row);
					}
				}
			}
			CCSVFileIO.saveByListList(table, file);
		}

		{
			CFile file = dir.findOrCreateFile("authors.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			table.add(KFAuthor.header());
			for (KFAuthor each : world.getAuthors()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

		{
			CFile file = dir.findOrCreateFile("groups.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			table.add(KFGroup.header());
			for (KFGroup each : world.getGroups()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

		{
			CFile file = dir.findOrCreateFile("views.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			table.add(KFView.header());
			for (KFView each : world.getViews()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

		{
			CFile file = dir.findOrCreateFile("attachments.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			table.add(KFAttachment.header());
			for (KFAttachment each : world.getAttachments()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

	}

}
