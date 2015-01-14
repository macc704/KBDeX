package kfl.converter.basic.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.zoolib.ZID;
import org.zoolib.ZTuple;

public class BasicK4Object {

	private String id;
	private String type;
	private ZTuple tuple;
	
	private Map<String, BasicK4Link> toLinks = new LinkedHashMap<String, BasicK4Link>();
	private Map<String, BasicK4Link> fromLinks = new LinkedHashMap<String, BasicK4Link>();

	public BasicK4Object(ZID zid, ZTuple tuple) {
		this.id = zid.toString();
		this.type = tuple.getString("Object");
		this.tuple = tuple;
	}

	public String getId() {
		return id;
	}
	
	public String getType() {
		return type;
	}
	
	public ZTuple getTuple() {
		return tuple;
	}

	public void addToLink(BasicK4Link toLink) {
		toLinks.put(toLink.getId(), toLink);
	}

	public void addFromLink(BasicK4Link fromLink) {
		fromLinks.put(fromLink.getId(), fromLink);
	}

}
