package com.accountserver.service;

import com.accountserver.ctrl.BlackListCtrl;
import com.accountserver.ctrl.LoginCtrl;
import com.accountserver.ctrl.SupervisorListCtrl;
import com.accountserver.entity.BlackDeviceEntity;
import com.accountserver.errcode.AccountErrCode;
import com.alibaba.fastjson.JSONObject;
import com.webcore.code.Code;
import com.webcore.config.Config;
import com.webcore.exception.BussinessException;
import com.webcore.util.*;
import com.webcore.util.httpnew.KeyValuePair;
import com.webcore.util.httpnew.RequestSync;
import com.webcore.util.httpnew.ResponseCallBack;
import org.apache.http.HttpResponse;
import org.apache.http.client.utils.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Login servlet handles login & create account request.
 */
@WebServlet(
		name = "login",
		urlPatterns = {"/login"}
)
public class LoginSrv extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger("Web");

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String body = HttpUtil.extractBody(req);
		String requestId = HttpUtil.getHeaderRequestId(req);

		logger.info("login request body: {}", body);

		JSONObject parameters = HttpUtil.parseJSONBody(body);
		if (parameters == null) {
			HttpUtil.responseCode(resp, AccountErrCode.EMPTY_REQUEST, requestId);
			return;
		}

		String remoteAddress = HttpUtil.getIpAddress(req);
		String deviceId = parameters.getString("deviceId");

		// 检测是否监管设备或IP，只有监管设备或IP才能登录
		if (SupervisorListCtrl.getInstance().isEnable() && this.notSupervised(remoteAddress, deviceId)) {
			HttpUtil.responseCode(resp, AccountErrCode.SIGNATURE_CHECK_FAILURE, requestId);
			return;
		}

		// if passed platform informations, will bind platform information with device id.
		// if not, just use device id as the only credential.
		String platform = parameters.getString("platform");
		String platformId = parameters.getString("platformId");

		String device = "";
		if (parameters.containsKey("device")) {
			device = parameters.getString("device");
		}

		String device_os = "";
		if (parameters.containsKey("device_os")) {
			device_os = parameters.getString("device_os");
		}

		String carrierName = "";
		if (parameters.containsKey("carrierName")) {
			carrierName = parameters.getString("carrierName");
		}

		logger.info("device: {}, device_os:{}, carrierName:{}", device, device_os, carrierName);

		String allowEmptyDevice = Config.getInstance().getProperty("allowEmptyDevice");
		boolean allowEmpty = Boolean.parseBoolean(allowEmptyDevice);
		if (!allowEmpty && (StringUtil.isEmpty(device) || StringUtil.isEmpty(deviceId))) {
			HttpUtil.responseCode(resp, AccountErrCode.SIGNATURE_CHECK_FAILURE, requestId);
			return;
		}

		String blackDevice = Config.getInstance().getProperty("blackDevice");
		if (StringUtil.isNotEmpty(device) && StringUtil.isNotEmpty(blackDevice)) {
			if (blackDevice.equals(device)) {
				HttpUtil.responseCode(resp, AccountErrCode.SIGNATURE_CHECK_FAILURE, requestId);
				return;
			}
		}

		boolean isBlackDevice = false;
		BlackDeviceEntity blackDeviceEntity = null;
		if (BlackListCtrl.getInstance().isBlackDevice(deviceId)) {
			blackDeviceEntity = BlackListCtrl.getInstance().getBlackDeviceEntity(deviceId);
			isBlackDevice = true;
		}

		String userId = null;
		String authId = null;
		if (parameters.containsKey("userId")) {
			userId = parameters.getString("userId");
		}
		if (parameters.containsKey("authId")) {
			authId = parameters.getString("authId");
		}

		String invitationCode = parameters.getString("invitationCode");

		String account = parameters.getString("account");
		String password = parameters.getString("password");
		if ((account == null || account.isEmpty()) || (password == null || password.isEmpty())) {
			HttpUtil.responseCode(resp, AccountErrCode.BAD_PARAMETER, requestId);
			return;
		}

		try {
			if (StringUtil.isNotEmpty(deviceId)) {
				if (!EncryptUtil.check(req, body)) {
					HttpUtil.responseCode(resp, AccountErrCode.SIGNATURE_CHECK_FAILURE, requestId);
					return;
				}

				JSONObject accountInfo = LoginCtrl.getInstance().login(deviceId, platform, platformId, userId, authId, remoteAddress, carrierName, device, device_os, invitationCode);
				if (accountInfo != null && !CommonUtil.isEmptyMap(accountInfo)) {
					if (accountInfo.containsKey("isSuccess") && !accountInfo.getBoolean("isSuccess")) {
						if (accountInfo.containsKey("errCode") && accountInfo.get("errCode") != null && accountInfo.get("errCode") == AccountErrCode.BUSINESS_USER_DELETED) {
							HttpUtil.responseCode(resp, AccountErrCode.BUSINESS_USER_DELETED, requestId);
						} else {
							HttpUtil.responseCode(resp, Code.INVALID_TOKEN, requestId);
						}
						return;
					}

					accountInfo.put("token", EncryptUtil.md5Crypt(userId + accountInfo.getString("accountId")));
					accountInfo.put("timestamp", TimeUtil.getTimeInSeconds());
					accountInfo.put("timezone", TimeUtil.getTimeZoneOffset());
					accountInfo.put("isBlackDevice", isBlackDevice);
					if (blackDeviceEntity != null) {
						accountInfo.put("blockReason", AccountErrCode.INVALID_DEVICE_FOR_OTHER.getCode());
					}

					boolean isTesterUser = false;
					String whiteIpListStr = Config.getInstance().getProperty("whiteIpList");
					String[] ipListStr = whiteIpListStr.split(";");
					if (CollectionUtil.contains(ipListStr, remoteAddress)) {
						isTesterUser = true;
					}
					accountInfo.put("isTesterUser", isTesterUser);

					if (Boolean.parseBoolean(Config.getInstance().getProperty("local"))) {
						HttpUtil.response(resp, accountInfo, requestId);
					} else {
						platformVerifyLogin(account, password, accountInfo, resp, requestId);
					}
				} else {
					HttpUtil.responseCode(resp, Code.INVALID_TOKEN, requestId);
				}
			} else {
				HttpUtil.responseCode(resp, AccountErrCode.BAD_PARAMETER, requestId);
			}

		} catch (Exception e) {
			BussinessException.catchEx(e);
			HttpUtil.responseCode(resp, Code.SERVER_INTERNAL_ERROR, requestId);
		}
	}

	private boolean notSupervised(String remoteAddress, String deviceId) {
		return !SupervisorListCtrl.getInstance().isSupervised(remoteAddress, deviceId);
	}

	private boolean platformVerifyLogin(String account, String password, JSONObject accountInfo, HttpServletResponse resp, String requestId) throws IOException {
		logger.info("平台验证account:{}, password:{}, accountInfo:{}, requestId：{}", account, password, accountInfo, requestId);
		String loginVerificationUrl = Config.getInstance().getProperty("login_verification_url");
		RequestSync loginVerification = new RequestSync() {
			@Override
			public void execute() throws Exception {
				List<KeyValuePair> urlParams = new ArrayList<KeyValuePair>() {
					{
						add(new KeyValuePair("username", account));
						add(new KeyValuePair("password", password));
					}
				};
				syncNormalPost(loginVerificationUrl, urlParams, responseCallBack);
			}

			ResponseCallBack responseCallBack = new ResponseCallBack<HttpResponse>() {
				@Override
				public void completed(HttpResponse httpResponse) {
					try {
						JSONObject jsonObject = getJSONObject(httpResponse);
						logger.info("返回状态码{}, 返回内容{}", jsonObject.get("code"), jsonObject);
						if (jsonObject.getIntValue("code") != 0) {
							HttpUtil.responseCode(resp, Code.LOGIN_VERIFICATION_FAILED, requestId);
							return;
                        }
						HttpUtil.response(resp, accountInfo, requestId);
					} catch (Exception e) {
						logger.error("账号验证:{}", e);
					}
					finally {
						HttpClientUtils.closeQuietly(httpResponse);
					}
				}

				@Override
				public void failed(Exception e) {
					logger.error("失败:{}", e);
				}

				@Override
				public void cancelled() {
					logger.error("取消");
				}
			};
		};

		try {
			loginVerification.execute();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

}
