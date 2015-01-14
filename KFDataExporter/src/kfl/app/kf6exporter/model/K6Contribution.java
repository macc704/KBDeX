package kfl.app.kf6exporter.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class K6Contribution extends K6Object {
	public Date created;
	public String title;
	public String permission;
	public boolean locked;
	public List<String> authors = new ArrayList<String>();
}
