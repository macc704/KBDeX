package kbdex.app;

import java.util.ArrayList;
import java.util.List;

import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.table.CCSVFileIO;

public class ConverterMain {

	public static void main(String[] args) {
		new ConverterMain().run();
	}
	
	private String infile = "in.csv";
	private String outfile = "data.csv";
	
	private int studentRow = 2;
	private int contentRow = 8;

	void run() {
		String previousContent = "";

		CFile in = CFileSystem.getExecuteDirectory().findOrCreateFile(infile);
		String[][] tableIn = CCSVFileIO.load(in);
		List<List<String>> tableOut = new ArrayList<List<String>>();
		int counter = -1;
		long time = 1350270000000L;
		for (String[] rowIn : tableIn) {
			counter++;
			if (counter == 0) {
				List<String> rowOut = new ArrayList<String>();
				rowOut.add("ID");
				rowOut.add("student");
				rowOut.add("content");
				rowOut.add("group");
				rowOut.add("time");
				tableOut.add(rowOut);
				continue;
			}

			time = time + 1000;
			String content = rowIn[contentRow - 1];

			if (previousContent.equals(content)) {
				System.out.println("the same content id=" + counter);
				continue;
			}

			String studentsStr = rowIn[studentRow - 1];
			String[] students = studentsStr.split(",");
			for (String student : students) {
				if (student.isEmpty()) {
					continue;
				}
				List<String> rowOut = new ArrayList<String>();
				rowOut.add(Integer.toString(counter));
				rowOut.add(student);
				rowOut.add(content);
				rowOut.add("Default Group");
				rowOut.add(Long.toString(time));
				tableOut.add(rowOut);
				previousContent = content;
			}
		}

		CFile out = CFileSystem.getExecuteDirectory().findOrCreateFile(outfile);
		CCSVFileIO.saveByListList(tableOut, out);
	}

}
