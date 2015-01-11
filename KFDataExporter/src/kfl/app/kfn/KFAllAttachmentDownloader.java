package kfl.app.kfn;

import kfl.connector.KFLoginModel;

import org.zoolib.ZID;
import org.zoolib.ZTuple;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;

public class KFAllAttachmentDownloader {

	private KFAttachmentDownloader downloader = new KFAttachmentDownloader();
	private CDirectory attachmentDir;

	public void start(KFLoginModel login, CDirectory dir) throws Exception {
		downloader.initialize(login);
		attachmentDir = dir.findOrCreateDirectory("attachments");
		KFSerializeFolder folder = new KFSerializeFolder(dir);
		folder.process("objects", new KFTupleProcessor() {
			public void processOne(ZID id, ZTuple tuple) throws Exception {
				process(id, tuple);
			}
		});
	}

	private void process(ZID id, ZTuple tuple) throws Exception{
		String type = tuple.getString("Object");
		if (type.equals("attachment")) {
			CFile file = attachmentDir.findOrCreateFile(id.toString());
			downloader.download(id.toString(), file.toJavaFile());
		}
	}
}
