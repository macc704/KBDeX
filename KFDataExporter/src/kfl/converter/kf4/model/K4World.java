package kfl.converter.kf4.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zoolib.ZID;

public class K4World implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<ZID, K4Element> elements = new HashMap<ZID, K4Element>();
	private List<K4Note> notes = new ArrayList<K4Note>();
	private List<K4Author> authors = new ArrayList<K4Author>();
	private List<K4Group> groups = new ArrayList<K4Group>();
	private List<K4View> views = new ArrayList<K4View>();
	private List<K4Attachment> attachments = new ArrayList<K4Attachment>();

	private List<K4Log> logs = new ArrayList<K4Log>();

	public void addElement(ZID id, K4Element element) {
		elements.put(id, element);
		if (element instanceof K4Note) {
			notes.add((K4Note) element);
		}
		if (element instanceof K4Author) {
			authors.add((K4Author) element);
		}
		if (element instanceof K4Group) {
			groups.add((K4Group) element);
		}
		if (element instanceof K4View) {
			views.add((K4View) element);
		}
		if (element instanceof K4Attachment) {
			attachments.add((K4Attachment) element);
		}
	}

	public K4Element get(ZID id) {
		return elements.get(id);
	}

	public void addLog(K4Log log) {
		logs.add(log);
	}

	public List<K4Log> getLogs() {
		return logs;
	}

	public List<K4Note> getNotes() {
		return notes;
	}

	public List<K4Author> getAuthors() {
		return authors;
	}

	public List<K4Group> getGroups() {
		return groups;
	}

	public List<K4View> getViews() {
		return views;
	}
	
	public List<K4Attachment> getAttachments() {
		return attachments;
	}

}
