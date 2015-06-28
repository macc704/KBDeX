/*
 * KFLoginPanelGroup.java
 * Created on Jul 16, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kfl.connector;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import clib.view.windowmanager.CWindowCentraizer;

/**
 * @author macchan
 */
public class KFLoginPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel protocolLabel = new JLabel("Protocol:");
	private JComboBox<String> protocols = new JComboBox<String>();
	private JLabel hostLabel = new JLabel("Host:");
	private JTextField host = new JTextField(20);
	private JLabel portLabel = new JLabel("Port:");
	private JTextField port = new JTextField(5);
	private JLabel databaseLabel = new JLabel("Database:");
	private JTextField database = new JTextField(20);
	private JLabel userLabel = new JLabel("User:");
	private JTextField user = new JTextField(15);
	private JLabel passwordLabel = new JLabel("Password:");
	private JPasswordField password = new JPasswordField(15);

	private JButton login = new JButton("Login");

	private String failiureMessage = null;
	private boolean ok = false;

	private boolean showDatabase = true;

	private KFLoginModel model;

	public KFLoginPanel() {
		initializeComponents();
	}

	public void setShowDatabase(boolean showDatabase) {
		this.showDatabase = showDatabase;
	}

	public boolean isShowDatabase() {
		return showDatabase;
	}

	@Deprecated
	public void setFailed(boolean failed) {
		if (failed) {
			this.failiureMessage = "Failed - Try Again";
		} else {
			this.failiureMessage = null;
		}
	}

	public void setFailiureMessage(String message) {
		this.failiureMessage = message;
	}

	public boolean isOk() {
		return ok;
	}

	public void setModel(KFLoginModel model) {
		this.model = model;
		host.setText(model.getHost());
		port.setText(Integer.toString(model.getPort()));
		database.setText(model.getDBName());
		user.setText(model.getUser());
		password.setText(model.getPassword());
	}

	private void refreshModel() {
		model.setProtocol((String) protocols.getSelectedItem());
		model.setHost(host.getText());
		model.setPort(Integer.parseInt(port.getText()));
		model.setDBName(database.getText());
		model.setUser(user.getText());
		model.setPassword(new String(password.getPassword()));
	}

	void initializeComponents() {
		protocols.setPreferredSize(new Dimension(100, protocols
				.getPreferredSize().height));
		protocols.addItem("https");
		protocols.addItem("http");
		protocols.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if ("https".equals(protocols.getSelectedItem())) {
					port.setText("443");
				}
				if ("http".equals(protocols.getSelectedItem())) {
					port.setText("80");
				}
			}
		});
		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ok = true;
				if (dialog != null) {
					dialog.setVisible(false);
				}
			}
		});
		doMyLayout();
	}

	void doMyLayout() {
		removeAll();
		setLayout(null);

		int X = 50;
		int Y = 20;
		int w1 = 100;
		int h = 30;

		int x = X;
		int y = Y;

		addC(new JLabel("Login to KF:"), x, y);

		if (failiureMessage != null) {
			JLabel label = new JLabel(failiureMessage);
			label.setForeground(Color.RED);
			addC(label, x + w1, y);
		}
		y += h;
		addLine(protocolLabel, protocols, w1, x, y);
		y += h;
		addLine(hostLabel, host, w1, x, y);
		y += h;
		addLine(portLabel, port, w1, x, y);
		y += h;
		if (isShowDatabase()) {
			addLine(databaseLabel, database, w1, x, y);
			y += h;
		}
		addLine(userLabel, user, w1, x, y);
		y += h;
		addLine(passwordLabel, password, w1, x, y);
		y += h;
		addC(login, x + w1, y);
		y += h;

		setPreferredSize(new Dimension(w1 * 3 + X * 2, y + Y));
	}

	private void addLine(Component comp1, Component comp2, int w1, int x, int y) {
		addC(comp1, x, y);
		x += w1;
		addC(comp2, x, y);
	}

	void addC(Component comp, int x, int y) {
		comp.setSize(comp.getPreferredSize());
		comp.setLocation(x, y);
		add(comp);
	}

	private JDialog dialog;

	public void openDialog() {
		ok = false;
		dialog = new JDialog((JFrame) null, true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		doMyLayout();
		dialog.getContentPane().add(this);
		dialog.pack();
		CWindowCentraizer.centerWindow(dialog);
		dialog.setVisible(true);

		dialog.dispose();

		refreshModel();
	}

	public static void main(String[] args) {
		// CFrameTester.open(new KFLoginPanel());
		new KFLoginPanel().openDialog();
		System.exit(0);
	}
}
