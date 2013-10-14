/*
 * KMetricsGraphPanel.java
 * Created on 2011/11/26
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.network.metrics;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import kbdex.controller.KDiscourseController;
import kbdex.controller.KDiscourseControllerAdapter;
import kbdex.controller.KNNetworkController;
import kbdex.controller.metrics.IKTemporalMetricsScorer;
import kbdex.controller.metrics.KGraphTemporalMetricsScorer;
import kbdex.controller.metrics.KVertexTemporalMetricsScorer;
import kbdex.model.kbmodel.KBElement;
import clib.common.model.ICModelChangeListener;
import clib.view.chart.controller.CVChartController;
import clib.view.chart.controller.CVNavigator;
import clib.view.chart.controller.ICVChartDataSelectionListener;
import clib.view.chart.model.CAxis;
import clib.view.chart.model.CDefaultDataSet;
import clib.view.chart.model.CUnit;
import clib.view.chart.model.ICDataSet;
import clib.view.chart.viewer.model.CVDataSet;
import clib.view.common.CUpdatingLocker;

/**
 * @author macchan
 */
public class KMetricsChartPanel<V extends KBElement> extends JPanel {

	private static final long serialVersionUID = 1L;

	private CVChartController chart = new CVChartController();

	private KDiscourseController discourseController;
	private KNNetworkController<V> networkController;
	private List<IKTemporalMetricsScorer> scorers;

	private CUpdatingLocker locker = new CUpdatingLocker();

	public KMetricsChartPanel(KDiscourseController discourseController,
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

		chart.setNavigationEnabled(true);
		chart.getNavigator().addModelListener(new ICModelChangeListener() {
			@Override
			public void modelUpdated(Object... args) {
				if (args.length == 1
						&& args[0] == CVNavigator.Event.VALUE_SET_FINISHED) {
					locker.lock();//ループになる危険性有り　ちょっと危険
					int value = (int) chart.getNavigator().getValue();
					discourseController.doToFrame(value);
					locker.unlock();
				}
			}
		});

		addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent event) {
				networkController
						.addNetworkControllerListener(discourseListener);
				for (IKTemporalMetricsScorer scorer : scorers) {
					scorer.addModelListener(scorerListener);
				}
			}

			@Override
			public void ancestorRemoved(AncestorEvent event) {
				networkController
						.removeNetworkControllerListener(discourseListener);
				for (IKTemporalMetricsScorer scorer : scorers) {
					scorer.removeModelListener(scorerListener);
				}
			}

			@Override
			public void ancestorMoved(AncestorEvent event) {
			}
		});

		chart.addDataSelectionListener(new ICVChartDataSelectionListener() {
			@Override
			public void selectionStateChanged(List<CVDataSet> selectedSets) {
				List<V> selected = getVertices(selectedSets);
				if (selected.size() > 0) {
					networkController.selectModel(selected);
				}
			}
		});

		add(chart.getContainerPanel());
	}

	private boolean dirty = true;

	private ICModelChangeListener scorerListener = new ICModelChangeListener() {
		@Override
		public void modelUpdated(Object... args) {
			dirty = true;
		}
	};

	private KDiscourseControllerAdapter discourseListener = new KDiscourseControllerAdapter() {
		@Override
		public void refreshWithAnimation() {
			if (!locker.isLocked()) {
				refresh();
			}
		}
	};

	private void refresh() {
		if (dirty) {
			refreshChartData();
			dirty = false;
		}
		int frameNo = discourseController.getFrameNo();
		chart.getNavigator().setValue(frameNo);
		repaint();
	}

	private void refreshChartData() {
		chart.getChart().clear();
		datasetMap.clear();

		CUnit unitX = new CUnit("FrameNo.");
		CAxis axisX = new CAxis("X", unitX);

		for (IKTemporalMetricsScorer scorer : scorers) {
			if (scorer instanceof KGraphTemporalMetricsScorer) {
				KGraphTemporalMetricsScorer graphScorer = (KGraphTemporalMetricsScorer) scorer;
				chart.getChart().addData(
						createDataSetForGraph(axisX, graphScorer));
			} else if (scorer instanceof KVertexTemporalMetricsScorer) {
				@SuppressWarnings("unchecked")
				KVertexTemporalMetricsScorer<V> vertexScorer = (KVertexTemporalMetricsScorer<V>) scorer;
				for (V v : vertexScorer.getVertices()) {
					chart.getChart().addData(
							createDataSetForVertex(axisX, vertexScorer, v));
				}
			} else {
				throw new RuntimeException();
			}
		}

		chart.refreshViews();
	}

	private ICDataSet createDataSetForGraph(CAxis axisX,
			KGraphTemporalMetricsScorer scorer) {
		String name = scorer.getScorer().getName();
		CUnit unitY = new CUnit("Value");
		CAxis axisY = new CAxis("Y", unitY);
		CDefaultDataSet ds = new CDefaultDataSet(name, axisX, axisY, null);
		int size = scorer.size();
		for (int i = 0; i < size; i++) {
			ds.addPoint(i, scorer.getDoubleValue(i));
		}
		return ds;
	}

	private Map<ICDataSet, V> datasetMap = new HashMap<ICDataSet, V>();

	private ICDataSet createDataSetForVertex(CAxis axisX,
			KVertexTemporalMetricsScorer<V> scorer, V v) {
		String name = v.toString();
		CUnit unitY = new CUnit("Value");
		CAxis axisY = new CAxis("Y", unitY);
		CDefaultDataSet ds = new CDefaultDataSet(name, axisX, axisY, null);
		int size = scorer.size();
		for (int i = 0; i < size; i++) {
			ds.addPoint(i, scorer.getDoubleValue(i, v));
		}
		datasetMap.put(ds, v);
		return ds;
	}

	public List<V> getVertices(List<CVDataSet> datasets) {
		List<V> vertices = new ArrayList<V>();
		for (CVDataSet dataset : datasets) {
			vertices.add(datasetMap.get(dataset.getModel()));
		}
		return vertices;
	}
}
