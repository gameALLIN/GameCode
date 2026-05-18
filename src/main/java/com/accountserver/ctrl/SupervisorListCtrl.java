package com.accountserver.ctrl;

import com.accountserver.entity.SupervisorEntity;
import com.webcore.config.Config;
import com.webcore.data.persist.DbManager;
import com.webcore.exception.BussinessException;
import com.webcore.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class SupervisorListCtrl {
    private final Logger log = LoggerFactory.getLogger("Web");
    private static SupervisorListCtrl instance = new SupervisorListCtrl();

    private Set<String> supervisorIPList = new ConcurrentSkipListSet<>();

    private long lastUpdate = 0;


    private Set<String> supervisorDeviceList = new ConcurrentSkipListSet<>();

    public static SupervisorListCtrl getInstance() {
        return instance;
    }

    public Set<String> getSupervisorIPList() {
        return supervisorIPList;
    }

    public Set<String> getSupervisorDeviceList() {
        return supervisorDeviceList;
    }

    public void reloadForever() {
        Runnable task = () -> {
            while (true) {
                if (System.currentTimeMillis() - lastUpdate > 60 * 1000) {
                    try {
                        this.reload(); // reload supervisor list
                        lastUpdate = System.currentTimeMillis();
                    } catch (Exception e) {
                        BussinessException.catchEx(e);
                    }
                }
                try {
                    Thread.sleep(1000); // reload every 60 seconds
                } catch (InterruptedException e) {
                    BussinessException.catchEx(e);
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private void reload() {
        supervisorIPList.clear();
        supervisorDeviceList.clear();

        List<SupervisorEntity> entities = DbManager.getInstance().query("from SupervisorEntity where invalid = 0");
        if (entities != null && entities.size() > 0) {
            for (SupervisorEntity entity : entities) {
                if (StringUtil.isNotEmpty(entity.getRemoteIp())) {
                    supervisorIPList.add(entity.getRemoteIp());
                }
                if (StringUtil.isNotEmpty(entity.getDevice())) {
                    supervisorDeviceList.add(entity.getDevice());
                }
            }

            log.info("Supervisor reloaded, supervisorIPs: {}, supervisorDevices: {}", supervisorIPList, supervisorDeviceList);
        }
    }

    public boolean isEnable() {
        return Boolean.parseBoolean(Config.getInstance().getProperty("enableSupervisor"));
    }

    public boolean isSupervised(String ip, String device) {
        return supervisorIPList.contains(ip) || supervisorDeviceList.contains(device);
    }
}
