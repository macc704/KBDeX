/*
 * KGraphLayoutChooser.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.adapters.jung;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class KGraphLayoutChooser extends JPanel {

	private static final long serialVersionUID = 1L;

	private CLayoutCombobox comboBoxLayout = CLayoutCombobox
			.createDefaultCombobox();
	private JButton setButton = new JButton("Set");

	private KNetworkPanel panel;

	public KGraphLayoutChooser(KNetworkPanel panel) {
		this.panel = panel;
		initialize();
	}

	private void initialize() {
		this.add(comboBoxLayout);
		this.add(setButton);

		comboBoxLayout.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				resetLayout();
			}
		});
		setButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetLayout();
			}
		});
	}

	/**
	 * @return the comboBoxLayout
	 */
	public JComboBox getComboBox() {
		return comboBoxLayout;
	}

	/**
	 * @return the setButton
	 */
	public JButton getResetButton() {
		return setButton;
	}

	public void resetLayout() {
		Layout layout = comboBoxLayout.getSelectedLayout(panel.getGraph());
		panel.setGraphLayout(layout);
	}

	public void selectLayout(Class<? extends Layout> clazz) {
		comboBoxLayout.selectLayout(clazz);
	}

}

@SuppressWarnings({ "unchecked", "rawtypes" })
class CLayoutCombobox extends JComboBox {

	private static final long serialVersionUID = 1L;

	private Map<Class, String> names = new HashMap<Class, String>();

	public static CLayoutCombobox createDefaultCombobox() {
		CLayoutCombobox chooser = new CLayoutCombobox();
		chooser.addLayoutClass(KCircleLayout.class, "Circle");
		chooser.addLayoutClass(FRLayout.class, "Fruchterman-Reingold");
		chooser.addLayoutClass(KKLayout.class, "Kamada-Kawai");
		chooser.addLayoutClass(SpringLayout.class, "Spring");
		//chooser.addLayoutClass(SpringLayout2.class, "Spring2");
		//chooser.addLayoutClass(ISOMLayout.class, "Self-Organizing Map");
		return chooser;
	}

	public void addLayoutClass(Class<? extends Layout> clazz, String name) {
		names.put(clazz, name);
		this.addItem(clazz);
	}

	public CLayoutCombobox() {
		this.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				String valueString = names.get(value);
				return super.getListCellRendererComponent(list, valueString,
						index, isSelected, cellHasFocus);
			}
		});
	}

	public Layout getSelectedLayout(Graph graph) {
		try {
			Class<? extends Layout> layoutC = (Class<? extends Layout>) this
					.getSelectedItem();
			Constructor<? extends Layout> constructor = layoutC
					.getConstructor(new Class[] { Graph.class });
			Object[] constructorArgs = { graph };
			Object o = constructor.newInstance(constructorArgs);
			return (Layout) o;
		} catch (Exception ex) {
			throw new RuntimeException();
		}
	}

	public void selectLayout(Class<? extends Layout> clazz) {
		setSelectedItem(clazz);
	}
}
