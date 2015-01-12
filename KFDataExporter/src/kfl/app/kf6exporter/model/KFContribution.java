package kfl.app.kf6exporter.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KFContribution extends KFObject {
	public Date created;
	public String title;
	public List<String> authors = new ArrayList<String>();
}
