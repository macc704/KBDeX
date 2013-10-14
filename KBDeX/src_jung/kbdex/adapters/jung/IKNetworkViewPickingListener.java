/*
 * IKNetworkViewPickingListener.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.adapters.jung;

import java.util.List;

public interface IKNetworkViewPickingListener<V, E> {

	public void pickingStateChanged(List<V> pickedNodes, List<E> pickedEdges);

}
