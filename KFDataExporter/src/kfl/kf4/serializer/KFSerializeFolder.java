package kfl.kf4.serializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Properties;

import org.zoolib.ZID;
import org.zoolib.ZTuple;

import clib.common.filesystem.CDirectory;

public class KFSerializeFolder {

	private CDirectory dir;

	public KFSerializeFolder(CDirectory dir) {
		this.dir = dir;
	}

	public void process(String name, IKFTupleProcessor processor)
			throws Exception {
		File metaFile = dir.findOrCreateFile("meta.txt").toJavaFile();
		Properties prop = new Properties();
		prop.load(new FileInputStream(metaFile));
		int len = Integer.parseInt(prop.getProperty(name));

		File file = dir.findFile(name + ".db").toJavaFile();
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		for (int i = 0; i < len; i++) {
			Object obj;
			obj = ois.readObject();
			ZID id = (ZID) obj;
			obj = ois.readObject();
			ZTuple t = (ZTuple) obj;
			processor.processOne(id, t);
		}
		ois.close();
	}
}
