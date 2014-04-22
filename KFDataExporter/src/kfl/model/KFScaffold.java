package kfl.model;

import java.util.ArrayList;
import java.util.List;

public class KFScaffold extends KFElement {
	
	private static final long serialVersionUID = 1L;
	
	private String name;

	private List<KFSupport> supports = new ArrayList<KFSupport>();
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void addSupport(KFSupport support) {
		supports.add(support);
	}

	public List<KFSupport> getSupports() {
		return supports;
	}

	@Override
	public String getType() {
		return "scaffold";
	}
	
}
