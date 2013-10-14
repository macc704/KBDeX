/*
 * KRecordFileIO.java
 * Created on 2011/11/12
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse;

import java.util.ArrayList;
import java.util.List;

import kbdex.app.KBDeX;
import clib.common.filesystem.CFile;
import clib.common.string.CStringCleaner;
import clib.common.table.CCSVFileIO;

/**
 * @author macchan
 */
public class KRecordFileIO {

	public static void save(List<KDDiscourseRecord> records, CFile file) {
		int rowCount = records.size();
		int columnCount = 5;

		String[][] values = new String[rowCount + 1][columnCount];

		//header
		values[0][0] = "Id";
		values[0][1] = "Author";
		values[0][2] = "Text";
		values[0][3] = "Group";
		values[0][4] = "Time";

		//values
		for (int i = 0; i < rowCount; i++) {
			int row = i + 1;
			KDDiscourseRecord record = records.get(i);
			values[row][0] = record.getIdAsText();
			values[row][1] = record.getAgentName();
			values[row][2] = record.getText();
			values[row][3] = record.getGroupName();
			values[row][4] = Long.toString(record.getTimeAsLong());
		}

		file.setEncodingOut(KBDeX.ENCODING);
		CCSVFileIO.save(values, file);
	}

	public static final List<KDDiscourseRecord> load(CFile file) {
		String[][] values = CCSVFileIO.load(file);

		List<KDDiscourseRecord> records = new ArrayList<KDDiscourseRecord>();
		int rowCount = values.length;
		for (int i = 0; i < rowCount; i++) {
			KDDiscourseRecord record = loadOne(values[i], i);
			if (record != null) {
				records.add(record);
			}
		}
		return records;
	}

	private static final KDDiscourseRecord loadOne(String[] values, int i) {
		if (i == 0) {
			return null;//header
		}
		int len = values.length;
		if (len < 3) {
			throw new RuntimeException();
		}

		long id = Long.parseLong(values[0]);
		String name = CStringCleaner.cleaning(values[1]);
		String text = CStringCleaner.cleaning(values[2]);

		KDDiscourseRecord record = new KDDiscourseRecord(id, name, text);

		if (len >= 4) {
			try {
				String group = CStringCleaner.cleaning(values[3]);
				record.setGroupName(group);
			} catch (Exception ex) {
			}
		}
		if (len >= 5) {
			try {
				String time = CStringCleaner.cleaning(values[4]);
				record.setTime(Long.parseLong(time));
			} catch (Exception ex) {
			}
		}

		return record;
	}
}
