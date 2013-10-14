/*
 * KDictionary.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import clib.common.model.CAbstractModelObject;

public abstract class KDictionary<T> extends CAbstractModelObject {

	private static final long serialVersionUID = 1L;

	public Map<String, T> elements = new LinkedHashMap<String, T>();

	public void addElement(String text) {
		getElement(text);
	}

	public T getElement(String text) {
		if (text == null || text.length() == 0) {
			return null;
		}
		T element;
		if (elements.containsKey(text)) {
			element = elements.get(text);
		} else {
			element = createInstance(text);
			elements.put(text, element);
			fireModelUpdated();
		}
		return element;
	}

	public T removeElement(String text) {
		return elements.remove(text);
	}

	protected abstract T createInstance(String text);

	public List<T> addElements(List<String> texts) {
		return getElements(texts);
	}

	public List<T> getElements(List<String> texts) {
		List<T> selectedElements = new ArrayList<T>();
		for (String text : texts) {
			T t = elements.get(text);
			if (t != null) {
				selectedElements.add(t);
			}
		}
		return selectedElements;
	}

	public List<T> getElements() {
		return new ArrayList<T>(elements.values());
	}

	public boolean contains(String text) {
		return this.elements.containsKey(text);
	}

	public boolean containsElement(T element) {
		return this.elements.containsValue(element);
	}

	public void clear() {
		this.elements.clear();
	}

	public String toString() {
		return elements.toString();
	}
}
