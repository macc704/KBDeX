package kfl.app.csvexporter;

import java.util.ArrayList;
import java.util.List;

import kfl.app.csvexporter.model.K4Attachment;
import kfl.app.csvexporter.model.K4Author;
import kfl.app.csvexporter.model.K4Group;
import kfl.app.csvexporter.model.K4Log;
import kfl.app.csvexporter.model.K4Note;
import kfl.app.csvexporter.model.K4OwnerObject;
import kfl.app.csvexporter.model.K4View;
import kfl.app.csvexporter.model.K4World;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.table.CCSVFileIO;

public class KFCSVExporter {
	
	public void export(CDirectory dir, K4World world) throws Exception {
		{
			CFile file = dir.findOrCreateFile("log.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			table.add(K4Log.header());
			for (K4Log each : world.getLogs()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

		{
			CFile file = dir.findOrCreateFile("notes.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			List<String> header = K4Note.header();
			header.add("viewId");
			header.add("authorId");
			table.add(header);
			for (K4Note each : world.getNotes()) {
				for (K4View eachView : each.getViews()) {
					for (K4OwnerObject eachAuthor : each.getAuthors()) {
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
			table.add(K4Author.header());
			for (K4Author each : world.getAuthors()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

		{
			CFile file = dir.findOrCreateFile("groups.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			table.add(K4Group.header());
			for (K4Group each : world.getGroups()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

		{
			CFile file = dir.findOrCreateFile("views.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			table.add(K4View.header());
			for (K4View each : world.getViews()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

		{
			CFile file = dir.findOrCreateFile("attachments.csv");
			List<List<String>> table = new ArrayList<List<String>>();
			table.add(K4Attachment.header());
			for (K4Attachment each : world.getAttachments()) {
				table.add(each.getStrings());
			}
			CCSVFileIO.saveByListList(table, file);
		}

	}
}
