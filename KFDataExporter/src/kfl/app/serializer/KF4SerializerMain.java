/*
 * Main.java
 * Created on Oct 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kfl.app.serializer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import kfl.kf4.connector.KFConnector;
import kfl.kf4.connector.KFLoginModel;
import kfl.kf4.serializer.KFAllAttachmentDownloader;
import kfl.kf4.serializer.KFDataSerializer;

import org.zoolib.tuplebase.ZTB;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;
import clib.common.thread.ICTask;
import clib.view.progress.CPanelProcessingMonitor;

/*
 * @author macchan 
 * http://analysis.ikit.org/kfcoders/index.php/Main_Page
 * 
 */
public class KF4SerializerMain {
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new KF4SerializerMain().doLoad();
	}

	private static DateFormat format = new SimpleDateFormat("-yyyyMMdd-HHmmss");

	private void doLoad() throws Exception {
		final KFLoginModel model = new KFLoginModel();

		// initialize
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
		final CDirectory newDir = baseDir.findOrCreateDirectory(model
				.getDBName() + format.format(new Date()));
		// final CDirectory newDir =
		// baseDir.findOrCreateDirectory("ICS Lab School-20150112-174341");

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
					new KFDataSerializer().dump(model, newDir);
					new KFAllAttachmentDownloader().start(model, newDir);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				System.out.println("kf4data serialized to "
						+ newDir.getNameByString());
				System.exit(0);
			}
		});
	}
}
