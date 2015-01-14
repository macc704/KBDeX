package kfl.app.csvexporter.model;


public class K4Support extends K4Element {
	
	private static final long serialVersionUID = 1L;
	
	private String name;

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String getType() {
		return "support";
	}
	
	@Override
	public String getShortDescrption() {
		return name;
	}
}
