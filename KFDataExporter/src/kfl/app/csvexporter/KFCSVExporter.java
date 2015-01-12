package kfl.app.csvexporter;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import kfl.app.csvexporter.model.KFAttachment;
import kfl.app.csvexporter.model.KFAuthor;
import kfl.app.csvexporter.model.KFGroup;
import kfl.app.csvexporter.model.KFLog;
import kfl.app.csvexporter.model.KFNote;
import kfl.app.csvexporter.model.KFOwnerObject;
import kfl.app.csvexporter.model.KFView;
import kfl.app.csvexporter.model.KFWorld;

import org.zoolib.tuplebase.ZTB;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.table.CCSVFileIO;
import clib.common.utils.ICProgressMonitor;

public class KFCSVExporter {
	public void build(ZTB conn, CDirectory dir, ICProgressMonitor monitor)
			throws Exception {
		// try {
		// System.setOut(new PrintStream(new File("test.out")));
		// } catch (Exception ex) {
		// throw new RuntimeException(ex);
		// }

		KFDataBuilder builder = new KFDataBuilder();
		builder.build(conn, monitor);
		conn.close();

		KFWorld world = builder.getWorld();

		{
			CFile dumpfile = dir.findOrCreateFile("serialized");
			ObjectOutputStream oos = new ObjectOutputStream(
					dumpfile.openOutputStream());
			oos.writeObject(world);
			oos.close();
		}

		{
			CFile file = dir.findOrCreateFile("log.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			table.add(KFLog.header());
			for (KFLog each : world.getLogs()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

		{
			CFile file = dir.findOrCreateFile("notes.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			List<String> header = KFNote.header();
			header.add("viewId");
			header.add("authorId");
			table.add(header);
			for (KFNote each : world.getNotes()) {
				for (KFView eachView : each.getViews()) {
					for (KFOwnerObject eachAuthor : each.getAuthors()) {
						List<String> row = each.getStrings();
						row.add(eachView.getIdAsString());
						row.add(eachAuthor.getIdAsString());
						table.add(row);
					}
				}
			}
			CCSVFileIO.saveByListList(table, file);
		}

		{
			CFile file = dir.findOrCreateFile("authors.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			table.add(KFAuthor.header());
			for (KFAuthor each : world.getAuthors()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

		{
			CFile file = dir.findOrCreateFile("groups.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			table.add(KFGroup.header());
			for (KFGroup each : world.getGroups()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

		{
			CFile file = dir.findOrCreateFile("views.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			table.add(KFView.header());
			for (KFView each : world.getViews()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

		{
			CFile file = dir.findOrCreateFile("attachments.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			table.add(KFAttachment.header());
			for (KFAttachment each : world.getAttachments()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

	}
}
