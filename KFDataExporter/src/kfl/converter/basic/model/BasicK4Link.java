package kfl.converter.basic.model;

import org.zoolib.ZID;
import org.zoolib.ZTuple;

public class BasicK4Link {
	
	private String id;
	private String type;
	private ZTuple tuple;
	
	public BasicK4Object from;
	public BasicK4Object to;

	public BasicK4Link(ZID zid, ZTuple tuple) {
		this.id = zid.toString();
		this.type = tuple.getString("Link");
		this.tuple = tuple;
	}
	
	public ZTuple getTuple() {
		return tuple;
	}
	
	public String getType() {
		return type;
	}
	
	public String getId() {
		return id;
	}
}
