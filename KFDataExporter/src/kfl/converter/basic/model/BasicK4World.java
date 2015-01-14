package kfl.converter.basic.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BasicK4World {	
	private String name;
	
	private Map<String, BasicK4Object> objects = new LinkedHashMap<String, BasicK4Object>();	
	private Map<String, BasicK4Link> links = new LinkedHashMap<String, BasicK4Link>();
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void putObject(BasicK4Object obj){
		objects.put(obj.getId(), obj);
	}
	
	public void putLink(BasicK4Link link){
		links.put(link.getId(), link);
	}
	
	public BasicK4Object getObject(String id){
		return objects.get(id);
	}
	
	public List<BasicK4Object> getObjects() {
		return new ArrayList<BasicK4Object>(objects.values());
	}
	
	public List<BasicK4Link> getLinks() {
		return new ArrayList<BasicK4Link>(links.values());
	}
}
