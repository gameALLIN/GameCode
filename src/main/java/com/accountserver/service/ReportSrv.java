package com.accountserver.service;

import com.accountserver.ctrl.ReportCtrl;
import com.accountserver.ctrl.RoleCtrl;
import com.accountserver.errcode.AccountErrCode;
import com.alibaba.fastjson.JSONObject;
import com.webcore.code.Code;
import com.webcore.code.ErrorCode;
import com.webcore.exception.BussinessException;
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
		name = "report",
		urlPatterns = {"/report"}
)
public class ReportSrv extends HttpServlet {

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
			HttpUtil.responseCode(resp, AccountErrCode.EMPTY_REQUEST, requestId);
			return;
		}

		logger.info("report request body: {}", body);

		String accountId = parameters.getString("accountId");
		int serverId = parameters.getInteger("serverId");
		int roleId = parameters.getInteger("roleId");
		int level = parameters.getInteger("level");

		long playerId = 0;
		if (parameters.containsKey("playerId")) {
			playerId = parameters.getLong("playerId");
		}

		String ltid = "";
		if (parameters.containsKey("ltid")) {
			ltid = parameters.getString("ltid");
		}

		try {
			if (StringUtil.isNotEmpty(accountId)) {
				ErrorCode code = ReportCtrl.getInstance().report(accountId, serverId, roleId, level);
				HttpUtil.responseCode(resp, code, requestId);

				if (playerId > 0) {
					RoleCtrl.getInstance().update(accountId, ltid, playerId);
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
