/*
 * KScreenShotMessageCreator.java
 * Created on 2011/11/13
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view;

import java.awt.Component;
import java.util.Date;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import kbdex.controller.KDiscourseController;
import kbdex.model.discourse.KDDiscourse;
import kbdex.model.discourse.KDDiscourseRecord;
import clib.common.string.CStringChopper;
import clib.view.screenshot.CScreenShotTaker;
import clib.view.screenshot.ICMessageCreater;

/**
 * @author macchan
 *
 */
public class KScreenShotMessageCreater implements ICMessageCreater {

	public static void addScreenShotFunction(JMenuBar menuBar, Component comp,
			KDiscourseController controller) {
		{
			JMenu menu = new JMenu("ScreenShot");
			menuBar.add(menu);
			CScreenShotTaker taker = new CScreenShotTaker(comp,
					new KScreenShotMessageCreater(controller));
			menu.add(taker.createToClipboardAction());
			menu.add(taker.createToFileAction());
		}
	}

	private KDiscourseController controller;

	public KScreenShotMessageCreater(KDiscourseController controller) {
		this.controller = controller;
	}

	@Override
	public void getMessages(List<String> messages) {
		KDDiscourse d = controller.getDiscourse();
		String info;

		info = "";
		info += "" + new Date().toString();
		info += ", " + d.getName();
		messages.add(info);

		info = "";
		info += "" + "lifetime=" + d.getLifetime();
		info += ", " + "frameNo=" + controller.getFrameNo();
		info += ", " + "curRecordID=" + getCurRecordID();
		messages.add(info);

		info = "";
		for (String word : d.getSelectedWords()) {
			info += word + ", ";
		}
		messages.add("words=" + CStringChopper.chopped(info.trim()));
	}

	String getCurRecordID() {
		KDDiscourseRecord r = controller.getCurrentRecord();
		if (r == null) {
			return "NA";
		}
		return r.getIdAsText();
	}
}
