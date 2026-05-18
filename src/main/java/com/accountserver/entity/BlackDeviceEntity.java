package com.accountserver.entity;

import com.webcore.data.persist.AbstractEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * BlackIPEntity represents the model of a blocked ltid.
 */
@Entity
@Table(name = "black_device")
public class BlackDeviceEntity extends AbstractEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;

	@Column(name = "device", unique = true)
	private String device;

	@Column(name = "reason_type", nullable = false)
	private int reasonType;

	@Column(name = "reason", nullable = false)
	private String reason;

	public BlackDeviceEntity() {
	}

	public BlackDeviceEntity(String device) {
		this.device = device;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public int getReasonType() {
		return reasonType;
	}

	public void setReasonType(int reasonType) {
		this.reasonType = reasonType;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public void copy(BlackDeviceEntity entity) {
		if (entity != null) {
			this.setDevice(entity.getDevice());
		}
	}

	@Override
	public String toString() {
		return "BlackDeviceEntity{" +
				"device='" + device + '\'' +
				'}';
	}
}

