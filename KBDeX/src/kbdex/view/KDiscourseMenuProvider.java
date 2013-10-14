/*
 * KDiscourseMenuProvider.java
 * Created on Apr 18, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view;

import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import kbdex.controller.KDiscourseController;
import clib.view.actions.CAction;
import clib.view.screenshot.CScreenShotTaker;

/**
 * @author macchan
 */
public class KDiscourseMenuProvider {

	private KDiscourseController controller;
	private KDiscourseControllerView controllerView;

	public KDiscourseMenuProvider(KDiscourseController controller,
			KDiscourseControllerView controllerView) {
		this.controller = controller;
		this.controllerView = controllerView;
	}

	public JToolBar createToolBar() {
		JToolBar menu = new JToolBar();

		menu.add(new JLabel("Control:"));
		menu.add(controller.createResetAction());
		menu.add(controller.createStepBackAction());
		menu.add(controller.createStepAction());
		menu.add(controller.createToLastAction());

		menu.addSeparator();

		menu.add(controller.createStartAction());
		menu.add(controller.createStopAction());
		menu.add(new KSpeedControlSlider(controller));
		menu.addSeparator();

		menu.add(new JLabel("No:"));
		JFormattedTextField field = new JFormattedTextField(new DecimalFormat());
		field.setColumns(3);
		menu.add(field);
		menu.add(controller.createToFrameAction(field));

		menu.addSeparator();
		menu.add(new JLabel("Selection:"));
		menu.add(controller.createSelectWordAction());
		menu.add(controller.createSelectAgentAction());
		menu.add(controller.createSelectTimeAction());
		menu.add(controller.createOpenPropertyAction());

		menu.addSeparator();
		CScreenShotTaker taker = new CScreenShotTaker(
				controller.getOwnerFrame(), new KScreenShotMessageCreater(
						controller));
		CAction action = taker.createToClipboardAction();
		action.setName("SS");
		menu.add(action);

		menu.addSeparator();
		menu.add(new JLabel("Window:"));
		menu.add(controllerView.createOpenAgentViewerAction());
		menu.add(controllerView.createOpenUnitViewerAction());
		menu.add(controllerView.createOpenWordViewerAction());
		menu.add(controllerView.createTorontoLayoutAction());
		menu.add(controllerView.createWordLayoutAction());
		menu.add(controllerView.createUnitLayoutAction());

		return menu;
	}

	public JMenuBar createMenuBar() {
		JMenuBar bar = new JMenuBar();

		//		{
		//			JMenu menu = new JMenu("ScreenShot");
		//			bar.add(menu);
		//			CScreenShotTaker taker = new CScreenShotTaker(
		//					controller.getOwnerFrame(), new KScreenShotMessageCreater(
		//							controller));
		//			menu.add(taker.createToClipboardAction());
		//			menu.add(taker.createToFileAction());
		//		}

		//		{// Menu Setting
		//			JMenu menu = new JMenu("Discourse");
		//			bar.add(menu);
		//
		//			menu.add(controller.createSelectWordAction());
		//			menu.add(controller.createSelectAgentAction());
		//			menu.add(controller.createSelectTimeAction());
		//			menu.add(controller.createOpenPropertyAction());
		//		}

		//		{// Menu Tools
		//			JMenu menu = new JMenu("Tools");
		//			bar.add(menu);
		//
		//			menu.add(controller.createDoStepWiseAction());
		//		}

		return bar;
	}
}
