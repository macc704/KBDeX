/*
 * KVertexMetricsPanel.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.network.metrics.deprecated;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import kbdex.controller.IKDiscourseControllerListener;
import kbdex.controller.KDiscourseController;
import kbdex.controller.KNNetworkController;
import kbdex.model.kbmodel.KBElement;
import kbdex.model.network.metrics.IKGraphMetricsScorer;
import kbdex.view.KDiscourseControllerView;
import clib.common.thread.ICTask;
import clib.view.actions.CAction;
import clib.view.actions.CActionUtils;
import clib.view.actions.CKeyStroke;
import clib.view.common.CSeparator;
import clib.view.table.chartbridge.CRecordableTablePanel;
import clib.view.table.model.CListTableModel;
import clib.view.table.model.ICListTableModel;
import clib.view.table.model.ICTableModelDescripter;
import clib.view.table.record.CTableRecorderTableModel;

/**
 * @author macchan
 * @deprecated
 */
public class KGraphMetricsPanel<V extends KBElement> extends JPanel implements
		IKDiscourseControllerListener {

	private static final long serialVersionUID = 1L;

	private KNNetworkController<V> controller;
	private List<KGraphMetricsScorerWrapper> scorers = new ArrayList<KGraphMetricsScorerWrapper>();

	private JPanel scoreListPanel = new JPanel();
	private CRecordableTablePanel tablePanel;

	private boolean repaintRequest = false;

	public KGraphMetricsPanel(KDiscourseController discourseController,
			KNNetworkController<V> controller, KDiscourseControllerView desktop) {
		this.controller = controller;
		tablePanel = new KRecordableTablePanelForInternalFrame(desktop,
				discourseController, controller, false);
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());

		//Install Scorers 
		for (IKGraphMetricsScorer<?> scorer : controller.getMetricsManager()
				.getGraphScorers()) {
			scorers.add(new KGraphMetricsScorerWrapper(scorer));//install
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
				for (KGraphMetricsScorerWrapper scorer : scorers) {
					scorer.setActive(allCheckbox.isSelected());
				}
			}
		});
		scoreListPanel.add(new CSeparator());
		for (KGraphMetricsScorerWrapper scorer : scorers) {
			scoreListPanel.add(scorer.getView());
		}
		add(scoreListPanel, BorderLayout.WEST);

		//TablePanel
		tablePanel.setBorder(BorderFactory.createTitledBorder("Metrics"));
		tablePanel.addAncestorListener(new KListenerHooker());
		tablePanel.setModel(createTableModel());
		add(tablePanel, BorderLayout.CENTER);

		//		//EastPanel
		//		JPanel eastPanel = new JPanel();
		//		eastPanel.setLayout(new BorderLayout());
		//		add(eastPanel, BorderLayout.CENTER);
		//
		//		//TablePanel
		//		tablePanel.setBorder(BorderFactory.createTitledBorder("Metrics"));
		//		tablePanel.addAncestorListener(new KListenerHooker());
		//		tablePanel.setModel(createTableModel());
		//		eastPanel.add(tablePanel, BorderLayout.CENTER);
		//		
		//		//Graph Shotcut
		//		JPanel graphButtonPanel = new JPanel();
		//		graphButtonPanel.setBorder(BorderFactory
		//				.createTitledBorder("Open ShortCut"));
		//		JButton button = new JButton(createOpenChartAction());
		//		graphButtonPanel.add(button);
		//		eastPanel.add(graphButtonPanel, BorderLayout.SOUTH);
	}

	private ICListTableModel<?> createTableModel() {
		CListTableModel<KGraphMetricsScorerWrapper> listTableModel = new CListTableModel<KGraphMetricsScorerWrapper>(
				scorers, new KGraphMetricsScorerWrapperDescriptor());
		return listTableModel;
	}

	public JMenuBar createJMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Open");
		menuBar.add(menu);
		menu.add(createOpenChartAction());
		return menuBar;
	}

	public Action createOpenChartAction() {
		CAction action = CActionUtils.createAction("OpenChart", new ICTask() {
			public void doTask() {
				List<Integer> rows = new ArrayList<Integer>();
				int row = 0;
				for (KGraphMetricsScorerWrapper scorer : scorers) {
					if (scorer.isActive()) {
						rows.add(row);
					}
					row++;
				}
				if (rows.size() > 0) {
					CTableRecorderTableModel model = tablePanel
							.createNewModelWithChart("Graph Metrics");
					tablePanel.addCellsForRecording(model, 1, rows);
				}
			}
		});
		action.setAcceralator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				CKeyStroke.CTRL_MASK));
		return action;
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

	private int frame = 0;
	private int cashed = 0;

	public void hardReset() {
		frame = 0;
		cashed = 0;
		tablePanel.clear();

		tick();//initial tick
	}

	public void reset() {
		frame = 0;
	}

	public void tick() {
		frame++;

		while (cashed < frame) {
			for (KGraphMetricsScorerWrapper scorer : scorers) {
				scorer.update();
			}
			tablePanel.tick();
			repaintRequest = true;//TODO 10 これだけだと要素が増えたときにアップデートされない
			cashed++;
		}
	}

	public boolean isAnimationFinished() {
		return true;
	}

	public void refreshWithAnimation() {
		if (repaintRequest) {
			tablePanel.getListTableModel().fireTableDataChanged();
			repaintRequest = false;
		}
	}

	public void refreshWithoutAnimation() {
		if (repaintRequest) {
			tablePanel.getListTableModel().fireTableDataChanged();
			repaintRequest = false;
		}
	}

	class KGraphMetricsScorerWrapper {
		public static final long serialVersionUID = 1L;

		private IKGraphMetricsScorer<?> scorer;
		private JCheckBox view = new JCheckBox();

		public KGraphMetricsScorerWrapper(IKGraphMetricsScorer<?> scorer) {
			this.scorer = scorer;
			initialize();
		}

		private void initialize() {
			view.setText(scorer.getName());
			view.setSelected(false);
			view.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					update();
					tablePanel.getListTableModel().modelUpdated();
				}
			});
		}

		//		public IKGraphMetricsScorer<?> getScorer() {
		//			return scorer;
		//		}

		public JCheckBox getView() {
			return view;
		}

		public void update() {
			if (isActive()) {
				scorer.calculate();
			}
		}

		public String getName() {
			return scorer.getName();
		}

		public Object getScore() {
			if (isActive()) {
				return scorer.getScore();
			} else {
				return Double.NaN;
			}
		}

		public void setActive(boolean selected) {
			view.setSelected(selected);
		}

		public boolean isActive() {
			return view.isSelected();
		}

		public String toString() {
			return getName();
		}
	}

	class KGraphMetricsScorerWrapperDescriptor implements
			ICTableModelDescripter<KGraphMetricsScorerWrapper> {

		private Class<?>[] types = new Class<?>[] { String.class, Double.class };
		private String[] names = new String[] { "Name", "Value" };

		public int getVariableCount() {
			return types.length;
		}

		public Class<?> getValiableClass(int index) {
			return types[index];
		}

		public Object getVariableAt(KGraphMetricsScorerWrapper model, int index) {
			switch (index) {
			case 0:
				return model.getName();
			case 1:
				return model.getScore();
			}
			throw new RuntimeException();
		}

		public String getVariableName(int index) {
			return names[index];
		}
	}
}
