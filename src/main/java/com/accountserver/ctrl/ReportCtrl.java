package com.accountserver.ctrl;

import com.accountserver.entity.AccountEntity;
import com.accountserver.errcode.AccountErrCode;
import com.webcore.code.Code;
import com.webcore.code.ErrorCode;
import com.webcore.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Report client information.
 */
public class ReportCtrl {
	private final Logger log = LoggerFactory.getLogger("Web");

	private static final ReportCtrl instance = new ReportCtrl();

	private ReportCtrl() {
	}

	public static ReportCtrl getInstance() {
		return instance;
	}

	public ErrorCode report(String accuntId, int serverId, int roleId, int level) {
		AccountEntity accountEntity = new AccountEntity();
		accountEntity.setAccountId(accuntId);
		accountEntity.fill();
		if (accountEntity.isHasDataInDb()) {
			AccountEntity.RoleInfo roleInfo = new AccountEntity.RoleInfo();
			roleInfo.setRoleId(roleId);
			roleInfo.setLevel(level);
			roleInfo.setServerId(serverId);
			roleInfo.setUpdatedAt(TimeUtil.getTimeInSeconds());

			accountEntity.updateRoleInfo(roleInfo);

			return Code.SUCCESS;
		}

		return AccountErrCode.NO_USER_FOUND;
	}

}
