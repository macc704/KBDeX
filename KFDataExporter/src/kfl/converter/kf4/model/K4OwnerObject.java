package kfl.converter.kf4.model;

import java.util.ArrayList;
import java.util.List;

public abstract class K4OwnerObject extends K4Element {

	private static final long serialVersionUID = 1L;
	
	private List<K4Element> belongings = new ArrayList<K4Element>();

	public void addBelonging(K4Element element) {
		belongings.add(element);
	}

	public List<K4Element> getBelongings() {
		return belongings;
	}
}
