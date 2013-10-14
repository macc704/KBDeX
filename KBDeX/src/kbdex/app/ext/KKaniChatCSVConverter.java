/*
 * KBChatConverter.java
 * Created on 2011/07/12
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.app.ext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kbdex.model.discourse.KDDiscourseRecord;
import kbdex.model.discourse.KRecordFileIO;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.system.CEncoding;
import clib.common.table.CCSVFileIO;

/**
 * かにチャット形式のデータをImportするプログラムです。
 * 2011/11月からCSVファイルがダウンロードできるようになったので，こちらを利用します．
 * @author macchan
 */
public class KKaniChatCSVConverter {

	public static void main(String[] args) {
		new KKaniChatCSVConverter().test();
	}

	public KKaniChatCSVConverter() {
	}

	void test() {
		CDirectory dir = CFileSystem.getExecuteDirectory().findDirectory(
				"testcases/importkanicsv");
		CFile in = dir.findFile("test1.csv");
		CFile out = dir.findOrCreateFile("test1.out.csv");
		convert(in, out);
	}

	public void convert(CFile in, CFile out) {
		String[][] values = CCSVFileIO.load(in);

		List<KDDiscourseRecord> records = new ArrayList<KDDiscourseRecord>();
		int rows = values.length;
		for (int i = 0; i < rows; i++) {
			if (i == 0) {//header
				continue;
			}

			try {
				KDDiscourseRecord record = processOne(values[i], i);
				if (record != null) {
					records.add(record);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		out.setEncodingOut(CEncoding.Shift_JIS);
		KRecordFileIO.save(records, out);
	}

	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy/MM/dd hh:mm:ss");

	private KDDiscourseRecord processOne(String[] line, long id)
			throws Exception {
		if (line[1].startsWith("OWNER")) {//SPECIAL NAME
			return null;
		}
		if (line[2].startsWith("ENTER")) {//SPECIAL TEXT
			return null;
		}
		if (line[2].startsWith("EXIT")) {//SPECIAL TEXT
			return null;
		}

		String student = line[1];
		String text = line[2];
		KDDiscourseRecord record = new KDDiscourseRecord(id, student, text);

		//group
		record.setGroupName("DefaultGroup");

		//date
		Date date = dateFormat.parse(line[7]);
		record.setTime(date.getTime());

		return record;
	}
}
