/*
 * KKaniChartImporterUI.java
 * Created on 2011/11/12
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.app.ext;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import kbdex.app.KBDeX;
import kbdex.app.manager.KDDiscourseManager;
import kbdex.model.discourse.KDDiscourseFile;
import clib.common.filesystem.CFile;

/**
 * @author macchan
 *
 */
public class KKaniChatCSVImporter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new KKaniChatCSVImporter().doImport(null);
		System.exit(0);
	}

	public void doImport(JFrame owner) {
		JPanel panel = new JPanel(new BorderLayout());
		CFileChooserPanel chooserPanel = new CFileChooserPanel(owner, 30);
		chooserPanel.setFilter("CSV file", "csv");
		chooserPanel.getChooser().setMultiSelectionEnabled(true);
		chooserPanel.getChooser().setFileSelectionMode(JFileChooser.FILES_ONLY);
		panel.add(chooserPanel, BorderLayout.CENTER);
		JLabel label = new JLabel("Please Select a File to Import");
		panel.add(label, BorderLayout.NORTH);
		int res = JOptionPane.showConfirmDialog(owner, panel,
				"Import KaniChat CSV File", JOptionPane.OK_CANCEL_OPTION);
		if (res != JOptionPane.OK_OPTION) {
			return;
		}
		if (!chooserPanel.hasSelectedFile()) {
			return;
		}
		List<CFile> files = chooserPanel.getSelectedFiles();
		for (CFile file : files) {
			try {
				convertOne(file);
			} catch (Exception ex) {
				ex.printStackTrace();
				//do nothing special
			}
		}
	}

	private void convertOne(CFile inFile) {
		String name = inFile.getName().getName();
		KDDiscourseManager dManager = KBDeX.getInstance().getDiscourseManager();
		KDDiscourseFile dFile = dManager.createNewDiscourse(name);
		CFile outFile = dFile.getRecordFile();

		KKaniChatCSVConverter converter = new KKaniChatCSVConverter();
		converter.convert(inFile, outFile);
	}
}
