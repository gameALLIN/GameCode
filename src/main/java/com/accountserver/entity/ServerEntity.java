package com.accountserver.entity;

import com.alibaba.fastjson.JSONObject;
import com.webcore.data.persist.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * ServerEntity represents the model of server list.
 */
@Entity
@Table(name = "server_list")
public class ServerEntity extends AbstractEntity {
	@Id
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "name")
	private String name;

	@Column(name = "host")
	private String host;

	@Column(name = "port")
	private int port;

	@Column(name = "iap_port")
	private int iapPort;

	@Column(name = "gm_port")
	private int gmPort;

	@Column(name = "status")
	private int status;

	@Column(name = "order")
	private int order;

	@Column(name = "is_hide")
	private boolean isHide;

	@Column(name = "is_maintained")
	private boolean isMaintained;

	@Column(name = "open_timestamp")
	protected int openTimestamp;

	@Column(name = "proxy_ip")
	private String proxyIp;

	@Column(name = "proxy_port")
	private Integer proxyPort;

	@Column(name = "proxy_switch")
	private boolean proxySwitch;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHost() {
		if (this.isProxySwitch()) {
			return this.getProxyIp();
		}
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		if (this.isProxySwitch()) {
			return this.getProxyPort();
		}

		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getGmPort() {
		return gmPort;
	}

	public void setGmPort(int gmPort) {
		this.gmPort = gmPort;
	}

	public int getIapPort() {
		return iapPort;
	}

	public void setIapPort(int iapPort) {
		this.iapPort = iapPort;
	}

	public boolean isHide() {
		return isHide;
	}

	public void setHide(boolean hide) {
		isHide = hide;
	}

	public int getOpenTimestamp() {
		return openTimestamp;
	}

	public void setOpenTimestamp(int openTimestamp) {
		this.openTimestamp = openTimestamp;
	}

	public String getProxyIp() {
		return proxyIp;
	}

	public void setProxyIp(String proxyIp) {
		this.proxyIp = proxyIp;
	}

	public Integer getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}

	public boolean isProxySwitch() {
		return proxySwitch;
	}

	public void setProxySwitch(boolean proxySwitch) {
		this.proxySwitch = proxySwitch;
	}

	public JSONObject toJSON(boolean isWhiteIp) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", this.getId());
		jsonObject.put("name", this.getName());
		jsonObject.put("host", this.getHost());
		jsonObject.put("port", this.getPort());
		if (isMaintained && !isWhiteIp) {
			jsonObject.put("state", 4);
		} else {
			jsonObject.put("state", this.getStatus());
		}
		jsonObject.put("order", this.getOrder());

		return jsonObject;
	}
}

