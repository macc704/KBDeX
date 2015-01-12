/*
 * KFNoteBuilder.java
 * Created on Jul 16, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kfl.app.csvexporter;

import java.awt.Point;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kfl.app.csvexporter.model.KFAttachment;
import kfl.app.csvexporter.model.KFAuthor;
import kfl.app.csvexporter.model.KFElement;
import kfl.app.csvexporter.model.KFGroup;
import kfl.app.csvexporter.model.KFKeyword;
import kfl.app.csvexporter.model.KFLog;
import kfl.app.csvexporter.model.KFNote;
import kfl.app.csvexporter.model.KFOwnerObject;
import kfl.app.csvexporter.model.KFRiseAbove;
import kfl.app.csvexporter.model.KFScaffold;
import kfl.app.csvexporter.model.KFSupport;
import kfl.app.csvexporter.model.KFTextLocator;
import kfl.app.csvexporter.model.KFUnknownElement;
import kfl.app.csvexporter.model.KFView;
import kfl.app.csvexporter.model.KFWorld;

import org.zoolib.ZID;
import org.zoolib.ZTuple;
import org.zoolib.ZTxn;
import org.zoolib.tuplebase.ZTB;
import org.zoolib.tuplebase.ZTBIter;
import org.zoolib.tuplebase.ZTBQuery;
import org.zoolib.tuplebase.ZTBSpec;
import org.zoolib.tuplebase.ZTBTxn;

import clib.common.utils.ICProgressMonitor;

/**
 * http://analysis.ikit.org/kfcoders/index.php/Main_Page
 * 
 * @author macchan
 */
public class KFDataBuilder {

	private static final ZTBQuery ALL_OBJECT_QUERY = new ZTBQuery(
			ZTBSpec.sHas("Object"));
	private static final ZTBQuery All_LINKS_QUERY = new ZTBQuery(
			ZTBSpec.sHas("Link")).sorted("crea", true);;

	private KFWorld world = new KFWorld();

	public KFWorld getWorld() {
		return world;
	}

	public void build(ZTB conn, ICProgressMonitor monitor) {
		monitor.setMax(2);

		for (;;) {
			ZTxn txn = new ZTxn();
			ZTBTxn transaction = new ZTBTxn(txn, conn);

			monitor.setWorkTitle("load objects");
			loadAllObjects(transaction);
			monitor.progress(1);

			monitor.setWorkTitle("load links");
			loadAllLinks(transaction);
			monitor.progress(1);

			if (txn.commit()) {
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void loadAllObjects(ZTBTxn transaction) {
		Map<String, String> unhandled = new HashMap<String, String>();

		for (ZTBIter i = new ZTBIter(transaction, ALL_OBJECT_QUERY); i
				.hasValue(); i.advance()) {
			ZTuple tuple = i.get();
			ZID id = i.getZID();
			String kind = tuple.getString("Object");

			KFElement element;
			if (kind.equals("author")) {
				// author=Object=author, debugger=, emai=, fnam=James, last=,
				// lnam=Mcguire, org=, pass=hasicrt!, stat=active, type=manager,
				// unam=James McGuire,
				KFAuthor author = new KFAuthor();
				element = author;
				author.setLastLogin(tuple.getTime("last"));
				author.setFirstName(tuple.getString("fnam"));
				author.setLastName(tuple.getString("lnam"));
				author.setStatus(tuple.getString("stat"));
				author.setUserName(tuple.getString("unam"));
				author.setRole(tuple.getString("type"));
			} else if (kind.equals("group")) {
				// group=Object=group, fsiz=, modi=, titl=Sample Group,
				KFGroup group = new KFGroup();
				element = group;
				group.setName(tuple.getString("titl"));
			} else if (kind.equals("note")) {
				// note=LSASVLWC=, LSAWC=, LSAmodi=, LSAspace=self, LSAvector=,
				// LSAvectorLength=, LSAvectorLengthScaled=, LSAversion=,
				// Object=note, crea=, frmt=text, lndx=, modi=, offsets=,
				// stat=active, text= ??n, titl=Purpose of This View,
				KFNote note = new KFNote();
				element = note;
				note.setTitle(tuple.getString("titl"));
				note.setText(tuple.getString("text"));
				note.setOffsets(tuple.getList("offsets"));
			} else if (kind.equals("view")) {
				// view:{ Object = "view"; crea = date(1.251136096156E9)/* Mon
				// Aug 24 13:48:16 EDT 2009 */; fontSize = int32(10); hdht =
				// int32(2); hdsb = int32(1); level = int32(1); lock =
				// "unlocked"; modi = date(1.251136129908E9)/* Mon Aug 24
				// 13:48:49 EDT 2009 */; perm = "public"; revi = int32(1); stat
				// = "active"; titl = "Webcams in High Crime areas"; }
				KFView view = new KFView();
				element = view;
				view.setName(tuple.getString("titl"));
			} else if (kind.equals("keyword")) {
				// keyword=Object=keyword, text=portfolio,
				KFKeyword keyword = new KFKeyword();
				element = keyword;
				keyword.setWord(tuple.getString("text"));
			} else if (kind.equals("scaffold")) {
				KFScaffold scaffold = new KFScaffold();
				element = scaffold;
				scaffold.setName(tuple.getString("text"));
			} else if (kind.equals("support")) {
				KFSupport support = new KFSupport();
				element = support;
				support.setName(tuple.getString("text"));
			} else if (kind.equals("riseabove")) {
				KFRiseAbove riseabove = new KFRiseAbove();
				element = riseabove;
			} else if (kind.equals("attachment")) {
				// attachment={ Object = "attachment"; crea =
				// date(1.250789762545E9)/* Thu Aug 20 13:36:02 EDT 2009 */;
				// file = "toplogo.gif"; mime = "image/gif"; modi =
				// date(1.250789762545E9)/* Thu Aug 20 13:36:02 EDT 2009 */;
				// stat = "active"; titl = "toplogo.gif"; userFileName =
				// "toplogo.gif"; }
				KFAttachment attachment = new KFAttachment();
				element = attachment;
				attachment.setTitle(tuple.getString("titl"));
				attachment.setPath(tuple.getString("file"));
				attachment.setMime(tuple.getString("mime"));
				attachment.setUserFileName(tuple.getString("userFileName"));
			} else {
				KFUnknownElement unknown = new KFUnknownElement(kind);
				element = unknown;
				unknown.load(tuple);
				if (!unhandled.containsKey(kind)) {
					unhandled
							.put(kind, tuple.toString()/* unknown.paramString() */);
				}
			}
			element.setId(id);
			if (tuple.has("crea")) {
				element.setCreated(tuple.getTime("crea"));
			}
			if (tuple.has("modi")) {
				element.setModified(tuple.getTime("modi"));
			}
			if (tuple.has("stat")) {
				element.setStatus(tuple.getString("stat"));
			}
			world.addElement(id, element);
		}

		System.err.println("unhandled objects:");
		for (String key : unhandled.keySet()) {
			String value = unhandled.get(key);
			System.err.println(key + "=" + value);
		}
	}

	private void loadAllLinks(ZTBTxn transaction) {
		Map<String, String> unhandled = new HashMap<String, String>();
		for (ZTBIter i = new ZTBIter(transaction, All_LINKS_QUERY); i
				.hasValue(); i.advance()) {
			ZTuple tuple = i.get();
			ZID from = tuple.getZID("from");
			ZID to = tuple.getZID("to");
			KFElement fromObject = world.get(from);
			KFElement toObject = world.get(to);
			if (fromObject == null || toObject == null) {
				// System.err.println("missing link:");
				continue;
			}
			Date creationTime = tuple.getTime("crea");
			String kind = tuple.getString("Link") + " from:"
					+ fromObject.getType() + " to:" + toObject.getType();

			/* log */
			if (kind.startsWith("read from:author")) {
				KFLog log = new KFLog("read", creationTime,
						(KFAuthor) fromObject, toObject);
				world.addLog(log);
			} else if (kind.startsWith("modified from:author")) {
				KFLog log = new KFLog("modified", creationTime,
						(KFAuthor) fromObject, toObject);
				world.addLog(log);
			} else if (kind.startsWith("created from:author")) {
				KFLog log = new KFLog("created", creationTime,
						(KFAuthor) fromObject, toObject);
				world.addLog(log);
			} else if (kind.startsWith("cleared")) {
				KFAuthor author = (KFAuthor) world.get(tuple.getZID("creator"));
				KFLog log = new KFLog("cleared", creationTime, author,
						fromObject, toObject);
				world.addLog(log);
			}
			/* group and owns */
			else if (kind.startsWith("contains from:group")) {
				KFGroup group = (KFGroup) fromObject;
				KFAuthor author = (KFAuthor) toObject;
				group.addMember(author);
			} else if (kind.startsWith("owns")) {
				KFOwnerObject owner = (KFOwnerObject) fromObject;
				owner.addBelonging(toObject);
				toObject.addAuthor(owner);
			}
			/* view */
			else if (kind.startsWith("contains from:view")) {
				KFView view = (KFView) fromObject;
				Point p = tuple.getPoint("location");
				view.addElement(toObject, p);
				toObject.addView(view);
			} else if (kind.startsWith("uses from:view to:scaffold")) {
				KFView view = (KFView) fromObject;
				KFScaffold scaffold = (KFScaffold) toObject;
				view.addScaffold(scaffold);
			}
			/* scaffold */
			else if (kind.startsWith("contains from:scaffold")) {
				KFScaffold scaffold = (KFScaffold) fromObject;
				KFSupport support = (KFSupport) toObject;
				scaffold.addSupport(support);
			}
			/* note */
			else if (kind.startsWith("buildson")) {
				KFNote fromNote = (KFNote) fromObject;
				KFNote toNote = (KFNote) toObject;
				fromNote.setBuildson(toNote);
			} else if (kind.startsWith("contains from:riseabove to:note")) {
				KFRiseAbove riseabove = (KFRiseAbove) fromObject;
				KFNote note = (KFNote) toObject;
				riseabove.addNote(note);
			} else if (kind.startsWith("contains from:note to:riseabove")) {
				KFNote note = (KFNote) fromObject;
				KFRiseAbove riseabove = (KFRiseAbove) toObject;
				note.addRiseabove(riseabove);
			} else if (kind.startsWith("describes from:keyword to:note")) {
				KFKeyword keyword = (KFKeyword) fromObject;
				KFNote note = (KFNote) toObject;
				note.addKeyword(keyword);
			} else if (kind.startsWith("supports from:support to:note")) {
				KFSupport support = (KFSupport) fromObject;
				KFNote note = (KFNote) toObject;
				KFTextLocator locator = new KFTextLocator();
				String text = tuple.getString("text");
				locator.setText(text);
				int loca = tuple.getInt32("loca");
				locator.setOffset(loca);
				note.addSupport(support, locator);
			} else if (kind.startsWith("references from:note to:note")) {
				KFNote noteFrom = (KFNote) fromObject;
				KFNote noteTo = (KFNote) toObject;
				KFTextLocator locator = new KFTextLocator();
				String text = tuple.getString("quotation");
				locator.setText(text);
				int loca = tuple.getInt32("loca");
				locator.setOffset(loca);
				noteFrom.addReference(noteTo, locator);
			} else {
				if (!unhandled.containsKey(kind)) {
					unhandled.put(kind, tuple.toString());
				}
			}
		}

		System.err.println("unhandled links:");
		for (String key : unhandled.keySet()) {
			String value = unhandled.get(key);
			System.err.println(key + "=" + value);
		}
	}

}
