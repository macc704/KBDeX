/*
 * KWordSelectionPanel.java
 * Created on Jul 20, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.discourse.selection.word;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import kbdex.app.KBDeX;
import kbdex.model.discourse.KDDiscourse;
import kbdex.view.discourse.KDiscourseViewerPanel;
import clib.common.system.CJavaSystem;
import clib.common.thread.ICTask;
import clib.view.dialogs.ICOKCancelDialogListener;
import clib.view.editor.ICDirtyStateListener;
import clib.view.progress.CPanelProcessingMonitor;

/**
 * @author macchan
 */
public class KWordSelectionPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private KDDiscourse discourse;

	private KWordSelectionTextEditor textEditor = new KWordSelectionTextEditor();
	private KHistgramViewer histgramViewer = new KHistgramViewer();
	private KDiscourseViewerPanel discourseViewer = new KDiscourseViewerPanel();

	public KWordSelectionPanel(KDDiscourse discourse, boolean showInvalidRecords) {
		this.discourse = discourse;
		discourseViewer.setShowInvalidRecord(showInvalidRecords);
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());

		JSplitPane splitter1 = new JSplitPane();
		add(splitter1);

		textEditor.setPreferredSize(new JScrollPane(new JTable())
				.getPreferredSize());
		textEditor
				.setBorder(BorderFactory.createTitledBorder("Selected Words"));
		splitter1.setLeftComponent(textEditor);
		JSplitPane splitter2 = new JSplitPane();
		splitter1.setRightComponent(splitter2);

		splitter2.setLeftComponent(histgramViewer);
		splitter2.setRightComponent(discourseViewer);

		//"preferredSize"で配置が決まる
		splitter1.setResizeWeight(0.3);
		splitter2.setResizeWeight(0.5);

		textEditor.setFile(discourse.getFile().getSelectedWordFile());
		histgramViewer.setDiscourse(discourse);
		histgramViewer.addWordSelectionListener(new IKWordSelectionListener() {
			public void wordSelected(List<String> words) {
				textEditor.addWords(words);
				doSave();
			}

			public void wordDeselected(List<String> words) {
				textEditor.removeWords(words);
				doSave();
			}
		});
		discourseViewer.setDiscourse(discourse);
	}

	public JMenu createMenu() {
		JMenu menuFile = new JMenu("File");
		menuFile.add(createSaveAction());
		menuFile.add(createReloadAction());

		return menuFile;
	}

	private Action createSaveAction() {
		Action action = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				doSave();
			}
		};
		action.putValue(Action.NAME, "Save");
		action.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
		if (CJavaSystem.getInstance().isMac()) {
			action.putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.META_MASK));
		}
		return action;
	}

	private Action createReloadAction() {
		Action action = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				doReload();
			}
		};
		action.putValue(Action.NAME, "Load");
		action.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		return action;
	}

	private void doSave() {
		textEditor.doSave();
		doReload();
	}

	private void doReload() {
		final CPanelProcessingMonitor monitor = KBDeX.getInstance()
				.getMonitor();
		monitor.doTaskWithDialog(new ICTask() {
			public void doTask() {
				textEditor.doLoad();
				//discourse.reloadSelectedWords(monitor);
				discourse.reloadFilterTexts(monitor);
				discourseViewer.refreshView();
				histgramViewer.refreshVocaburary();
				histgramViewer.refreshSelectedWords();
			}
		});
	}

	public ICOKCancelDialogListener getOKCancelListener() {
		return new ICOKCancelDialogListener() {

			@Override
			public boolean canOkProcess() {
				return askSaveIfDirty();
			}

			@Override
			public boolean canCancelProcess() {
				return askSaveIfDirty();
			}
		};
	}

	private boolean askSaveIfDirty() {
		if (!textEditor.isDirty()) {
			return true;//no problem
		}

		//in case of dirty
		int res = JOptionPane.showConfirmDialog(null,
				"Would you like save words?", "Your word list is not saved",
				JOptionPane.YES_NO_CANCEL_OPTION);
		if (res == JOptionPane.YES_OPTION) {
			textEditor.doSave();
			return true;
		} else if (res == JOptionPane.NO_OPTION) {
			return true;
		} else {//cancel
			return false;
		}
	}

	public void setDirtyStateListener(ICDirtyStateListener dirtyStateListener) {
		this.textEditor.setDirtyStateListener(dirtyStateListener);
	}
}
