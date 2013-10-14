package kfl.app.kfn;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import kfl.model.KFView;
import clib.view.windowmanager.CWindowCentraizer;

public class KFViewChooser extends JPanel {

	private static final long serialVersionUID = 1L;

	private Window parentWindow;

	private List<KFViewBox> viewboxes = new ArrayList<KFViewBox>();

	public KFViewChooser(List<KFView> views, Window parentWindow) {
		this.parentWindow = parentWindow;
		for (KFView view : views) {
			viewboxes.add(new KFViewBox(view));
		}
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());

		JPanel northPanel = new JPanel();
		add(northPanel, BorderLayout.NORTH);
		JLabel title = new JLabel("Please select view(s)");
		northPanel.add(title);

		JPanel mainPanel = new JPanel();
		add(mainPanel);
		mainPanel.setLayout(new BorderLayout());
		JPanel listPanel = new JPanel();
		listPanel.setBackground(Color.WHITE);
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		for (KFViewBox viewbox : viewboxes) {
			listPanel.add(viewbox);
		}
		JScrollPane scroll = new JScrollPane(listPanel);
		mainPanel.add(scroll);

		JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);
		JButton button = new JButton("OK");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doOK();
			}
		});
		buttonPanel.add(button);
	}

	private void doOK() {
		parentWindow.dispose();
	}

	public List<KFView> getSelectedViews() {
		List<KFView> selectedViews = new ArrayList<KFView>();
		for (KFViewBox viewbox : viewboxes) {
			if (viewbox.isSelected()) {
				selectedViews.add(viewbox.getKfView());
			}
		}
		return selectedViews;
	}

	class KFViewBox extends JCheckBox {
		private static final long serialVersionUID = 1L;
		private KFView kfView;

		KFViewBox(KFView kfView) {
			super(kfView.toString());
			setBackground(Color.WHITE);
			this.kfView = kfView;
		}

		public KFView getKfView() {
			return kfView;
		}
	}

	public static void main(String[] args) {
		List<KFView> views = new ArrayList<KFView>();
		views.add(new KFView("A"));
		views.add(new KFView("B"));
		views.add(new KFView("C"));
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(300, 300);
		CWindowCentraizer.centerWindow(frame);
		KFViewChooser chooser = new KFViewChooser(views, frame);
		frame.getContentPane().add(chooser);
		frame.setVisible(true);
	}

}
