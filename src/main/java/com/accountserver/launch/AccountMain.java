package com.accountserver.launch;


import com.accountserver.ctrl.BlackListCtrl;
import com.accountserver.ctrl.ServerListCtrl;
import com.accountserver.ctrl.SupervisorListCtrl;
import com.webcore.config.Config;
import com.webcore.exception.BussinessException;
import com.webcore.server.Server;

/**
 * App Entrance
 */
public class AccountMain {
	public static void main(String[] args) {
		Server server = new Server();
		try {
			server.init(AccountMain.class);

			// init sdk
			ServerListCtrl.getInstance().startUpdateServerListTask();
			BlackListCtrl.getInstance().startUpdateBlackListTask();
			if (SupervisorListCtrl.getInstance().isEnable()) {
				SupervisorListCtrl.getInstance().reloadForever();
			}

			server.start();
		} catch (Exception e) {
			BussinessException.catchEx(e);
		}
	}
}
