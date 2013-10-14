/*
 * KStepWiseAnalyzeViewer.java
 * Created on Sep 26, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.controller.tools.stepwise;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import kbdex.controller.KDiscourseController;
import kbdex.model.kbmodel.KBAgent;
import clib.view.actions.CKeyStroke;
import clib.view.chart.model.CAxis;
import clib.view.chart.model.CDefaultDataSet;
import clib.view.chart.model.CUnit;
import clib.view.chart.viewer.CVChartFrame;
import clib.view.chart.viewer.model.CVChart;
import clib.view.editor.CSimpleTextEditor;
import clib.view.panels.CVerticalFlowLayout;
import clib.view.table.common.CCommonTablePanel;
import clib.view.table.model2d.C2DTable;
import clib.view.table.model2d.C2DTableTableModel;
import clib.view.windowmanager.CWindowManager;

/**
 * @author macchan
 * 
 */
public class KStepWiseAnalyzeViewer extends JFrame {

	private static final long serialVersionUID = 1L;

	private KDiscourseController controller;
	private Map<IKPhase, C2DTable<Object>> results;

	private CSimpleTextEditor textEditor = new CSimpleTextEditor();

	public KStepWiseAnalyzeViewer(KDiscourseController controller,
			Map<IKPhase, C2DTable<Object>> results) {
		this.controller = controller;
		this.results = results;
		initialize();
	}

	@SuppressWarnings("serial")
	void initialize() {
		getContentPane().setLayout(new CVerticalFlowLayout());

		JPanel panel = new JPanel();
		getContentPane().add(panel);
		panel.add(new JLabel("All:"));
		for (final IKPhase phase : results.keySet()) {
			JButton button = new JButton(phase.getName());
			panel.add(button);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showTable(phase, results.get(phase));
				}
			});
		}

		//textEditor.setFile(controller.getDiscourse().getDao().getHBWFile());
		textEditor.doLoad();
		getContentPane().add(textEditor);

		panel = new JPanel();
		getContentPane().add(panel);
		panel.add(new JLabel("Filterd:"));
		for (final IKPhase phase : results.keySet()) {
			JButton button = new JButton(phase.getName());
			panel.add(button);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					doSave();// TEMP
					C2DTable<Object> table = new C2DTable<Object>(results
							.get(phase));
					table.setRowFilter(controller.getDiscourse().getHBWs());
					showTable(phase, table);
				}
			});
		}

		panel = new JPanel();
		getContentPane().add(panel);
		JButton rsbutton = new JButton("Result(変位=>絶対値=>加算)");
		panel.add(rsbutton);
		rsbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSave();// TEMP
				showTable(getResultTableModel(results));
			}
		});
		JButton rsbutton2 = new JButton("Result(加算=>変位=>絶対値)");
		panel.add(rsbutton2);
		rsbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSave();// TEMP
				showTable(getResultTableModel2(results));
			}
		});

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menuFile = new JMenu("File");
		Action save = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				doSave();
			}
		};
		save.putValue(Action.NAME, "Save");
		save.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_S, CKeyStroke.CTRL_MASK));
		menuFile.add(save);

		menuBar.add(menuFile);
	}

	private void doSave() {
		textEditor.doSave();
	}

	private void showTable(IKPhase phase, C2DTable<Object> result) {
		C2DTableTableModel model = new C2DTableTableModel(result);
		CCommonTablePanel panel = new CCommonTablePanel();
		panel.getTable().setModel(model);
		JFrame frame = CWindowManager.createFrame(panel);
		frame.setTitle("StepWize Analysis Result - " + phase.getName());
		frame.setSize(400, 300);
		frame.setVisible(true);
	}

	private TableModel getResultTableModel(
			Map<IKPhase, C2DTable<Object>> intables) {
		List<String> hbws = controller.getDiscourse().getHBWs();
		List<IKPhase> phases = new ArrayList<IKPhase>(intables.keySet());
		List<KBAgent> agents = controller.getWorldController().getModel()
				.getAgents().getElements();
		int colsize = phases.size() + 1;
		int rowsize = agents.size() + 1;

		Object[] header = new Object[rowsize];
		Object[][] table = new Object[rowsize][colsize];

		header[0] = "";
		for (int i = 0; i < phases.size(); i++) {
			header[i + 1] = phases.get(i).getName();
		}

		table[0] = header;
		for (int i = 0; i < phases.size(); i++) {
			C2DTable<Object> intable = intables.get(phases.get(i));

			for (int j = 0; j < agents.size(); j++) {
				KBAgent agent = agents.get(j);
				table[j + 1][0] = agent.getName();

				double amount = 0d;
				for (String hbw : hbws) {
					Object baseObject = intable
							.get(hbw, KStepWiseAnalyzer.ALL);
					double base = 0d;
					if (baseObject != null) {
						base = (Double) baseObject;
					}

					Object valueObject = intable.get(hbw, agent);
					double value = 0d;
					if (valueObject != null) {
						value = (Double) valueObject;
					}
					double diff = base - value;
					// @todo 変異の和をどうする？
					diff = Math.abs(diff);
					amount += diff;
				}

				// amount = Math.abs(amount);
				table[j + 1][i + 1] = amount;
			}
		}

		DefaultTableModel model = new DefaultTableModel(table, header);
		model.setColumnCount(colsize);

		return model;
	}

	private TableModel getResultTableModel2(
			Map<IKPhase, C2DTable<Object>> intables) {
		List<String> hbws = controller.getDiscourse().getHBWs();
		List<IKPhase> phases = new ArrayList<IKPhase>(intables.keySet());
		List<KBAgent> agents = controller.getWorldController().getModel()
				.getAgents().getElements();
		int colsize = phases.size() + 1;
		int rowsize = agents.size() + 1;

		Object[] header = new Object[rowsize];
		Object[][] table = new Object[rowsize][colsize];

		header[0] = "";
		for (int i = 0; i < phases.size(); i++) {
			header[i + 1] = phases.get(i).getName();
		}

		table[0] = header;
		for (int i = 0; i < phases.size(); i++) {
			C2DTable<Object> intable = intables.get(phases.get(i));

			for (int j = 0; j < agents.size(); j++) {
				KBAgent agent = agents.get(j);
				table[j + 1][0] = agent.getName();

				double amount = 0d;
				for (String hbw : hbws) {
					Object baseObject = intable
							.get(hbw, KStepWiseAnalyzer.ALL);
					double base = 0d;
					if (baseObject != null) {
						base = (Double) baseObject;
					}

					Object valueObject = intable.get(hbw, agent);
					double value = 0d;
					if (valueObject != null) {
						value = (Double) valueObject;
					}
					double diff = base - value;
					// @todo 変異の和をどうする？
					// diff = Math.abs(diff);
					amount += diff;
				}

				amount = Math.abs(amount);
				table[j + 1][i + 1] = amount;
			}
		}

		DefaultTableModel model = new DefaultTableModel(table, header);
		model.setColumnCount(colsize);

		return model;
	}

	private void showTable(final TableModel model) {
		CCommonTablePanel panel = new CCommonTablePanel();
		panel.getTable().setModel(model);
		JFrame frame = CWindowManager.createFrame(panel);
		frame.setTitle("StepWize Analysis Result");
		frame.setSize(400, 300);
		JMenuBar bar = new JMenuBar();
		JMenu menu = new JMenu("Chart");
		bar.add(menu);
		JMenuItem item = new JMenuItem("Open");
		menu.add(item);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CVChartFrame frame = new CVChartFrame();
				CVChart chart = frame.getController().getChart();
				CAxis axisX = new CAxis("X", new CUnit("Phase"));
				CAxis axisY = new CAxis("Y", new CUnit("BC"));
				int colsize = model.getColumnCount();
				int rowsize = model.getRowCount();
				for (int row = 1; row < rowsize; row++) {
					CDefaultDataSet ds = new CDefaultDataSet(model.getValueAt(
							row, 0).toString(), axisX, axisY, null);
					for (int col = 1; col < colsize; col++) {
						ds.addPoint(col, Double.parseDouble(model.getValueAt(
								row, col).toString()));
					}
					chart.addData(ds);
				}
				frame.setVisible(true);
			}
		});
		frame.setJMenuBar(bar);
		frame.setVisible(true);
	}
}
