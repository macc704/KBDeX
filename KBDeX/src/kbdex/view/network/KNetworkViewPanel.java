/*
 * KNetworkViewPanel.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.network;

import java.awt.BorderLayout;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import kbdex.adapters.jung.KGraph;
import kbdex.adapters.jung.KGraphLayoutChooser;
import kbdex.adapters.jung.KNetworkPanel;
import kbdex.controller.IKDiscourseControllerListener;
import kbdex.controller.KNNetworkController;
import kbdex.model.kbmodel.KBElement;
import kbdex.model.kbmodel.KBRelation;
import clib.common.thread.ICTask;
import clib.view.actions.CActionUtils;

/**
 * @author macchan
 */
public class KNetworkViewPanel<V extends KBElement, E extends KBRelation>
		extends JPanel implements IKDiscourseControllerListener {

	private static final long serialVersionUID = 1L;

	private KNNetworkController<V> controller;

	private KNetworkPanel<V, E> networkPanel = new KNetworkPanel<V, E>();

	private KNetworkScaleController<V, E> scaleController = new KNetworkScaleController<V, E>(
			networkPanel);
	private KNetworkVertexDecorationController<V, E> vertexController;
	private KNetworkEdgeDecorationController<V, E> edgeController;
	private KNetworkExportActionContributor<V, E> exporter = new KNetworkExportActionContributor<V, E>(
			networkPanel);
	private KGraphLayoutChooser layoutChooser = new KGraphLayoutChooser(
			networkPanel);
	private KNetworkFontController<V, E> fontController = new KNetworkFontController<V, E>(
			networkPanel);

	private KParameterProvider<Integer> weightPerStroke;

	public KNetworkViewPanel(KNNetworkController<V> controller, Icon icon,
			KParameterProvider<Integer> weightPerStroke) {
		this.controller = controller;
		this.weightPerStroke = weightPerStroke;
		this.edgeController = new KNetworkEdgeDecorationController<V, E>(
				networkPanel, weightPerStroke);
		this.vertexController = new KNetworkVertexDecorationController<V, E>(
				networkPanel, icon);
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		add(networkPanel, BorderLayout.CENTER);
		add(createToolBar(), BorderLayout.SOUTH);
		networkPanel.addAncestorListener(new KListenerHooker());
	}

	public KNNetworkController<V> getController() {
		return controller;
	}

	public JMenuBar createMenubar() {
		JMenuBar menuBar = new JMenuBar();

		{
			JMenu menu = new JMenu("File");
			menuBar.add(menu);

			{
				menu.add(exporter.createExportPajekAction());
				menu.add(exporter.createExportRAction());
			}
		}

		{
			JMenu menu = new JMenu("View");
			menuBar.add(menu);

			{
				JCheckBox checkbox = new JCheckBox();
				checkbox.setSelected(true);
				checkbox.setAction(createToggleActiveAction(checkbox));
				menu.add(checkbox);
			}

			{
				menu.add(vertexController.getToggleLabelCheckBox());
				menu.add(vertexController.getToggleIconCheckBox());
			}

			{
				menu.add(edgeController.getToggleLineCurveCheckBox());
			}

			{
				menu.add(fontController.createDefaultFontAction());
				menu.add(fontController.createDialogFontAction());
			}
			
			{
				JMenu sub = new JMenu("WeightPerStroke");
				menu.add(sub);

				sub.add(createWeightPerStrokeAction(1));
				sub.add(createWeightPerStrokeAction(2));				
				sub.add(createWeightPerStrokeAction(3));				
				sub.add(createWeightPerStrokeAction(4));				
				sub.add(createWeightPerStrokeAction(8));
				sub.add(createWeightPerStrokeAction(16));
				sub.add(createWeightPerStrokeAction(32));
			}
		}

		return menuBar;
	}
	
	private Action createWeightPerStrokeAction(final int x){
		return CActionUtils.createAction(""+ x,
				new ICTask() {
			public void doTask() {
				weightPerStroke.set(x);
				networkPanel.getViewer().repaint();
			}
		});
	}

	protected Action createToggleActiveAction(final JToggleButton button) {
		return CActionUtils.createAction("Active", new ICTask() {
			public void doTask() {
				networkPanel.setActive(button.isSelected());
				networkPanel.getViewer().repaint();
			}
		});
	}

	private JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar();

		{//Chooser
			toolBar.add(layoutChooser.getComboBox());
			toolBar.add(layoutChooser.getResetButton());
		}

		toolBar.addSeparator();

		{
			toolBar.add(scaleController.createScaleDownAction());
			toolBar.add(scaleController.createScaleDefaultAction());
			toolBar.add(scaleController.createScaleUpAction());
			//			JPanel panel = new JPanel();
			//			panel.add(new JButton(scaleController.createScaleDownAction()));
			//			panel.add(new JButton(scaleController.createScaleDefaultAction()));
			//			panel.add(new JButton(scaleController.createScaleUpAction()));
			//			toolBar.add(panel);
		}

		toolBar.addSeparator();

		{
			toolBar.add(vertexController.createSizeDownAction());
			toolBar.add(vertexController.createSizeDefaultAction());
			toolBar.add(vertexController.createSizeUpAction());
			//			JPanel panel = new JPanel();
			//			panel.add(new JButton(vertexController.createSizeDownAction()));
			//			panel.add(new JButton(vertexController.createSizeDefaultAction()));
			//			panel.add(new JButton(vertexController.createSizeUpAction()));
			//			toolBar.add(panel);
		}

		return toolBar;
	}

	public KNetworkPanel<V, E> getNetworkPanel() {
		return networkPanel;
	}

	public KGraphLayoutChooser getGraphLayoutChooser() {
		return layoutChooser;
	}

	/******************************************************
	 * Implementation of IKDiscourseControllerListener
	 ******************************************************/

	class KListenerHooker implements AncestorListener {
		public void ancestorAdded(AncestorEvent event) {
			hookListeners();
		}

		public void ancestorRemoved(AncestorEvent event) {
			unhookListeners();
		}

		public void ancestorMoved(AncestorEvent event) {

		}
	}

	public void hookListeners() {
		controller.addNetworkControllerListener(this);
	}

	public void unhookListeners() {
		controller.removeNetworkControllerListener(this);
	}

	public boolean isAnimationFinished() {
		return networkPanel.isAnimationFinished();
	}

	public void hardReset() {
	}

	public void reset() {
	}

	public void tick() {
	}

	@SuppressWarnings("unchecked")
	public void refreshWithoutAnimation() {
		if (networkPanel.isActive()) {
			networkPanel.setGraph((KGraph<V, E>) controller.getModel()
					.getGraph());
			networkPanel.refresh();
		}
	}

	@SuppressWarnings("unchecked")
	public void refreshWithAnimation() {
		if (networkPanel.isActive()) {
			networkPanel.setGraph((KGraph<V, E>) controller.getModel()
					.getGraph());
			networkPanel.startAnimation();
		}
	}
}
