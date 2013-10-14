/*
 * KMetricsChooser.java
 * Created on 2011/11/26
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.network.metrics;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import kbdex.controller.metrics.IKTemporalMetricsScorer;
import kbdex.controller.metrics.KMetricsScorerManager;
import kbdex.controller.metrics.KMetricsScorerSuite;
import kbdex.model.kbmodel.KBElement;

/**
 * @author macchan
 */
public abstract class KAbstractMetricsScorerChooserPanel<V extends KBElement>
		extends JPanel {

	private static final long serialVersionUID = 1L;

	private KMetricsScorerManager<V> manager;

	private JPanel panel = new JPanel();

	public KAbstractMetricsScorerChooserPanel(KMetricsScorerManager<V> manager) {
		this.manager = manager;
		initialize();
		refresh();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		add(new JScrollPane(panel));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	}

	public void refresh() {
		panel.removeAll();

		for (KMetricsScorerSuite<V> suite : manager.getScorers()) {
			if (suite.isActive()) {
				for (IKTemporalMetricsScorer scorer : getChoosableScorers(suite)) {
					panel.add(getCheckBoxPanel(scorer));
				}
			}
		}

		panel.validate();
		panel.repaint();
	}

	protected abstract List<IKTemporalMetricsScorer> getChoosableScorers(
			KMetricsScorerSuite<V> suite);

	private Map<IKTemporalMetricsScorer, CheckBoxPanel> checkboxes = new HashMap<IKTemporalMetricsScorer, CheckBoxPanel>();

	public CheckBoxPanel getCheckBoxPanel(IKTemporalMetricsScorer scorer) {
		if (!checkboxes.containsKey(scorer)) {
			CheckBoxPanel cb = new CheckBoxPanel(scorer);
			checkboxes.put(scorer, cb);
		}
		return checkboxes.get(scorer);
	}

	class CheckBoxPanel extends JCheckBox {
		private static final long serialVersionUID = 1L;

		IKTemporalMetricsScorer scorer;

		CheckBoxPanel(IKTemporalMetricsScorer scorer) {
			this.scorer = scorer;
			setText(scorer.getScorer().getName());
		}

		IKTemporalMetricsScorer getScorer() {
			return scorer;
		}
	}

	public List<IKTemporalMetricsScorer> getSelectedScorers() {
		List<IKTemporalMetricsScorer> scorers = new ArrayList<IKTemporalMetricsScorer>();
		for (Component c : panel.getComponents()) {
			@SuppressWarnings("unchecked")
			CheckBoxPanel cbPanel = (CheckBoxPanel) c;
			if (cbPanel.isSelected()) {
				scorers.add(cbPanel.getScorer());
			}
		}
		return scorers;
	}
}
