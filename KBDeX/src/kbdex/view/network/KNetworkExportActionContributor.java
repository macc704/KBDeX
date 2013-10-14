/*
 * KNetworkExportActionContributor.java
 * Created on Apr 23, 2011 
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.view.network;

import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import kbdex.adapters.jung.KNetworkPanel;
import kbdex.app.KBDeX;
import kbdex.model.kbmodel.KBElement;
import kbdex.model.kbmodel.KBRelation;

import org.apache.commons.collections15.Transformer;

import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.thread.ICTask;
import clib.view.actions.CActionUtils;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.PajekNetWriter;

/**
 * @author macchan
 *
 */
public class KNetworkExportActionContributor<V, E> {

	private JFileChooser fileChooser = new JFileChooser();

	private KNetworkPanel<V, E> networkPanel;

	public KNetworkExportActionContributor(KNetworkPanel<V, E> networkPanel) {
		this.networkPanel = networkPanel;
	}

	protected Action createExportPajekAction() {
		return CActionUtils.createAction("Export to Pajek Format",
				new ICTask() {
					public void doTask() {
						exportPajek();
					}
				});
	}

	protected Action createExportRAction() {
		return CActionUtils.createAction("Export to R Format", new ICTask() {
			public void doTask() {
				exportR();
			}
		});
	}

	private void exportPajek() {
		try {
			//int res = fileChooser.showSaveDialog(this);
			int res = fileChooser.showSaveDialog(getOwner());
			if (res == JFileChooser.APPROVE_OPTION) {
				File f = fileChooser.getSelectedFile();
				Transformer<V, String> vertexTransformer = new Transformer<V, String>() {
					public String transform(V vertex) {
						return vertex.toString();
					}
				};
				Transformer<E, Number> edgeTransformer = new Transformer<E, Number>() {
					public Number transform(E arg0) {
						return 1;
					}
				};
				String path = f.getAbsolutePath();
				if (!path.endsWith(".net")) {
					path += ".net";
				}

				PajekNetWriter<V, E> writer = new PajekNetWriter<V, E>();
				writer.save(networkPanel.getGraph(), path, vertexTransformer,
						edgeTransformer);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void exportR() {
		try {
			int res = fileChooser.showSaveDialog(getOwner());
			if (res == JFileChooser.APPROVE_OPTION) {
				File f = fileChooser.getSelectedFile();
				String path = f.getAbsolutePath();
				if (!path.endsWith(".R")) {
					path += ".R";
				}
				KNetworkRExporter<V, E> exporter = new KNetworkRExporter<V, E>();
				exporter.export(new File(path), networkPanel.getGraph());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private JFrame getOwner() {
		return ((JFrame) SwingUtilities.getWindowAncestor(networkPanel));
	}
}

class KNetworkRExporter<V, E> {
	private Map<V, Integer> indexMap = new HashMap<V, Integer>();
	private List<V> nodeMap = new ArrayList<V>();

	void export(File f, Graph<V, E> graph) throws Exception {
		//indexing
		int i = 0;
		for (V v : graph.getVertices()) {
			indexMap.put(v, i);
			nodeMap.add(v);
			i++;
		}

		PrintStream writer;
		//writer = System.out;//for debug
		writer = new PrintStream(f, KBDeX.ENCODING.toString());

		//write header
		URL url = getClass().getResource("Rfunctions.txt");
		CFile functionFile = CFileSystem.findFile(url.getPath());
		writer.println(functionFile.loadText());

		//list
		boolean first = true;
		writer.print("e.list <- c(");
		//For BothDirection
		//		for (V v : graph.getVertices()) {
		//			for (V neighbor : graph.getNeighbors(v)) {
		//				if (first) {
		//					first = false;
		//				} else {
		//					writer.print(",");
		//				}
		//				int v1 = indexMap.get(v);
		//				int v2 = indexMap.get(neighbor);
		//				writer.print(v1 + "," + v2);
		//			}
		//		}
		// for single direction
		for (E e : graph.getEdges()) {
			if (first) {
				first = false;
			} else {
				writer.print(",");
			}
			KBElement e1 = ((KBRelation) e).getTo();
			KBElement e2 = ((KBRelation) e).getFrom();
			int v1 = indexMap.get(e1);
			int v2 = indexMap.get(e2);
			writer.print(v1 + "," + v2);
		}
		writer.println(")");

		//create graph
		int n = graph.getVertexCount();
		writer.println("g <- graph(e.list, n=" + n + ", directed=FALSE)");

		//create label
		writer.print("V(g)$name<-c(");
		first = true;
		for (V v : nodeMap) {
			if (first) {
				first = false;
			} else {
				writer.print(",");
			}
			writer.print("\"" + v + "\"");
		}
		writer.println(")");

		writer.println("V(g)$label<-V(g)$name");

		writer.println("kb.rev(kb.sortedmetrics(g, betweenness(g)))");

		writer.close();
	}
}
