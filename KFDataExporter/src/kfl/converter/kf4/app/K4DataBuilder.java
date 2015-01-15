/*
 * KFNoteBuilder.java
 * Created on Jul 16, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kfl.converter.kf4.app;

import java.awt.Point;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kfl.converter.kf4.model.K4Attachment;
import kfl.converter.kf4.model.K4Author;
import kfl.converter.kf4.model.K4Element;
import kfl.converter.kf4.model.K4Group;
import kfl.converter.kf4.model.K4Keyword;
import kfl.converter.kf4.model.K4Log;
import kfl.converter.kf4.model.K4Note;
import kfl.converter.kf4.model.K4OwnerObject;
import kfl.converter.kf4.model.K4RiseAbove;
import kfl.converter.kf4.model.K4Scaffold;
import kfl.converter.kf4.model.K4Support;
import kfl.converter.kf4.model.K4TextLocator;
import kfl.converter.kf4.model.K4UnknownElement;
import kfl.converter.kf4.model.K4View;
import kfl.converter.kf4.model.K4World;
import kfl.kf4serializer.serializer.IKFTupleProcessor;
import kfl.kf4serializer.serializer.KFSerializeFolder;

import org.zoolib.ZID;
import org.zoolib.ZTuple;

/**
 * http://analysis.ikit.org/kfcoders/index.php/Main_Page
 * 
 * @author macchan
 */
public class K4DataBuilder {

	private K4World world = new K4World();

	public K4World getWorld() {
		return world;
	}

	private Map<String, String> unhandled = new HashMap<String, String>();
	private Map<String, String> unhandledLinks = new HashMap<String, String>();

	public void build(KFSerializeFolder folder)
			throws Exception {

		folder.processObjects(new IKFTupleProcessor() {
			public void processOne(ZID id, ZTuple tuple) throws Exception {
				processObject(id, tuple.getString("Object"), tuple);
			}
		});
		System.err.println("unhandled objects:");
		for (String key : unhandled.keySet()) {
			String value = unhandled.get(key);
			System.err.println(key + "=" + value);
		}

		folder.processLinks(new IKFTupleProcessor() {
			public void processOne(ZID id, ZTuple tuple) throws Exception {
				processLink(tuple.getString("Link"), tuple);
			}
		});
		System.err.println("unhandled links:");
		for (String key : unhandledLinks.keySet()) {
			String value = unhandledLinks.get(key);
			System.err.println(key + "=" + value);
		}
	}

	@SuppressWarnings("unchecked")
	private void processObject(ZID id, String kind, ZTuple tuple)
			throws Exception {
		K4Element element;
		if (kind.equals("author")) {
			// author=Object=author, debugger=, emai=, fnam=James, last=,
			// lnam=Mcguire, org=, pass=hasicrt!, stat=active, type=manager,
			// unam=James McGuire,
			K4Author author = new K4Author();
			element = author;
			author.setLastLogin(tuple.getTime("last"));
			author.setFirstName(tuple.getString("fnam"));
			author.setLastName(tuple.getString("lnam"));
			author.setStatus(tuple.getString("stat"));
			author.setUserName(tuple.getString("unam"));
			author.setRole(tuple.getString("type"));
		} else if (kind.equals("group")) {
			// group=Object=group, fsiz=, modi=, titl=Sample Group,
			K4Group group = new K4Group();
			element = group;
			group.setName(tuple.getString("titl"));
		} else if (kind.equals("note")) {
			// note=LSASVLWC=, LSAWC=, LSAmodi=, LSAspace=self, LSAvector=,
			// LSAvectorLength=, LSAvectorLengthScaled=, LSAversion=,
			// Object=note, crea=, frmt=text, lndx=, modi=, offsets=,
			// stat=active, text= ??n, titl=Purpose of This View,
			K4Note note = new K4Note();
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
			K4View view = new K4View();
			element = view;
			view.setName(tuple.getString("titl"));
		} else if (kind.equals("keyword")) {
			// keyword=Object=keyword, text=portfolio,
			K4Keyword keyword = new K4Keyword();
			element = keyword;
			keyword.setWord(tuple.getString("text"));
		} else if (kind.equals("scaffold")) {
			K4Scaffold scaffold = new K4Scaffold();
			element = scaffold;
			scaffold.setName(tuple.getString("text"));
		} else if (kind.equals("support")) {
			K4Support support = new K4Support();
			element = support;
			support.setName(tuple.getString("text"));
		} else if (kind.equals("riseabove")) {
			K4RiseAbove riseabove = new K4RiseAbove();
			element = riseabove;
		} else if (kind.equals("attachment")) {
			// attachment={ Object = "attachment"; crea =
			// date(1.250789762545E9)/* Thu Aug 20 13:36:02 EDT 2009 */;
			// file = "toplogo.gif"; mime = "image/gif"; modi =
			// date(1.250789762545E9)/* Thu Aug 20 13:36:02 EDT 2009 */;
			// stat = "active"; titl = "toplogo.gif"; userFileName =
			// "toplogo.gif"; }
			K4Attachment attachment = new K4Attachment();
			element = attachment;
			attachment.setTitle(tuple.getString("titl"));
			attachment.setPath(tuple.getString("file"));
			attachment.setMime(tuple.getString("mime"));
			attachment.setUserFileName(tuple.getString("userFileName"));
		} else {
			K4UnknownElement unknown = new K4UnknownElement(kind);
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

	private void processLink(String type, ZTuple tuple) {
		ZID from = tuple.getZID("from");
		ZID to = tuple.getZID("to");
		K4Element fromObject = world.get(from);
		K4Element toObject = world.get(to);
		if (fromObject == null || toObject == null) {
			// System.err.println("missing link:");
			return;
		}
		Date creationTime = tuple.getTime("crea");
		String kind = tuple.getString("Link") + " from:" + fromObject.getType()
				+ " to:" + toObject.getType();

		/* log */
		if (kind.startsWith("read from:author")) {
			K4Log log = new K4Log("read", creationTime, (K4Author) fromObject,
					toObject);
			world.addLog(log);
		} else if (kind.startsWith("modified from:author")) {
			K4Log log = new K4Log("modified", creationTime,
					(K4Author) fromObject, toObject);
			world.addLog(log);
		} else if (kind.startsWith("created from:author")) {
			K4Log log = new K4Log("created", creationTime,
					(K4Author) fromObject, toObject);
			world.addLog(log);
		} else if (kind.startsWith("cleared")) {
			K4Author author = (K4Author) world.get(tuple.getZID("creator"));
			K4Log log = new K4Log("cleared", creationTime, author, fromObject,
					toObject);
			world.addLog(log);
		}
		/* group and owns */
		else if (kind.startsWith("contains from:group")) {
			K4Group group = (K4Group) fromObject;
			K4Author author = (K4Author) toObject;
			group.addMember(author);
		} else if (kind.startsWith("owns")) {
			K4OwnerObject owner = (K4OwnerObject) fromObject;
			owner.addBelonging(toObject);
			toObject.addAuthor(owner);
		}
		/* view */
		else if (kind.startsWith("contains from:view")) {
			K4View view = (K4View) fromObject;
			Point p = tuple.getPoint("location");
			view.addElement(toObject, p);
			toObject.addView(view);
		} else if (kind.startsWith("uses from:view to:scaffold")) {
			K4View view = (K4View) fromObject;
			K4Scaffold scaffold = (K4Scaffold) toObject;
			view.addScaffold(scaffold);
		}
		/* scaffold */
		else if (kind.startsWith("contains from:scaffold")) {
			K4Scaffold scaffold = (K4Scaffold) fromObject;
			K4Support support = (K4Support) toObject;
			scaffold.addSupport(support);
		}
		/* note */
		else if (kind.startsWith("buildson")) {
			K4Note fromNote = (K4Note) fromObject;
			K4Note toNote = (K4Note) toObject;
			fromNote.setBuildson(toNote);
		} else if (kind.startsWith("contains from:riseabove to:note")) {
			K4RiseAbove riseabove = (K4RiseAbove) fromObject;
			K4Note note = (K4Note) toObject;
			riseabove.addNote(note);
		} else if (kind.startsWith("contains from:note to:riseabove")) {
			K4Note note = (K4Note) fromObject;
			K4RiseAbove riseabove = (K4RiseAbove) toObject;
			note.addRiseabove(riseabove);
		} else if (kind.startsWith("describes from:keyword to:note")) {
			K4Keyword keyword = (K4Keyword) fromObject;
			K4Note note = (K4Note) toObject;
			note.addKeyword(keyword);
		} else if (kind.startsWith("supports from:support to:note")) {
			K4Support support = (K4Support) fromObject;
			K4Note note = (K4Note) toObject;
			K4TextLocator locator = K4TextLocator.fromSupports(tuple);
			note.addSupport(support, locator);
		} else if (kind.startsWith("references from:note to:note")) {
			K4Note noteFrom = (K4Note) fromObject;
			K4Note noteTo = (K4Note) toObject;
			K4TextLocator locator = K4TextLocator.fromReferences(tuple);
			noteFrom.addReference(noteTo, locator);
		} else {
			if (!unhandled.containsKey(kind)) {
				unhandled.put(kind, tuple.toString());
			}
		}
	}

}
