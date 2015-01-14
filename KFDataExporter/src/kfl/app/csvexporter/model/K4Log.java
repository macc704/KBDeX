package kfl.app.csvexporter.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class K4Log implements Serializable {

	private static final long serialVersionUID = 1L;

	private String method;
	private Date time;
	private K4Author author;
	private K4Element target1;
	private K4Element target2;

	public K4Log(String method, Date time, K4Author author, K4Element target1) {
		this.method = method;
		this.time = time;
		this.author = author;
		this.target1 = target1;
	}

	public K4Log(String method, Date time, K4Author author, K4Element target1,
			K4Element target2) {
		this.method = method;
		this.time = time;
		this.author = author;
		this.target1 = target1;
		this.target2 = target2;
	}

	public String getMethod() {
		return method;
	}

	public Date getTime() {
		return time;
	}

	public K4Author getAuthor() {
		return author;
	}

	public K4Element getTarget() {
		return getTarget1();
	}

	public K4Element getTarget1() {
		return target1;
	}

	public K4Element getTarget2() {
		return target2;
	}
	
	public static List<String> header(){
		return Arrays.asList("crea", "action", "uid", "uname", 
				"obj_id", "obj_type", "obj_info",
				"obj2_id", "obj2_type", "obj2_info");
	}

	public List<String> getStrings() {
		List<String> strings = new ArrayList<String>();
		strings.add(getTime().toString());
		strings.add(getMethod());
		if (getAuthor() != null) {
			strings.add(getAuthor().getIdAsString());
			strings.add(getAuthor().getUserName().toString());
		} else {
			strings.add("");
			strings.add("");
		}
		if (target1 != null) {
			strings.add(getTarget1().getIdAsString());
			strings.add(getTarget1().getType());
//			strings.add(getTarget1().getShortDescrption());
		} else {
			strings.add("");
			strings.add("");
			strings.add("");
		}
		if (target2 != null) {
			strings.add(getTarget2().getIdAsString());
			strings.add(getTarget2().getType());
//			strings.add(getTarget2().getShortDescrption());
		} else {
			strings.add("");
			strings.add("");
			strings.add("");
		}
		return strings;

	}
}
