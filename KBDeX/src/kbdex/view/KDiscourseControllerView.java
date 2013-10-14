/*
 * KDiscourseControllerView.java
 * Created on Apr 12, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import kbdex.adapters.jung.scorer.KVertexBetweennessScorer;
import kbdex.adapters.jung.scorer.KVertexFormalizedDegreeScorer;
import kbdex.app.KBDeX;
import kbdex.controller.KDiscourseController;
import kbdex.controller.KNNetworkController;
import kbdex.controller.metrics.IKTemporalMetricsScorer;
import kbdex.controller.metrics.KMetricsScorerSuite;
import kbdex.model.kbmodel.KBAgent;
import kbdex.model.kbmodel.KBDiscourseUnit;
import kbdex.model.kbmodel.KBElement;
import kbdex.model.kbmodel.KBRelation;
import kbdex.model.kbmodel.KBWord;
import kbdex.view.network.KNetworkViewFactory;
import kbdex.view.network.KNetworkViewPanel;
import kbdex.view.network.metrics.KMetricsManagerPanel;
import kbdex.view.network.metrics.KMetricsWindowOpenerPanel;
import clib.common.thread.ICTask;
import clib.view.actions.CActionManager;
import clib.view.actions.CActionUtils;

/**
 * @author macchan
 *
 */
public class KDiscourseControllerView implements IKWindowManager {

	private KDiscourseController controller;

	private CActionManager actionManager = new CActionManager();
	private JDesktopPane desktop = new JDesktopPane();
	private KTorontoLayoutManager layoutManager = new KTorontoLayoutManager(
			desktop);
	private KNetworkViewFactory viewFactory;
	private KDiscourseMenuProvider menuProvider;

	private JInternalFrame discourseViewer;

	public KDiscourseControllerView(KDiscourseController controller) {
		this.controller = controller;
		controller.getDiscourseViewer().setWindowManager(this);
		this.viewFactory = new KNetworkViewFactory(controller, this);
		this.menuProvider = new KDiscourseMenuProvider(controller, this);
	}

	public JFrame openMainFrame() {
		final JFrame frame = new JFrame();
		this.controller.setOwnerFrame(frame);
		frame.setTitle("KBDeX - " + controller.getDiscourse().getName());
		frame.setIconImage(KBDeX.getInstance().getIconImage16());
		frame.setJMenuBar(menuProvider.createMenuBar());

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(menuProvider.createToolBar(),
				BorderLayout.NORTH);
		//JDesktopPane, and JInternalFrame moving repaint strategy is too slow.
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		frame.getContentPane().add(desktop, BorderLayout.CENTER);

		//SetSize
		int offset = 10;
		GraphicsEnvironment gEnv = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		//GraphicsDevice gDevice = gEnv.getDefaultScreenDevice();
		Rectangle r = gEnv.getMaximumWindowBounds();
		r.grow(-offset, -offset);
		frame.setBounds(r);

		//Toronto and Open
		frame.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				initializeAsTorontoStyle();
			}
		});
		frame.setVisible(true);

		return frame;
	}

	private void initializeAsTorontoStyle() {
		//Open Frames
		openDiscourseViewer();

		//Layout
		doTorontoLayout();
	}

	private void doTorontoLayout() {
		clearFrames();
		openAgentViewer();
		openDiscourseUnitViewer();
		openWordViewer();
		initializeLayout();
		controller.doReloadToThisFrame(true);
	}

	public Action createOpenAgentViewerAction() {
		Action action = actionManager.createAction("[A]", new ICTask() {
			public void doTask() {
				openAgentViewer();
			}
		});
		return action;
	}

	public Action createOpenUnitViewerAction() {
		Action action = actionManager.createAction("[U]", new ICTask() {
			public void doTask() {
				openDiscourseUnitViewer();
			}
		});
		return action;
	}

	public Action createOpenWordViewerAction() {
		Action action = actionManager.createAction("[W]", new ICTask() {
			public void doTask() {
				openWordViewer();
			}
		});
		return action;
	}

	public Action createTorontoLayoutAction() {
		Action action = CActionUtils.createAction("[Toronto]", new ICTask() {
			public void doTask() {
				doTorontoLayout();
			}
		});
		return action;
	}

	public Action createWordLayoutAction() {
		Action action = CActionUtils.createAction("[BSL-Wd]", new ICTask() {
			public void doTask() {
				disableAllScorers();
				clearFrames();
				KNNetworkController<KBWord> networkController = controller
						.getWorldController().getWordNetworkController();
				KMetricsWindowOpenerPanel.openChart(controller,
						networkController, KDiscourseControllerView.this,
						getGraphScorers(networkController));
				openWordViewer();
				KMetricsWindowOpenerPanel.openChart(controller,
						networkController, KDiscourseControllerView.this,
						getNodesScorers(networkController));
				layoutManager.doLayout();
				controller.doReloadToThisFrame(true);
			}
		});
		return action;
	}

	public Action createUnitLayoutAction() {
		Action action = CActionUtils.createAction("[BSL-Ut]", new ICTask() {
			public void doTask() {
				disableAllScorers();
				clearFrames();
				KNNetworkController<KBDiscourseUnit> networkController = controller
						.getWorldController().getUnitsNetworkController();
				KMetricsWindowOpenerPanel.openChart(controller,
						networkController, KDiscourseControllerView.this,
						getGraphScorers(networkController));
				openDiscourseUnitViewer();
				KMetricsWindowOpenerPanel.openChart(controller,
						networkController, KDiscourseControllerView.this,
						getNodesScorers(networkController));
				layoutManager.doLayout();
				controller.doReloadToThisFrame(true);
			}
		});
		return action;
	}

	private void disableAllScorers() {
		for (KNNetworkController<?> nController : controller
				.getWorldController().getAllNetworkControllers()) {
			nController.getMetricsManager().disableAllScoreres();
		}
	}

	@SuppressWarnings({ "rawtypes" })
	private List<IKTemporalMetricsScorer> getGraphScorers(
			KNNetworkController networkController) {
		List<IKTemporalMetricsScorer> scorers = new ArrayList<IKTemporalMetricsScorer>();
		//for V2
		{
			KMetricsScorerSuite suite = networkController.getMetricsManager()
					.getScorer(KVertexFormalizedDegreeScorer.NAME);
			suite.setActive(true);
			scorers.add((IKTemporalMetricsScorer) suite.getGraphScorers()
					.get(1));//Total
		}

		//for v1
		// get(0) .. Average
		//		{
		//			KMetricsScorerSuite suite = networkController.getMetricsManager()
		//					.getScorer(KVertexFormalizedDegreeScorer.NAME);
		//			suite.setActive(true);
		//			scorers.add((IKTemporalMetricsScorer) suite.getGraphScorers()
		//					.get(2));//Centralization
		//		}
		//		{
		//			KMetricsScorerSuite suite = networkController.getMetricsManager()
		//					.getScorer(KVertexClosenessCentralityScorer.NAME);
		//			suite.setActive(true);
		//			scorers.add((IKTemporalMetricsScorer) suite.getGraphScorers()
		//					.get(2));
		//		}
		//		{
		//			KMetricsScorerSuite suite = networkController.getMetricsManager()
		//					.getScorer(KVertexBetweennessScorer.NAME);
		//			suite.setActive(true);
		//			scorers.add((IKTemporalMetricsScorer) suite.getGraphScorers()
		//					.get(2));
		//		}
		//		{
		//			KMetricsScorerSuite suite = networkController.getMetricsManager()
		//					.getScorer(KVertexClusteringScorer.NAME);
		//			suite.setActive(true);
		//			scorers.add((IKTemporalMetricsScorer) suite.getGraphScorers()
		//					.get(0));
		//		}

		return scorers;
	}

	@SuppressWarnings("rawtypes")
	private List<IKTemporalMetricsScorer> getNodesScorers(
			KNNetworkController<?> networkController) {
		List<IKTemporalMetricsScorer> scorers = new ArrayList<IKTemporalMetricsScorer>();
		KMetricsScorerSuite suite = networkController.getMetricsManager()
				.getScorer(KVertexBetweennessScorer.NAME);
		suite.setActive(true);
		scorers.add(suite.getVertexScorer());
		return scorers;
	}

	private void clearFrames() {
		for (JInternalFrame frame : desktop.getAllFrames()) {
			if (frame != discourseViewer) {
				frame.dispose();
			}
		}
	}

	public Action createOpenMetricsManagerAction(
			final KNNetworkController<? extends KBElement> controller) {
		Action action = CActionUtils.createAction("Metrics", new ICTask() {
			public void doTask() {
				openMetricsManager(controller);
			}
		});
		return action;
	}

	//	public Action createOpenVertexMetricsViewerAction(
	//			final KNNetworkController<? extends KBElement> controller) {
	//		Action action = CActionUtils.createAction("Node Metrics", new ICTask() {
	//			public void doTask() {
	//				openVertexMetricsViewer(controller);
	//			}
	//		});
	//		return action;
	//	}
	//
	//	public Action createOpenGraphMetricsViewerAction(
	//			final KNNetworkController<? extends KBElement> controller) {
	//		Action action = CActionUtils.createAction("Graph Metrics",
	//				new ICTask() {
	//					public void doTask() {
	//						openGraphMetricsViewer(controller);
	//					}
	//				});
	//		return action;
	//	}	

	private JInternalFrame openDiscourseViewer() {
		JMenuBar menuBar = new JMenuBar();
		{// Menu View
			JMenu menu = new JMenu("View");
			menu.add(controller.getDiscourseViewer().getInvalidShowOffAction());
			menu.add(controller.getDiscourseViewer().getInvalidShowOnAction());
			menuBar.add(menu);
		}
		JInternalFrame frame = openInternalFrame(
				controller.getDiscourseViewer(), "Discourse - "
						+ controller.getDiscourse().getName(), menuBar, null);
		discourseViewer = frame;
		frame.setClosable(false);
		return frame;
	}

	protected KNetworkViewPanel<KBAgent, KBRelation> openAgentViewer() {
		KNetworkViewPanel<KBAgent, KBRelation> view = viewFactory
				.createAgentNetworkPanel(controller.getWorldController()
						.getAgentNetworkController());
		openNetworkFrame(view, "Network - Students");
		return view;
	}

	protected KNetworkViewPanel<KBDiscourseUnit, KBRelation> openDiscourseUnitViewer() {
		KNetworkViewPanel<KBDiscourseUnit, KBRelation> view = viewFactory
				.createUnitNetworkPanel(controller.getWorldController()
						.getUnitsNetworkController());
		openNetworkFrame(view, "Network - Discourse Units");
		return view;
	}

	protected KNetworkViewPanel<KBWord, KBRelation> openWordViewer() {
		KNetworkViewPanel<KBWord, KBRelation> view = viewFactory
				.createWordNetworkPanel(controller.getWorldController()
						.getWordNetworkController());
		openNetworkFrame(view, "Network - Words");
		return view;
	}

	protected JInternalFrame openNetworkFrame(
			KNetworkViewPanel<? extends KBElement, KBRelation> view,
			String title) {
		JMenuBar menuBar = view.createMenubar();
		{
			JMenu menu = new JMenu("Metrics");
			menuBar.add(menu);
			menu.add(createOpenMetricsManagerAction(view.getController()));
			//menu.add(createOpenVertexMetricsViewerAction(view.getController()));
			//menu.add(createOpenGraphMetricsViewerAction(view.getController()));
		}

		JInternalFrame frame = openInternalFrame(view, title, menuBar, null);

		KScreenShotMessageCreater.addScreenShotFunction(menuBar, frame,
				controller);

		return frame;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void openMetricsManager(
			KNNetworkController<? extends KBElement> controller) {
		KMetricsManagerPanel panel = new KMetricsManagerPanel(this.controller,
				controller, this);
		openInternalFrame(panel,
				"Metrics - " + controller.getNetworkTypeName(), null,
				new Rectangle(100, 100, 700, 400));
	}

	//	@SuppressWarnings({ "unchecked", "rawtypes" })
	//	protected void openVertexMetricsViewer(
	//			KNNetworkController<? extends KBElement> controller) {
	//		KVertexMetricsPanel view = new KVertexMetricsPanel(this.controller,
	//				controller, this);
	//		openInternalFrame(view,
	//				"Node Metrics - " + controller.getNetworkTypeName(),
	//				view.createJMenuBar(), null);
	//	}
	//
	//	@SuppressWarnings({ "unchecked", "rawtypes" })
	//	protected void openGraphMetricsViewer(
	//			KNNetworkController<? extends KBElement> controller) {
	//		KGraphMetricsPanel view = new KGraphMetricsPanel(this.controller,
	//				controller, this);
	//		openInternalFrame(view,
	//				"Graph Metrics - " + controller.getNetworkTypeName(),
	//				view.createJMenuBar(), null);
	//	}

	/* (non-Javadoc)
	 * @see kbdex.view.IKWindowManager#openFrame(javax.swing.JComponent, java.lang.String, javax.swing.JMenuBar)
	 */
	@Override
	public Component openFrame(JComponent comp, String title, JMenuBar menuBar,
			Rectangle bounds) {
		return openInternalFrame(comp, title, menuBar, bounds);
	}

	public JInternalFrame openInternalFrame(JComponent comp, String title,
			JMenuBar menuBar, Rectangle bounds) {
		JInternalFrame frame = new JInternalFrame(title);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (menuBar != null) {
			frame.setJMenuBar(menuBar);
		}
		frame.setResizable(true);
		frame.setIconifiable(true);
		frame.setVisible(true);
		frame.setClosable(true);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(comp);
		if (bounds != null) {
			frame.setBounds(bounds);
		} else {
			frame.pack();
			frame.setLocation(100, 100);
		}
		desktop.add(frame);
		layoutManager.add(frame);
		frame.toFront();
		return frame;
	}

	protected void initializeLayout() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				layoutManager.doLayout();
			}
		});
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				layoutManager.resetNetworkLayouts();
			}
		});

	}
}

class KTorontoLayoutManager {
	Component mainComponent;
	int offset = 10;
	int bottomOffset = 30;
	List<Component> components = new ArrayList<Component>();

	public KTorontoLayoutManager(JComponent mainComponent) {
		this.mainComponent = mainComponent;
	}

	public void add(final Component component) {
		if (component.isShowing()) {
			components.add(component);
		}
		if (component instanceof JComponent) {
			((JComponent) component)
					.addAncestorListener(new AncestorListener() {
						public void ancestorRemoved(AncestorEvent event) {
							if (components.contains(component)) {
								components.remove(component);
							}
						}

						public void ancestorMoved(AncestorEvent event) {
						}

						public void ancestorAdded(AncestorEvent event) {
							if (!components.contains(component)) {
								components.add(component);
							}
						}
					});
		}
	}

	public void doLayout() {
		Dimension d = mainComponent.getSize();
		int width = (d.width - (offset * 2)) / 2;
		int height = (d.height - (offset + bottomOffset)) / 2;

		try {
			components.get(0).setBounds(offset, offset, width, height);
			components.get(1).setBounds(offset + width, offset, width, height);
			components.get(2).setBounds(offset, offset + height, width, height);
			components.get(3).setBounds(offset + width, offset + height, width,
					height);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}

	public void resetNetworkLayouts() {
		for (Component frame : components) {
			resetNetworkLayout(frame);
		}
	}

	private void resetNetworkLayout(Component frame) {
		if (!(frame instanceof JInternalFrame)) {
			return;
		}
		JInternalFrame iframe = (JInternalFrame) frame;
		if (iframe.getContentPane().getComponentCount() <= 0) {
			return;
		}
		Component c = iframe.getContentPane().getComponent(0);
		if (!(c instanceof KNetworkViewPanel<?, ?>)) {
			return;
		}
		KNetworkViewPanel<?, ?> view = (KNetworkViewPanel<?, ?>) c;
		view.getGraphLayoutChooser().resetLayout();
	}
}
