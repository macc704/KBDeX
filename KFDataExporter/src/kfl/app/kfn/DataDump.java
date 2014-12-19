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
	//private static final ZTBQuery All_LINKS_QUERY = new ZTBQuery(ZTBSpec.sHas("Link")).sorted("crea", true);;
	private static final ZTBQuery All_LINKS_QUERY = new ZTBQuery(ZTBSpec.sHas("Link"));

	// public static void main(String[] args) throws IOException {
	public void dump(String db_host, int db_port, String db_name,
			String db_user, String db_pass, File file, File file2)
			throws IOException {

		// Database connection
		ZTB theTB = K5TBConnector.sGetTB_HTTP_UserName(
				new K5TBConnector.HostInfo(db_host, db_port, db_name), null,
				db_user, db_pass, null);

		ZTxn theTxn = new ZTxn();

		dumpOne(theTxn, theTB, ALL_OBJECT_QUERY, file);
		dumpOne(theTxn, theTB, All_LINKS_QUERY, file2);
	}

	private void dumpOne(ZTxn theTxn, ZTB theTB, ZTBQuery all_q, File file)
			throws IOException {

		ZTBIter all_it = new ZTBIter(theTxn, theTB, all_q);

		// Prepare file
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		int x = 0;
		while (all_it.hasNext()) {
			StringBuilder sb = new StringBuilder();
			sb.append(all_it.getZID().toString())
					.append(all_it.getTuple().toString()).append("\n");
			bw.write(sb.toString());
			all_it.advance();
			System.out.println(x++);
		}

		bw.close();
	}
}
