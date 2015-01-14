package kfl.converter.basic.app;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;

public class K4BasicProcessorMain {

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Args length has to be 1.");
			return;
		}
		new K4BasicProcessorMain().run(args[0]);
	}

	void run(String name) throws Exception {
		CDirectory baseDir = CFileSystem.getExecuteDirectory()
				.findOrCreateDirectory("kf.out");
		CDirectory dir = baseDir.findDirectory(name);
		if (dir == null) {
			System.out.println("database " + name + " is not found.");
			return;
		}
		K4BasicWorldBuilder builder = new K4BasicWorldBuilder();
		builder.build(dir);
	}


}
