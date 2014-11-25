package kfl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class KFLog implements Serializable {

	private static final long serialVersionUID = 1L;

	private String method;
	private Date time;
	private KFAuthor author;
	private KFElement target1;
	private KFElement target2;

	public KFLog(String method, Date time, KFAuthor author, KFElement target1) {
		this.method = method;
		this.time = time;
		this.author = author;
		this.target1 = target1;
	}

	public KFLog(String method, Date time, KFAuthor author, KFElement target1,
			KFElement target2) {
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

	public KFAuthor getAuthor() {
		return author;
	}

	public KFElement getTarget() {
		return getTarget1();
	}

	public KFElement getTarget1() {
		return target1;
	}

	public KFElement getTarget2() {
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
