package com.accountserver.service;

import com.accountserver.ctrl.BlackListCtrl;
import com.accountserver.ctrl.ServerListCtrl;
import com.alibaba.fastjson.JSONObject;
import com.webcore.config.Config;
import com.webcore.util.CollectionUtil;
import com.webcore.util.HttpUtil;
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
		name = "serverlist",
		urlPatterns = {"/servers"}
)
public class ServerListSrv extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger("Web");

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String requestId = HttpUtil.getHeaderRequestId(req);

		String whiteIpListStr = Config.getInstance().getProperty("whiteIpList");
		String whiteSidListStr = Config.getInstance().getProperty("whiteSidList");
		String[] ipListStr = whiteIpListStr.split(";");
		String remoteAddress = HttpUtil.getIpAddress(req);

		boolean isWhiteIp = false;
		if (CollectionUtil.contains(ipListStr, remoteAddress)) {
			isWhiteIp = true;
		}

		boolean isBlackAccount = false;
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("servers", ServerListCtrl.getInstance().getGameServers(isWhiteIp));
		jsonObject.put("isWhiteIp", isWhiteIp);
		jsonObject.put("checkCountry", Boolean.parseBoolean(Config.getInstance().getProperty("checkCountry")));

		if (BlackListCtrl.getInstance().isBlackIp(remoteAddress)) {
			isBlackAccount = true;
		}
		jsonObject.put("isBlackAccount", isBlackAccount);
		jsonObject.put("whiteSids", whiteSidListStr);

		HttpUtil.responseRaw(resp, jsonObject, requestId);
	}
}
