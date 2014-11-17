package kbdex.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.table.CCSVFileIO;

/**
 * Converter for to divide CoauthorShip note into two
 */
public class ConverterMain {

	public static void main(String[] args) throws Exception{
		new ConverterMain().run();
	}

	private String infile = "in.csv";
	private String outfile = "data.csv";

	private int studentRow = 9;
	private int contentRow = 13;
	private int timeRow = 3;

	//02/05/2001 11:32:27
	//<code>"EEE, d MMM yyyy HH:mm:ss Z"</code>
	private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	void run() throws Exception{
		String previousContent = "";

		CFile in = CFileSystem.getExecuteDirectory().findOrCreateFile(infile);
		String[][] tableIn = CCSVFileIO.load(in);
		List<List<String>> tableOut = new ArrayList<List<String>>();
		int counter = -1;
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

			String content = rowIn[contentRow - 1];
			content = content.replaceAll(":}", ":} ");

			String timeStr = rowIn[timeRow - 1];
			Date d = dateFormat.parse(timeStr);

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
				rowOut.add(Long.toString(d.getTime()));
				tableOut.add(rowOut);
				previousContent = content;
			}
		}

		CFile out = CFileSystem.getExecuteDirectory().findOrCreateFile(outfile);
		CCSVFileIO.saveByListList(tableOut, out);
	}

}
