/*
 * KDDiscourseManager.java
 * Created on Jul 20, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.app.manager;

import java.util.Locale;

import javax.swing.JFrame;

import kbdex.controller.KDiscourseController;
import kbdex.model.discourse.KDDiscourse;
import kbdex.model.discourse.KDDiscourse.Language;
import kbdex.model.discourse.KDDiscourseFile;
import kbdex.view.KDiscourseControllerView;
import clib.common.filesystem.CDirectory;
import clib.common.utils.ICProgressMonitor;

/**
 * @author macchan
 */
public class KDDiscourseManager {

	private CDirectory dataDir;

	public KDDiscourseManager(CDirectory baseDir) {
		this.dataDir = baseDir;
	}

	public CDirectory getBaseDirectory() {
		return dataDir;
	}

	public JFrame openDiscourse(String name, final ICProgressMonitor monitor) {
		CDirectory dir = dataDir.findOrCreateDirectory(name);
		KDDiscourseFile discourseFile = new KDDiscourseFile(dir);
		KDDiscourse discourse = new KDDiscourse(discourseFile, monitor);
		KDiscourseController controller = new KDiscourseController(discourse);
		KDiscourseControllerView view = new KDiscourseControllerView(controller);
		JFrame frame = view.openMainFrame();
		return frame;
	}

	public boolean existsDiscourse(String name) {
		return dataDir.getAbsolutePath().appendedPath(name).exists();
	}

	public KDDiscourseFile createNewDiscourse(String name) {
		name = createNonDuplicatedName(name);
		CDirectory dir = dataDir.findOrCreateDirectory(name);
		KDDiscourseFile dFile = new KDDiscourseFile(dir);
		if (Locale.getDefault().getCountry().contains("JP")) {
			dFile.saveLanguage(Language.JAPANESE);
		}
		return new KDDiscourseFile(dir);
	}

	private String createNonDuplicatedName(String baseName) {
		String name = baseName;
		int counter = 2;
		while (existsDiscourse(name)) {
			name = baseName + "_" + (counter++);
		}
		return name;
	}

	//	void doCutSentence() {
	//		String name = doCopy();
	//		if (name == null) {
	//			return;
	//		}
	//
	//		KDDiscourseFile file = new KDDiscourseFile(baseDir.findDirectory(name));
	//		file.load();
	//		KDDiscourse discourse = file.getDiscourse();
	//		List<KDDiscourseRecord> newRecords = new ArrayList<KDDiscourseRecord>();
	//		List<KDDiscourseRecord> records = discourse.getAllRecords();
	//		long id = 0;
	//		for (KDDiscourseRecord record : records) {
	//			String text = record.getText();
	//			String[] texts = text.split("[.] |。");
	//			for (int i = 0; i < texts.length; i++) {
	//				if (texts[i].length() != 0) {
	//					id++;
	//					KDDiscourseRecord newRecord = new KDDiscourseRecord(id,
	//							record.getAgentName(), texts[i] + ".");
	//					newRecords.add(newRecord);
	//				}
	//			}
	//		}
	//
	//		file.saveRecords(newRecords);
	//	}

}
