/*
 * KGroupSelectionPanel.java
 * Created on Mar 4, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.discourse.selection.agent;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import kbdex.model.discourse.KDAgent;
import kbdex.model.discourse.KDGroup;
import kbdex.model.discourse.KDDiscourse;
import kbdex.model.discourse.filters.KAgentNameDiscourseFilter;
import clib.view.panels.CCheckboxListPanel;
import clib.view.panels.CPanelUtils;

/**
 * @author macchan
 * 
 */
public class KAgentFilteringPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private KDDiscourse discourse;

	private CCheckboxListPanel<String> agentPanel = new CCheckboxListPanel<String>();
	private JPanel groupPanel = CPanelUtils.createListPanel();;

	public KAgentFilteringPanel(KDDiscourse discourse) {
		this.discourse = discourse;
		initialize();// view
		initializeData();
	}

	private void initialize() {
		setLayout(new BorderLayout());

		// Agent Panel
		agentPanel.setBorder(BorderFactory.createTitledBorder("Agent"));
		add(agentPanel);

		// Group Panel
		JPanel groupTitlePanel = new JPanel();
		groupTitlePanel.setBorder(BorderFactory.createTitledBorder("Group"));
		add(groupTitlePanel, BorderLayout.WEST);

		JScrollPane scrollGroup = new JScrollPane();
		groupTitlePanel.add(scrollGroup);
		scrollGroup.setViewportView(groupPanel);
	}

	private void initializeData() {
		// Agent
		List<String> agentNames = new ArrayList<String>();
		for (KDAgent agent : discourse.getAllAgents()) {
			agentNames.add(agent.getName());
		}
		agentPanel.addModels(agentNames);
		agentPanel.setSelection(discourse.getAgentFilter().getAgentNames());

		// Group
		for (KDGroup group : discourse.getGroups()) {
			if (group.getName().length() <= 0) {
				continue;
			}
			addGroup(group);
		}
	}

	private void addGroup(final KDGroup group) {
		JButton button = new JButton(group.getName());
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean isShiftDown = (e.getModifiers() & MouseEvent.SHIFT_MASK) == MouseEvent.SHIFT_MASK;
				if (!isShiftDown) {
					agentPanel.setSelection(group.getMemberNames());
				} else {
					agentPanel.addSelection(group.getMemberNames());
				}
			}
		});
		groupPanel.add(button);
	}

	public KAgentNameDiscourseFilter getAgentFilter() {
		List<String> agents = agentPanel.getSelectedModels();
		KAgentNameDiscourseFilter filter = new KAgentNameDiscourseFilter(agents);
		return filter;
	}
}
