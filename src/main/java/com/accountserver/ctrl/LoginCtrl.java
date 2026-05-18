package com.accountserver.ctrl;

import com.accountserver.definitions.CredentialType;
import com.accountserver.entity.AccountEntity;
import com.accountserver.errcode.AccountErrCode;
import com.alibaba.fastjson.JSONObject;
import com.webcore.code.Code;
import com.webcore.config.Config;
import com.webcore.data.persist.DbManager;
import com.webcore.util.CollectionUtil;
import com.webcore.util.CommonUtil;
import com.webcore.util.IdGenerator;
import com.webcore.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Login controller
 */
public class LoginCtrl {
	private static final LoginCtrl instance = new LoginCtrl();
	private final Logger logger = LoggerFactory.getLogger("Web");

	private LoginCtrl() {
	}

	public static LoginCtrl getInstance() {
		return instance;
	}

	/**
	 * Processes login logic:
	 * if has an account already, will return the account information.
	 * if not, will create a new account by credential information:
	 * a. if passed platform informations, will bind platform information with device id.
	 * b. if not, just use device id as the only credential.
	 *
	 * @param deviceId   the device id.
	 * @param platform   the platform, can be Facebook, GooglePlay, Appstore.
	 * @param platformId the id of the platform, maybe null.
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public JSONObject login(String deviceId, String platform, String platformId, String userId, String authId, String remoteAddr, String carrierName, String device, String device_os, String invitationCode) {
		AccountEntity accountInfo = this.loginWithDevice(deviceId);
		if (accountInfo == null) {
			accountInfo = this.createNewAccount(deviceId, platform, platformId, remoteAddr);
		} else {
			return accountInfo.toJSON();
		}
		return accountInfo.toJSON();
	}

	private void bindWithPlatform(AccountEntity accountEntity, CredentialType platform, String platformId) {
		switch (platform) {
			case FACEBOOK_ID:
				accountEntity.setFacebookId(platformId);
				break;
			default:
		}
	}

	public AccountEntity loginWithDevice(String deviceId) {
		AccountEntity accountEntity = new AccountEntity();
		accountEntity.setDeviceId(deviceId);
		accountEntity.setTester(false);
		accountEntity.fill();
		if (accountEntity.isHasDataInDb()) {
			return accountEntity;
		}
		return null;
	}

	public long loadDeviceIdAccountSize(String deviceId) {
		String sql = String.format(
				"select count(1) as num from account where device_id= \"%s\"", deviceId);
		List<Object[]> resultList = DbManager.getInstance().sqlQuery(sql);
		if (resultList != null) {
			Object num = resultList.get(0);
			if (num != null) {
				return Integer.valueOf(String.valueOf(num));
			}
		}

		return 0;
	}

	public int loadRegisterIpAccountSize(String registerIP) {
		String sql = String.format(
				"select count(1) as num from account where register_ip= \"%s\"", registerIP);
		List<Object[]> resultList = DbManager.getInstance().sqlQuery(sql);
		if (resultList != null) {
			Object num = resultList.get(0);
			if (num != null) {
				return Integer.valueOf(String.valueOf(num));
			}
		}

		return 0;
	}

	public AccountEntity loginWithLtid(String ltid) {
		AccountEntity accountEntity = new AccountEntity();
		accountEntity.setLtid(ltid);
		accountEntity.fill();
		if (accountEntity.isHasDataInDb()) {
			return accountEntity;
		}
		return null;
	}

	private AccountEntity createNewAccount(String deviceId, String platform, String platformId, String remoteAddr) {
		AccountEntity accountInfo = new AccountEntity();
		String accountId = IdGenerator.generate(deviceId);
		if (accountId != null) {
			accountInfo.setAccountId(accountId);
			accountInfo.setDeviceId(deviceId);
			accountInfo.setAgreePrivacyPolicy(true);
			accountInfo.setTester(false);
			accountInfo.setRegisterIp(remoteAddr);
			if (StringUtil.isNotEmpty(platform) && StringUtil.isNotEmpty(platformId)) {
				this.bindWithPlatform(accountInfo, CredentialType.valueOf(platform), platformId);
			}
			accountInfo.create();
			return accountInfo;
		}

		return accountInfo;
	}

	public UCVerifyResult ucVerify(String userId, String authId) {
		return null;
	}

	public static class UCVerifyResult {
		private boolean isSuccess;
		private AccountErrCode errCode;

		public UCVerifyResult(boolean isSuccess, String errCode) {
			this.isSuccess = isSuccess;
			if ("BUSINESS_USER_DELETED".equals(errCode)) {
				this.errCode = AccountErrCode.BUSINESS_USER_DELETED;
			}
		}

		public boolean isSuccess() {
			return isSuccess;
		}

		public void setSuccess(boolean success) {
			isSuccess = success;
		}

		public AccountErrCode getErrCode() {
			return errCode;
		}

		public void setErrCode(AccountErrCode errCode) {
			this.errCode = errCode;
		}

		public JSONObject toJson() {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("isSuccess", this.isSuccess);
			jsonObject.put("errCode", this.errCode);
			return jsonObject;
		}
	}
}
