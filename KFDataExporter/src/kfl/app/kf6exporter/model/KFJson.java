package kfl.app.kf6exporter.model;

import java.util.ArrayList;
import java.util.List;

public class KFJson {
	public KFCommunity community;
	public List<KFAuthor> authors = new ArrayList<KFAuthor>();
	public List<KFContribution> contributions = new ArrayList<KFContribution>();
	public List<KFLink> links = new ArrayList<KFLink>();
	public List<KFRecord> records = new ArrayList<KFRecord>();
}
