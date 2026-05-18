package com.accountserver.service;

import com.accountserver.ctrl.LoginCtrl;
import com.accountserver.entity.AccountEntity;
import com.accountserver.errcode.AccountErrCode;
import com.alibaba.fastjson.JSONObject;
import com.webcore.code.Code;
import com.webcore.exception.BussinessException;
import com.webcore.util.EncryptUtil;
import com.webcore.util.HttpUtil;
import com.webcore.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Login servlet handles login & create account request.
 */
@WebServlet(
		name = "updatePrivacyPolicy",
		urlPatterns = {"/updatePrivacyPolicy"}
)
public class UpdatePrivacyPolicySrv extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger("Web");

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String body = HttpUtil.extractBody(req);
		String requestId = HttpUtil.getHeaderRequestId(req);

		JSONObject parameters = HttpUtil.parseJSONBody(body);
		logger.info("updatePrivacyPolicy request body: {}", body);

		String ltid = null;
		if (parameters.containsKey("ltid")) {
			ltid = parameters.getString("ltid");
		}

		String deviceId = parameters.getString("deviceId");
		Boolean agreePrivacyPolicy = null;
		String privacyPolicyVersion = null;

		if (parameters.containsKey("agreePrivacyPolicy")) {
			agreePrivacyPolicy = parameters.getBoolean("agreePrivacyPolicy");
		}

		if (parameters.containsKey("privacyPolicyVersion")) {
			privacyPolicyVersion = parameters.getString("privacyPolicyVersion");
		}

		try {
			if (StringUtil.isNotEmpty(deviceId)) {
				if (!EncryptUtil.check(req, body)) {
					HttpUtil.responseCode(resp, AccountErrCode.SIGNATURE_CHECK_FAILURE, requestId);
					return;
				}

				AccountEntity accountInfo;
				if (StringUtil.isNotEmpty(ltid)) {
					accountInfo = LoginCtrl.getInstance().loginWithLtid(ltid);
				} else {
					accountInfo = LoginCtrl.getInstance().loginWithDevice(deviceId);
				}

				if (accountInfo != null) {
					accountInfo.setPrivacyPolicyVersion(privacyPolicyVersion);
					accountInfo.setAgreePrivacyPolicy(agreePrivacyPolicy);
					accountInfo.update();
					HttpUtil.response(resp, accountInfo.toJSON(), requestId);

				} else {
					HttpUtil.responseCode(resp, Code.INVALID_ACCOUNT, requestId);
				}
			} else {
				HttpUtil.responseCode(resp, AccountErrCode.BAD_PARAMETER, requestId);
			}
		} catch (Exception e) {
			BussinessException.catchEx(e);
			HttpUtil.responseCode(resp, Code.SERVER_INTERNAL_ERROR, requestId);
		}
	}
}
