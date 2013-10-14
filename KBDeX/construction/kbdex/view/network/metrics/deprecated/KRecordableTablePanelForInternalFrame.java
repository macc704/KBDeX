/*
 * CRecordableTablePanelForFrame.java
 * Created on May 3, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.network.metrics.deprecated;

import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import kbdex.controller.KDiscourseController;
import kbdex.controller.KNNetworkController;
import kbdex.model.kbmodel.KBElement;
import kbdex.view.KDiscourseControllerView;
import clib.common.model.ICModelChangeListener;
import clib.common.thread.ICTask;
import clib.view.actions.CActionUtils;
import clib.view.chart.controller.CVChartController;
import clib.view.chart.controller.ICVChartDataSelectionListener;
import clib.view.chart.viewer.model.CVDataSet;
import clib.view.table.chartbridge.CRecordableTablePanel;
import clib.view.table.chartbridge.CTableModelListeningTableChartAdapter;
import clib.view.table.record.CTableRecorderTableModel;

/**
 * @author macchan
 * @deprecated
 */
public class KRecordableTablePanelForInternalFrame extends
		CRecordableTablePanel {

	private static final long serialVersionUID = 1L;

	private KDiscourseControllerView desktop;
	private KDiscourseController discourseController;
	private KNNetworkController<?> networkController;
	private boolean selectingSupport;

	public KRecordableTablePanelForInternalFrame(
			KDiscourseControllerView desktop,
			KDiscourseController discourseController,
			KNNetworkController<?> networkController, boolean selectingSupport) {
		this.desktop = desktop;
		this.discourseController = discourseController;
		this.networkController = networkController;
		this.selectingSupport = selectingSupport;
	}

	protected JComponent openChartComponent(CTableRecorderTableModel model,
			String name) {
		return openChartComponentInternal(model, name, true, true);
	}

	protected JComponent openChartComponentInternal(
			final CTableRecorderTableModel model, final String name,
			boolean active, final boolean isRoot) {
		//Basic Setting
		final CVChartController chartController = new CVChartController();
		final CTableModelListeningTableChartAdapter adapter = new CTableModelListeningTableChartAdapter(
				model, chartController, name);

		//navigation setting
		chartController.setNavigationEnabled(true);
		chartController.getNavigator().addModelListener(
				new ICModelChangeListener() {
					@Override
					public void modelUpdated(Object... args) {
						//このコードちょっぴり危険　ループする可能性がある．
						int value = (int) chartController.getNavigator()
								.getValue();
						discourseController.doToFrame(value);
					}
				});

		//selection setting 
		if (selectingSupport) {//selectionSupport は GraphMetricsの場合false
			//chart -> network 
			chartController
					.addDataSelectionListener(new ICVChartDataSelectionListener() {
						public void selectionStateChanged(
								List<CVDataSet> selectedSets) {
							List<String> pointers = new ArrayList<String>();
							for (CVDataSet dataset : selectedSets) {
								pointers.add(dataset.getModel().getLabel());
							}
							networkController.selectModelByPointer(pointers);
						}
					});
			//network -> chart
			chartController.getContainerPanel().addAncestorListener(
					new KDiscourseListeningAncestorAdapter(networkController) {
						public void refreshWithAnimation() {
							//refreshing strategy
							adapter.refreshChartDataFromModel();
							int frameNo = discourseController.getFrameNo();
							chartController.getNavigator().setValue(frameNo);

							List<String> pointers = new ArrayList<String>();
							for (Object o : networkController.getModel()
									.getNodes()) {
								if (((KBElement) o).isSelected()) {
									pointers.add(((KBElement) o).getName());
								}
							}
							chartController.setDataSelectionByPointer(pointers);
						}
					});
		} else {
			//refreshing strategy
			chartController.getContainerPanel().addAncestorListener(
					new KDiscourseListeningAncestorAdapter(networkController) {
						public void refreshWithAnimation() {
							adapter.refreshChartDataFromModel();
							int frameNo = discourseController.getFrameNo();
							chartController.getNavigator().setValue(frameNo);
						}
					});
		}

		//open window
		final JInternalFrame frame = desktop.openInternalFrame(chartController
				.getContainerPanel(), "", null, new Rectangle(100, 100, 700,
				500));
		frame.addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosed(InternalFrameEvent e) {
				//adapter.setActive(false);
			}
		});
		adapter.setActive(true);//initial refreshing
		setChartActiveState(adapter, frame, name, active, isRoot);

		//MenuBar
		JMenuBar menuBar = new JMenuBar();
		{
			JMenu menu = new JMenu("File");
			menuBar.add(menu);
			menu.add(chartController.getContainerPanel()
					.createExportImageController().createAction());
		}
		{
			JMenu menu = new JMenu("Edit");
			menuBar.add(menu);

			final JCheckBox checkbox = new JCheckBox("Active", active);
			checkbox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					setChartActiveState(adapter, frame, name,
							checkbox.isSelected(), isRoot);
				}
			});
			menu.add(checkbox);
		}
		{
			JMenu menu = new JMenu("View");
			menuBar.add(menu);

			menu.add(CActionUtils.createAction("Pin (Copy stable)",
					new ICTask() {
						public void doTask() {
							openChartComponentInternal(model, name, false,
									false);
						}
					}));
			menu.add(CActionUtils.createAction("Copy", new ICTask() {
				public void doTask() {
					openChartComponentInternal(model, name, true, false);
				}
			}));
			menu.add(CActionUtils.createAction("Open Table", new ICTask() {
				public void doTask() {
					openTableComponentInternal(model, name);
				}
			}));
			menu.add(CActionUtils.createAction("Open Overview", new ICTask() {
				public void doTask() {
					desktop.openInternalFrame(chartController
							.getContainerPanel().createOverviewPanel(),
							"Overview of " + name, null, null);
				}
			}));
		}
		frame.setJMenuBar(menuBar);

		return chartController.getContainerPanel();
	}

	private void setChartActiveState(
			CTableModelListeningTableChartAdapter adapter,
			JInternalFrame frame, String name, boolean active, boolean isRoot) {
		adapter.setActive(active);
		String title = "Chart: " + name;
		if (isRoot) {
			title = "* " + title;
		}
		if (!active) {
			title = title + " (Inactive Mode)";
		}
		frame.setTitle(title);
	}

	protected JComponent openTableComponent(CTableRecorderTableModel model,
			String name) {
		return openTableComponentInternal(model, name);
	}

	protected JComponent openTableComponentInternal(
			final CTableRecorderTableModel model, final String name) {
		final KRecordableTablePanelForInternalFrame panel = new KRecordableTablePanelForInternalFrame(
				desktop, discourseController, networkController,
				selectingSupport);
		panel.setModel(model);
		JInternalFrame frame = desktop.openInternalFrame(panel, "Table: "
				+ name, null, new Rectangle(100, 100, 400, 400));

		JMenuBar menuBar = new JMenuBar();
		{
			JMenu menu = new JMenu("View");
			menuBar.add(menu);

			menu.add(CActionUtils.createAction("Open Chart", new ICTask() {
				public void doTask() {
					openChartComponentInternal(model, name, true, false);
				}
			}));
		}
		{
			JMenu menu = new JMenu("Edit");
			menuBar.add(menu);

			menu.add(CActionUtils.createAction("Select All", new ICTask() {
				public void doTask() {
					panel.getTable().selectAll();
				}
			}));
		}
		frame.setJMenuBar(menuBar);
		return frame;
	}

}
