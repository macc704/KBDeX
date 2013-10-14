/*
 * IKDiscourseControllerListener.java
 * Created on Apr 15, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.controller;

/**
 * @author macchan
 *
 */
public interface IKDiscourseControllerListener {

	public void hardReset();

	public void reset();

	public void tick();

	public void refreshWithoutAnimation();

	public void refreshWithAnimation();

	public boolean isAnimationFinished();
}
