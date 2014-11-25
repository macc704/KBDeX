package kfl.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KFView extends KFElement {

	private static final long serialVersionUID = 1L;

	private String name;

	private List<KFScaffold> scaffolds = new ArrayList<KFScaffold>();
	private Map<KFElement, Point> elements = new LinkedHashMap<KFElement, Point>();

	// mode = "as link", or "in place"
	// locked = true, or false

	public KFView() {
		this("");
	}

	@Override
	public String getType() {
		return "view";
	}

	public KFView(String name) {
		this.name = name;
	}

	public void addElement(KFElement element, Point p) {
		// System.out.println(element+""+p);
		elements.put(element, p);
	}

	public List<KFElement> getElements() {
		return new ArrayList<KFElement>(elements.keySet());
	}

	public void addScaffold(KFScaffold scaffold) {
		scaffolds.add(scaffold);
	}

	public List<KFScaffold> getScaffolds() {
		return scaffolds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof KFView)) {
			return false;
		}

		return toString().equals(((KFView) obj).toString());
	}

	@Override
	public String toString() {
		return "((view)" + getName() + ")";
	}

	@Override
	public String getShortDescrption() {
		return this.getType() + "-" + getName();
	}

	public static List<String> header() {
		return Arrays.asList("id", "crea", "modi", "titl", "elements");
	}

	public List<String> getStrings() {
		List<String> strings = new ArrayList<String>();
		addBasicStrings(strings);
		strings.add(getName());
		strings.add(mapToString("elements", elements));
		return strings;
	}

}
