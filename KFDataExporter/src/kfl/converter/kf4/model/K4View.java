package kfl.converter.kf4.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class K4View extends K4Element {

	private static final long serialVersionUID = 1L;

	private String name;

	private List<K4Scaffold> scaffolds = new ArrayList<K4Scaffold>();
	private Map<K4Element, Point> elements = new LinkedHashMap<K4Element, Point>();

	// mode = "as link", or "in place"
	// locked = true, or false

	public K4View() {
		this("");
	}

	@Override
	public String getType() {
		return "view";
	}

	public K4View(String name) {
		this.name = name;
	}

	public void addElement(K4Element element, Point p) {
		// System.out.println(element+""+p);
		elements.put(element, p);
	}

	public List<K4Element> getElements() {
		return new ArrayList<K4Element>(elements.keySet());
	}

	public void addScaffold(K4Scaffold scaffold) {
		scaffolds.add(scaffold);
	}

	public List<K4Scaffold> getScaffolds() {
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
		if (!(obj instanceof K4View)) {
			return false;
		}

		return toString().equals(((K4View) obj).toString());
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
