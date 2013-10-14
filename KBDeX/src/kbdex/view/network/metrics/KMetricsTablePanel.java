/*
 * KMetricsGraphPanel.java
 * Created on 2011/11/26
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.network.metrics;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import kbdex.controller.KDiscourseController;
import kbdex.controller.KDiscourseControllerAdapter;
import kbdex.controller.KNNetworkController;
import kbdex.controller.metrics.IKTemporalMetricsScorer;
import kbdex.controller.metrics.KGraphTemporalMetricsScorer;
import kbdex.controller.metrics.KVertexTemporalMetricsScorer;
import kbdex.model.kbmodel.KBElement;
import clib.view.table.common.CTableUtils;

/**
 * @author macchan
 */
public class KMetricsTablePanel<V extends KBElement> extends JPanel {

	private static final long serialVersionUID = 1L;

	private KDiscourseController discourseController;
	private KNNetworkController<V> networkController;
	private List<IKTemporalMetricsScorer> scorers;

	private JTable table = new JTable();

	public KMetricsTablePanel(KDiscourseController discourseController,
			KNNetworkController<V> networkController,
			List<IKTemporalMetricsScorer> scorers) {
		this.discourseController = discourseController;
		this.networkController = networkController;
		this.scorers = scorers;
		initialize();
		refresh();
	}

	private void initialize() {
		setLayout(new BorderLayout());

		table.setAutoCreateRowSorter(true);
		JScrollPane scroll = new JScrollPane(table);
		add(scroll);

		addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent event) {
				networkController
						.addNetworkControllerListener(discourseListener);
			}

			@Override
			public void ancestorRemoved(AncestorEvent event) {
				networkController
						.removeNetworkControllerListener(discourseListener);
			}

			@Override
			public void ancestorMoved(AncestorEvent event) {
			}

		});

		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (e.getValueIsAdjusting()) {
							return;
						}
						refreshSelectionViewToModel();
					}
				});
	}

	private KDiscourseControllerAdapter discourseListener = new KDiscourseControllerAdapter() {
		@Override
		public void refreshWithAnimation() {
			refresh();
		}
	};

	private void refreshSelectionViewToModel() {
		List<Integer> selectedIndexs = CTableUtils.getSelectedModelRows(table);
		List<V> selected = new ArrayList<V>();
		TableModel model = table.getModel();
		for (int i = 0; i < selectedIndexs.size(); i++) {
			Object o = model.getValueAt(selectedIndexs.get(i), 0);
			if (o instanceof KBElement) {
				@SuppressWarnings("unchecked")
				V v = (V) o;
				selected.add(v);
			}
		}
		if (selected.size() > 0) {
			networkController.selectModel(selected);
		}
	}

	private void refresh() {
		if (scorers.size() <= 0) {
			return;
		}

		if (scorers.get(0) instanceof KVertexTemporalMetricsScorer) {
			refreshForVertices();
		} else if (scorers.get(0) instanceof KGraphTemporalMetricsScorer) {
			refreshForGraph();
		} else {
			throw new RuntimeException();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void refreshForVertices() {
		DefaultTableModel tableModel = new DefaultTableModel();

		List<KVertexTemporalMetricsScorer<V>> vScorers = new ArrayList<KVertexTemporalMetricsScorer<V>>();
		for (IKTemporalMetricsScorer scorer : this.scorers) {
			if (scorer instanceof KVertexTemporalMetricsScorer) {
				vScorers.add((KVertexTemporalMetricsScorer) scorer);
			}
		}

		//Header
		tableModel.addColumn("Name");
		for (KVertexTemporalMetricsScorer<V> scorer : vScorers) {
			tableModel.addColumn(scorer.getVertexScorer().getName());
		}

		int frameNo = discourseController.getFrameNo();

		for (V v : vScorers.get(0).getVertices()) {
			Vector<Object> vec = new Vector<Object>();
			vec.add(v);
			for (KVertexTemporalMetricsScorer<V> scorer : vScorers) {
				vec.add(scorer.getDoubleValue(frameNo, v));
			}
			tableModel.addRow(vec);
		}

		table.setModel(tableModel);
		tableModel.fireTableStructureChanged();
		repaint();
	}

	private void refreshForGraph() {
		DefaultTableModel tableModel = new DefaultTableModel();

		List<KGraphTemporalMetricsScorer> gScorers = new ArrayList<KGraphTemporalMetricsScorer>();
		for (IKTemporalMetricsScorer scorer : this.scorers) {
			if (scorer instanceof KGraphTemporalMetricsScorer) {
				gScorers.add((KGraphTemporalMetricsScorer) scorer);
			}
		}

		//Header
		tableModel.addColumn("Name");
		tableModel.addColumn("Value");

		int frameNo = discourseController.getFrameNo();

		for (KGraphTemporalMetricsScorer scorer : gScorers) {
			Vector<Object> vec = new Vector<Object>();
			vec.add(scorer.getScorer().getName());
			vec.add(scorer.getDoubleValue(frameNo));
			tableModel.addRow(vec);
		}

		table.setModel(tableModel);
		tableModel.fireTableStructureChanged();
		repaint();
	}
}
