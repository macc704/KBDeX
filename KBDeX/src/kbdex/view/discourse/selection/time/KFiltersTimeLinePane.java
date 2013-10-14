/*
 * KFiltersTimeLinePane.java
 * Created on Mar 24, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.discourse.selection.time;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import kbdex.model.discourse.filters.KTimeDiscourseFilter;
import clib.common.time.CTime;
import clib.common.time.CTimeRange;
import clib.view.panels.CEditableLabel;
import clib.view.panels.ICLabelChangedListener;
import clib.view.timeline.model.CTimeTransformationModel;
import clib.view.timeline.pane.CAbstractTimeLinePane;
import clib.view.timeline.pane.CTimeLinePane;

import com.visutools.nav.bislider.BiSlider;
import com.visutools.nav.bislider.ColorisationEvent;
import com.visutools.nav.bislider.ColorisationListener;

/**
 * @author macchan
 * TODO 100 BiSliderがClip Threadを大量に立ち上げている？
 */
public class KFiltersTimeLinePane extends
		CAbstractTimeLinePane<KTimeDiscourseFilter> {

	private static final long serialVersionUID = 1L;

	private CTimeLinePane upperPane;

	public KFiltersTimeLinePane(CTimeLinePane upperPane) {
		this.upperPane = upperPane;
	}

	public void addFilterModel(KTimeDiscourseFilter filter) {
		setToNonDuplicatedName(filter);
		addModel(filter);
	}

	public JComponent createLeftPanel(KTimeDiscourseFilter model) {
		return new KLeftPanel(model);
	}

	class KLeftPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		private JCheckBox checkbox = new JCheckBox();
		private KTimeDiscourseFilter model;

		public KLeftPanel(KTimeDiscourseFilter model) {
			this.model = model;
			initialize();
		}

		private void initialize() {
			setOpaque(false);
			setLayout(new BorderLayout());

			add(checkbox, BorderLayout.EAST);

			//final JTextField label = new JTextField(model.getName());
			final CEditableLabel label = new CEditableLabel(model.getName());
			label.addLabelChangedListener(new ICLabelChangedListener() {
				public void labelChanged(String newName) {
					if (checkName(model, newName)) {
						model.setName(label.getText());
					} else {
						JOptionPane.showMessageDialog(null,
								"The name is duplicated.", "Warning",
								JOptionPane.YES_OPTION);
						label.setText(model.getName());
					}
				}
			});
			add(label);

			// pulling panel
			JPanel pullingPanel = new JPanel();
			//pullingPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			pullingPanel.setBackground(Color.WHITE);
			pullingPanel.setLayout(new BorderLayout());
			pullingPanel.add(new JLabel("+", SwingConstants.CENTER));
			pullingPanel.setPreferredSize(new Dimension(20, 20));
			MouseAdapter l = createDragMouseListener(model);
			pullingPanel.addMouseListener(l);
			pullingPanel.addMouseMotionListener(l);
			add(pullingPanel, BorderLayout.WEST);
		}

		public boolean isSelected() {
			return checkbox.isSelected();
		}

		public KTimeDiscourseFilter getModel() {
			return model;
		}
	}

	public JComponent createRightPanel(KTimeDiscourseFilter model) {
		KRightPanel panel = new KRightPanel(model);
		return panel;
	}

	class KRightPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		private BiSlider slider = new BiSlider();
		private KTimeDiscourseFilter model;

		/**
		 * 
		 */
		public KRightPanel(KTimeDiscourseFilter model) {
			this.model = model;
			initialize();
		}

		private void initialize() {
			setOpaque(false);
			// panel.setLayout(new BorderLayout());
			FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
			setLayout(layout);

			slider.setBackground(Color.WHITE);
			CTimeTransformationModel transModel = getTimelinePane()
					.getTimeTransModel();
			CTime start = model.getRange().getStart();
			CTime end = model.getRange().getEnd();
			slider.setValues(0d, 1d);
			slider.setColoredValues(transModel.getRateByTime(start),
					transModel.getRateByTime(end));
			upperPane.createIndicator(Color.RED);
			upperPane.createIndicator(Color.BLACK);
			slider.addColorisationListener(new ColorisationListener() {
				public void newColors(ColorisationEvent evt) {
					CTimeTransformationModel transModel = getTimelinePane()
							.getTimeTransModel();
					double min = evt.getMinimum();
					double max = evt.getMaximum();
					CTime start = transModel.getTimeByRate(min);
					CTime end = transModel.getTimeByRate(max);
					model.setRange(new CTimeRange(start, end));
					upperPane.setIndicatorTime(start, end);
				}
			});
			// slider.addMouseListener(new MouseAdapter() {
			// public void mouseClicked(MouseEvent e) {
			// if (e.getClickCount() == 2) {
			// CTimeTransformationModel model = getTimelinePane()
			// .getTransModel();
			// double x = e.getX();
			// double rate = x / model.getPreferredWidth();
			// // CTime time = model.x2Time(x);
			// System.out.println(rate);
			// e.consume();
			// }
			// }
			// });
			add(slider);

			// 大きさ変更に追従したいが，パネル幅よりも対象範囲が狭くなると，パネル幅が変わらないので追従しなくなる，
			// そのため，倍率変更の通知で変更をする
			getTimelinePane().getTimeTransModel().addPropertyChangeListener(
					new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent evt) {
							refreshSize();
						}
					});
			refreshSize();
		}

		private void refreshSize() {
			CTimeTransformationModel transModel = getTimelinePane()
					.getTimeTransModel();
			Dimension d = new Dimension();
			d.height = getComponentHeight();
			d.width = transModel.getPreferredWidth();
			slider.setPreferredSize(d);
			slider.setSize(d);
		}

		/**
		 * @return the model
		 */
		public KTimeDiscourseFilter getModel() {
			return this.model;
		}
	}

	public List<KTimeDiscourseFilter> getSelectedTimeFilters() {
		List<KTimeDiscourseFilter> filters = new ArrayList<KTimeDiscourseFilter>();
		for (JComponent panel : getVPanels()) {
			KLeftPanel filterPanel = (KLeftPanel) panel;
			if (filterPanel.isSelected()) {
				filters.add(filterPanel.getModel());
			}
		}
		return filters;
	}

	public List<KTimeDiscourseFilter> getTimeFilters() {
		List<KTimeDiscourseFilter> filters = new ArrayList<KTimeDiscourseFilter>();
		for (JComponent panel : getVPanels()) {
			KLeftPanel filterPanel = (KLeftPanel) panel;
			filters.add(filterPanel.getModel());
		}
		return filters;
	}

	private void setToNonDuplicatedName(KTimeDiscourseFilter filter) {
		String baseName = filter.getName();
		int counter = 1;
		while (checkName(filter, filter.getName()) == false) {
			filter.setName(baseName + counter);
			counter++;
		}
	}

	private boolean checkName(KTimeDiscourseFilter me, String name) {
		for (KTimeDiscourseFilter model : getModels()) {
			if (model != me && name.equals(model.getName())) {
				return false;//NG;
			}
		}
		return true;//OK
	}

	public int getComponentHeight() {
		return 30;
	}

}
