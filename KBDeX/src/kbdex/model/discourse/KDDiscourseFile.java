/*
 * KDDiscourseFile.java
 * Created on Jul 20, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kbdex.app.KBDeX;
import kbdex.model.discourse.KDDiscourse.DiscourseUnitType;
import kbdex.model.discourse.KDDiscourse.Language;
import kbdex.model.discourse.filters.KAgentNameDiscourseFilter;
import kbdex.model.discourse.filters.KTimeDiscourseFilter;
import kbdex.utils.KDictionary;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileElement;
import clib.common.system.CEncoding;
import clib.common.system.CEncodingDetector;
import clib.common.table.CCSVFileIO;

/**
 * DAO of the KDDiscourse
 * 
 * ファイルからの読込，ファイルへの書込，複製，削除を担当する．
 */
public class KDDiscourseFile {

	//private static final CEncoding ENCODING = KBDeX.ENCODING;

	private static final String RECORD_FILE = "data.csv";
	private static final String WORD_FILE = "word.txt";
	public static final String LANGUAGE_FILE = "lang.txt";
	public static final String UNIT_FILE = "unit.txt";
	public static final String AGENT_FILTER_FILE = "agent_filter.txt";
	public static final String CURRENT_TIME_FILTER_FILE = "current_time_filter.txt";
	public static final String TIME_FILTER_FILE = "time_filter.txt";
	public static final String GROUP_FILE = "group.csv";
	public static final String LIFETIME_FILE = "lifetime.txt";

	private CDirectory dir;

	public KDDiscourseFile(CDirectory dir) {
		this.dir = dir;
	}

	/***************************************************************
	 * Operations for this directory
	 ***************************************************************/

	public String getName() {
		return dir.getNameByString();
	}

	public void copyTo(String name) {
		if (dir.getNameByString().equals(name)) {
			throw new RuntimeException();
		}
		CDirectory newDir = dir.getParentDirectory()
				.findOrCreateDirectory(name);
		copyTo(newDir);
	}

	private void copyTo(CDirectory newDir) {
		for (CFileElement elem : dir.getChildren()) {
			if (elem instanceof CFile) {
				((CFile) elem).copyTo(newDir, elem.getName());
			}
		}
	}

	public void delete() {
		dir.delete();
	}

	public void renameTo(String newName) {
		dir.renameTo(newName);
	}

	/***************************************************************
	 * Save and Load 
	 ***************************************************************/

	public CFile getRecordFile() {
		return openCFile(RECORD_FILE);
	}

	protected List<KDDiscourseRecord> loadRecords() {
		return KRecordFileIO.load(getRecordFile());
	}

	public CFile getSelectedWordFile() {
		return openCFile(WORD_FILE);
	}

	protected KDictionary<String> loadSelectedWords() {
		CFile file = getSelectedWordFile();
		KDictionary<String> keywords = new KDictionary<String>() {
			private static final long serialVersionUID = 1L;

			protected String createInstance(String text) {
				return text;
			}
		};
		List<String> words = file.loadTextAsList();
		for (String word : words) {
			if (word.startsWith("#")) {
				continue;
			}

			keywords.getElement(word);
		}
		return keywords;
	}

	protected KAgentNameDiscourseFilter loadAgentFilter() {
		CFile file = openCFile(AGENT_FILTER_FILE);
		if (file.loadText().isEmpty()) {
			return null;
		}
		return new KAgentNameDiscourseFilter(file.loadTextAsList());
	}

	protected void saveAgentFilter(KAgentNameDiscourseFilter filter) {
		CFile file = openCFile(AGENT_FILTER_FILE);
		file.saveTextFromList(filter.getAgentNames());
	}

	protected void saveTimeFilters(List<KTimeDiscourseFilter> filters) {
		// create line
		List<String[]> lines = new ArrayList<String[]>();
		for (KTimeDiscourseFilter filter : filters) {
			String[] line = new String[3];
			line[0] = filter.getName();
			line[1] = Long.toString(filter.getRange().getStart().getAsLong());
			line[2] = Long.toString(filter.getRange().getEnd().getAsLong());
			lines.add(line);
		}

		// write
		CFile file = openCFile(TIME_FILTER_FILE);
		CCSVFileIO.save(lines, file);
	}

	protected Map<String, KTimeDiscourseFilter> loadTimeFilters() {
		Map<String, KTimeDiscourseFilter> filters = new LinkedHashMap<String, KTimeDiscourseFilter>();
		CFile file = openCFile(TIME_FILTER_FILE);
		String[][] values = CCSVFileIO.load(file);
		for (String[] line : values) {
			try {
				String name = line[0];
				long from = Long.parseLong(line[1]);
				long to = Long.parseLong(line[2]);
				filters.put(name, new KTimeDiscourseFilter(name, from, to));
			} catch (Exception ex) {
				ex.printStackTrace();
				continue;
			}
		}
		return filters;
	}

	protected String loadTimeFilterName() {
		try {
			CFile file = openCFile(CURRENT_TIME_FILTER_FILE);
			return file.loadTextAsList().get(0);
		} catch (Exception ex) {
			return "";
		}
	}

	protected void saveTimeFilterName(String name) {
		CFile file = openCFile(CURRENT_TIME_FILTER_FILE);
		file.saveText(name);
	}

	protected List<KDGroup> loadGroups() {
		Map<String, KDGroup> groups = new LinkedHashMap<String, KDGroup>();
		CFile file = openCFile(GROUP_FILE);

		String[][] values = CCSVFileIO.load(file);
		int len = values.length;
		for (int i = 0; i < len; i++) {
			try {
				String groupName = values[i][0];
				if (groupName == null) {
					continue;
				}
				String agentName = values[i][1];
				if (!groups.containsKey(groupName)) {
					groups.put(groupName, new KDGroup(groupName));
				}
				KDGroup group = groups.get(groupName);
				group.addMember(agentName);
			} catch (Exception ex) {
				ex.printStackTrace();
				continue;
			}
		}
		return new ArrayList<KDGroup>(groups.values());
	}

	/**
	 * @param language
	 */
	public void saveLanguage(Language language) {
		CFile file = openCFile(LANGUAGE_FILE);
		file.saveText(language.toString());
	}

	/**
	 * @return
	 */
	public Language loadLanguage() {
		CFile file = openCFile(LANGUAGE_FILE);
		if (file.loadText().isEmpty()) {
			return Language.ENGLISH;
		}
		try {
			Language language = Language.valueOf(file.loadText());
			return language;
		} catch (Exception ex) {
			ex.printStackTrace();
			return Language.ENGLISH;
		}
	}

	/**
	 * @param unitType
	 */
	public void saveUnitType(DiscourseUnitType unitType) {
		CFile file = openCFile(UNIT_FILE);
		file.saveText(unitType.toString());
	}

	/**
	 * @return
	 */
	public DiscourseUnitType loadUnitType() {
		CFile file = openCFile(UNIT_FILE);
		if (file.loadText().isEmpty()) {
			return DiscourseUnitType.NOTE;
		}
		try {
			DiscourseUnitType type = DiscourseUnitType.valueOf(file.loadText());
			return type;
		} catch (Exception ex) {
			ex.printStackTrace();
			return DiscourseUnitType.NOTE;
		}
	}

	/**
	 * @param lifetime
	 */
	public void saveLifetime(int lifetime) {
		CFile file = openCFile(LIFETIME_FILE);
		file.saveText(Integer.toString(lifetime));
	}

	/**
	 * @param lifetime
	 */
	public int loadLifetime() {
		CFile file = openCFile(LIFETIME_FILE);
		if (file.loadText().isEmpty()) {
			return KDDiscourse.DEFAULT_LIFETIME;
		}
		try {
			return Integer.parseInt(file.loadText());
		} catch (Exception ex) {
			ex.printStackTrace();
			return KDDiscourse.DEFAULT_LIFETIME;
		}
	}

	private CFile openCFile(String filename) {
		CFile file = dir.findOrCreateFile(filename);
		CEncoding enc = CEncodingDetector.detect(file.toJavaFile());
		if (enc != CEncoding.UNKNOWN) {
			file.setEncodingIn(enc);
		}
		file.setEncodingOut(KBDeX.ENCODING_OUT);
		return file;
	}

}
