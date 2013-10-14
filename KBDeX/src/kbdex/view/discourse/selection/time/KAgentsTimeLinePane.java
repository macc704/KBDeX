/*
 * KFiltersTimeLinePane.java
 * Created on Mar 24, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.discourse.selection.time;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import kbdex.model.discourse.KDAgent;
import kbdex.model.discourse.KDDiscourseRecord;
import clib.common.time.CTime;
import clib.view.timeline.model.CTimeTransformationModel;
import clib.view.timeline.pane.CAbstractTimeLinePane;

/**
 * @author macchan
 */
public class KAgentsTimeLinePane extends CAbstractTimeLinePane<KDAgent> {

	private static final long serialVersionUID = 1L;

	public KAgentsTimeLinePane() {
	}

	public JComponent createLeftPanel(KDAgent model) {
		JComponent panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BorderLayout());

		// name label
		JLabel label = new JLabel(model.getName());
		panel.add(label);

		// pulling panel
		JPanel pullingPanel = new JPanel();
		pullingPanel.setBackground(Color.WHITE);
		pullingPanel.setLayout(new BorderLayout());
		pullingPanel.add(new JLabel("+", SwingConstants.CENTER));
		pullingPanel.setPreferredSize(new Dimension(20, 20));
		MouseAdapter l = createDragMouseListener(model);
		pullingPanel.addMouseListener(l);
		pullingPanel.addMouseMotionListener(l);
		panel.add(pullingPanel, BorderLayout.WEST);
		return panel;
	}

	public JComponent createRightPanel(KDAgent model) {
		KTimelinePanelOneAgent mpanel = new KTimelinePanelOneAgent(
				getTimelinePane().getTimeTransModel(), model);
		mpanel.setOpaque(false);

		//mpanel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); //for test
		return mpanel;
	}

	public int getComponentHeight() {
		return 30;
	}

	/**
	 * 一つのAgentのタイムラインを表すパネル．ノートの位置に黒い点（パネル）を打つ
	 */
	public class KTimelinePanelOneAgent extends JPanel {

		private static final long serialVersionUID = 1L;

		private CTimeTransformationModel time;
		private KDAgent model;

		/**
		 * Constructor
		 */
		public KTimelinePanelOneAgent(CTimeTransformationModel time,
				KDAgent model) {
			this.time = time;
			this.model = model;

			initialize();
		}

		public Dimension getPreferredSize() {
			return new Dimension(super.getPreferredSize().width,
					getComponentHeight());
		}

		public KDAgent getModel() {
			return model;
		}

		private PropertyChangeListener listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				layoutRecords();
			}
		};

		private void initialize() {
			setLayout(null);
			setBackground(Color.WHITE);
			setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

			for (KDDiscourseRecord unit : model.getRecords()) {
				RecordPanel p = new RecordPanel(unit);
				add(p);
			}

			//スケールが変わったときに，タイム点の位置をレイアウトし直す
			time.addPropertyChangeListener(listener);
			addAncestorListener(new AncestorListener() {
				boolean removed = false;

				public void ancestorAdded(AncestorEvent event) {
					if (removed == true) {
						time.addPropertyChangeListener(listener);
						removed = false;
					}
				}

				public void ancestorRemoved(AncestorEvent event) {
					if (removed = false) {
						time.removePropertyChangeListener(listener);
						removed = true;
					}
				}

				public void ancestorMoved(AncestorEvent event) {
				}
			});
			// 以下の方が良いと思ったが，ウインドウ幅よりパネルが小さくなる場合，ウインドウ幅が変わらないので以下では対応できない．
			// addComponentListener(new ComponentAdapter() {
			// public void componentResized(ComponentEvent e) {
			// layoutRecords();
			// }
			// });

			layoutRecords();
		}

		private void layoutRecords() {
			int y = getComponentHeight() / 2;
			for (Component c : getComponents()) {
				RecordPanel panel = (RecordPanel) c;
				KDDiscourseRecord record = panel.getRecord();
				int x = (int) time.time2X(new CTime(record.getTimeAsDate()));
				c.setSize(c.getPreferredSize());
				Rectangle r = c.getBounds();
				r.x = x - r.width / 2;
				r.y = y - r.height / 2;
				c.setBounds(r);
			}
		}

		class RecordPanel extends JPanel {
			private static final long serialVersionUID = 1L;

			private KDDiscourseRecord record;

			public KDDiscourseRecord getRecord() {
				return record;
			}

			public RecordPanel(KDDiscourseRecord record) {
				setPreferredSize(new Dimension(4, 4));
				setBackground(Color.BLACK);
				this.record = record;
				addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						//TODO 10 実装すること
						System.out.println("click:" + RecordPanel.this.record);
					}
				});
			}
		}
	}
}
