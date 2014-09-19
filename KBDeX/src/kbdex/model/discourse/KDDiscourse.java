/*
 * KDDiscourse.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kbdex.model.discourse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kbdex.controller.tools.stepwise.IKPhase;
import kbdex.model.discourse.filters.IKDiscourseFilter;
import kbdex.model.discourse.filters.KAgentNameDiscourseFilter;
import kbdex.model.discourse.filters.KTimeDiscourseFilter;
import kbdex.model.discourse.wordprocessing.KWordProcessorFactory;
import kbdex.utils.KDictionary;
import kbdex.view.network.KParameterProvider;
import clib.common.collections.CVocaburary;
import clib.common.model.CObject;
import clib.common.time.CTime;
import clib.common.time.CTimeRange;
import clib.common.utils.ICProgressMonitor;

/**
 * ディスコースを表現するクラス
 * 
 * ＜定義＞
 * ディスコースは複数のレコードからなる．
 * 解析する言語を知っている．
 * フィルターを内蔵し，フィルターしたデータを取得することができる．
 * @author macchan
 */
public class KDDiscourse extends CObject {

	public static final int DEFAULT_LIFETIME = 0;

	public enum Language {
		JAPANESE, ENGLISH
	};

	public enum DiscourseUnitType {
		NOTE, SENTENCE
	};

	private KDDiscourseFile file;//DAO

	private List<KDDiscourseRecord> records;
	private KDictionary<String> selectedWords;
	private Language language = Language.JAPANESE;
	private DiscourseUnitType unitType = DiscourseUnitType.NOTE;
	private KAgentNameDiscourseFilter agentFilter;
	private KTimeDiscourseFilter currentTimeFilter;
	private List<KTimeDiscourseFilter> timeFilters;
	private List<KDGroup> groups;
	private int lifetime = DEFAULT_LIFETIME;

	public KDDiscourse(KDDiscourseFile file, ICProgressMonitor monitor) {
		this.file = file;
		load();
		createAllCash(monitor);
		refreshRecordFiltering();
	}

	private void load() {
		this.language = this.file.loadLanguage();
		this.unitType = this.file.loadUnitType();
		this.lifetime = this.file.loadLifetime();
		this.records = this.file.loadRecords();
		this.selectedWords = this.file.loadSelectedWords();
		this.groups = getGroupsByDiscourse();
		this.groups.addAll(this.file.loadGroups());

		//agent filter
		this.agentFilter = this.file.loadAgentFilter();
		if (this.agentFilter == null) {
			this.agentFilter = createDefaultAgentFilter();
		}

		// time filters
		this.timeFilters = new ArrayList<KTimeDiscourseFilter>();
		Map<String, KTimeDiscourseFilter> timeFilterMap = this.file
				.loadTimeFilters();
		if (timeFilterMap.isEmpty()) {
			this.timeFilters.add(createDefaultTimeFilter());
		} else {
			this.timeFilters.addAll(timeFilterMap.values());
		}

		//time filter
		String timeFilterName = file.loadTimeFilterName();
		if (timeFilterMap.containsKey(timeFilterName)) {
			this.currentTimeFilter = timeFilterMap.get(timeFilterName);
		} else {
			this.currentTimeFilter = timeFilters.get(0);
		}
	}

	public KDDiscourseFile getFile() {
		return file;
	}

	public String getName() {
		return file.getName();
	}

	public List<KDDiscourseRecord> getAllRecords() {
		return records;
		//return new ArrayList<KDDiscourseRecord>(records);
	}

	private List<KDDiscourseRecord> validRecordsCash;

	//bottle neckになっているのでcash
	public List<KDDiscourseRecord> getFilteredRecords() {
		if (validRecordsCash == null) {
			List<KDDiscourseRecord> validRecords = new ArrayList<KDDiscourseRecord>();
			for (KDDiscourseRecord record : records) {
				if (record.isValid()) {
					validRecords.add(record);
				}
			}
			validRecordsCash = validRecords;
		}

		return validRecordsCash;
	}

	public List<KDDiscourseRecord> getAgentFilteredRecords() {
		return getFilteredRecords(getAgentFilter());
	}

	private List<KDDiscourseRecord> getFilteredRecords(IKDiscourseFilter filter) {
		List<KDDiscourseRecord> filtered = new ArrayList<KDDiscourseRecord>();
		for (KDDiscourseRecord record : records) {
			if (filter.accept(record)) {
				filtered.add(record);
			}
		}
		return filtered;
	}

	public List<String> getSelectedWords() {
		return selectedWords.getElements();
	}

	public CVocaburary getVocaburary() {
		CVocaburary vocaburary = new CVocaburary();
		for (KDDiscourseRecord record : getFilteredRecords()) {
			vocaburary.addAll(record.getVocaburary());
		}
		return vocaburary;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language, ICProgressMonitor monitor) {
		if (this.language != language) {
			this.language = language;
			this.file.saveLanguage(language);
			createAllCash(monitor);
		}
	}

	public DiscourseUnitType getUnitType() {
		return unitType;
	}

	public void setUnitType(DiscourseUnitType unitType) {
		if (this.unitType != unitType) {
			this.unitType = unitType;
			this.file.saveUnitType(unitType);
		}
	}

	public List<KDAgent> getAllAgents() {
		return new ArrayList<KDAgent>(getAgents(getAllRecords()).values());
	}

	public List<KDAgent> getFilteredAgents() {
		return new ArrayList<KDAgent>(getAgents(getAgentFilteredRecords())
				.values());
	}

	private Map<String, KDAgent> getAgents(List<KDDiscourseRecord> records) {
		Map<String, KDAgent> agents = new LinkedHashMap<String, KDAgent>();
		for (KDDiscourseRecord unit : records) {
			String name = unit.getAgentName();
			if (!agents.containsKey(name)) {
				agents.put(name, new KDAgent(name));
			}
			agents.get(name).add(unit);
		}
		return agents;
	}

	public List<KDGroup> getGroups() {
		return groups;
	}

	private List<KDGroup> getGroupsByDiscourse() {
		Map<String, KDGroup> groups = new HashMap<String, KDGroup>();
		for (KDDiscourseRecord record : getAllRecords()) {
			String name = record.getGroupName();
			if (name == null) {
				continue;
			}
			if (!groups.containsKey(name)) {
				groups.put(name, new KDGroup(name));
			}
			KDGroup group = groups.get(name);
			group.addMember(record.getAgentName());
		}
		return new ArrayList<KDGroup>(groups.values());
	}

	/***************************************************************
	 * Filter系
	 ***************************************************************/

	public void setAgentFilter(KAgentNameDiscourseFilter agentFilter) {
		this.agentFilter = agentFilter;
		file.saveAgentFilter(agentFilter);
		refreshRecordFiltering();
	}

	public KAgentNameDiscourseFilter getAgentFilter() {
		return this.agentFilter;
	}

	public void setTimeFilters(List<KTimeDiscourseFilter> filters) {
		this.timeFilters = filters;
		file.saveTimeFilters(filters);
		refreshRecordFiltering();
	}

	public List<KTimeDiscourseFilter> getTimeFilters() {
		return timeFilters;
	}

	public void setCurrentTimeFilter(KTimeDiscourseFilter timeFilter) {
		this.currentTimeFilter = timeFilter;
		file.saveTimeFilterName(timeFilter.getName());
		refreshRecordFiltering();
	}

	public KTimeDiscourseFilter getCurrentTimeFilter() {
		return this.currentTimeFilter;
	}

	private void refreshRecordFiltering() {
		clearFilters();
		applyFilter(getAgentFilter());
		applyFilter(getCurrentTimeFilter());
		validRecordsCash = null;
	}

	private void clearFilters() {
		for (KDDiscourseRecord record : getAllRecords()) {
			record.setValid(true);
		}
	}

	private void applyFilter(IKDiscourseFilter filter) {
		for (KDDiscourseRecord record : getAllRecords()) {
			if (filter.accept(record) == false) {
				record.setValid(false);
			}
		}
	}

	public KAgentNameDiscourseFilter createDefaultAgentFilter() {
		KAgentNameDiscourseFilter filter = new KAgentNameDiscourseFilter();
		for (KDAgent agent : getAllAgents()) {
			filter.add(agent.getName());
		}
		return filter;
	}

	public KTimeDiscourseFilter createDefaultTimeFilter() {
		return new KTimeDiscourseFilter("NewFilter", getTimeRange());
	}

	public CTimeRange getTimeRange() {
		if (records.size() <= 0) {
			return new CTimeRange();
		}
		//このアルゴリズムは，時間順に並んでいることを前提としてしまっている（そうでないと，灰色）
		//		CTime start = records.get(0).getTime().getStartOfWeek();
		//		CTime end = records.get(records.size() - 1).getTime().getStartOfWeek()
		//				.getNextWeek();

		//新アルゴリズム（走査してもとめる） #bugfix 1.5.7
		CTime start = records.get(0).getTime().getStartOfWeek();
		CTime end = records.get(0).getTime().getStartOfWeek();
		for (KDDiscourseRecord record : records) {
			CTime rStart = record.getTime().getStartOfWeek();
			CTime rEnd = record.getTime().getStartOfWeek().getNextWeek();
			if (rStart.before(start)) {
				start = rStart;
			}
			if (rEnd.after(end)) {
				end = rEnd;
			}
		}

		return new CTimeRange(start, end);
	}

	public void setLifetime(int lifetime) {
		this.lifetime = lifetime;
		file.saveLifetime(lifetime);
	}

	public int getLifetime() {
		return lifetime;
	}

	/***************************************************************
	 * 未使用
	 ***************************************************************/

	/**
	 * @return
	 */
	public List<IKPhase> getPhases() {
		return null;
	}

	/**
	 * @return
	 */
	public List<String> getHBWs() {
		return null;
	}

	/***************************************************************
	 * Cash系
	 ***************************************************************/

	private KWordProcessorFactory getFactory() {
		return KWordProcessorFactory.createFactory(language);
	}

	public void reloadSelectedWords(ICProgressMonitor monitor) {
		this.selectedWords = this.file.loadSelectedWords();
		createKeywordingCash(monitor);
	}

	private void createAllCash(ICProgressMonitor monitor) {
		createSentenceCash(monitor);
		createVocaburaryCash(monitor);
		createKeywordingCash(monitor);
	}

	private void createSentenceCash(ICProgressMonitor monitor) {
		monitor.setWorkTitle("Cutting Sentence");
		monitor.setMax(records.size());

		for (KDDiscourseRecord record : records) {
			record.createSentencesCash(getFactory());
			monitor.progress(1);
		}
	}

	private void createKeywordingCash(ICProgressMonitor monitor) {
		monitor.setWorkTitle("Cashing record");
		monitor.setMax(records.size());

		for (KDDiscourseRecord record : records) {
			record.createKeywordingCash(selectedWords, getFactory());
			monitor.progress(1);
		}
	}

	private void createVocaburaryCash(ICProgressMonitor monitor) {
		monitor.setWorkTitle("Cashing vocaburary");
		monitor.setMax(records.size());

		for (KDDiscourseRecord record : records) {
			record.createVocaburaryCash(selectedWords, getFactory());
			monitor.progress(1);
		}
	}

	private KParameterProvider<Integer> agentWeightParameter = new KParameterProvider<Integer>(
			1);

	public KParameterProvider<Integer> getAgentWeightParameter() {
		return agentWeightParameter;
	}

	private KParameterProvider<Integer> discourseUnitWeightParameter = new KParameterProvider<Integer>(
			1);

	public KParameterProvider<Integer> getDiscourseUnitWeightParameter() {
		return discourseUnitWeightParameter;
	}

	private KParameterProvider<Integer> wordWeightParameter = new KParameterProvider<Integer>(
			1);

	public KParameterProvider<Integer> getWordWeightParameter() {
		return wordWeightParameter;
	}

}
