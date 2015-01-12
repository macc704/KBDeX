package kfl.app.csvexporter.model;

import java.util.ArrayList;
import java.util.List;

public abstract class KFOwnerObject extends KFElement {

	private static final long serialVersionUID = 1L;
	
	private List<KFElement> belongings = new ArrayList<KFElement>();

	public void addBelonging(KFElement element) {
		belongings.add(element);
	}

	public List<KFElement> getBelongings() {
		return belongings;
	}
}
