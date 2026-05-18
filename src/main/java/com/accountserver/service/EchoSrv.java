package com.accountserver.service;

import com.alibaba.fastjson.JSONObject;
import com.webcore.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "echo",
        urlPatterns = {"/demo/echo"}
)
public class EchoSrv extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger("Web");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject response = new JSONObject();
        response.put("msg", "hello, this is account server.");
        HttpUtil.response(resp, response);
    }
}
