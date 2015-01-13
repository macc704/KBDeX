package kfl.kf4.serializer;

import org.zoolib.ZID;
import org.zoolib.ZTuple;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;

public class KFAllAttachmentDownloader {

	private KFAttachmentDownloader downloader = new KFAttachmentDownloader();
	private CDirectory attachmentDir;

	public void start(KFSerializeFolder folder) throws Exception {
		downloader.initialize(folder.getLoginModel());
		attachmentDir = folder.getAttachmentDir();
		folder.processObjects(new IKFTupleProcessor() {
			public void processOne(ZID id, ZTuple tuple) throws Exception {
				process(id, tuple);
			}
		});
	}

	private void process(ZID id, ZTuple tuple) throws Exception {
		String type = tuple.getString("Object");
		if (type.equals("attachment")) {
			if (attachmentDir.findFile(id.toString()) != null) {
				System.out.println("Attachment " + id.toString()
						+ " is already downloaded. Skip it.");
				return;
			}
			CFile file = attachmentDir.findOrCreateFile(id.toString());
			downloader.download(id.toString(), file.toJavaFile());
		}
	}
}
