package kfl.app.kf6exporter.model;

public class KFObject {
	public String _id;
	public String type;
	
	@Override
	public String toString() {
		return "a " + type;
	}
}
