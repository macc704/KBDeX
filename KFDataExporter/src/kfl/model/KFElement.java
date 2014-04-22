/*
 * KFElement.java
 * Created on Jul 21, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kfl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.zoolib.ZID;

/**
 * @author macchan
 */
public abstract class KFElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private ZID id;
	private Date created;
	private Date modified;
	private String status;

	public ZID getId() {
		return id;
	}

	public long getIdAsLong() {
		return id.longValue();
	}

	public String getIdAsString() {
		return Long.toString(getIdAsLong());
	}

	public void setId(ZID id) {
		this.id = id;
	}
	
	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created
	 *            the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the modified
	 */
	public Date getModified() {
		return modified;
	}

	/**
	 * @param modified
	 *            the modified to set
	 */
	public void setModified(Date modified) {
		this.modified = modified;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

	abstract public String getType();

	@Override
	public String toString() {
		return getIdAsString() + "(" + getType() + ")";
	}

	public String getShortDescrption() {
		return "(a " + getType() + ")";
	}
	
	protected void addBasicStrings(List<String> strings) {
		strings.add(getIdAsString());
		if(getCreated() != null){
			strings.add(getCreated().toString());	
		}else{
			strings.add("");
		}		
		if (getModified() != null) {
			strings.add(getModified().toString());
		} else {
			strings.add("");
		}
	}

	protected String listToString(String name, List<? extends KFElement> list) {
		StringBuffer buf = new StringBuffer();
		buf.append(name + "{");
		for (int i = 0; i < list.size(); i++) {
			if (i != 0) {
				buf.append(", ");
			}
			buf.append(list.get(i).getIdAsString());
			buf.append("(");
			buf.append(list.get(i).getShortDescrption());
			buf.append(")");
		}
		buf.append("}");
		return buf.toString();
	}

	protected String mapToString(String name, Map<? extends KFElement, ?> map) {
		StringBuffer buf = new StringBuffer();
		buf.append(name + "{");
		List<? extends KFElement> keys = new ArrayList<KFElement>(
				map.keySet());
		for (int i = 0; i < keys.size(); i++) {
			if (i != 0) {
				buf.append(", ");
			}
			KFElement key = keys.get(i);
			Object value = map.get(key); 
			buf.append(key.getIdAsString());
			buf.append("(");
			buf.append(key.getShortDescrption());
			buf.append(")");
			buf.append("=");
			buf.append(value);
		}
		buf.append("}");
		return buf.toString();
	}

}
