/*
 * KNetworkScaleController.java
 * Created on Apr 23, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.network;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;

import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JToggleButton;

import kbdex.adapters.jung.KNetworkPanel;
import kbdex.model.kbmodel.KBRelation;

import org.apache.commons.collections15.Transformer;

import clib.common.thread.ICTask;
import clib.view.actions.CActionUtils;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;

/**
 * @author macchan
 */
public class KNetworkEdgeDecorationController<V, E> {

	private KNetworkPanel<V, E> networkPanel;

	private Transformer<Context<Graph<V, E>, E>, Shape> quad = new EdgeShape.QuadCurve<V, E>();
	private Transformer<Context<Graph<V, E>, E>, Shape> straight = new EdgeShape.Line<V, E>();

	private JCheckBox toggleLineCurveCheckBox = new JCheckBox();
	
	private KParameterProvider<Integer> weightPerStroke = null;

	public KNetworkEdgeDecorationController(KNetworkPanel<V, E> networkPanel, KParameterProvider<Integer> weightPerStroke) {
		this.networkPanel = networkPanel;
		this.weightPerStroke = weightPerStroke;
		initialize();
	}

	protected void initialize() {
		networkPanel.getViewer().getRenderContext().setEdgeStrokeTransformer(
				new Transformer<E, Stroke>() {
					int MAX = 10;
					BasicStroke[] strokes;					

					void initialize() {
						strokes = new BasicStroke[MAX];
						for (int i = 0; i < MAX; i++) {
							strokes[i] = new BasicStroke(i + 1);
						}
					}

					Stroke get(int count) {
						if (count <= 0) {
							count = 1;
						} else if (count > MAX) {
							count = MAX;
						}
						if (strokes == null) {
							initialize();
						}
						return strokes[count - 1];
					}

					public Stroke transform(E e) {
						int wps = weightPerStroke.get();
						int count = ((KBRelation) e).getReasonCount();
						return get(count/wps);
					};
				});

		toggleLineCurveCheckBox.setSelected(false);
		toggleLineCurveCheckBox
				.setAction(createToggleLineCurveAction(toggleLineCurveCheckBox));
		setCurveToggleStatus(false);
	}

	protected JCheckBox getToggleLineCurveCheckBox() {
		return toggleLineCurveCheckBox;
	}

	private Action createToggleLineCurveAction(final JToggleButton button) {
		return CActionUtils.createAction("EdgeLine Curve", new ICTask() {
			public void doTask() {
				setCurveToggleStatus(button.isSelected());
				networkPanel.getViewer().repaint();
			}
		});
	}

	private void setCurveToggleStatus(boolean status) {
		if (status) {
			networkPanel.getViewer().getRenderContext()
					.setEdgeShapeTransformer(quad);
		} else {
			networkPanel.getViewer().getRenderContext()
					.setEdgeShapeTransformer(straight);
		}
	}

}
