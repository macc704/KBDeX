/*
 * KDiscoursePropertyViewerPanel.java
 * Created on Apr 19, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.discourse;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;

import kbdex.model.discourse.KDDiscourse;
import clib.common.thread.ICTask;
import clib.view.progress.CPanelProcessingMonitor;

/**
 * @author macchan
 */
public class KDiscoursePropertyViewerPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private KDDiscourse discourse;

	private CEnumComboBox<KDDiscourse.Language> languageCombobox = new CEnumComboBox<KDDiscourse.Language>(
			KDDiscourse.Language.class);
	private CEnumComboBox<KDDiscourse.DiscourseUnitType> unitCombobox = new CEnumComboBox<KDDiscourse.DiscourseUnitType>(
			KDDiscourse.DiscourseUnitType.class);
	private JCheckBox lifetimeCheckbox = new JCheckBox("Enable Lifetime");
	private JTextField lifetimeTextField = new JFormattedTextField(
			new DecimalFormat());

	public KDiscoursePropertyViewerPanel(KDDiscourse discourse) {
		this.discourse = discourse;
		initialize();
	}

	private void initialize() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		{
			JPanel panel = new JPanel();
			panel.setMaximumSize(new Dimension(10000, 100));
			panel.setPreferredSize(new Dimension(300, 100));
			panel.setBorder(BorderFactory.createTitledBorder("Language"));
			panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			panel.add(languageCombobox);

			this.add(panel);
		}
		languageCombobox.setSelectedItem(discourse.getLanguage());

		{
			JPanel panel = new JPanel();
			panel.setMaximumSize(new Dimension(10000, 100));
			panel.setPreferredSize(new Dimension(300, 100));
			panel.setBorder(BorderFactory.createTitledBorder("Discourse Unit"));
			panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			panel.add(unitCombobox);

			this.add(panel);
		}
		unitCombobox.setSelectedItem(discourse.getUnitType());

		{
			JPanel panel = new JPanel();
			panel.setMaximumSize(new Dimension(10000, 200));
			panel.setPreferredSize(new Dimension(300, 150));
			panel.setBorder(BorderFactory
					.createTitledBorder("Discourse Unit LifeTime"));
			panel.setLayout(new BorderLayout());
			JPanel northPanel = new JPanel();
			northPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			northPanel.add(lifetimeCheckbox);
			panel.add(northPanel, BorderLayout.NORTH);

			JPanel centerPanel = new JPanel();
			centerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			lifetimeTextField.setColumns(10);
			lifetimeTextField.setEnabled(false);
			centerPanel.add(lifetimeTextField);
			panel.add(centerPanel, BorderLayout.CENTER);

			lifetimeCheckbox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					lifetimeTextField.setEnabled(lifetimeCheckbox.isSelected());
				}
			});

			this.add(panel);
		}
		int lifetime = discourse.getLifetime();
		if (lifetime > 0) {
			lifetimeCheckbox.setSelected(true);
			lifetimeTextField.setText(Integer.toString(lifetime));
		} else {
			lifetimeCheckbox.setSelected(false);
			lifetimeTextField.setText(Integer.toString(0));
		}
	}

	public void doOK() {
		final CPanelProcessingMonitor monitor = new CPanelProcessingMonitor();
		monitor.doTaskWithDialog(new ICTask() {
			public void doTask() {
				discourse.setLanguage(languageCombobox.getSelectedItem(),
						monitor);
				discourse.setUnitType(unitCombobox.getSelectedItem());
				if (lifetimeCheckbox.isSelected()) {
					int lifetime = Integer.parseInt(lifetimeTextField.getText());
					discourse.setLifetime(lifetime);
				} else {
					discourse.setLifetime(0);
				}
			}
		});
	}
}

//http://code.google.com/p/jcfx/source/browse/fx/trunk/fx-backtest/src/main/java/com/jeff/fx/gui/field/CEnumComboBox.java?r=312
class CEnumComboBox<T extends Enum<T>> extends JComboBox<T> {

	private static final long serialVersionUID = 1L;

	private Class<T> type;

	public CEnumComboBox(Class<T> enumClass) {
		this.type = enumClass;
		setModel(new DefaultComboBoxModel<T>(values()));
		setSelectedIndex(-1);
	}

	@SuppressWarnings("unchecked")
	public T resolve(String str) {
		try {
			return (T) type.getField(str).get(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public T getSelectedItem() {
		return (T) super.getSelectedItem();
	}

	@SuppressWarnings({ "unchecked" })
	private T[] values() {
		try {
			return (T[]) type.getMethod("values").invoke(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
