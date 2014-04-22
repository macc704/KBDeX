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


	// public static void main(String[] args) throws IOException {
	public void dump(String db_host, int db_port, String db_name,
			String db_user, String db_pass, File file) throws IOException {

		// Database connection
		ZTB theTB = K5TBConnector.sGetTB_HTTP_UserName(
				new K5TBConnector.HostInfo(db_host, db_port, db_name), null,
				db_user, db_pass, null);

		ZTxn theTxn = new ZTxn();

		// Query all tuples
		ZTBQuery all_q = new ZTBQuery(ZTBSpec.sHas("Object")).or(new ZTBQuery(
				ZTBSpec.sHas("Link")));
		ZTBIter all_it = new ZTBIter(theTxn, theTB, all_q);

		// Get all tuples and store in a string builder
		StringBuilder sb = new StringBuilder();
		// StringBuilder sb_ids = new StringBuilder();
		System.out.println("Reading data... May take a few minitues...");
		// int i = 0; // for testing
		while (all_it.hasNext()) {
			sb.append(all_it.getZID().toString())
					.append(all_it.getTuple().toString()).append("\n");
			all_it.advance();
			// if(i++ > 40) break; // for testing
		}

		// Dump data into a txt file
		System.out.println("Writing file... Almost there...");
		//DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		//String date = df.format(new Date());
		//File file = new File("tuplestore_dump_" + db_name + "_" + date + ".txt");
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(sb.toString());

		bw.close();

		System.out.println("Done!");

		// System.exit(0);
	}
}
