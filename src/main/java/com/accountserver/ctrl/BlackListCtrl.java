package com.accountserver.ctrl;

import com.accountserver.entity.BlackDeviceEntity;
import com.accountserver.entity.BlackIPEntity;
import com.accountserver.entity.BlackLtidEntity;
import com.webcore.data.persist.DbManager;
import com.webcore.exception.BussinessException;
import com.webcore.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * BlackListCtrl
 */
public class BlackListCtrl {
	private final Logger log = LoggerFactory.getLogger("Web");

	private static final BlackListCtrl instance = new BlackListCtrl();
	private Set<String> blackIPList = new ConcurrentSkipListSet<>();
	private Set<String> blackLtidList = new ConcurrentSkipListSet<>();
	private Set<String> blackDeviceList = new ConcurrentSkipListSet<>();

	private long lastUpdate = 0;

	private BlackListCtrl() {
	}

	public static BlackListCtrl getInstance() {
		return instance;
	}

	public void startUpdateBlackListTask() {
		Runnable task = () -> {
			while (true) {
				if (TimeUtil.getTimeInMillis() - this.lastUpdate > 1000 * 60) {
					this.lastUpdate = TimeUtil.getTimeInMillis();
					try {
						this.loadBlackList();
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

	private void loadBlackList() {
		this.blackIPList.clear();
		this.blackLtidList.clear();
		this.blackDeviceList.clear();

		List<BlackIPEntity> entities = DbManager.getInstance().query("from BlackIPEntity");
		if (entities != null && entities.size() > 0) {
			for (BlackIPEntity entity : entities) {
				this.blackIPList.add(entity.getIpAddr());
			}
		}

		List<BlackLtidEntity> ltidEntities = DbManager.getInstance().query("from BlackLtidEntity");
		if (ltidEntities != null && ltidEntities.size() > 0) {
			for (BlackLtidEntity entity : ltidEntities) {
				this.blackLtidList.add(entity.getLtid());
			}
		}

		List<BlackDeviceEntity> deviceEntities = DbManager.getInstance().query("from BlackDeviceEntity");
		if (deviceEntities != null && deviceEntities.size() > 0) {
			for (BlackDeviceEntity entity : deviceEntities) {
				this.blackDeviceList.add(entity.getDevice());
			}
		}
	}

	public boolean isBlackIp(String ipAddr) {
		return this.blackIPList.contains(ipAddr);
	}

	public boolean isBlackLtid(String ltid) {
		return this.blackIPList.contains(ltid);
	}

	public boolean isBlackDevice(String deviceId) {
		return this.blackDeviceList.contains(deviceId);
	}

	public BlackDeviceEntity getBlackDeviceEntity(String deviceId) {
		BlackDeviceEntity blackDeviceEntity = DbManager.getInstance().fetch("from BlackDeviceEntity where device = ?", deviceId);
		return blackDeviceEntity;
	}

	public void createBlackDevice(String deviceId, String reason, int reasonType) {
		BlackDeviceEntity blackDeviceEntity = new BlackDeviceEntity(deviceId);
		blackDeviceEntity.setReasonType(reasonType);
		blackDeviceEntity.setReason(reason);
		blackDeviceEntity.create();
	}
}
