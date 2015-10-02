package info.matsuzawalab.kf.kf6connector.model;

public class K6Author extends K6Object {
	public String userId;
	public String role;
	public String userName;
	public String firstName;
	public String lastName;
	public K6Community _community;

	@Override
	public String toString() {
		return _community.title;
	}
}
