package kfl.app.kf6exporter.model;

import java.util.ArrayList;
import java.util.List;

public class K6Json {
	public K6Community community;
	public List<K6Author> authors = new ArrayList<K6Author>();
	public List<K6Contribution> contributions = new ArrayList<K6Contribution>();
	public List<K6Link> links = new ArrayList<K6Link>();
	public List<K6Record> records = new ArrayList<K6Record>();
}
