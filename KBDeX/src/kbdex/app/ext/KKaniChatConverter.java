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
import clib.common.table.CCSVFileIO;

/**
 * かにチャット形式のデータをImportするプログラムです。
 * 2011/11月までCSVファイルのダウンロードができなかったので，
 * HTMLからコピーペーストしたものをこちらで解析していました．（今は利用していません）
 * @author macchan
 */
public class KKaniChatConverter {

	public static void main(String[] args) {
		new KKaniChatConverter().test();
	}

	public KKaniChatConverter() {
	}

	void test() {
		CDirectory dir = CFileSystem.getExecuteDirectory().findDirectory(
				"testcases/importkani");
		CFile in = dir.findFile("test1.csv");
		CFile out = dir.findOrCreateFile("test1.out.csv");
		convert(in, out);
	}

	public void convert(CFile in, CFile out) {
		String[][] values = CCSVFileIO.load(in);

		List<KDDiscourseRecord> records = new ArrayList<KDDiscourseRecord>();

		for (int i = 0; i < values.length; i = i + 2) {
			try {
				KDDiscourseRecord record = processLine(values[i],
						values[i + 1], i / 2);
				if (record != null) {
					records.add(record);
				}
			} catch (Exception ex) {
				//ex.printStackTrace();
				//do nothing continue next
			}
		}

		KRecordFileIO.save(records, out);
	}

	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy/MM/dd hh:mm");

	private KDDiscourseRecord processLine(String[] line1, String[] line2, int id)
			throws Exception {
		if (line1.length < 3) {
			throw new RuntimeException();
		}
		if (line2.length < 3) {
			throw new RuntimeException();
		}

		//name, contents
		String[] contents = line2[0].split(" : ");
		if (contents.length < 2) {
			throw new RuntimeException();
		}
		if (contents[0].startsWith("OWNER")) {//SPECIAL NAME
			throw new RuntimeException();
		}
		if (contents[1].startsWith("ENTER")) {//SPECIAL TEXT
			throw new RuntimeException();
		}
		if (contents[1].startsWith("EXIT")) {//SPECIAL TEXT
			throw new RuntimeException();
		}
		String student = contents[0];
		String text = contents[1];

		KDDiscourseRecord record = new KDDiscourseRecord(id, student, text);

		//group
		record.setGroupName("DefaultGroup");

		//date
		Date date = dateFormat.parse(line1[1]);
		record.setTime(date.getTime());
		return record;
	}
}
