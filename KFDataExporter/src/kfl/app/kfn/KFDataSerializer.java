/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kfl.app.kfn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Properties;

import kfl.connector.KFLoginModel;

import org.zoolib.ZTuple;
import org.zoolib.ZTxn;
import org.zoolib.tuplebase.ZTB;
import org.zoolib.tuplebase.ZTBIter;
import org.zoolib.tuplebase.ZTBQuery;
import org.zoolib.tuplebase.ZTBSpec;

import clib.common.filesystem.CDirectory;

import com.knowledgeforum.k5.common.K5TBConnector;

/**
 * 
 * @author bodong
 */
public class KFDataSerializer {
	private static final ZTBQuery ALL_OBJECT_QUERY = new ZTBQuery(
			ZTBSpec.sHas("Object"));
	private static final ZTBQuery All_LINKS_QUERY = new ZTBQuery(
			ZTBSpec.sHas("Link"));

	// public static void main(String[] args) throws IOException {
	public void dump(KFLoginModel login, CDirectory dir) throws IOException {

		// Database connection
		ZTB theTB = K5TBConnector.sGetTB_HTTP_UserName(
				new K5TBConnector.HostInfo(login.getHost(), login.getPort(),
						login.getDBName()), null, login.getUser(), login
						.getPassword(), null);

		ZTxn theTxn = new ZTxn();

		File objsFile = dir.findOrCreateFile("objects.db").toJavaFile();
		int numObjects = dumpOne(theTxn, theTB, ALL_OBJECT_QUERY, objsFile);
		File linksFile = dir.findOrCreateFile("links.db").toJavaFile();
		int numLinks = dumpOne(theTxn, theTB, All_LINKS_QUERY, linksFile);

		File metaFile = dir.findOrCreateFile("meta.txt").toJavaFile();
		Properties prop = new Properties();
		prop.setProperty("host", login.getHost());
		prop.setProperty("name", login.getDBName());
		prop.setProperty("objects", Integer.toString(numObjects));
		prop.setProperty("links", Integer.toString(numLinks));
		prop.store(new FileOutputStream(metaFile), "");
	}

	private int dumpOne(ZTxn theTxn, ZTB theTB, ZTBQuery all_q, File file)
			throws IOException {

		// Prepare file
		if (!file.exists()) {
			file.createNewFile();
		}

		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				file));

		int count = 0;
		ZTBIter itr = new ZTBIter(theTxn, theTB, all_q);
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
