/*
 * KFNoteBuilder.java
 * Created on Jul 16, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kfl.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import kfl.model.KFAuthor;
import kfl.model.KFGroup;
import kfl.model.KFNote;
import kfl.model.KFView;

import org.zoolib.ZID;
import org.zoolib.ZTuple;
import org.zoolib.ZTxn;
import org.zoolib.tuplebase.ZTB;
import org.zoolib.tuplebase.ZTBIter;
import org.zoolib.tuplebase.ZTBQuery;
import org.zoolib.tuplebase.ZTBSpec;
import org.zoolib.tuplebase.ZTBTxn;

import clib.common.utils.CNullProgressMonitor;
import clib.common.utils.ICProgressMonitor;

/**
 * http://analysis.ikit.org/kfcoders/index.php/Main_Page
 * 
 * @author macchan
 */
public class KFNoteBuilder {

	private static final ZTBQuery NOTE_QUERY = new ZTBQuery(ZTBSpec
			.sEquals("Object", "note").and(ZTBSpec.sEquals("stat", "active"))
			.and(ZTBSpec.sHas("crea"))).sorted("crea", true);
	private static final ZTBQuery AUTHOR_QUERY = new ZTBQuery(ZTBSpec.sEquals(
			"Object", "author").and(ZTBSpec.sEquals("stat", "active")));
	private static final ZTBQuery GROUP_QUERY = new ZTBQuery(ZTBSpec.sEquals(
			"Object", "group")/* .and(ZTBSpec.sEquals("stat", "active")) */);
	private static final ZTBQuery VIEW_QUERY = new ZTBQuery(ZTBSpec.sEquals(
			"Object", "view").and(ZTBSpec.sEquals("stat", "active")));
	private static final ZTBQuery NOTE_OWNS_QUERY = new ZTBQuery(
			ZTBSpec.sEquals("Link", "owns")).and(new ZTBQuery("from",
			AUTHOR_QUERY).and(new ZTBQuery("to", NOTE_QUERY)));
	private static final ZTBQuery GROUP_CONTAINS_QUERY = new ZTBQuery(
			ZTBSpec.sEquals("Link", "contains")).and(new ZTBQuery("from",
			GROUP_QUERY).and(new ZTBQuery("to", AUTHOR_QUERY)));
	private static final ZTBQuery VIEW_CONTAINS_QUERY = new ZTBQuery(
			ZTBSpec.sEquals("Link", "contains")).and(new ZTBQuery("from",
			VIEW_QUERY).and(new ZTBQuery("to", NOTE_QUERY)));

	private List<KFView> views;
	private List<KFNote> notes;
	private List<KFAuthor> authors;
	private List<KFGroup> groups;

	public List<KFView> getViews() {
		return views;
	}

	/**
	 * @return the notes
	 */
	public List<KFNote> getNotes() {
		return notes;
	}

	public List<KFNote> getNotes(List<KFView> views) {
		List<KFNote> notes = new ArrayList<KFNote>();
		for (KFNote note : getNotes()) {
			if (note.isContained(views)) {
				notes.add(note);
			}
		}
		return notes;
	}

	/**
	 * @return the authors
	 */
	public List<KFAuthor> getAuthors() {
		return authors;
	}

	/**
	 * @return the groups
	 */
	public List<KFGroup> getGroups() {
		return groups;
	}

	public List<KFView> prefetchViews(ZTB conn) {
		Map<ZID, KFView> views;
		for (;;) {
			ZTxn txn = new ZTxn();
			ZTBTxn transaction = new ZTBTxn(txn, conn);
			views = getAllViews(transaction);
			if (txn.commit()) {
				break;
			}
		}
		return new ArrayList<KFView>(views.values());
	}

	public void build(ZTB conn) {
		build(conn, new CNullProgressMonitor());
	}

	public void build(ZTB conn, ICProgressMonitor monitor) {
		monitor.setMax(9);

		// retrieve data from KF
		Map<ZID, KFView> views;
		Map<ZID, KFGroup> groups;
		Map<ZID, KFAuthor> authors;
		Map<ZID, KFNote> notes;

		for (;;) {
			ZTxn txn = new ZTxn();
			ZTBTxn transaction = new ZTBTxn(txn, conn);
			monitor.setWorkTitle("getting views");
			views = getAllViews(transaction);
			monitor.progress(1);
			monitor.setWorkTitle("getting groups");
			groups = getAllGroups(transaction);
			monitor.progress(1);
			monitor.setWorkTitle("getting authors");
			authors = getAllAuthors(transaction);
			monitor.progress(1);
			monitor.setWorkTitle("getting notes");
			notes = getAllNotes(transaction);
			monitor.progress(1);
			monitor.setWorkTitle("making view connections");
			makeViewConnections(transaction, views, notes);
			monitor.progress(1);
			monitor.setWorkTitle("making note connections");
			makeNoteConnections(transaction, authors, notes);
			monitor.progress(1);
			monitor.setWorkTitle("making group connections");
			makeGroupConnections(transaction, groups, authors);
			monitor.progress(1);
			if (txn.commit()) {
				break;
			}
		}

		// set unknown author
		monitor.setWorkTitle("setting unknown author");
		KFAuthor defaultAuthor = new KFAuthor();
		defaultAuthor.setFirstName("Author");
		defaultAuthor.setLastName("Unknown");
		for (KFNote note : notes.values()) {
			if (note.getAuthor() == null) {
				note.setAuthor(defaultAuthor);
			}
		}
		monitor.progress(1);

		// set unknown group
		monitor.setWorkTitle("setting unknown group");
		KFGroup defaultGroup = new KFGroup();
		defaultGroup.setName("*NONAME GROUP*");
		defaultAuthor.setGroup(defaultGroup);
		for (KFAuthor author : authors.values()) {
			if (author.getGroup() == null) {
				author.setGroup(defaultGroup);
			}
		}
		monitor.progress(1);

		// set object lists
		this.views = new ArrayList<KFView>(views.values());
		this.groups = new ArrayList<KFGroup>(groups.values());
		this.notes = new ArrayList<KFNote>(notes.values());
		this.authors = new ArrayList<KFAuthor>(authors.values());
	}

	private Map<ZID, KFGroup> getAllGroups(ZTBTxn transaction) {
		Map<ZID, KFGroup> groups = new TreeMap<ZID, KFGroup>();

		for (ZTBIter i = new ZTBIter(transaction, GROUP_QUERY); i.hasValue(); i
				.advance()) {
			ZTuple tuple = i.get();
			ZID id = i.getZID();

			KFGroup group = new KFGroup();
			group.setId(id);
			group.setName(tuple.getString("titl"));

			groups.put(group.getId(), group);
		}

		return groups;
	}

	private Map<ZID, KFView> getAllViews(ZTBTxn transaction) {
		Map<ZID, KFView> views = new TreeMap<ZID, KFView>();

		for (ZTBIter i = new ZTBIter(transaction, VIEW_QUERY); i.hasValue(); i
				.advance()) {
			ZTuple tuple = i.get();
			ZID id = i.getZID();

			KFView view = new KFView();
			view.setId(id);
			view.setName(tuple.getString("titl"));

			views.put(view.getId(), view);
		}

		return views;
	}

	private Map<ZID, KFAuthor> getAllAuthors(ZTBTxn transaction) {
		Map<ZID, KFAuthor> authors = new TreeMap<ZID, KFAuthor>();

		for (ZTBIter i = new ZTBIter(transaction, AUTHOR_QUERY); i.hasValue(); i
				.advance()) {
			ZTuple tuple = i.get();
			ZID id = i.getZID();

			KFAuthor author = new KFAuthor();
			author.setId(id);
			author.setFirstName(tuple.getString("fnam"));
			author.setLastName(tuple.getString("lnam"));

			authors.put(author.getId(), author);
		}

		return authors;
	}

	private Map<ZID, KFNote> getAllNotes(ZTBTxn transaction) {
		Map<ZID, KFNote> notes = new TreeMap<ZID, KFNote>();

		for (ZTBIter i = new ZTBIter(transaction, NOTE_QUERY); i.hasValue(); i
				.advance()) {
			ZTuple tuple = i.get();
			ZID id = i.getZID();

			KFNote note = new KFNote();
			// note.setId(id.longValue());
			note.setId(id);
			// System.out.println(tuple.keySet());
			// System.out.println(tuple.getList("offsets"));
			// System.out.println(tuple.getList("styles"));
			note.setTitle(tuple.getString("titl"));
			note.setText(tuple.getString("text"));
			note.setCreated(tuple.getTime("crea"));
			note.setModified(tuple.getTime("modi"));

			notes.put(note.getId(), note);
		}

		return notes;
	}

	private void makeNoteConnections(ZTBTxn transaction,
			Map<ZID, KFAuthor> authors, Map<ZID, KFNote> notes) {

		for (ZTBIter i = new ZTBIter(transaction, NOTE_OWNS_QUERY); i
				.hasValue(); i.advance()) {
			ZTuple tuple = i.get();
			ZID from = tuple.getZID("from");
			ZID to = tuple.getZID("to");

			KFAuthor author = authors.get(from);
			KFNote note = notes.get(to);

			if (author == null || note == null) {
				System.err.println("This is invalid link for note. from="
						+ from + ", to=" + to);
				continue;
			}

			note.setAuthor(author);
			author.addNotes(note);
		}
	}

	private void makeGroupConnections(ZTBTxn transaction,
			Map<ZID, KFGroup> groups, Map<ZID, KFAuthor> authors) {

		for (ZTBIter i = new ZTBIter(transaction, GROUP_CONTAINS_QUERY); i
				.hasValue(); i.advance()) {
			ZTuple tuple = i.get();
			ZID from = tuple.getZID("from");
			ZID to = tuple.getZID("to");

			KFGroup group = groups.get(from);
			KFAuthor author = authors.get(to);

			if (group == null || author == null) {
				System.err.println("This is invalid link for group. from="
						+ from + ", to=" + to);
				continue;
			}

			group.addAuthor(author);
			author.setGroup(group);
		}
	}

	private void makeViewConnections(ZTBTxn transaction,
			Map<ZID, KFView> views, Map<ZID, KFNote> notes) {

		for (ZTBIter i = new ZTBIter(transaction, VIEW_CONTAINS_QUERY); i
				.hasValue(); i.advance()) {
			ZTuple tuple = i.get();
			ZID from = tuple.getZID("from");
			ZID to = tuple.getZID("to");

			KFView view = views.get(from);
			KFNote note = notes.get(to);

			if (view == null || note == null) {
				System.err.println("This is invalid link for view. from="
						+ from + ", to=" + to);
				continue;
			}

			view.addNote(note);
			note.addView(view);
		}
	}

}
