package kfl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zoolib.ZID;

public class KFWorld implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<ZID, KFElement> elements = new HashMap<ZID, KFElement>();
	private List<KFNote> notes = new ArrayList<KFNote>();
	private List<KFAuthor> authors = new ArrayList<KFAuthor>();
	private List<KFGroup> groups = new ArrayList<KFGroup>();
	private List<KFView> views = new ArrayList<KFView>();
	private List<KFAttachment> attachments = new ArrayList<KFAttachment>();

	private List<KFLog> logs = new ArrayList<KFLog>();

	public void addElement(ZID id, KFElement element) {
		elements.put(id, element);
		if (element instanceof KFNote) {
			notes.add((KFNote) element);
		}
		if (element instanceof KFAuthor) {
			authors.add((KFAuthor) element);
		}
		if (element instanceof KFGroup) {
			groups.add((KFGroup) element);
		}
		if (element instanceof KFView) {
			views.add((KFView) element);
		}
		if (element instanceof KFAttachment) {
			attachments.add((KFAttachment) element);
		}
	}

	public KFElement get(ZID id) {
		return elements.get(id);
	}

	public void addLog(KFLog log) {
		logs.add(log);
	}

	public List<KFLog> getLogs() {
		return logs;
	}

	public List<KFNote> getNotes() {
		return notes;
	}

	public List<KFAuthor> getAuthors() {
		return authors;
	}

	public List<KFGroup> getGroups() {
		return groups;
	}

	public List<KFView> getViews() {
		return views;
	}
	
	public List<KFAttachment> getAttachments() {
		return attachments;
	}

}
