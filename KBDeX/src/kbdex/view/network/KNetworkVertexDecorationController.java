/*
 * KNetworkScaleController.java
 * Created on Apr 23, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.network;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JToggleButton;

import kbdex.adapters.jung.KNetworkPanel;
import kbdex.model.kbmodel.KBElement;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import clib.common.thread.ICTask;
import clib.view.actions.CActionUtils;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

/**
 * @author macchan
 */
public class KNetworkVertexDecorationController<V, E> {

	private KNetworkPanel<V, E> networkPanel;

	private int defaultSize = 10;
	private int currentSize = defaultSize;//radius

	private JCheckBox toggleLabelCheckBox = new JCheckBox();
	private JCheckBox toggleIconCheckBox = new JCheckBox();
	private Icon icon;

	private KToStringTransformer toStringTransformer = new KToStringTransformer();
	private KNullStringTransformer nullStringTransformer = new KNullStringTransformer();
	private KPaintTransformer paintTransformer;

	public KNetworkVertexDecorationController(KNetworkPanel<V, E> networkPanel,
			Icon icon) {
		this.networkPanel = networkPanel;
		this.paintTransformer = new KPaintTransformer();//順序に意味あり
		this.icon = icon;
		initialize();
	}

	protected void initialize() {
		networkPanel.getViewer().getRenderContext()
				.setVertexFillPaintTransformer(paintTransformer);
		networkPanel.getViewer().getRenderer().getVertexLabelRenderer()
				.setPosition(Position.CNTR);
		networkPanel.getViewer().getRenderContext()
				.setVertexDrawPaintTransformer(new Transformer<V, Paint>() {
					public Paint transform(V v) {
						return Color.BLACK;
					};
				});

		toggleLabelCheckBox.setSelected(true);
		toggleLabelCheckBox
				.setAction(createToggleLabelAction(toggleLabelCheckBox));
		setToggleLabelStatus(true);
		toggleIconCheckBox.setSelected(false);
		toggleIconCheckBox
				.setAction(createToggleIconAction(toggleIconCheckBox));
		setToggleIconStatus(false);
	}

	protected JCheckBox getToggleLabelCheckBox() {
		return toggleLabelCheckBox;
	}

	private Action createToggleLabelAction(final JToggleButton button) {
		return CActionUtils.createAction("Vertex CLabel", new ICTask() {
			public void doTask() {
				setToggleLabelStatus(button.isSelected());
				networkPanel.getViewer().repaint();
			}
		});
	}

	private void setToggleLabelStatus(boolean status) {
		if (status) {
			networkPanel.getViewer().getRenderContext()
					.setVertexLabelTransformer(toStringTransformer);
		} else {
			networkPanel.getViewer().getRenderContext()
					.setVertexLabelTransformer(nullStringTransformer);
		}
	}

	/**
	 * @return the toggleIconCheckBox
	 */
	protected JCheckBox getToggleIconCheckBox() {
		return toggleIconCheckBox;
	}

	private Action createToggleIconAction(final JToggleButton button) {
		return CActionUtils.createAction("Vertex Icon", new ICTask() {
			public void doTask() {
				setToggleIconStatus(button.isSelected());
				networkPanel.getViewer().repaint();
			}
		});
	}

	private void setToggleIconStatus(boolean status) {
		if (status) {
			networkPanel.getViewer().getRenderContext()
					.setVertexIconTransformer(new Transformer<V, Icon>() {
						@Override
						public Icon transform(V v) {
							return icon;
						}
					});
		} else {
			networkPanel.getViewer().getRenderContext()
					.setVertexIconTransformer(null);
		}
	}

	protected Action createSizeUpAction() {
		return CActionUtils.createAction("+", new ICTask() {
			public void doTask() {
				changeVertexSize(1);
				networkPanel.getViewer().getRenderContext()
						.setVertexShapeTransformer(getShape());
				networkPanel.getViewer().repaint();
			}
		});
	}

	protected Action createSizeDownAction() {
		return CActionUtils.createAction("-", new ICTask() {
			public void doTask() {
				changeVertexSize(-1);
				networkPanel.getViewer().getRenderContext()
						.setVertexShapeTransformer(getShape());
				networkPanel.getViewer().repaint();
			}
		});
	}

	protected Action createSizeDefaultAction() {
		return CActionUtils.createAction("V", new ICTask() {
			public void doTask() {
				currentSize = defaultSize;
				networkPanel.getViewer().getRenderContext()
						.setVertexShapeTransformer(getShape());
				networkPanel.getViewer().repaint();
			}
		});
	}

	private void changeVertexSize(int x) {
		this.currentSize += x;
		if (this.currentSize <= 0) {
			this.currentSize = 1;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Transformer<V, Shape> getShape() {
		Transformer<V, Shape> vertexShapeTransformer = new ConstantTransformer(
				new Ellipse2D.Float(-currentSize, -currentSize,
						currentSize * 2, currentSize * 2));
		return vertexShapeTransformer;
	}

	class KToStringTransformer implements Transformer<V, String> {
		public String transform(V node) {
			return node.toString();
		}
	}

	class KNullStringTransformer implements Transformer<V, String> {
		public String transform(V node) {
			return "";
		}
	}

	class KPaintTransformer extends PickableVertexPaintTransformer<V> {
		public KPaintTransformer() {
			super(networkPanel.getViewer().getRenderContext()
					.getPickedVertexState(), Color.YELLOW, Color.RED);
		}

		public Paint transform(V v) {
			if (v instanceof KBElement && ((KBElement) v).isSelected()) {
				return picked_paint;
			} else if (v instanceof KBElement && !((KBElement) v).isValid()) {
				return Color.GRAY;
			} else {
				return fill_paint;
			}
		}
	}

}
