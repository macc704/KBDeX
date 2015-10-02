/*
 * KDiscourseManagerFrame.java
 * Created on Apr 8, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.app.manager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import clib.common.filesystem.CDirectory;
import clib.common.thread.ICTask;
import clib.view.actions.CAction;
import clib.view.actions.CActionManager;
import clib.view.actions.CActionUtils;
import clib.view.app.javainfo.CJavaInfoPanels;
import clib.view.dialogs.CCreateNameDialog;
import clib.view.list.CDirectoryListModel;
import clib.view.list.CListPanel;
import clib.view.panels.CPanelUtils;
import clib.view.progress.CPanelProcessingMonitor;
import kbdex.app.KBDeX;
import kbdex.app.ext.KKF5Importer;
import kbdex.app.ext.KKF6Importer;
import kbdex.app.ext.KKFDiscourseImporter;
import kbdex.app.ext.KKaniChatCSVImporter;
import kbdex.model.discourse.KDDiscourseFile;

/**
 * @author macchan
 *
 */
public class KDiscourseManagerFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private KDDiscourseManager manager;

	private CListPanel<CDirectory> listPanel = new CListPanel<CDirectory>();
	private CreateNameDialog createNameDialog = new CreateNameDialog(this);

	private CActionManager actionManager = new CActionManager();
	private Action actionOpen = actionManager.createAction("Open",
			new ICTask() {
				public void doTask() {
					doOpen();
				}
			});
	private Action actionRename = actionManager.createAction("Rename",
			new ICTask() {
				public void doTask() {
					doRename();
				}
			});
	private Action actionCopy = actionManager.createAction("Copy",
			new ICTask() {
				public void doTask() {
					doCopy();
				}
			});
	private Action actionDelete = actionManager.createAction("Delete",
			new ICTask() {
				public void doTask() {
					doDelete();
				}
			});

	public KDiscourseManagerFrame(KDDiscourseManager manager,
			String windowTitle) {
		this.manager = manager;
		setTitle(windowTitle);
		initialize();
	}

	private void initialize() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setIconImage(KBDeX.getInstance().getIconImage32());
		initializePanel();
		initializeMenu();
	}

	private void initializeMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		{
			JMenu menu = new JMenu("Import");
			menuBar.add(menu);
			{
				final KKF6Importer importer = new KKF6Importer();
				CAction action = CActionUtils.createAction("From KF6",
						new ICTask() {
							@Override
							public void doTask() {
								importer.doLoad();
								refreshDiscourseList();
							}
						});
				menu.add(action);
			}
			{
				final KKF5Importer importer = new KKF5Importer();
				CAction action = CActionUtils.createAction("From KF5",
						new ICTask() {
							@Override
							public void doTask() {
								importer.doLoad();
								refreshDiscourseList();
							}
						});
				menu.add(action);
			}
			{
				final KKFDiscourseImporter importer = new KKFDiscourseImporter();
				CAction action = CActionUtils.createAction("From KF4",
						new ICTask() {
							@Override
							public void doTask() {
								importer.doLoad();
								refreshDiscourseList();
							}
						});
				menu.add(action);
			}
			{
				final KKaniChatCSVImporter importer = new KKaniChatCSVImporter();
				CAction action = CActionUtils.createAction("From KaniChat CSV",
						new ICTask() {
							@Override
							public void doTask() {
								importer.doImport(KDiscourseManagerFrame.this);
								refreshDiscourseList();
							}
						});
				menu.add(action);
			}
		}

		{
			JMenu menu = new JMenu("Info");
			menuBar.add(menu);
			menu.add(CJavaInfoPanels.createJavaInformationAction());
		}

	}

	private void initializePanel() {

		//ListPanel		
		listPanel.getJList()
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listPanel.setModel(new CDirectoryListModel(manager.getBaseDirectory()));
		listPanel.getJList().getSelectionModel()
				.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						updateButtonState();
					}
				});
		listPanel.refresh();
		listPanel.getJList().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					doOpen();
				}
			}
		});
		getContentPane().add(listPanel);

		//ButtonPanel
		JPanel buttonPanel = CPanelUtils.createListPanel();
		int w = 100;
		int h = new JButton().getPreferredSize().height;

		{
			JButton button = new JButton(actionOpen);
			button.setPreferredSize(new Dimension(w, h));
			buttonPanel.add(button);
		}

		{
			JButton button = new JButton(actionRename);
			button.setPreferredSize(new Dimension(w, h));
			buttonPanel.add(button);
		}

		{
			JButton button = new JButton(actionCopy);
			button.setPreferredSize(new Dimension(w, h));
			buttonPanel.add(button);
		}

		{
			JButton button = new JButton(actionDelete);
			button.setPreferredSize(new Dimension(w, h));
			buttonPanel.add(button);
		}

		updateButtonState();
		getContentPane().add(buttonPanel, BorderLayout.EAST);
	}

	public void openWindowInDefaultSize() {
		setLocation(100, 100);
		setSize(500, 250);
		setVisible(true);
	}

	protected void doOpen() {
		final CDirectory dir = listPanel.getSelectedElement();
		final CPanelProcessingMonitor monitor = KBDeX.getInstance()
				.getMonitor();
		monitor.doTaskWithDialog(new ICTask() {
			public void doTask() {
				try {
					manager.openDiscourse(dir.getNameByString(), monitor);
				} catch (Exception ex) {
					KBDeX.getInstance()
							.handleException(KDiscourseManagerFrame.this, ex);
				}
			}
		});
	}

	protected void doRename() {
		KDDiscourseFile file = new KDDiscourseFile(
				listPanel.getSelectedElement());

		createNameDialog.setTitle("Rename");
		createNameDialog.setDefaultName(file.getName());
		createNameDialog.open();
		if (createNameDialog.getState() != CCreateNameDialog.State.INPUTTED) {
			return;
		}

		file.renameTo(createNameDialog.getInputtedName());
		refreshDiscourseList();
	}

	protected void doCopy() {
		KDDiscourseFile file = new KDDiscourseFile(
				listPanel.getSelectedElement());

		createNameDialog.setTitle("Copy");
		createNameDialog.setDefaultName(file.getName());
		createNameDialog.open();
		if (createNameDialog.getState() != CCreateNameDialog.State.INPUTTED) {
			return;
		}

		file.copyTo(createNameDialog.getInputtedName());
		refreshDiscourseList();
	}

	protected void doDelete() {
		KDDiscourseFile file = new KDDiscourseFile(
				listPanel.getSelectedElement());
		int res = JOptionPane.showConfirmDialog(this,
				"Do you want to delete the file \"" + file.getName() + "\" ?",
				"Final Confirm", JOptionPane.WARNING_MESSAGE);
		if (res != JOptionPane.OK_OPTION) {
			return;
		}

		file.delete();
		refreshDiscourseList();
	}

	private void updateButtonState() {
		boolean active = listPanel.getSelectedElement() != null;
		actionManager.setActionStates(active);
	}

	public void refreshDiscourseList() {
		listPanel.refresh();
	}

	class CreateNameDialog extends CCreateNameDialog {
		private static final long serialVersionUID = 1L;

		public CreateNameDialog(Frame owner) {
			super(owner);
		}

		protected String validCheck(String text) {
			if (manager.existsDiscourse(text)) {
				return "ERROR: The name is already used.";
			} else {
				return null;
			}
		}

		protected String getInputTitle() {
			return "Name";
		}
	};
}
