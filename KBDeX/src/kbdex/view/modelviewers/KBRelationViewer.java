/*
 * KBRelationViewer.java
 * Created on Apr 23, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.modelviewers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import kbdex.model.kbmodel.IKBReason;
import kbdex.model.kbmodel.KBDiscourseUnit;
import kbdex.model.kbmodel.KBObjectSharingReason;
import kbdex.model.kbmodel.KBRelation;
import kbdex.view.IKWindowManager;
import clib.common.model.ICModelChangeListener;
import clib.view.list.CListPanel;

/**
 * @author macchan
 *
 */
public class KBRelationViewer<R extends IKBReason> extends JPanel {

	private static final long serialVersionUID = 1L;

	private KBRelation model;
	private IKWindowManager wManager;

	private CListPanel<R> listPanel = new CListPanel<R>();

	public KBRelationViewer(KBRelation model, IKWindowManager wManager) {
		this.model = model;
		this.wManager = wManager;
		initialize();
		refresh();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		listPanel.setPreferredSize(new Dimension(400, 300));
		add(listPanel, BorderLayout.CENTER);
		listPanel.getJList().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						List<R> elements = listPanel.getSelectedElements();
						doSelect(elements);
					}
				});
		model.addModelListener(new ICModelChangeListener() {
			public void modelUpdated(Object... args) {
				refresh();
			}
		});
		listPanel.getJList().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (listPanel.getSelectedElements().size() == 1) {
						R r = listPanel.getSelectedElements().get(0);
						if (r instanceof KBObjectSharingReason<?>) {
							Object shared = ((KBObjectSharingReason<?>) r)
									.getSharedObject();
							if (shared instanceof KBDiscourseUnit) {
								KBDiscourseUnitViewer viewer = new KBDiscourseUnitViewer(
										(KBDiscourseUnit) shared);
								wManager.openFrame(viewer, viewer.getTitle(),
										null, null);
							}
						}
					}
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void refresh() {
		listPanel.setList((List<R>) model.getReasons());
	}

	public String toParamString() {
		return model.toParamString();
	}

	public void doSelect(List<R> selected) {
		//for overriding
		//default nothing
	}
}
