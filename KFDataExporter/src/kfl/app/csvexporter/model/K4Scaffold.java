package kfl.app.csvexporter.model;

import java.util.ArrayList;
import java.util.List;

public class K4Scaffold extends K4Element {
	
	private static final long serialVersionUID = 1L;
	
	private String name;

	private List<K4Support> supports = new ArrayList<K4Support>();
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void addSupport(K4Support support) {
		supports.add(support);
	}

	public List<K4Support> getSupports() {
		return supports;
	}

	@Override
	public String getType() {
		return "scaffold";
	}
	
}
