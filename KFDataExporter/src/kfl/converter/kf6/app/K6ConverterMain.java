package kfl.converter.kf6.app;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import kfl.converter.basic.app.K4BasicWorldBuilder;
import kfl.converter.basic.model.BasicK4Link;
import kfl.converter.basic.model.BasicK4Object;
import kfl.converter.basic.model.BasicK4World;
import kfl.converter.kf6.model.K6Attachment;
import kfl.converter.kf6.model.K6Author;
import kfl.converter.kf6.model.K6Community;
import kfl.converter.kf6.model.K6Contains;
import kfl.converter.kf6.model.K6Contribution;
import kfl.converter.kf6.model.K6Drawing;
import kfl.converter.kf6.model.K6Json;
import kfl.converter.kf6.model.K6Link;
import kfl.converter.kf6.model.K6Note;
import kfl.converter.kf6.model.K6Object;
import kfl.converter.kf6.model.K6Record;
import kfl.converter.kf6.model.K6Shape;

import org.zoolib.ZID;
import org.zoolib.ZTuple;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;

import com.google.gson.Gson;

public class K6ConverterMain {

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Args length has to be 1.");
			return;
		}
		new K6ConverterMain().run(args[0]);
	}

	void run(String name) throws Exception {
		CDirectory baseDir = CFileSystem.getExecuteDirectory()
				.findOrCreateDirectory("kf.out");
		CDirectory dir = baseDir.findDirectory(name);
		if (dir == null) {
			System.out.println("database " + name + " is not found.");
			return;
		}
		process(dir);
	}

	private K6Json data;
	// private Map<ZID, K6Object> objects = new LinkedHashMap<ZID, K6Object>();
	private Set<String> unsupportedTypes = new HashSet<String>();
	private K6ViewLocationConverter viewLocationConverter = new K6ViewLocationConverter();

	private PrintStream unsupportedOut = System.out;
	private PrintStream missingLinkOut = System.out;

	void process(CDirectory dir) throws Exception {
		unsupportedOut = new PrintStream(dir.findOrCreateFile(
				"unsupportedLog.txt").toJavaFile());
		missingLinkOut = new PrintStream(dir.findOrCreateFile(
				"missingLinkLog.txt").toJavaFile());

		K4BasicWorldBuilder k4builder = new K4BasicWorldBuilder();
		k4builder.build(dir);
		BasicK4World k4world = k4builder.getWorld();

		K6Community community = new K6Community();
		community.title = k4world.getName();

		data = new K6Json();
		data.community = community;

		for (BasicK4Object object : k4world.getObjects()) {
			processObject(object);
		}
		for (BasicK4Link link : k4world.getLinks()) {
			processLink(link);
		}

		viewLocationConverter.doConvert();

		File jsonFile = dir.findOrCreateFile("data.json").toJavaFile();
		Gson gson = new Gson();
		String json = gson.toJson(data);
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(
				jsonFile), "UTF8");
		osw.write(json);
		osw.close();
	}

	@SuppressWarnings("unchecked")
	private void processObject(BasicK4Object k4object) throws Exception {
		String id = k4object.getId();
		String type = k4object.getType();
		ZTuple t = k4object.getTuple();
		K6Contribution contribution;
		if (type.equals("author")) {
			// System.out.println(t.toString());
			K6Author author = new K6Author();
			author._id = id.toString();
			author.email = t.getString("unam");
			author.name = t.getString("fnam") + " " + t.getString("lnam");
			author.hashedPassword = t.getString("pass");
			author.role = t.getString("type");
			author.type = "Author";
			data.authors.add(author);
			data.put(author);
			return;
		} else if (type.equals("note")) {
			// System.out.println(t.toString());
			K6Note note = new K6Note();
			contribution = note;
			note.type = "Note";
			note._id = id.toString();
			note.title = t.getString("titl");
			note.body = t.getString("text");
			data.contributions.add(note);
		} else if (type.equals("view")) {
			// System.out.println(t.toString());
			contribution = new K6Contribution();
			contribution.type = "View";
			contribution.title = t.getString("titl");
			data.community.views.add(id.toString());
		} else if (type.equals("scaffold")) {
			contribution = new K6Contribution();
			contribution.type = "Scaffold";
			contribution.title = t.getString("text");
			data.community.scaffolds.add(id.toString());
		} else if (type.equals("support")) {
			contribution = new K6Contribution();
			contribution.type = "Support";
			contribution.title = t.getString("text");
		} else if (type.equals("drawing")) {
			// System.out.println(t.toString());
			contribution = new K6Drawing();
			contribution.type = "Drawing";
			contribution.title = t.getString("titl") /* + id */;
			// if (t.has("preview")) {
			// byte[] preview = t.getRaw("preview");
			// save(preview, id+"preview.png");
			// }
			// if (t.has("thumbnail")) {
			// byte[] thumbnail = t.getRaw("thumbnail");
			// save(thumbnail, id+"thumbnail.png");
			// }
		} else if (type.equals("attachment")) {
			// System.out.println(t.keySet());
			// { Object = "attachment"; crea = date(1.251138324512E9)/* Mon Aug
			// 24 14:25:24 EDT 2009 */; file = "Picture 1.png"; mime =
			// "image/png"; modi = date(1.251138324512E9)/* Mon Aug 24 14:25:24
			// EDT 2009 */; stat = "active"; titl = "Picture 1.png";
			// userFileName = "Picture 1.png"; }
			K6Attachment attachment = new K6Attachment();
			contribution = attachment;
			attachment.title = t.getString("titl");
			attachment.mime = t.getString("mime");
			attachment.type = "Attachment";
			attachment.url = t.getString("file");
			attachment.originalName = t.getString("file");
		} else if (type.equals("shape")) {
			// { Object = "shape"; color = int32(-16777216); crea =
			// date(1.398175088758E9)/* Tue Apr 22 09:58:08 EDT 2014 */; modi =
			// date(1.398175088758E9)/* Tue Apr 22 09:58:08 EDT 2014 */;
			// pen-width = int32(1); point1 = point(0, 0); point2 = point(22,
			// 129); shape = "line"; stat = "active"; }
			// { Object = "shape"; color = int32(-16777216); crea =
			// date(1.398175287848E9)/* Tue Apr 22 10:01:27 EDT 2014 */; modi =
			// date(1.398175287848E9)/* Tue Apr 22 10:01:27 EDT 2014 */;
			// pen-width = int32(1); points = [point(124, 28), point(113, 28),
			// point(104, 28), point(98, 27), point(94, 23), point(92, 19),
			// point(93, 14), point(100, 8), point(113, 3), point(125, 0),
			// point(139, 0), point(150, 2), point(163, 13), point(171, 31),
			// point(172, 48), point(170, 60), point(163, 75), point(155, 87),
			// point(147, 96), point(131, 106), point(117, 109), point(97, 110),
			// point(75, 110), point(53, 107), point(27, 99), point(14, 96),
			// point(8, 94), point(6, 92), point(4, 91), point(2, 91), point(1,
			// 91), point(0, 90)]; shape = "brush"; stat = "active"; }
			// { Object = "shape"; color = int32(-16777216); crea =
			// date(1.398175371966E9)/* Tue Apr 22 10:02:51 EDT 2014 */; modi =
			// date(1.398175371966E9)/* Tue Apr 22 10:02:51 EDT 2014 */;
			// pen-width = int32(1); point1 = point(0, 0); point2 = point(103,
			// 101); shape = "rect"; stat = "active"; }
			// { Object = "shape"; color = int32(-16777216); crea =
			// date(1.398175373736E9)/* Tue Apr 22 10:02:53 EDT 2014 */; modi =
			// date(1.398175373736E9)/* Tue Apr 22 10:02:53 EDT 2014 */;
			// pen-width = int32(1); point1 = point(0, 0); point2 = point(164,
			// 115); shape = "oval"; stat = "active"; }
			// System.out.println(t.toString());
			K6Shape shape = new K6Shape();
			contribution = shape;
			shape.type = "Shape";
			shape.shapeType = t.getString("shape");

			if (t.has("color")) {
				shape.color = parseColor(t.getInt32("color"));
			}
			if (t.has("fill")) {
				shape.fill = parseColor(t.getInt32("fill"));
			}

			if (shape.shapeType.equals("brush")) {
				shape.points = t.getList("points");
			} else {// line, rect, or oval
				shape.point1 = t.getPoint("point1");
				shape.point2 = t.getPoint("point2");
			}
			shape.penWidth = t.getInt32("pen-width");
		} else if (type.equals("text")) {
			// System.out.println(t.toString());
			// pending
			return;
		} else if (type.equals("historicalNote")) {
			// temporary
			K6Note note = new K6Note();
			contribution = note;
			note.type = "HistoricalNote";
			note.body = t.getString("text");
			// System.out.println(t.toString());
			// return;
		} else if (type.equals("backpack")) {
			// System.out.println(t.toString());
			contribution = new K6Contribution();
			contribution.type = "View";
			ZID authorId = t.getZID("author");
			Object authorObj = data.get(authorId.toString());
			if (authorObj == null) {
				System.out.println("author is null for backpack");
				return;
			}
			K6Author author = (K6Author) authorObj;
			contribution.authors.add(author._id);
			contribution.title = "BackPack: " + author.name;
		} else if (type.equals("session")) {
			return;
		} else {
			warnIfNotShowBefore("unsupported object: ", type, t);
			contribution = new K6Contribution();
			contribution.type = type;
		}
		contribution._id = id.toString();
		contribution.created = t.getTime("crea");
		contribution.permission = t.getString("perm"); // "public" or "private"
		contribution.locked = t.getString("lock").equals("locked"); // "locked"
																	// or
																	// "unlocked"

		if (!contribution.type.equals("Shape")) {
			data.contributions.add(contribution);
		}
		data.put(contribution);
	}

	private void processLink(BasicK4Link k4link) {
		String type = k4link.getType();
		ZTuple t = k4link.getTuple();
		K6Object to = data.get(k4link.to.getId());
		K6Object from = data.get(k4link.from.getId());
		if (to == null || from == null) {
			missingLinkOut.println("missing link: type=" + type + ", from="
					+ from + ", to=" + to);
			return;
		}

		K6Link link = new K6Link();
		link.type = type;
		link.from = t.getZID("from").toString();
		link.to = t.getZID("to").toString();

		if (type.equals("predates")) {
			// historical note to note
			// detail is not implemented yet
		} else if (type.equals("contains") && from.type.equals("View")) {
			if (to.type.equals("Shape")) {// Direct Shape will be converted to
											// Drawing
				K6Shape shape = (K6Shape) to;
				K6Drawing drawing = new K6Drawing();
				drawing._id = to._id;
				drawing.title = "FromShape:" + to._id;
				drawing.type = "Drawing";
				drawing.addShape(shape, new Point(0, 0));
				data.contributions.add(drawing);
				data.put(drawing);
				to = drawing;
			}
			link.type = "onviewref";

			K6Contains contains = new K6Contains();
			Point p;
			if (t.has("location")) {
				p = t.getPoint("location");
				// be careful they can be located in the negative position
				contains.x = p.x;
				contains.y = p.y;
			} else {
				p = new Point(10, 10);
			}

			viewLocationConverter.put(from, link);

			contains.z = t.getDouble("z");
			if (t.has("mode") && t.getString("mode").equals("in place")) {
				contains.showInPlace = true;
			} else if (!t.has("mode") && to.type.equals("Drawing")) {
				contains.showInPlace = true;
			}
			if (t.has("locked")) {
				contains.fixed = t.getBool("locked");
			}
			link.data = contains;

		} else if (type.equals("contains") && from.type.equals("group")) {
			// group does not support yet.
			return;
		} else if (type.equals("contains") && from.type.equals("Scaffold")) {
			// do nothing
		} else if (type.equals("contains") && from.type.equals("Drawing")
				&& to.type.equals("Shape")) {
			// System.out.println(t.toString());
			K6Drawing drawing = (K6Drawing) from;
			K6Shape shape = (K6Shape) to;
			Point location = t.getPoint("location");
			drawing.addShape(shape, location);
			return;
		} else if (type.equals("owns") && from.type.equals("Author")) {
			K6Author author = (K6Author) from;
			K6Contribution contribution = (K6Contribution) to;
			contribution.authors.add(author._id);
			return;
		} else if ((type.equals("read") || type.equals("modified") || type
				.equals("created"))
				&& from.type.equals("Author")
				&& !to.type.equals("Author")) {
			K6Record record = new K6Record();
			K6Author author = (K6Author) from;
			K6Contribution contribution = (K6Contribution) to;
			record.authorId = author._id;
			record.targetId = contribution._id;
			record.type = type;
			if (type.equals("modified")) {
				record.type = "edit";
			}
			if (type.equals("created")) {
				record.type = "create";
			}
			record.timestamp = t.getTime("crea");
			data.records.add(record);
			return;
		} else if (type.equals("buildson")) {
			// not necessary to do
		} else if (type.equals("references")) {
			// not necessary to do
		} else if (type.equals("supports")) {
			// support to note
			// pending
		} else if (type.equals("describes")) {
			// keyword to note
			// pending
			return;
		} else {
			// does not support
			// known unsupported "cleared", "beacon", "read_ideas", "issued",
			// "idea_reminds", "enabledfor"// notification related
			// "hostlist", "prefers" // from author, "nominated", "pauseRemind"
			// // to view, "check_tracker"// to view, "identifies"// to idea
			String concreteType = type + "From:" + from.type + "To:" + to.type;
			warnIfNotShowBefore("unsupported link type: ", concreteType, t);
			return;
		}
		data.links.add(link);
	}

	private Color parseColor(int color) {
		int b = (color & 0x000000FF);// 0
		int g = ((color >> 8) & 0x000000FF);// 0
		int r = ((color >> 16) & 0x000000FF);// 0
		int a = ((color >> 24) & 0x000000FF);// 255
		return new Color(r, g, b, a);
	}

	@SuppressWarnings("unused")
	private void save(byte[] data, String name) throws Exception {
		FileOutputStream os = new FileOutputStream(name, false);
		os.write(data);
		os.close();
	}

	private void warnIfNotShowBefore(String msg, String type, ZTuple t) {
		if (unsupportedTypes.contains(type)) {
			return;
		}
		unsupportedTypes.add(type);
		unsupportedOut.println(msg + type);
		unsupportedOut.println(t.toString());
	}

}
