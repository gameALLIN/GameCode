package com.accountserver.ctrl;

import com.accountserver.entity.ServerEntity;
import com.alibaba.fastjson.JSONObject;
import com.webcore.config.Config;
import com.webcore.data.persist.DbManager;
import com.webcore.exception.BussinessException;
import com.webcore.util.StringUtil;
import com.webcore.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ServerListCtrl
 */
public class ServerListCtrl {
	private final Logger log = LoggerFactory.getLogger("Web");

	private static final ServerListCtrl instance = new ServerListCtrl();
	private Map<Integer, ServerEntity> serverList = new ConcurrentHashMap<>();
	private long lastUpdate = 0;

	private ServerListCtrl() {
	}

	public static ServerListCtrl getInstance() {
		return instance;
	}

	public void startUpdateServerListTask() {
		Runnable task = () -> {
			while (true) {
				if (TimeUtil.getTimeInMillis() - this.lastUpdate > 1000 * 10) {
					this.lastUpdate = TimeUtil.getTimeInMillis();
					try {
						this.loadServerList();
					} catch (Exception e) {
						BussinessException.catchEx(e);
					}
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		Thread thread = new Thread(task);
		thread.start();
	}

	private void loadServerList() {
		List<ServerEntity> entities = DbManager.getInstance().query("from ServerEntity");
		if (entities.size() > 0) {
			for (ServerEntity serverEntity : entities) {
				this.serverList.put(serverEntity.getId(), serverEntity);
			}
		}
	}

	public List<JSONObject> getGameServers(boolean isWhiteIp) {
		List<JSONObject> servers = new ArrayList<>();

		String supportShadownServerStr = Config.getInstance().getProperty("supportShadowServer");
		boolean isSupportShadownServer = false;

		if (StringUtil.isNotEmpty(supportShadownServerStr)) {
			isSupportShadownServer = Boolean.parseBoolean(supportShadownServerStr);
		}
		Iterator<Map.Entry<Integer, ServerEntity>> it = this.serverList.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, ServerEntity> kv = it.next();
			if ((!kv.getValue().isHide()
					&& kv.getValue().getOpenTimestamp() <= TimeUtil.getTimeInSeconds())
					&& (kv.getValue().getId() < 1000 || !isSupportShadownServer) || isWhiteIp) {
				servers.add(kv.getValue().toJSON(isWhiteIp));
			}
		}

		return servers;
	}

	public List<JSONObject> getSpecialGameServers(boolean isWhiteIp) {
		List<JSONObject> servers = new ArrayList<>();

		Iterator<Map.Entry<Integer, ServerEntity>> it = this.serverList.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, ServerEntity> kv = it.next();
			if ((!kv.getValue().isHide() && kv.getValue().getOpenTimestamp() <= TimeUtil.getTimeInSeconds()) && kv.getValue().getId() >= 1000 || isWhiteIp) {
				servers.add(kv.getValue().toJSON(isWhiteIp));
			}
		}

		return servers;
	}

	public ServerEntity getServerById(int serverId) {
		return this.serverList.get(serverId);
	}
}
