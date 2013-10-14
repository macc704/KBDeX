/*
 * KTimeFilteringPanel.java
 * Created on Apr 5, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.discourse.selection.time;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import kbdex.model.discourse.KDAgent;
import kbdex.model.discourse.KDDiscourse;
import kbdex.model.discourse.filters.KTimeDiscourseFilter;
import clib.common.time.CTime;
import clib.common.time.CTimeRange;
import clib.view.timeline.model.CTimeTransformationModel;
import clib.view.timeline.pane.CTimeLinePaneConnector;

/**
 * @author macchan
 * 
 */
public class KTimeFilteringPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private KDDiscourse discourse;

	private KAgentsTimeLinePane agentsPane;
	private KFiltersTimeLinePane filtersPane;

	private JComboBox<KTimeDiscourseFilter> currentFilterCombobox = new JComboBox<KTimeDiscourseFilter>();
	private JComboBox<FilterCommand> filterCommandCombobox = new JComboBox<FilterCommand>(
			createFilterActions());

	public KTimeFilteringPanel(KDDiscourse discourse) {
		this.discourse = discourse;
		initialize();
		refreshCurrentFilters();
	}

	private void initialize() {

		// create model
		CTimeTransformationModel model = new CTimeTransformationModel(
				discourse.getTimeRange());
		model.setOffset(25);

		// set student pane
		agentsPane = new KAgentsTimeLinePane();
		agentsPane.getTimelinePane().setTimeTransModel(model);
		for (KDAgent agent : discourse.getFilteredAgents()) {
			agentsPane.addModel(agent);
		}

		// set phase pane
		filtersPane = new KFiltersTimeLinePane(agentsPane.getTimelinePane());
		filtersPane.getTimelinePane().setTimeTransModel(model);
		List<KTimeDiscourseFilter> filters = discourse.getTimeFilters();
		for (KTimeDiscourseFilter filter : filters) {
			filtersPane.addModel(filter);
		}

		// add pane
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.add(agentsPane);
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		southPanel.add(createFilterControlPanel(), BorderLayout.NORTH);
		southPanel.add(filtersPane, BorderLayout.CENTER);
		split.add(southPanel);
		split.setResizeWeight(0.5);

		// connect
		agentsPane.getTimelinePane().createDefaultButtons();
		CTimeLinePaneConnector.connect(agentsPane.getTimelinePane(),
				filtersPane.getTimelinePane());

		setLayout(new BorderLayout());
		add(split);
	}

	private JPanel createFilterControlPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		leftPanel.add(new JLabel("Current:"));
		leftPanel.add(currentFilterCombobox);

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		rightPanel.add(filterCommandCombobox);
		doFilterCommandAction.putValue(Action.NAME, "Do");
		rightPanel.add(new JButton(doFilterCommandAction));

		panel.add(leftPanel, BorderLayout.WEST);
		panel.add(rightPanel, BorderLayout.EAST);
		return panel;
	}

	private Vector<FilterCommand> createFilterActions() {
		List<FilterCommand> controllers = new ArrayList<FilterCommand>();
		controllers.add(new FilterCommand("Add New Filter") {
			void doCommand() {
				filtersPane.addFilterModel(discourse.createDefaultTimeFilter());
				refreshCurrentFilters();
			}
		});
		controllers.add(new FilterCommand("Add Weekly Filters") {
			void doCommand() {
				addWeeklyFilters();
				refreshCurrentFilters();
			}
		});
		controllers.add(new FilterCommand("Remove Selected Filter(s)") {
			void doCommand() {
				for (KTimeDiscourseFilter filter : filtersPane
						.getSelectedTimeFilters()) {
					filtersPane.removeModel(filter);
				}
				if (filtersPane.getModelCount() <= 0) {
					filtersPane.addFilterModel(discourse
							.createDefaultTimeFilter());
				}
				refreshCurrentFilters();
			}
		});
		controllers.add(new FilterCommand("Remove All Filter(s)") {
			void doCommand() {
				for (KTimeDiscourseFilter filter : filtersPane.getTimeFilters()) {
					filtersPane.removeModel(filter);
				}
				filtersPane.addFilterModel(discourse.createDefaultTimeFilter());
				refreshCurrentFilters();
			}
		});
		return new Vector<FilterCommand>(controllers);
	}

	private Action doFilterCommandAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			doFilterCommand();
		}
	};

	private void doFilterCommand() {
		((FilterCommand) filterCommandCombobox.getSelectedItem()).doCommand();
	}

	abstract class FilterCommand {
		private String name;

		public FilterCommand(String name) {
			this.name = name;
		}

		abstract void doCommand();

		public String toString() {
			return name;
		}
	}

	/*******************************************
	 * Current Filters
	 *******************************************/

	private void refreshCurrentFilters() {
		KTimeDiscourseFilter selected = getCurrentTimeFilter();
		if (selected == null) {
			selected = discourse.getCurrentTimeFilter();
		}
		currentFilterCombobox.removeAllItems();
		for (KTimeDiscourseFilter filter : filtersPane.getModels()) {
			currentFilterCombobox.addItem(filter);
		}
		currentFilterCombobox.setSelectedItem(selected);
	}

	/*******************************************
	 * public interface
	 *******************************************/

	public void fitScale() {
		agentsPane.getTimelinePane().fitScale();
	}

	public List<KTimeDiscourseFilter> getTimeFilters() {
		return filtersPane.getTimeFilters();
	}

	public KTimeDiscourseFilter getCurrentTimeFilter() {
		return (KTimeDiscourseFilter) currentFilterCombobox.getSelectedItem();
	}

	/*******************************************
	 * Create Weekly Filters
	 *******************************************/

	protected void addWeeklyFilters() {
		DayChoosePanel panel = new DayChoosePanel();
		JOptionPane.showMessageDialog(null, panel);
		int firstDayOfWeek = panel.getSelectedIndex() + Calendar.SUNDAY;

		CTimeRange range = discourse.getTimeRange();
		int count = 1;
		CTime min = range.getStart();
		CTime max = range.getEnd();
		CTime current = min.getStartOfWeek(firstDayOfWeek);
		while (current.before(max)) {
			CTime start = new CTime(current);
			current = current.getNextWeek();
			CTime end = new CTime(current);

			if (start.before(min)) {
				start = min;
			}
			if (end.after(max)) {
				end = max;
			}
			filtersPane.addFilterModel(new KTimeDiscourseFilter("Week"
					+ (count++), new CTimeRange(start, end)));
		}
	}

	class DayChoosePanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private String[] DAYS = { "Sunday", "Monday", "Tuesday", "WedndesDay",
				"Thursday", "Friday", "Saturday" };
		private JComboBox<String> combobox = new JComboBox<String>(DAYS);

		DayChoosePanel() {
			setLayout(new BorderLayout());
			add(new JLabel("The first Day of Week?"), BorderLayout.NORTH);
			add(combobox);
		}

		int getSelectedIndex() {
			return combobox.getSelectedIndex();
		}
	}

}
