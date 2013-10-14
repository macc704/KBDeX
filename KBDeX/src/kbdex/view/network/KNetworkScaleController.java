/*
 * KNetworkScaleController.java
 * Created on Apr 23, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.network;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import javax.swing.Action;

import kbdex.adapters.jung.KNetworkPanel;
import clib.common.thread.ICTask;
import clib.view.actions.CActionUtils;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.control.LayoutScalingControl;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;

/**
 * @author macchan
 *
 */
public class KNetworkScaleController<V, E> {

	private static Point2D ZERO = new Point2D.Double(0, 0);

	private KNetworkPanel<V, E> networkPanel;

	private LayoutScalingControl layoutScaleControl = new LayoutScalingControl();

	public KNetworkScaleController(KNetworkPanel<V, E> networkPanel) {
		this.networkPanel = networkPanel;
	}

	protected Action createScaleUpAction() {
		return CActionUtils.createAction("+", new ICTask() {
			public void doTask() {
				layoutScaleControl.scale(networkPanel.getViewer(), 1.1f,
						getCenter());
			}
		});
	}

	protected Action createScaleDownAction() {
		return CActionUtils.createAction("-", new ICTask() {
			public void doTask() {
				layoutScaleControl.scale(networkPanel.getViewer(), 1 / 1.1f,
						getCenter());
			}
		});
	}

	protected Action createScaleDefaultAction() {
		return CActionUtils.createAction("<->", new ICTask() {
			public void doTask() {
				MutableTransformer modelTransformer = networkPanel.getViewer()
						.getRenderContext().getMultiLayerTransformer()
						.getTransformer(Layer.LAYOUT);
				modelTransformer.setScale(1d, 1d, ZERO);
				modelTransformer.setTranslate(0, 0);
			}
		});
	}

	private Point2D getCenter() {
		Dimension d = networkPanel.getViewer().getSize();
		return new Point2D.Double(d.width / 2, d.height / 2);
	}

}
