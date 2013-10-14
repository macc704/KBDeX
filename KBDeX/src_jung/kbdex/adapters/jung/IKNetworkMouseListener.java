/*
 * IKNetworkMouseListener.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.adapters.jung;

public interface IKNetworkMouseListener<V, E> {

	public void nodeClicked(V o, int clickCount);

	public void edgeClicked(E o, int clickCount);

}
