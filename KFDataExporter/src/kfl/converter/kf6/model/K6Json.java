package kfl.converter.kf6.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class K6Json {
	private transient Map<String, K6Object> objects = new LinkedHashMap<String, K6Object>();

	public void put(K6Object obj) {
		objects.put(obj._id, obj);
	}

	public K6Object get(String id) {
		return objects.get(id);
	}

	public K6Community community;
	public List<K6Author> authors = new ArrayList<K6Author>();
	public List<K6Contribution> contributions = new ArrayList<K6Contribution>();
	public List<K6Link> links = new ArrayList<K6Link>();
	public List<K6Record> records = new ArrayList<K6Record>();
}
