package kfl.converter.kf6.model;

import java.util.ArrayList;
import java.util.List;

public class K6Author extends K6Object {
	public String email;
	public String name;
	public String hashedPassword;
	public String role;
	public List<String> workspaces = new ArrayList<String>();
}
