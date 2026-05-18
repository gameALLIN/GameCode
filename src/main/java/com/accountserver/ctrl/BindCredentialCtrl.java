package com.accountserver.ctrl;

import com.accountserver.definitions.CredentialType;
import com.accountserver.entity.AccountEntity;
import com.accountserver.errcode.AccountErrCode;
import com.webcore.code.Code;
import com.webcore.code.ErrorCode;
import com.webcore.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bind credential with account.
 */
public class BindCredentialCtrl {
	private final Logger log = LoggerFactory.getLogger("Web");

	private static final BindCredentialCtrl instance = new BindCredentialCtrl();

	private BindCredentialCtrl() {
	}

	public static BindCredentialCtrl getInstance() {
		return instance;
	}

	public ErrorCode bind(String accuntId, String platform, String platformId) {
		AccountEntity accountEntity = new AccountEntity();
		accountEntity.setAccountId(accuntId);
		accountEntity.fill();
		if (accountEntity.isHasDataInDb()) {
			CredentialType credentialType = CredentialType.valueOf(platform);
			switch (credentialType) {
				case FACEBOOK_ID:
					if (StringUtil.isEmpty(accountEntity.getFacebookId())) {
						accountEntity.setFacebookId(platformId);
						accountEntity.update();
					} else {
						// already bind with facebook id. need unbind first.
						return AccountErrCode.ALREADY_BINDED;
					}
					break;
				default:
					log.error("ignore unknown credential type: {}", platform);
					return AccountErrCode.BAD_PARAMETER;
			}
			return Code.SUCCESS;
		}

		return AccountErrCode.NO_USER_FOUND;
	}

}
