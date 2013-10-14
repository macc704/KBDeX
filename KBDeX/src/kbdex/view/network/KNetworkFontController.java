/*
 * KNetworkScaleController.java
 * Created on Apr 23, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.network;

import java.awt.Font;

import javax.swing.Action;

import kbdex.adapters.jung.KNetworkPanel;

import org.apache.commons.collections15.Transformer;

import clib.common.thread.ICTask;
import clib.view.actions.CActionUtils;

/**
 * @author macchan
 *
 */
public class KNetworkFontController<V, E> {

	private KNetworkPanel<V, E> networkPanel;
	private Transformer<V, Font> defaultVertexFontTransformer;
	private Transformer<E, Font> defaultEdgeFontTransformer;

	private Font vertexFont = new Font(null, Font.PLAIN, 10);
	private Font edgeFont = new Font(null, Font.PLAIN, 10);

	public KNetworkFontController(KNetworkPanel<V, E> networkPanel) {
		this.networkPanel = networkPanel;
		this.defaultVertexFontTransformer = networkPanel.getViewer()
				.getRenderContext().getVertexFontTransformer();
		this.defaultEdgeFontTransformer = networkPanel.getViewer()
				.getRenderContext().getEdgeFontTransformer();
	}

	public Action createDialogFontAction() {
		return CActionUtils.createAction("System Font", new ICTask() {
			public void doTask() {
				networkPanel.getViewer().getRenderContext()
						.setVertexFontTransformer(new Transformer<V, Font>() {
							public Font transform(V arg0) {
								return vertexFont;
							};
						});
				networkPanel.getViewer().getRenderContext()
						.setEdgeFontTransformer(new Transformer<E, Font>() {
							public Font transform(E arg0) {
								return edgeFont;
							};
						});
				networkPanel.refresh();
			}
		});
	}

	public Action createDefaultFontAction() {
		return CActionUtils.createAction("Default Font", new ICTask() {
			public void doTask() {
				networkPanel.getViewer().getRenderContext()
						.setVertexFontTransformer(defaultVertexFontTransformer);
				networkPanel.getViewer().getRenderContext()
						.setEdgeFontTransformer(defaultEdgeFontTransformer);
				networkPanel.refresh();
			}
		});
	}

}
