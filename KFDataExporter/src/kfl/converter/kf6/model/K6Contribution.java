package kfl.converter.kf6.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class K6Contribution extends K6Object {
	public Date created;
	public Date modified;
	public String title;
	public String status = "active";
	public String permission = "public";
	public List<String> authors = new ArrayList<String>();
	public Map<String, Object> data = new HashMap<String, Object>();
}
