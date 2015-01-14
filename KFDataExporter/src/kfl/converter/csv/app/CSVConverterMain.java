package kfl.converter.csv.app;

import kfl.converter.kf4.app.K4DataBuilder;
import kfl.kf4serializer.serializer.KFSerializeFolder;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;

public class CSVConverterMain {

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Args length has to be 1.");
			return;
		}
		new CSVConverterMain().run(args[0]);
	}

	void run(String name) throws Exception {
		CDirectory baseDir = CFileSystem.getExecuteDirectory()
				.findOrCreateDirectory("kf.out");
		CDirectory dir = baseDir.findDirectory(name);
		if (dir == null) {
			System.out.println("database " + name + " is not found.");
			return;
		}
		process(dir);
	}

	void process(CDirectory dir) throws Exception {
		KFSerializeFolder folder = new KFSerializeFolder(dir);
		folder.loadMeta();
		K4DataBuilder builder = new K4DataBuilder();
		builder.build(folder);
		CSVConverter exporter = new CSVConverter();
		exporter.export(dir, builder.getWorld());
	}
}
