package kfl.app.csvexporter.model;


public class KFKeyword extends KFElement {

	private static final long serialVersionUID = 1L;
	
	private String word;
	
	public String getWord() {
		return word;
	}
	
	public void setWord(String word) {
		this.word = word;
	}

	@Override
	public String getType() {
		return "keyword";
	}
	
	@Override
	public String getShortDescrption() {
		return word;
	}

}
