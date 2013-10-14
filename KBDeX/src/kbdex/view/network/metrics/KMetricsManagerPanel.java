/*
 * KMetricsManagerPanel.java
 * Created on 2011/11/26
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.network.metrics;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import kbdex.controller.KDiscourseController;
import kbdex.controller.KNNetworkController;
import kbdex.controller.metrics.KMetricsScorerSuite;
import kbdex.model.kbmodel.KBElement;
import kbdex.view.KDiscourseControllerView;

/**
 * @author macchan
 */
public class KMetricsManagerPanel<V extends KBElement> extends JPanel {

	private static final long serialVersionUID = 1L;

	private KDiscourseController discourseController;
	private KNNetworkController<V> networkController;
	private KDiscourseControllerView desktop;

	private KMetricsWindowOpenerPanel<V> graphWindowMakerPanel;
	private KMetricsWindowOpenerPanel<V> vertexWindowMakerPanel;

	public KMetricsManagerPanel(KDiscourseController discourseController,
			KNNetworkController<V> networkController,
			KDiscourseControllerView desktop) {
		this.discourseController = discourseController;
		this.networkController = networkController;
		this.desktop = desktop;
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());

		JSplitPane splitter1 = new JSplitPane();
		splitter1.setResizeWeight(0.0);
		add(splitter1);

		ActivationPanel activationPanel = new ActivationPanel();
		activationPanel.setBorder(BorderFactory
				.createTitledBorder("Activation"));
		splitter1.setLeftComponent(activationPanel);

		JSplitPane splitter2 = new JSplitPane();
		splitter2.setResizeWeight(0.5);
		splitter1.setRightComponent(splitter2);

		graphWindowMakerPanel = new KMetricsWindowOpenerPanel<V>(
				discourseController, networkController, desktop,
				new KGraphMetricsScorerChooserPanel<V>(
						networkController.getMetricsManager()));
		graphWindowMakerPanel.setBorder(BorderFactory
				.createTitledBorder("Open GraphMetricsViewer"));
		splitter2.setLeftComponent(graphWindowMakerPanel);

		vertexWindowMakerPanel = new KMetricsWindowOpenerPanel<V>(
				discourseController, networkController, desktop,
				new KVertexMetricsScorerChooserPanel<V>(
						networkController.getMetricsManager()));
		vertexWindowMakerPanel.setBorder(BorderFactory
				.createTitledBorder("Open NodeMetricsViewer"));
		splitter2.setRightComponent(vertexWindowMakerPanel);
	}

	class ActivationPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		public ActivationPanel() {
			setLayout(new BorderLayout());

			//ScoreListPanel
			JPanel scoreListPanel = new JPanel();
			scoreListPanel.setLayout(new BoxLayout(scoreListPanel,
					BoxLayout.Y_AXIS));
			add(scoreListPanel, BorderLayout.CENTER);

			// Each
			for (final KMetricsScorerSuite<V> score : networkController
					.getMetricsManager().getScorers()) {
				final JCheckBox checkbox = new JCheckBox(score.getName());
				scoreListPanel.add(checkbox);
				checkbox.setSelected(score.isActive());
				checkbox.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						boolean selected = checkbox.isSelected();
						score.setActive(selected);
						graphWindowMakerPanel.refresh(selected);
						vertexWindowMakerPanel.refresh(selected);
					}
				});
			}

			//RefreshPanel
			JPanel panel = new JPanel();
			add(panel, BorderLayout.SOUTH);
			JButton button = new JButton("Refresh");
			panel.add(button);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					doRefresh();
				}
			});
		}
	}

	private void doRefresh() {
		discourseController.doReloadToThisFrame(true);
	}
}
