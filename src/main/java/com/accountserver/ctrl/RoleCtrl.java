package com.accountserver.ctrl;

import com.accountserver.entity.RoleEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager game role information.
 *
 *
 */
public class RoleCtrl {
    private final Logger log = LoggerFactory.getLogger("Web");

    private static final RoleCtrl instance = new RoleCtrl();

    private RoleCtrl() {
    }

    public static RoleCtrl getInstance() {
        return instance;
    }

    public void update(String accuntId, String ltid, long roleId) {
        RoleEntity roleEntity = new RoleEntity(roleId);
        roleEntity.setLtid(ltid);
        roleEntity.setAccountId(accuntId);
        roleEntity.update();
    }

}
