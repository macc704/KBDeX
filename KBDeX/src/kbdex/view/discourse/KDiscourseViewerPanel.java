/*
 * KDiscourseViewerPanel.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.discourse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import kbdex.model.discourse.KDDiscourse;
import kbdex.model.discourse.KDDiscourseRecord;
import kbdex.model.kbmodel.KBDiscourseUnit;
import kbdex.view.IKWindowManager;
import kbdex.view.modelviewers.KBDiscourseUnitViewer;
import clib.view.table.model.CListTableModel;
import clib.view.table.model.ICTableModelDescripter;

public class KDiscourseViewerPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public KDDiscourse discourse;
	private IKWindowManager wManager;

	private JTable table = new JTable();

	public KDiscourseViewerPanel() {
		initialize();
	}

	public KDiscourseViewerPanel(KDDiscourse discourse) {
		this();
		setDiscourse(discourse);
	}

	private void initialize() {
		setLayout(new BorderLayout());

		table.setAutoCreateRowSorter(true);
		table.setRowHeight(20);
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					doOpenDiscourseRecordViewer(e);
				}
			}
		});
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(table);
		add(scroll);

		refreshInvalidShowLabel();
	}

	public void setDiscourse(KDDiscourse discourse) {
		this.discourse = discourse;
		refreshView();
	}

	/**
	 * @param kDiscourseControllerView
	 */
	public void setWindowManager(IKWindowManager wManager) {
		this.wManager = wManager;
	}

	@SuppressWarnings("unchecked")
	private void doOpenDiscourseRecordViewer(MouseEvent e) {
		if (wManager != null) {
			int row = table.getSelectedRow();
			row = table.getRowSorter().convertRowIndexToModel(row);
			CListTableModel<KDDiscourseRecord> model = (CListTableModel<KDDiscourseRecord>) table
					.getModel();
			KDDiscourseRecord record = model.getModel(row);
			KBDiscourseUnitViewer viewer = new KBDiscourseUnitViewer(
					new KBDiscourseUnit(record));
			wManager.openFrame(viewer, viewer.getTitle(), null, null);
		}
	}

	public void refreshView() {
		if (discourse == null) {
			return;
		}

		List<KDDiscourseRecord> records = getRecords();
		CListTableModel<KDDiscourseRecord> tableModel = new CListTableModel<KDDiscourseRecord>(
				records, new KRecordTableModelDescripter());
		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(25);
		table.getColumnModel().getColumn(1).setPreferredWidth(25);
		table.getColumnModel().getColumn(2).setPreferredWidth(50);
		table.getColumnModel().getColumn(3).setPreferredWidth(700);

		table.getColumnModel().getColumn(0)
				.setCellRenderer(new KRecordValidationRenderer());
		table.getColumnModel().getColumn(1)
				.setCellRenderer(new KRecordValidationRenderer());
		table.getColumnModel().getColumn(2)
				.setCellRenderer(new KRecordValidationRenderer());
		table.getColumnModel().getColumn(3)
				.setCellRenderer(new KCashLabelTableCellRenderer());
		//table.getColumnModel().getColumn(3)
		//		.setCellRenderer(new KRecordValidationRenderer());

		// 時間のかかる処理 Label作成時（HTMLのパースに時間がかかる）
		// 以下，キャッシュをあらかじめ作る処理
		// int rowsize = table.getRowCount();
		// for (int i = 0; i < rowsize; i++) {
		// Object value = table.getModel().getValueAt(i, 2);
		// renderer.getTableCellRendererComponent(table, value, false,
		// false, i, 2);
		// }
	}

	class KRecordTableModelDescripter implements
			ICTableModelDescripter<KDDiscourseRecord> {
		private Class<?>[] classes = new Class<?>[] { Long.class, Long.class,
				String.class, JLabel.class };
		private String[] names = new String[] { "No", "Id", "Name", "Text" };

		public String getVariableName(int index) {
			return names[index];
		}

		public Class<?> getValiableClass(int index) {
			return classes[index];
		}

		public int getVariableCount() {
			return 4;
		}

		public Object getVariableAt(KDDiscourseRecord model, int index) {
			switch (index) {
			case 0:
				return discourse.getFilteredRecords().indexOf(model);
			case 1:
				return model.getId();
			case 2:
				return model.getAgentName();
			case 3:
				// return model.getText();
				return model.getHighlightedHtmlText();
			}
			throw new RuntimeException();
		}
	}

	class KRecordValidationRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component c = super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);

			if (isSelected || hasFocus) {
				return c;
			}

			setValidationColor(row, c);
			return c;
		}

	}

	class KCashLabelTableCellRenderer implements TableCellRenderer {
		public static final long serialVersionUID = 1L;

		private transient DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		private transient Map<Object, JComponent> cash = new HashMap<Object, JComponent>();

		public KCashLabelTableCellRenderer() {
			renderer.setVerticalAlignment(JLabel.TOP);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (isSelected || hasFocus) {
				return renderer.getTableCellRendererComponent(table, value,
						isSelected, hasFocus, row, column);
			}

			//Object model = table.getModel().getValueAt(row, column); //何で？ #matsuzawa 2012.11.09
			Object model = value; //ソートされないので修正 #matsuzawa 2012.11.09

			if (!cash.containsKey(model)) {
				CellLabel label = new CellLabel();
				label.setOpaque(true);
				label.setVerticalAlignment(JLabel.TOP);
				label.setFont(table.getFont());
				label.setText(model.toString());

				JComponent c = label;
				cash.put(model, c);
			}

			JComponent c = cash.get(model);
			setValidationColor(row, c);
			return c;
		}

		class CellLabel extends JLabel {
			private static final long serialVersionUID = 1L;

			public CellLabel() {
			}

			public void invalidate() {
			}

			public void validate() {
			}

			public void revalidate() {
			}

			public void repaint(long tm, int x, int y, int width, int height) {
			}

			public void repaint(Rectangle r) {
			}

			public void repaint() {
			}

		}
	}

	private void setValidationColor(int row, Component c) {
		if (!getRecords().get(row).isValid()) {
			c.setBackground(Color.LIGHT_GRAY);
		} else {
			c.setBackground(Color.WHITE);
		}
	}

	public void setSelectionRecord(KDDiscourseRecord record) {
		if (record != null) {
			synchronized (getTreeLock()) {
				table.changeSelection(getRecords().indexOf(record), 0, false,
						false);
				table.repaint();
			}
		} else {
			table.clearSelection();
			table.repaint();
		}
	}

	/******************************************************
	 * ShowInvalidRecord関係
	 ******************************************************/

	private boolean showInvalidRecord = false;

	private Action invalidShowOnAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			setShowInvalidRecord(true);
		}
	};

	public Action getInvalidShowOnAction() {
		return invalidShowOnAction;
	}

	private Action invalidShowOffAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			setShowInvalidRecord(false);
		}
	};

	public Action getInvalidShowOffAction() {
		return invalidShowOffAction;
	}

	public boolean isShowInvalidRecord() {
		return showInvalidRecord;
	}

	public void setShowInvalidRecord(boolean invalidShow) {
		if (showInvalidRecord != invalidShow) {
			showInvalidRecord = invalidShow;
			refreshView();
			refreshInvalidShowLabel();
		}
	}

	private void refreshInvalidShowLabel() {
		if (this.showInvalidRecord) {
			invalidShowOnAction.putValue(Action.NAME, "*Show All");
			invalidShowOffAction.putValue(Action.NAME, "Show Only Selected");
		} else {
			invalidShowOnAction.putValue(Action.NAME, "Show All");
			invalidShowOffAction.putValue(Action.NAME, "*Show Only Selected");
		}
	}

	private List<KDDiscourseRecord> getRecords() {
		if (showInvalidRecord) {
			return discourse.getAllRecords();
		} else {
			return discourse.getFilteredRecords();
		}
	}

}
