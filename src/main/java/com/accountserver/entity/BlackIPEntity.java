package com.accountserver.entity;

import com.webcore.data.persist.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * BlackIPEntity represents the model of a blocked IP address.
 */
@Entity
@Table(name = "black_ip")
public class BlackIPEntity extends AbstractEntity {
	@Id
	@Column(name = "ip", unique = true)
	private String ipAddr;

	public BlackIPEntity() {
	}

	public BlackIPEntity(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public void copy(BlackIPEntity entity) {
		if (entity != null) {
			this.setIpAddr(entity.getIpAddr());
		}
	}

	@Override
	public String toString() {
		return "BlackIPEntity{" +
				"ipAddr='" + ipAddr + '\'' +
				'}';
	}
}

