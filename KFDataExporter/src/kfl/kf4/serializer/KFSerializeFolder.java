package kfl.kf4.serializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.util.Properties;

import kfl.kf4.connector.KFLoginModel;

import org.zoolib.ZID;
import org.zoolib.ZTuple;

import clib.common.filesystem.CDirectory;

public class KFSerializeFolder {

	private CDirectory dir;

	private KFLoginModel loginModel;
	private int objectCount;
	private int linkCount;

	public KFSerializeFolder(CDirectory dir) {
		this.dir = dir;
	}

	public void processObjects(IKFTupleProcessor processor) throws Exception {
		process(getObjectsFile(), objectCount, processor);
	}

	public void processLinks(IKFTupleProcessor processor) throws Exception {
		process(getLinksFile(), linkCount, processor);
	}

	private void process(File file, int len, IKFTupleProcessor processor)
			throws Exception {
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
	
	public CDirectory getDir() {
		return dir;
	}

	public File getLinksFile() {
		return dir.findOrCreateFile("links.db").toJavaFile();
	}

	public File getObjectsFile() {
		return dir.findOrCreateFile("objects.db").toJavaFile();
	}

	public File getMetaFile() {
		return dir.findOrCreateFile("meta.txt").toJavaFile();
	}

	public CDirectory getAttachmentDir() {
		return dir.findOrCreateDirectory("attachments");
	}

	public void setLoginModel(KFLoginModel loginModel) {
		this.loginModel = loginModel;
	}

	public KFLoginModel getLoginModel() {
		if (loginModel == null) {
			throw new RuntimeException("loginModel is null");
		}
		return this.loginModel;
	}

	public void setObjectCount(int objectCount) {
		this.objectCount = objectCount;
	}

	public int getObjectCount() {
		return objectCount;
	}

	public void setLinkCount(int linkCount) {
		this.linkCount = linkCount;
	}

	public int getLinkCount() {
		return linkCount;
	}

	public void loadMeta() {
		try {
			loginModel = new KFLoginModel();
			Properties prop = new Properties();
			prop.load(new FileInputStream(getMetaFile()));
			loginModel.setHost(prop.getProperty("host"));
			loginModel.setPort(Integer.parseInt(prop.getProperty("port")));
			loginModel.setDBName(prop.getProperty("dbName"));
			loginModel.setUser(prop.getProperty("user"));
			loginModel.setPassword(prop.getProperty("pass"));
			objectCount = Integer.parseInt(prop.getProperty("objects"));
			linkCount = Integer.parseInt(prop.getProperty("links"));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public void saveMeta() {
		try {
			Properties prop = new Properties();
			prop.setProperty("host", loginModel.getHost());
			prop.setProperty("port", Integer.toString(loginModel.getPort()));
			prop.setProperty("dbName", loginModel.getDBName());
			prop.setProperty("user", loginModel.getUser());
			prop.setProperty("pass", loginModel.getPassword());
			prop.setProperty("objects", Integer.toString(getObjectCount()));
			prop.setProperty("links", Integer.toString(getLinkCount()));
			prop.store(new FileOutputStream(getMetaFile()), "");
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
