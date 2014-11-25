package kfl.model;

import java.util.ArrayList;
import java.util.List;

//To retrive the body of attachement, throw a query in this form
//http://builder.ikit.org/attachment?DB=Susana_test&AttachmentID=1638
public class KFAttachment extends KFElement {

	private static final long serialVersionUID = 1L;

	private String title;
	private String path;
	private String mime;
	private String userFileName;

	public KFAttachment() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMime() {
		return mime;
	}

	public void setMime(String mime) {
		this.mime = mime;
	}

	public String getUserFileName() {
		return userFileName;
	}

	public void setUserFileName(String userFileName) {
		this.userFileName = userFileName;
	}

	@Override
	public String getType() {
		return "attachment";
	}

	@Override
	public String getShortDescrption() {
		return this.getType() + "-" + path;
	}

	public List<String> getStrings() {
		List<String> strings = new ArrayList<String>();
		addBasicStrings(strings);
		strings.add(getTitle());
		strings.add(getPath());
		strings.add(getMime());
		strings.add(getUserFileName());
		return strings;
	}
}
