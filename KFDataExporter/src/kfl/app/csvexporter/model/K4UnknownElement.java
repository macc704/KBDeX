package kfl.app.csvexporter.model;

import java.util.HashMap;
import java.util.Map;

import org.zoolib.ZTuple;

public class K4UnknownElement extends K4Element{

	private static final long serialVersionUID = 1L;
	
	private String type;
	
	private Map<String, String> props = new HashMap<String, String>();
			
	public K4UnknownElement(String type) {
		this.type = type;
	}
	
	@Override
	public String getType() {
		return type;
	}
	public void put(String key, String value){
		props.put(key, value);
	}
	
	public void load(ZTuple tuple) {
		for (Object o : tuple.keySet()) {
			String key = (String) o;
			String value = tuple.getString(key);
			props.put(key, value);
		}
	}
	
	public String paramString(){
		StringBuffer buf = new StringBuffer();
		for (String key : props.keySet()) {
			String value = props.get(key);
			buf.append(key + "=" + value + ", ");
		}
		return buf.toString();	
	}
	
}
