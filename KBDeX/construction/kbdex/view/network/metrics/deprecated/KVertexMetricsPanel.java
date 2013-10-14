/*
 * KVertexMetricsPanel.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.network.metrics.deprecated;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import kbdex.controller.IKDiscourseControllerListener;
import kbdex.controller.KDiscourseController;
import kbdex.controller.KNNetworkController;
import kbdex.model.kbmodel.KBElement;
import kbdex.model.network.metrics.IKVertexMetricsScorer;
import kbdex.view.KDiscourseControllerView;
import clib.common.collections.CObservableList;
import clib.common.thread.ICTask;
import clib.view.actions.CActionUtils;
import clib.view.common.CSeparator;
import clib.view.table.chartbridge.CRecordableTablePanel;
import clib.view.table.model.CDecoratableListTableModel;
import clib.view.table.model.CListTableModel;
import clib.view.table.model.CToStringNumberTableModelDescripter;
import clib.view.table.model.CToStringTableModelDescripter;
import clib.view.table.model.ICListTableModel;
import clib.view.table.record.CTableRecorderTableModel;

/**
 * @author macchan
 * @deprecated
 */
public class KVertexMetricsPanel<V extends KBElement> extends JPanel implements
		IKDiscourseControllerListener {

	private static final long serialVersionUID = 1L;

	private KNNetworkController<V> controller;
	private List<KVertexMetricsScorerWrapper<V>> scorers = new ArrayList<KVertexMetricsScorerWrapper<V>>();

	private JPanel scoreListPanel = new JPanel();
	private CRecordableTablePanel tablePanel;

	private boolean repaintRequest = false;
	private boolean updating = false;
	private boolean selecting = false;
	private boolean tableRefreshing = false;

	public KVertexMetricsPanel(KDiscourseController discourseController,
			KNNetworkController<V> controller, KDiscourseControllerView desktop) {
		this.controller = controller;
		tablePanel = new KRecordableTablePanelForInternalFrame(desktop,
				discourseController, controller, true);
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());

		//Install Scorers 
		for (IKVertexMetricsScorer<V, Number> scorer : controller
				.getMetricsManager().getVertexScorers()) {
			scorers.add(new KVertexMetricsScorerWrapper<V>(scorer));//install
		}

		//ScoreListPanel
		scoreListPanel
				.setBorder(BorderFactory.createTitledBorder("Activation"));
		scoreListPanel
				.setLayout(new BoxLayout(scoreListPanel, BoxLayout.Y_AXIS));
		final JCheckBox allCheckbox = new JCheckBox("All");
		scoreListPanel.add(allCheckbox);
		allCheckbox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				for (KVertexMetricsScorerWrapper<V> scorer : scorers) {
					scorer.getView().setSelected(allCheckbox.isSelected());
				}
			}
		});
		scoreListPanel.add(new CSeparator());
		for (KVertexMetricsScorerWrapper<V> scorer : scorers) {
			scoreListPanel.add(scorer.getView());
		}
		add(scoreListPanel, BorderLayout.WEST);

		//TablePanel
		tablePanel.setBorder(BorderFactory.createTitledBorder("Metrics"));
		tablePanel.addAncestorListener(new KListenerHooker());
		tablePanel.setModel(createTableModel());
		add(tablePanel, BorderLayout.CENTER);

		//Selection Handler
		JTable table = tablePanel.getTable();
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						if (e.getValueIsAdjusting()) {
							return;
						}
						refreshSelectionViewToModel();
					}
				});
	}

	private ICListTableModel<V> createTableModel() {
		//ListTableModel
		CListTableModel<V> listTableModel;
		if (controller.isIdSorting()) {
			listTableModel = new CListTableModel<V>(controller.getModel()
					.getNodes(), new CToStringNumberTableModelDescripter<V>());
		} else {
			listTableModel = new CListTableModel<V>(controller.getModel()
					.getNodes(), new CToStringTableModelDescripter<V>());
		}

		//MetricsAddedTableModel
		CObservableList<KVertexMetricsTableDecorator<V>> decorators = new CObservableList<KVertexMetricsTableDecorator<V>>();
		for (KVertexMetricsScorerWrapper<V> scorer : scorers) {
			decorators.add(scorer.getDecorator());
		}
		CDecoratableListTableModel<V> metricsAddedTableModel = new CDecoratableListTableModel<V>(
				listTableModel, decorators);
		return metricsAddedTableModel;
	}

	public JMenuBar createJMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Open");
		menuBar.add(menu);
		for (int i = 0; i < scorers.size(); i++) {
			menu.add(createOpenChartAction(scorers.get(i), i + 1));
		}
		return menuBar;
	}

	public Action createOpenChartAction(
			final KVertexMetricsScorerWrapper<V> scorer, final int column) {
		return CActionUtils.createAction("OpenChart - "
				+ scorer.getScorer().getName(), new ICTask() {
			public void doTask() {
				CTableRecorderTableModel model = tablePanel
						.createNewModelWithChart("Nodes Metrics - "
								+ scorer.getScorer().getName());
				tablePanel.addAllCellsInColumnForRecording(model, column);
			}
		});
	}

	/******************************************************
	 * For step wising
	 ******************************************************/

	public void hookListeners() {
		controller.addNetworkControllerListener(this);
	}

	public void unhookListeners() {
		controller.removeNetworkControllerListener(this);
	}

	public CRecordableTablePanel getRecordableTablePanel() {
		return tablePanel;
	}

	/******************************************************
	 * Implementation of IKDiscourseControllerListener
	 ******************************************************/

	class KListenerHooker implements AncestorListener {
		public void ancestorAdded(AncestorEvent event) {
			hookListeners();
		}

		public void ancestorRemoved(AncestorEvent event) {
			unhookListeners();
		}

		public void ancestorMoved(AncestorEvent event) {

		}
	}

	public void hardReset() {
		reset();
	}

	public void reset() {
		tablePanel.clear();
		for (KVertexMetricsScorerWrapper<V> scorer : scorers) {
			scorer.update();
		}
		tablePanel.tick();//initial tick
		repaintRequest = true;
	}

	public void tick() {
		for (KVertexMetricsScorerWrapper<V> scorer : scorers) {
			scorer.update();
		}
		tablePanel.tick();
		repaintRequest = true;//TODO 10 これだけだと要素が増えたときにアップデートされない
	}

	public boolean isAnimationFinished() {
		return true;
	}

	public void refreshWithAnimation() {
		refresh();
	}

	public void refreshWithoutAnimation() {
		refresh();
	}

	private void refresh() {
		if (repaintRequest) {
			tableRefreshing = true;
			tablePanel.getListTableModel().fireTableDataChanged();
			tableRefreshing = false;
			repaintRequest = false;
		}
		refreshSelectionModelToView();
	}

	private void refreshSelectionModelToView() {
		if (updating || selecting || tableRefreshing) {
			return;
		}

		updating = true;
		JTable table = tablePanel.getTable();
		ListSelectionModel selectionModel = table.getSelectionModel();
		selectionModel.setValueIsAdjusting(true);
		selectionModel.clearSelection();

		List<V> models = getListTableModel().getList();
		for (V v : controller.getModel().getNodes()) {
			if (v.isSelected()) {
				int row = models.indexOf(v);
				int index = tablePanel.getTable().getRowSorter()
						.convertRowIndexToView(row);
				selectionModel.addSelectionInterval(index, index);
			}
		}
		selectionModel.setValueIsAdjusting(false);

		ListSelectionModel columnSelectionModel = table.getColumnModel()
				.getSelectionModel();
		columnSelectionModel.setValueIsAdjusting(true);
		table.setColumnSelectionInterval(0, table.getColumnCount() - 1);
		columnSelectionModel.setValueIsAdjusting(false);
		updating = false;
	}

	private void refreshSelectionViewToModel() {
		if (updating || selecting || tableRefreshing) {
			return;
		}

		selecting = true;
		List<Integer> selectedIndexs = tablePanel.getSelectedModelRows();
		List<V> selected = new ArrayList<V>();
		for (int i = 0; i < selectedIndexs.size(); i++) {
			V v = getListTableModel().getModel(selectedIndexs.get(i));
			selected.add(v);
		}
		controller.selectModel(selected);
		selecting = false;
	}

	@SuppressWarnings("unchecked")
	private ICListTableModel<V> getListTableModel() {
		return ((ICListTableModel<V>) tablePanel.getListTableModel());
	}

	@SuppressWarnings("hiding")
	class KVertexMetricsScorerWrapper<V> {

		private IKVertexMetricsScorer<V, Number> scorer;

		private KVertexMetricsTableDecorator<V> decorator;
		private JCheckBox view = new JCheckBox();

		public KVertexMetricsScorerWrapper(
				IKVertexMetricsScorer<V, Number> scorer) {
			this.scorer = scorer;
			initialize();
		}

		private void initialize() {
			decorator = new KVertexMetricsTableDecorator<V>(scorer,
					scorer.getName());
			view.setText(scorer.getName());
			view.setSelected(false);
			view.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					decorator.setActive(view.isSelected());
					update();
					tablePanel.getListTableModel().modelUpdated();
				}
			});
		}

		public IKVertexMetricsScorer<V, ?> getScorer() {
			return scorer;
		}

		public KVertexMetricsTableDecorator<V> getDecorator() {
			return decorator;
		}

		public JCheckBox getView() {
			return view;
		}

		public void update() {
			if (decorator.isActive()) {
				scorer.calculate();
			}
		}
	}
}
