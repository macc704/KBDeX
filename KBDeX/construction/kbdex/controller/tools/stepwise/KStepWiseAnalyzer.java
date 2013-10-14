/*
 * KStepWiseAnalyzer.java
 * Created on Sep 25, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.controller.tools.stepwise;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableModel;

import kbdex.controller.KDiscourseController;
import kbdex.model.kbmodel.KBAgent;
import kbdex.model.kbmodel.KBWord;
import kbdex.view.network.metrics.deprecated.KVertexMetricsPanel;
import clib.view.table.chartbridge.CRecordableTablePanel;
import clib.view.table.model2d.C2DTable;

/**
 * @author macchan
 */
@SuppressWarnings("deprecation")
public class KStepWiseAnalyzer {

	public static final String ALL = "All";

	private int TARGET_COLUMN = 3;// BC

	private KDiscourseController controller;

	private List<IKPhase> phases = new ArrayList<IKPhase>();

	private KVertexMetricsPanel<KBWord> wordMetrics;
	private Map<IKPhase, C2DTable<Object>> results;

	public KStepWiseAnalyzer(KDiscourseController controller) {
		this.controller = controller;
	}

	/**
	 * @param phases
	 *            the phases to set
	 */
	public void setPhases(List<IKPhase> phases) {
		this.phases = phases;
	}

	public void execute() {

		// initialize metrics
		wordMetrics = new KVertexMetricsPanel<KBWord>(controller, controller
				.getWorldController().getWordNetworkController(), null);
		wordMetrics.hookListeners();

		// initialize results
		results = new LinkedHashMap<IKPhase, C2DTable<Object>>();

		// initialize network
		// resetToCompleteNetwork(); // no need because each phases do it.

		// do phase analysis
		for (IKPhase phase : phases) {
			C2DTable<Object> phaseResult = executeOnePhase(phase);
			results.put(phase, phaseResult);
		}

		// finalize
		wordMetrics.unhookListeners();
	}

	private C2DTable<Object> executeOnePhase(IKPhase phase) {
		C2DTable<Object> result = new C2DTable<Object>();

		// prepare phase notes and measure base value
		controller.doResetToCompleteNetwork();
		phase.setToThisPhase(controller.getWorldController().getModel());
		controller.doReloadToThisFrame(true);

		putCurrentValue(ALL, result);

		// execute step wize analysis for each agent
		List<KBAgent> agents = controller.getWorldController().getModel()
				.getAgents().getElements();
		for (KBAgent agent : agents) {
			executeOneAgent(agent, result);
		}

		return result;
	}

	private void executeOneAgent(KBAgent agent, C2DTable<Object> result) {
		// set agent hiding mode
		controller.getWorldController().getModel().clearSelection();
		controller.getWorldController().getModel().clearIgnoreAgents();
		controller.getWorldController().getModel()
				.addIgnoreAgent(agent.getName());
		controller.doReloadToThisFrame(true);

		// Calculate the result
		putCurrentValue(agent, result);
	}

	private void putCurrentValue(Object col, C2DTable<Object> result) {
		//wordMetrics.reCalculate();//?? calculate()? what is the target metrics kind!

		// prepare and get the table model
		CRecordableTablePanel panel = wordMetrics.getRecordableTablePanel();
		TableModel model = panel.getListTableModel();

		// calculate amount
		int size = model.getRowCount();
		for (int i = 0; i < size; i++) {
			String word = (String) model.getValueAt(i, 0);
			double bc = (Double) model.getValueAt(i, TARGET_COLUMN);
			result.set(word, col, bc);
		}
	}

	/**
	 * @return the results
	 */
	public Map<IKPhase, C2DTable<Object>> getResults() {
		return results;
	}

	// // for debug
	// public void display() {
	// if (results != null && results.size() > 0) {
	// for (IKPhase phase : results.keySet()) {
	// System.out.print("," + phase.getName());
	// }
	// System.out.println();
	//
	// List<KBAgent> agents = controller.getWorldController().getModel()
	// .getAgents().getElements();
	// for (KBAgent agent : agents) {
	// System.out.print(agent.getName());
	// for (IKPhase phase : results.keySet()) {
	// System.out.print(",");
	// System.out.print(results.get(phase).get(agent.getName()));
	// }
	// System.out.println();
	// }
	// }
	// }
	//
	// public TableModel getResultTableModel() {
	// if (results == null || results.size() <= 0) {
	// return null;
	// }
	//
	// List<IKPhase> phases = new ArrayList<IKPhase>(results.keySet());
	// List<KBAgent> agents = controller.getWorldController().getModel()
	// .getAgents().getElements();
	// int colsize = phases.size() + 1;
	// int rowsize = agents.size() + 1;
	//
	// Object[] header = new Object[rowsize];
	// Object[][] table = new Object[rowsize][colsize];
	//
	// header[0] = "";
	// for (int j = 0; j < phases.size(); j++) {
	// header[j + 1] = phases.get(j).getName();
	// }
	//
	// table[0] = header;
	// for (int i = 0; i < agents.size(); i++) {
	// table[i + 1][0] = agents.get(i).getName();
	// for (int j = 0; j < phases.size(); j++) {
	// table[i + 1][j + 1] = results.get(phases.get(j)).get(
	// agents.get(i).getName());
	// }
	// }
	//
	// DefaultTableModel model = new DefaultTableModel(table, header);
	// model.setColumnCount(colsize);
	// return model;
	// }
}
