/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kfl.app.kfn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.zoolib.ZTxn;
import org.zoolib.tuplebase.ZTB;
import org.zoolib.tuplebase.ZTBIter;
import org.zoolib.tuplebase.ZTBQuery;
import org.zoolib.tuplebase.ZTBSpec;

import com.knowledgeforum.k5.common.K5TBConnector;

/**
 * 
 * @author bodong
 */
public class DataDump {
	private static final ZTBQuery ALL_OBJECT_QUERY = new ZTBQuery(
			ZTBSpec.sHas("Object"));
	private static final ZTBQuery All_LINKS_QUERY = new ZTBQuery(ZTBSpec.sHas("Link"));

	// public static void main(String[] args) throws IOException {
	public void dump(String db_host, int db_port, String db_name,
			String db_user, String db_pass, File objsFile, File linksFile)
			throws IOException {

		// Database connection
		ZTB theTB = K5TBConnector.sGetTB_HTTP_UserName(
				new K5TBConnector.HostInfo(db_host, db_port, db_name), null,
				db_user, db_pass, null);

		ZTxn theTxn = new ZTxn();

		dumpOne(theTxn, theTB, ALL_OBJECT_QUERY, objsFile);
		dumpOne(theTxn, theTB, All_LINKS_QUERY, linksFile);
	}

	private void dumpOne(ZTxn theTxn, ZTB theTB, ZTBQuery all_q, File file)
			throws IOException {
		
		// Prepare file
		if (!file.exists()) {
			file.createNewFile();
		}

		ZTBIter itr = new ZTBIter(theTxn, theTB, all_q);
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		while (itr.hasNext()) {
			StringBuilder sb = new StringBuilder();
			sb.append(itr.getZID().toString())
					.append(itr.getTuple().toString()).append("\n");
			bw.write(sb.toString());
			itr.advance();
		}

		bw.close();
	}
}
