package com.accountserver.service;

import com.accountserver.ctrl.BindCredentialCtrl;
import com.accountserver.errcode.AccountErrCode;
import com.alibaba.fastjson.JSONObject;
import com.webcore.code.Code;
import com.webcore.code.ErrorCode;
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
 * Bind credential with account.
 */
@WebServlet(
		name = "bind",
		urlPatterns = {"/bind"}
)
public class BindCredentialSrv extends HttpServlet {
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

		if (parameters == null) {
			parameters = new JSONObject();
			parameters.put("deviceId", "1");
		}

		if (parameters == null) {
			HttpUtil.responseCode(resp, AccountErrCode.EMPTY_REQUEST, requestId);
			return;
		}

		logger.info("login request body: {}", body);

		String accountId = parameters.getString("accountId");
		String platform = parameters.getString("platform");
		String platformId = parameters.getString("platformId");

		try {
			if (StringUtil.isNotEmpty(accountId) && StringUtil.isNotEmpty(platform) && StringUtil.isNotEmpty(platformId)) {
				if (!EncryptUtil.check(req, body)) {
					HttpUtil.responseCode(resp, AccountErrCode.SIGNATURE_CHECK_FAILURE, requestId);
					return;
				}

				ErrorCode accountInfo = BindCredentialCtrl.getInstance().bind(accountId, platform, platformId);
				HttpUtil.responseCode(resp, accountInfo, requestId);
			} else {
				HttpUtil.responseCode(resp, AccountErrCode.BAD_PARAMETER, requestId);
			}

		} catch (Exception e) {
			BussinessException.catchEx(e);
			HttpUtil.responseCode(resp, Code.SERVER_INTERNAL_ERROR, requestId);
		}
	}
}
