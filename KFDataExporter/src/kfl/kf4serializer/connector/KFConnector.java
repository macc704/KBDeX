/*
 * KFConnector.java
 * Created on Jul 16, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package kfl.kf4serializer.connector;

import org.zoolib.ZID;
import org.zoolib.tuplebase.ZTB;

import com.knowledgeforum.k5.common.K5TBConnector;
import com.knowledgeforum.k5.common.K5TBConnector.HostInfo;
import com.knowledgeforum.k5.common.K5TBConnector.Options;

/**
 * @author macchan
 * 
 */
public class KFConnector {

	public static ZTB connect(KFLoginModel model) {
		ZTB conn;
		HostInfo host = new HostInfo(model.getHost(), model.getPort(), model
				.getDBName());
		Options hostOptions = new Options(1, 10);/* retryCount, interval */
		String userName = model.getUser();
		String password = model.getPassword();
		ZID[] sessionZID = { null };
		conn = K5TBConnector.sGetTB_HTTP_UserName(host, hostOptions, userName,
				password, sessionZID);
		return conn;
	}

	public static ZTB connectWithDialog(KFLoginModel model) {
		KFLoginPanel panel = new KFLoginPanel();
		panel.setModel(model);
		ZTB conn = null;

		while (conn == null) {
			panel.openDialog();
			if (!panel.isOk()) {// cancel
				break;
			}

			conn = /* KFConnector. */connect(model);
			if (conn == null) {
				panel.setFailed(true);
			}
		}
		return conn;
	}

}
