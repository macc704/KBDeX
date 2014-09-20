/*
 * KHistgramViewer.java
 * Created on Jul 21, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.discourse.selection.word;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import kbdex.model.discourse.KDDiscourse;
import clib.common.collections.CList;
import clib.view.table.model.CMapTableModel;

/**
 * 頻度グラフ表示パネル
 * @author macchan
 * 
 * ・問題　ソートがやたら遅い．→解決（なんでかわすれたが，ソートアルゴリズムでComparatorの呼ばれ方でcompareがボトルネックになっていた．
 * 		// 遅かった箇所を発見するためのテストコード
		// table.setRowSorter(new TableRowSorter<TableModel>(model) {
		// @SuppressWarnings("unchecked")
		// public Comparator<?> getComparator(int column) {
		// Class columnClass = getModel().getColumnClass(column);
		// if (columnClass.isAssignableFrom(Number.class)) {
		// System.out.println(columnClass);
		// }
		// return super.getComparator(column);
		// }
		// }); 
 */
public class KHistgramViewer extends JPanel {

	private static final long serialVersionUID = 1L;

	private KDDiscourse discourse;

	private JTable table = new JTable();
	private Map<String, String> selectedWordsCash = new HashMap<String, String>();

	private List<IKWordSelectionListener> wordSelectionListeners = new ArrayList<IKWordSelectionListener>();

	private JPopupMenu popupMenu = new JPopupMenu();

	public KHistgramViewer() {
		initialize();
	}

	private void initialize() {

		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(table);
		setLayout(new BorderLayout());
		add(scroll);

		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() == 2
						&& table.getSelectedColumn() == 0) { // left double click for word column
					List<String> words = getSelectingWords();
					for (String word : words) {
						//toggle
						if (selectedWordsCash.containsKey(word)) {
							fireWordDeselected(CList.createList(word));
						} else {
							fireWordSelected(CList.createList(word));
						}
					}
				}
				if (e.getButton() == MouseEvent.BUTTON3
						&& e.getClickCount() == 1) {// right click
					popupMenu.show(table, e.getX(), e.getY());
				}
			}
		});

		JMenuItem itemSelect = new JMenuItem("Select");
		itemSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<String> words = getSelectingWords();
				fireWordSelected(words);
			}
		});
		popupMenu.add(itemSelect);

		JMenuItem itemDeselect = new JMenuItem("Deselect");
		itemDeselect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<String> words = getSelectingWords();
				fireWordDeselected(words);
			}

		});
		popupMenu.add(itemDeselect);
	}

	private List<String> getSelectingWords() {
		List<String> words = new ArrayList<String>();
		int[] rows = table.getSelectedRows();
		for (int row : rows) {
			row = table.getRowSorter().convertRowIndexToModel(row);
			String word = (String) table.getModel().getValueAt(row, 0);
			words.add(word);
		}
		return words;
	}

	public void setDiscourse(KDDiscourse discourse) {
		this.discourse = discourse;
		refreshVocaburary();
		refreshSelectedWords();
	}

	public void refreshVocaburary() {
		table.setAutoCreateRowSorter(true);
		Map<String, Integer> histgram = discourse.getVocaburary().getHistgram();
		CMapTableModel<String, Integer> model = new CMapTableModel<String, Integer>(
				histgram) {
			private static final long serialVersionUID = 1L;

			public Class<?> getColumnClass(int column) {
				switch (column) {
				case 0:
					return String.class;
				case 1:
					return Integer.class;
				default:
					throw new RuntimeException();
				}
			}

			public String getColumnName(int column) {
				switch (column) {
				case 0:
					return "Word";
				case 1:
					return "Count";
				default:
					throw new RuntimeException();
				}
			}
		};
		table.setModel(model);
		table.getRowSorter().toggleSortOrder(1);
		table.getRowSorter().toggleSortOrder(1);
		table.getColumnModel().getColumn(0).setCellRenderer(new CellRenderer());
	}

	public void refreshSelectedWords() {
		this.selectedWordsCash.clear();
		for (String word : discourse.getSelectedWords()) {
			selectedWordsCash.put(word, word);
		}
		this.table.repaint();
	}

	class CellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component c = super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
			String word = (String) value;
			if (selectedWordsCash.containsKey(word)) {
				c.setForeground(Color.RED);
			} else {
				c.setForeground(Color.BLACK);
			}
			return c;
		}
	}

	public void addWordSelectionListener(IKWordSelectionListener listener) {
		wordSelectionListeners.add(listener);
	}

	public void removeWordSelectionListener(IKWordSelectionListener listener) {
		wordSelectionListeners.remove(listener);
	}

	protected void fireWordSelected(List<String> words) {
		for (IKWordSelectionListener listener : wordSelectionListeners) {
			listener.wordSelected(words);
		}
	}

	protected void fireWordDeselected(List<String> words) {
		for (IKWordSelectionListener listener : wordSelectionListeners) {
			listener.wordDeselected(words);
		}
	}

}
