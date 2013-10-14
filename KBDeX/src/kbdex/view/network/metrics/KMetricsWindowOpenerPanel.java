/*
 * KMetricsWindowMaker.java
 * Created on 2011/11/26
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.network.metrics;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import kbdex.controller.KDiscourseController;
import kbdex.controller.KNNetworkController;
import kbdex.controller.metrics.IKTemporalMetricsScorer;
import kbdex.model.kbmodel.KBElement;
import kbdex.view.KDiscourseControllerView;
import kbdex.view.KScreenShotMessageCreater;

/**
 * @author macchan
 */
public class KMetricsWindowOpenerPanel<V extends KBElement> extends JPanel {

	private static final long serialVersionUID = 1L;

	private KDiscourseController discourseController;
	private KNNetworkController<V> networkController;
	private KDiscourseControllerView desktop;

	private KAbstractMetricsScorerChooserPanel<V> chooser;

	private boolean activeDataAdded = false;

	public KMetricsWindowOpenerPanel(KDiscourseController discourseController,
			KNNetworkController<V> networkController,
			KDiscourseControllerView desktop,
			KAbstractMetricsScorerChooserPanel<V> chooser) {
		this.discourseController = discourseController;
		this.networkController = networkController;
		this.chooser = chooser;
		this.desktop = desktop;
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());

		//chooser
		add(this.chooser, BorderLayout.CENTER);

		//buttonpanel
		JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.EAST);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		{
			JButton button = new JButton("Chart");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					openChart();
				}
			});
			buttonPanel.add(button);
		}

		{
			JButton button = new JButton("Table");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					openTable();
				}
			});
			buttonPanel.add(button);
		}
	}

	private void collectDataIfNeed() {
		if (this.activeDataAdded) {
			discourseController.doReloadToThisFrame(false);
			this.activeDataAdded = false;
		}
	}

	public void refresh(boolean selected) {
		if (selected) {
			this.activeDataAdded = true;
		}
		chooser.refresh();
	}

	protected void openChart() {
		collectDataIfNeed();
		openChart(discourseController, networkController, desktop,
				chooser.getSelectedScorers());
	}

	public static <K extends KBElement> void openChart(
			KDiscourseController discourseController,
			KNNetworkController<K> networkController,
			KDiscourseControllerView desktop,
			List<IKTemporalMetricsScorer> scorers) {

		KMetricsChartPanel<K> graphPanel = new KMetricsChartPanel<K>(
				discourseController, networkController, scorers);
		JMenuBar menuBar = new JMenuBar();
		JInternalFrame f = desktop.openInternalFrame(
				graphPanel,
				"MetricsChartViewer - "
						+ networkController.getNetworkTypeName(), menuBar,
				new Rectangle(100, 100, 640, 480));
		KScreenShotMessageCreater.addScreenShotFunction(menuBar, f,
				discourseController);
	}

	protected void openTable() {
		collectDataIfNeed();
		KMetricsTablePanel<V> tablePanel = new KMetricsTablePanel<V>(
				discourseController, networkController,
				chooser.getSelectedScorers());
		JMenuBar menuBar = new JMenuBar();
		JInternalFrame f = desktop.openInternalFrame(
				tablePanel,
				"MetricsTableViewer - "
						+ networkController.getNetworkTypeName(), menuBar,
				new Rectangle(100, 100, 640, 480));
		KScreenShotMessageCreater.addScreenShotFunction(menuBar, f,
				discourseController);
	}

}
