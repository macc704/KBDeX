/*
 * KDiscourseController.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.controller;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenuBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import kbdex.controller.tools.stepwise.KStepWiseAnalyzeViewer;
import kbdex.controller.tools.stepwise.KStepWiseAnalyzer;
import kbdex.model.discourse.KDDiscourse;
import kbdex.model.discourse.KDDiscourse.DiscourseUnitType;
import kbdex.model.discourse.KDDiscourseRecord;
import kbdex.model.discourse.filters.KAgentNameDiscourseFilter;
import kbdex.view.discourse.KDiscoursePropertyViewerPanel;
import kbdex.view.discourse.KDiscourseViewerPanel;
import kbdex.view.discourse.selection.agent.KAgentFilteringPanel;
import kbdex.view.discourse.selection.time.KTimeFilteringPanel;
import kbdex.view.discourse.selection.word.KWordSelectionPanel;
import clib.common.thread.CRunnableThread;
import clib.common.thread.CThread;
import clib.common.thread.ICTask;
import clib.common.utils.ICProgressMonitor;
import clib.view.actions.CActionManager;
import clib.view.dialogs.COKCancelDialog;
import clib.view.editor.ICDirtyStateListener;
import clib.view.progress.CDelayProcessingMonitor;
import clib.view.progress.ICProgressTask;
import clib.view.windowmanager.CWindowCentraizer;

public class KDiscourseController {

	//private static final Dimension DIALOG_SIZE = new Dimension(1024, 768);
	private static final long DELAY = 2500;

	private KDDiscourse discourse;

	private KBWorldController worldController = new KBWorldController();
	private KDiscourseViewerPanel discourseViewer = new KDiscourseViewerPanel();
	private CThread thread;
	private int frameNo = -1;

	private int interval = 20;//ms	

	private Frame ownerFrame;

	private CActionManager actionManager = new CActionManager();

	public KDiscourseController(KDDiscourse discourse) {
		this.discourse = discourse;
		initialize();
	}

	private void initialize() {
		discourseViewer.setDiscourse(discourse);
		worldController.getModel().setLifetime(discourse.getLifetime());
		worldController.getModel().setSentenceMode(
				discourse.getUnitType() == DiscourseUnitType.SENTENCE);
	}

	public KDDiscourse getDiscourse() {
		return discourse;
	}

	public KBWorldController getWorldController() {
		return worldController;
	}

	public Frame getOwnerFrame() {
		return ownerFrame;
	}

	public void setOwnerFrame(Frame ownerFrame) {
		this.ownerFrame = ownerFrame;
	}

	public KDiscourseViewerPanel getDiscourseViewer() {
		return this.discourseViewer;
	}

	/**
	 * @return the interval
	 */
	public int getInterval() {
		return interval;
	}

	/**
	 * @param interval the interval to set
	 * 1 - 100;
	 */
	public void setInterval(int interval) {
		if (thread != null) {
			thread.setInterval(interval);
		}
		this.interval = interval;
	}

	/***********************************************
	 * Controls Action群
	 ***********************************************/

	public Action createStepAction() {
		Action action = actionManager.createAction(">", new ICTask() {
			public void doTask() {
				doStep();
			}
		});
		return action;
	}

	public Action createStepBackAction() {
		Action action = actionManager.createAction("<", new ICTask() {
			public void doTask() {
				doStepBack();
			}
		});
		return action;
	}

	public Action createToLastAction() {
		Action action = actionManager.createAction(">|", new ICTask() {
			public void doTask() {
				doToLast();
			}
		});
		return action;
	}

	public Action createResetAction() {
		Action action = actionManager.createAction("|<", new ICTask() {
			public void doTask() {
				doReset();
			}
		});
		return action;
	}

	public Action createToFrameAction(final JTextField field) {
		Action action = actionManager.createAction("Go", new ICTask() {
			public void doTask() {
				int number = Integer.parseInt(field.getText());
				doToFrame(number);
			}
		});
		return action;
	}

	public Action createStartAction() {
		Action action = actionManager.createAction("[Start]", new ICTask() {
			public void doTask() {
				doStart();
			}
		});
		return action;
	}

	public Action createStopAction() {
		Action action = actionManager.createAction("[Stop]", new ICTask() {
			public void doTask() {
				doStop();
			}
		});
		return action;
	}

	public Action createSelectWordAction() {
		Action action = actionManager.createAction("[Word]", new ICTask() {
			public void doTask() {
				doSelectWord();
			}
		});
		return action;
	}

	public Action createSelectAgentAction() {
		Action action = actionManager.createAction("[Agent]", new ICTask() {
			public void doTask() {
				doSelectAgent();
			}
		});
		return action;
	}

	public Action createSelectTimeAction() {
		Action action = actionManager.createAction("[Time]", new ICTask() {
			public void doTask() {
				doSelectTime();
			}
		});
		return action;
	}

	public Action createOpenPropertyAction() {
		Action action = actionManager.createAction("[Prop]", new ICTask() {
			public void doTask() {
				doOpenProperty();
			}
		});
		return action;
	}

	public Action createDoStepWiseAction() {
		Action action = actionManager.createAction("StepWise", new ICTask() {
			public void doTask() {
				doStepWise();
			}
		});
		return action;
	}

	/***********************************************
	 * Controls コマンド群
	 ***********************************************/

	private void doReset() {
		stop();
		reset();
		refreshViewWithAnimation();
	}

	private void doToLast() {
		CDelayProcessingMonitor.doTaskWithDialog(new ICProgressTask() {
			public void doTask(ICProgressMonitor monitor) {
				stop();
				if (isFinished()) {
					return;
				}
				toLast(monitor);
				refreshViewWithAnimation();
			}
		}, getOwnerFrame(), DELAY, "doToLast Task");
	}

	private void doStep() {
		stop();
		if (isFinished()) {
			return;
		}
		step();
		refreshViewWithAnimation();
	}

	private void doStepBack() {
		CDelayProcessingMonitor.doTaskWithDialog(new ICProgressTask() {
			public void doTask(ICProgressMonitor monitor) {
				stop();
				stepBack(monitor);
				refreshViewWithAnimation();
			}
		}, getOwnerFrame(), DELAY, "doStepBack Task");
	}

	public void doToFrame(final int frameNo) {
		CDelayProcessingMonitor.doTaskWithDialog(new ICProgressTask() {
			public void doTask(ICProgressMonitor monitor) {
				stop();
				reset();
				toFrame(frameNo, monitor);
				refreshViewWithAnimation();
			}
		}, getOwnerFrame(), DELAY, "doToFrame Task");
	}

	private void doStart() {
		stop();
		if (isFinished()) {
			return;
		}
		start();
	}

	private void doStop() {
		stop();
	}

	public void doReloadToThisFrame(final boolean hardReset) {
		CDelayProcessingMonitor.doTaskWithDialog(new ICProgressTask() {
			public void doTask(ICProgressMonitor monitor) {
				stop();
				int frameNo = getFrameNo();
				if (hardReset) {
					hardReset();
				} else {
					reset();
				}
				toFrame(frameNo, monitor);
				refreshViewWithoutAnimation();
			}
		}, getOwnerFrame(), DELAY, "Reload Task");
	}

	public void doResetToCompleteNetwork() {
		CDelayProcessingMonitor.doTaskWithDialog(new ICProgressTask() {
			public void doTask(ICProgressMonitor monitor) {
				stop();
				getWorldController().getModel().clearAllIgnoreStates();
				hardReset();
				toLast(monitor);
				refreshViewWithoutAnimation();
			}
		}, getOwnerFrame(), DELAY, "doResetToCompleteNetwork Task");
	}

	/***********************************************
	 * Controls コマンド群(Internal)
	 ***********************************************/

	private void toLast(ICProgressMonitor monitor) {
		toFrame(100000000, monitor);
	}

	private void toFrame(int targetFrameNo, ICProgressMonitor monitor) {
		monitor.setWorkTitle("Calculating Turn(s)");
		int target = Math.min(targetFrameNo, getRecords().size());
		int n = target - frameNo;
		monitor.setMax(n);
		while (!isFinished() && getFrameNo() < targetFrameNo) {
			step();
			monitor.progress(1);
			CThread.sleepForInterrupt(1);//for interrput
		}
	}

	private void step() {
		if (!isFinished() && worldController.isAnimationFinished()) {
			frameNo++;
			worldController.add(getCurrentRecord());
		}
	}

	private void stepBack(ICProgressMonitor monitor) {
		int targetFrameNo = getFrameNo() - 1;
		reset();
		toFrame(targetFrameNo, monitor);
	}

	private void hardReset() {
		this.frameNo = -1;
		worldController.hardReset();
		reset();//とりあえずのつなぎ
	}

	private void reset() {
		this.frameNo = -1;
		worldController.reset();
	}

	public int getFrameNo() {
		return this.frameNo;
	}

	public KDDiscourseRecord getCurrentRecord() {
		int frameNo = getFrameNo();
		if (frameNo < 0) {
			return null;
		}
		return getRecords().get(frameNo);
	}

	private boolean isFinished() {
		return getFrameNo() >= getRecords().size() - 1;
	}

	private List<KDDiscourseRecord> getRecords() {
		return discourse.getFilteredRecords();
	}

	private void refreshViewWithoutAnimation() {
		refreshViewWithAnimation();
	}

	private void refreshViewWithAnimation() {
		discourseViewer.setSelectionRecord(getCurrentRecord());
		worldController.refreshWithAnimation();
	}

	private synchronized void start() {
		if (thread == null) {
			thread = new CRunnableThread() {
				private Object lock = new Object();
				private boolean added = false;

				public void handleProcessStep() {
					synchronized (lock) {
						if (KDiscourseController.this.isFinished()) {
							stop();
						}
						if (!KDiscourseController.this.isFinished()
								&& worldController.isAnimationFinished()
								&& !added) {
							added = true;
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									synchronized (lock) {
										step();
										refreshViewWithAnimation();
										added = false;
									}
								}
							});
						}
					}
				}
			};
		}
		thread.setInterval(getInterval());
		thread.setPriority(Thread.currentThread().getPriority() - 1);
		thread.start();
	}

	private synchronized void stop() {
		if (thread != null) {
			thread.stop();
			thread.waitForStop();
		}
	}

	/***********************************************
	 * Select コマンド群
	 ***********************************************/

	public void doSelectWord() {
		stop();

		KWordSelectionPanel panel = new KWordSelectionPanel(discourse,
				discourseViewer.isShowInvalidRecord());
		final COKCancelDialog dialog = new COKCancelDialog(getOwnerFrame(),
				"Word Selection Window", panel);
		dialog.setOkCancelDialogListener(panel.getOKCancelListener());
		panel.setDirtyStateListener(new ICDirtyStateListener() {
			@Override
			public void dirtyStateChanged(boolean dirty) {
				if (dirty) {
					dialog.setTitle("Word Selection Window*");
				} else {
					dialog.setTitle("Word Selection Window");
				}
			}
		});
		dialog.setJMenuBar(new JMenuBar());
		dialog.getJMenuBar().add(panel.createFileMenu());
		dialog.getJMenuBar().add(panel.createEditMenu());
		dialog.setSize(createDialogSize());
		dialog.showDialog();

		if (dialog.isOK()) {
			discourseViewer.refreshView();
			doReloadToThisFrame(true);
		}

	}

	public void doSelectAgent() {
		stop();

		KAgentFilteringPanel panel = new KAgentFilteringPanel(discourse);
		COKCancelDialog dialog = new COKCancelDialog(getOwnerFrame(),
				"Agent Selection Window", panel);
		dialog.setSize(createDialogSize());
		dialog.showDialog();

		if (dialog.isOK()) {
			hardReset();
			KAgentNameDiscourseFilter filter = panel.getAgentFilter();
			discourse.setAgentFilter(filter);
			discourseViewer.refreshView();
		}
	}

	public void doSelectTime() {
		stop();

		final KTimeFilteringPanel panel = new KTimeFilteringPanel(discourse);
		COKCancelDialog dialog = new COKCancelDialog(getOwnerFrame(),
				"Time Selection Window", panel);
		dialog.setSize(createDialogSize());
		dialog.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				panel.fitScale();
			}
		});
		dialog.showDialog();

		if (dialog.isOK()) {
			hardReset();
			discourse.setTimeFilters(panel.getTimeFilters());
			discourse.setCurrentTimeFilter(panel.getCurrentTimeFilter());
			discourseViewer.refreshView();
		}
	}

	public void doOpenProperty() {
		stop();

		KDiscoursePropertyViewerPanel panel = new KDiscoursePropertyViewerPanel(
				discourse);
		COKCancelDialog dialog = new COKCancelDialog(getOwnerFrame(),
				"Discourse Properties", panel);
		//dialog.setSize(DIALOG_SIZE);
		dialog.pack();
		dialog.showDialog();

		if (dialog.isOK()) {
			panel.doOK();
			hardReset();
			worldController.getModel().setLifetime(discourse.getLifetime());
			worldController.getModel().setSentenceMode(
					discourse.getUnitType() == DiscourseUnitType.SENTENCE);
		}
	}

	/***********************************************
	 * Tools コマンド群
	 ***********************************************/

	public void doStepWise() {
		new Thread() {
			public void run() {
				KStepWiseAnalyzer analyzer = new KStepWiseAnalyzer(
						KDiscourseController.this);
				analyzer.setPhases(discourse.getPhases());
				analyzer.execute();

				KStepWiseAnalyzeViewer viewer = new KStepWiseAnalyzeViewer(
						KDiscourseController.this, analyzer.getResults());
				viewer.setTitle("Analyze Result Window");
				viewer.setSize(500, 500);
				CWindowCentraizer.centerWindow(viewer);
				viewer.setVisible(true);
			}
		}.start();
	}

	/***********************************************
	 * Dialog Size
	 ***********************************************/

	public Dimension createDialogSize() {
		GraphicsEnvironment gEnv = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		//GraphicsDevice gDevice = gEnv.getDefaultScreenDevice();
		Rectangle r = gEnv.getMaximumWindowBounds();
		int prefferedWidth = 1024;
		int calculatedWidth = r.width * 3 / 4;
		int width = Math.min(prefferedWidth, calculatedWidth);
		int prefferedHeight = 768;
		int calculatedHeight = r.height * 3 / 4;
		int height = Math.min(prefferedHeight, calculatedHeight);
		return new Dimension(width, height);
	}

}
