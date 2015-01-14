/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kfl.kf4serializer.serializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import kfl.kf4serializer.connector.KFLoginModel;

import org.zoolib.ZTuple;
import org.zoolib.ZTxn;
import org.zoolib.tuplebase.ZTB;
import org.zoolib.tuplebase.ZTBIter;
import org.zoolib.tuplebase.ZTBQuery;
import org.zoolib.tuplebase.ZTBSpec;

import com.knowledgeforum.k5.common.K5TBConnector;

/**
 * @author Yoshiaki Matsuzawa
 */
public class KFDataSerializer {
	private static final ZTBQuery ALL_OBJECT_QUERY = new ZTBQuery(
			ZTBSpec.sHas("Object"));
	private static final ZTBQuery All_LINKS_QUERY = new ZTBQuery(
			ZTBSpec.sHas("Link"));

	// public static void main(String[] args) throws IOException {
	public void start(KFSerializeFolder dir) throws IOException {

		// Database connection
		KFLoginModel login = dir.getLoginModel();
		ZTB theTB = K5TBConnector.sGetTB_HTTP_UserName(
				new K5TBConnector.HostInfo(login.getHost(), login.getPort(),
						login.getDBName()), null, login.getUser(), login
						.getPassword(), null);
		ZTxn theTxn = new ZTxn();

		File linksFile = dir.getLinksFile();
		int numLinks = dumpOne(theTxn, theTB, All_LINKS_QUERY, linksFile);
		dir.setLinkCount(numLinks);

		File objsFile = dir.getObjectsFile();
		int numObjects = dumpOne(theTxn, theTB, ALL_OBJECT_QUERY, objsFile);
		dir.setObjectCount(numObjects);

		dir.saveMeta();
	}

	private int dumpOne(ZTxn theTxn, ZTB theTB, ZTBQuery query, File file)
			throws IOException {

		// Prepare file
		if (!file.exists()) {
			file.createNewFile();
		}

		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				file));

		int count = 0;
		ZTBIter itr = new ZTBIter(theTxn, theTB, query);
		while (itr.hasNext()) {
			ZTuple t = itr.getTuple();
			oos.writeObject(itr.getZID());
			oos.writeObject(t);
			count++;
			itr.advance();
		}

		oos.close();

		return count;
	}
}
