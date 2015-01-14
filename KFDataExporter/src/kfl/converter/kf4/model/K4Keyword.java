package kfl.converter.kf4.model;


public class K4Keyword extends K4Element {

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
