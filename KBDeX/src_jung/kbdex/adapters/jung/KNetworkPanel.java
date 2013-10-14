package kbdex.adapters.jung;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import clib.common.thread.CTaskManager;
import clib.common.thread.ICTask;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.algorithms.layout.util.VisRunner;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.util.Animator;

public class KNetworkPanel<V, E> extends JPanel {

	private static final long serialVersionUID = 1L;

	private final Dimension INITIAL_SIZE = new Dimension(600, 600);

	private CTaskManager taskManager = new CTaskManager();

	private KVisualizationViewer<V, E> viewer;
	private Layout<V, E> layout;
	private KNetworkMouseHandler<V, E> mouseHandler;

	private boolean animation = true;
	private boolean active = true;

	public KNetworkPanel() {
		initializeTaskManager();
		initializeComponents();
	}

	private void initializeTaskManager() {
		this.addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent event) {
				taskManager.start();
				viewer.requestRefresh();
			}

			public void ancestorRemoved(AncestorEvent event) {
				taskManager.stop();
			}

			public void ancestorMoved(AncestorEvent event) {
			}
		});
	}

	private void initializeComponents() {
		// Graphを作成する
		Graph<V, E> graph = new SparseGraph<V, E>();

		// Layoutを作成する
		layout = new KCircleLayout<V, E>(graph);
		layout.setSize(INITIAL_SIZE);

		// Viewerを作成する
		viewer = new KVisualizationViewer<V, E>(layout, INITIAL_SIZE);
		viewer.setDoubleBuffered(false);
		viewer.setBackground(Color.WHITE);
		this.mouseHandler = new KNetworkMouseHandler<V, E>(viewer);
		viewer.setGraphMouse(mouseHandler);

		// Viewerを乗せる
		this.setLayout(new BorderLayout());
		GraphZoomScrollPane scroll = new GraphZoomScrollPane(viewer);
		this.add(scroll, BorderLayout.CENTER);
		//this.add(viewer, BorderLayout.CENTER);
	}

	public void setAnimation(boolean animation) {
		this.animation = animation;
	}

	public boolean isAnimation() {
		return animation;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public void setGraph(Graph<V, E> graph) {
		this.layout.setGraph(graph);
	}

	public Graph<V, E> getGraph() {
		return this.layout.getGraph();
	}

	public void setGraphLayout(Layout<V, E> layout) {
		this.layout = layout;
		refreshLayout();
	}

	public Layout<V, E> getGraphLayout() {
		return this.layout;
	}

	public VisualizationViewer<V, E> getViewer() {
		return this.viewer;
	}

	public boolean isAnimationFinished() {
		return !this.taskManager.isTaskProcessing();
	}

	public void startAnimation() {
		this.refreshLayout();
	}

	public void refresh() {
		viewer.requestRefresh();
		viewer.repaint();
	}

	private void refreshLayout() {
		if (active) {
			taskManager.cancelAllWaitingTasks();
			taskManager.addTask(new ICTask() {
				public void doTask() {
					StaticLayout<V, E> toLayout;
					Layout<V, E> currentLayout;
					synchronized (getTreeLock()) {
						currentLayout = viewer.getGraphLayout(); // StaticLayout
						layout.setSize(viewer.getSize());
						if (layout instanceof IterativeContext) { // 最後まで進める
							// layout.setInitializer(currentLayout);
							Relaxer relaxer = new VisRunner(
									(IterativeContext) layout);
							relaxer.prerelax();
						} else {// CircleLayoutの場合
							layout.initialize();
						}
						toLayout = new StaticLayout<V, E>(getGraph(), layout);
					}
					if (isAnimation()) {
						LayoutTransition<V, E> layoutTransition = new LayoutTransition<V, E>(
								viewer, currentLayout, toLayout);

						Animator animator = new Animator(layoutTransition);
						// animator.setSleepTime(100);						
						animator.run(); // animator.start();

						//						while (!layoutTransition.done()) {
						//							viewer.requestRefresh();
						//							layoutTransition.step();
						//							CThread.sleep(10L);
						//						}
					} else {
						viewer.setGraphLayout(toLayout);
						// while (!layoutTransition.done()) {
						// layoutTransition.step();
						// }
					}

					// viewer.getRenderContext().getMultiLayerTransformer()
					// .setToIdentity();
					viewer.requestRefresh();
					viewer.repaint();
				}
			});
		}
	}

	public void addContextMenuActions(List<Action> actions) {
		this.mouseHandler.addContextMenuActions(actions);
	}

	public void setAntialiasing(boolean antialiasing) {
		if (antialiasing) {
			viewer.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			viewer.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
		}
	}

	public boolean isAntialiasing() {
		return viewer.getRenderingHints().get(RenderingHints.KEY_ANTIALIASING) == RenderingHints.VALUE_ANTIALIAS_ON;
	}

	public void addNetworkListener(IKNetworkMouseListener<V, E> listener) {
		if (this.mouseHandler != null) {
			this.mouseHandler.addNetworkListener(listener);
		} else {
			throw new RuntimeException();
		}
	}

	public void addPickingListener(IKNetworkViewPickingListener<V, E> listener) {
		if (this.mouseHandler != null) {
			this.mouseHandler.addPickingListener(listener);
		} else {
			throw new RuntimeException();
		}
	}

}

class KVisualizationViewer<V, E> extends VisualizationViewer<V, E> {
	private static final long serialVersionUID = 1L;

	//private BufferedImage cash;

	public KVisualizationViewer(Layout<V, E> layout, Dimension preferredSize) {
		super(layout, preferredSize);
	}

	public void requestRefresh() {
		//		synchronized (getTreeLock()) {
		//			cash = null;
		//		}
	}

	//	protected void paintComponent(Graphics g) {
	//		synchronized (getTreeLock()) {
	//			if (cash == null) {
	//				cash = new BufferedImage(getWidth(), getHeight(),
	//						BufferedImage.TYPE_4BYTE_ABGR);
	//
	//				super.paintComponent(cash.getGraphics());
	//			}
	//			((Graphics2D) g).drawImage(cash, null, 0, 0);
	//		}
	//	}
}
