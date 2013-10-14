package kbdex.adapters.jung;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JPopupMenu;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.LayoutScalingControl;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.RotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;

public class KNetworkMouseHandler<V, E> extends PluggableGraphMouse {

	private JPopupMenu contextMenu = new JPopupMenu();

	public void addContextMenuActions(List<Action> actions) {
		for (Action action : actions) {
			this.contextMenu.add(action);
		}
	}

	public KNetworkMouseHandler(final VisualizationViewer<V, E> viewer) {
		this.add(new PickingGraphMousePlugin<V, E>() {

			private boolean dragged = false;

			public void mousePressed(MouseEvent e) {
				this.dragged = false;

				//handle click selection
				if (!e.isMetaDown()) {
					super.mousePressed(e);
					firePickingStateChanged(new ArrayList<V>(viewer
							.getPickedVertexState().getPicked()),
							new ArrayList<E>(viewer.getPickedEdgeState()
									.getPicked()));
				}
			}

			public void mouseDragged(MouseEvent e) {
				this.dragged = true;
				super.mouseDragged(e);
			}

			public void mouseReleased(MouseEvent e) {

				//handle marquee selection
				if (!e.isMetaDown() && this.dragged) {
					super.mouseReleased(e);
					firePickingStateChanged(new ArrayList<V>(viewer
							.getPickedVertexState().getPicked()),
							new ArrayList<E>(viewer.getPickedEdgeState()
									.getPicked()));
				}
			}

			public void mouseClicked(MouseEvent e) {
				if (!e.isMetaDown()) {
					super.mouseClicked(e);
					Point2D ip = e.getPoint();
					V vertex = viewer.getPickSupport().getVertex(
							viewer.getGraphLayout(), ip.getX(), ip.getY());
					if (vertex != null) {
						fireNodeClicked(vertex, e.getClickCount());
						return;
					}

					E edge = viewer.getPickSupport().getEdge(
							viewer.getGraphLayout(), ip.getX(), ip.getY());
					if (edge != null) {
						fireEdgeClicked(edge, e.getClickCount());
						return;
					}
				} else {
					if (contextMenu.getComponentCount() > 0) {
						contextMenu.show(viewer, e.getX(), e.getY());
					}
				}
			}
		});

		// this.add(new AnimatedPickingGraphMousePlugin<Number, Number>());
		this.add(new TranslatingGraphMousePlugin(InputEvent.CTRL_MASK
				| InputEvent.BUTTON3_MASK));
		this.add(new RotatingGraphMousePlugin(InputEvent.SHIFT_MASK
				| InputEvent.BUTTON3_MASK));
		this.add(new ScalingGraphMousePlugin(new LayoutScalingControl(), 0));

		// Key Picking
		viewer.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_UP:
					break;
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_DOWN:
					break;
				default:
					break;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_UP:
					pickTo(1);
					break;
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_DOWN:
					pickTo(-1);
					break;
				default:
					break;
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			private void pickTo(int i) {
				V picked = getPicked();
				if (picked == null) {
					return;
				}

				ArrayList<V> vertices = new ArrayList<V>(viewer.getModel()
						.getGraphLayout().getGraph().getVertices());
				int index = vertices.indexOf(picked);
				int size = vertices.size();
				if (!(0 <= index && index < size)) {
					return;
				}

				int newIndex = index + i;
				if (!(0 <= newIndex)) {
					newIndex = size - 1;
				}
				if (!(newIndex < size)) {
					newIndex = 0;
				}

				V newPicked = vertices.get(newIndex);
				viewer.getPickedVertexState().clear();
				viewer.getPickedVertexState().pick(newPicked, true);

				firePickingStateChanged(new ArrayList<V>(viewer
						.getPickedVertexState().getPicked()), new ArrayList<E>(
						viewer.getPickedEdgeState().getPicked()));
			}

			private V getPicked() {
				ArrayList<V> picked = new ArrayList<V>(viewer
						.getPickedVertexState().getPicked());
				if (!picked.isEmpty()) {
					return picked.get(0);
				} else {
					return null;
				}
			}
		});
	}

	// ---------------------------- listener

	private List<IKNetworkMouseListener<V, E>> networkListeners = new ArrayList<IKNetworkMouseListener<V, E>>();

	public void addNetworkListener(IKNetworkMouseListener<V, E> listener) {
		networkListeners.add(listener);
	}

	protected void fireNodeClicked(V node, int clickCount) {
		for (IKNetworkMouseListener<V, E> listener : networkListeners) {
			listener.nodeClicked(node, clickCount);
		}
	}

	protected void fireEdgeClicked(E edge, int clickCount) {
		for (IKNetworkMouseListener<V, E> listener : networkListeners) {
			listener.edgeClicked(edge, clickCount);
		}
	}

	private List<IKNetworkViewPickingListener<V, E>> pickingListeners = new ArrayList<IKNetworkViewPickingListener<V, E>>();

	public void addPickingListener(IKNetworkViewPickingListener<V, E> listener) {
		pickingListeners.add(listener);
	}

	protected void firePickingStateChanged(List<V> pickedNodes,
			List<E> pickedEdges) {
		for (IKNetworkViewPickingListener<V, E> listener : pickingListeners) {
			listener.pickingStateChanged(pickedNodes, pickedEdges);
		}
	}
}
