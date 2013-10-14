/*
 * CFileChooserPanel.java
 * Created on 2011/11/12
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.app.ext;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CPath;
import clib.common.string.CStringChopper;

/**
 * @author macchan
 */
public class CFileChooserPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final String DELIM = ";";

	private JFileChooser chooser = new JFileChooser();
	private JTextField field = new JTextField();
	private JButton button = new JButton();

	private JFrame parent;
	private int len;

	public CFileChooserPanel(JFrame parent, int len) {
		this.parent = parent;
		this.len = len;
		initialize();
		setDir(CFileSystem.getExecuteDirectory());
	}

	public JFileChooser getChooser() {
		return chooser;
	}

	public void setFilter(String description, String... extensions) {
		chooser.setFileFilter(new FileNameExtensionFilter(description,
				extensions));
	}

	public void setDir(CDirectory dir) {
		setFilesToField(new File[] { dir.getAbsolutePath().toJavaFile() });
	}

	private void initialize() {
		setLayout(new FlowLayout());
		field.setColumns(len);
		add(field);
		button.setText("Select");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				open();
			}
		});
		add(button);
	}

	private void open() {
		//Initializing Process 
		chooser.setSelectedFiles(getFilesFromField());

		//Process
		int res = chooser.showDialog(parent, "OK");

		//Finalizing Process
		if (res == JFileChooser.APPROVE_OPTION) {
			File[] files = chooser.getSelectedFiles();
			setFilesToField(files);
		}
	}

	private void setFilesToField(File[] files) {
		String pathString = "";
		for (File file : files) {
			pathString += file.getAbsolutePath().toString() + DELIM;
		}
		pathString = CStringChopper.chopped(pathString);

		field.setText(pathString);
		refreshCaretPosition();
	}

	private File[] getFilesFromField() {
		String pathString = field.getText();
		String[] paths = pathString.split(DELIM);
		File[] files = new File[paths.length];
		for (int i = 0; i < paths.length; i++) {
			files[i] = new File(paths[i]);
		}
		return files;
	}

	private void refreshCaretPosition() {
		int len = field.getText().length();
		field.setCaretPosition(len);
		field.requestFocus();
	}

	public boolean hasSelectedFile() {
		return getSelectedFiles() != null;
	}

	public CFile getSelectedFile() {
		List<CFile> files = getSelectedFiles();
		if (files.size() > 0) {
			return null;
		}
		return files.get(0);
	}

	public List<CFile> getSelectedFiles() {
		List<CFile> cFiles = new ArrayList<CFile>();
		List<CPath> paths = getSelectedPaths();
		for (CPath path : paths) {
			if (path != null && path.exists() && path.isFile()) {
				cFiles.add(CFileSystem.findFile(path));
			}
		}
		return cFiles;
	}

	private List<CPath> getSelectedPaths() {
		File[] files = getFilesFromField();
		List<CPath> paths = new ArrayList<CPath>();
		for (File file : files) {
			//if (file.exists()) {
			paths.add(new CPath(file));
			//}
		}
		return paths;
	}
}
