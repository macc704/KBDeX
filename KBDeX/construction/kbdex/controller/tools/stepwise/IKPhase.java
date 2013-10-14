/*
 * IKPhase.java
 * Created on Sep 26, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.controller.tools.stepwise;

import kbdex.model.kbmodel.KBWorld;


/**
 * @author macchan
 * 
 */
public interface IKPhase {
	public String getName();

	public void setToThisPhase(KBWorld world);
}
