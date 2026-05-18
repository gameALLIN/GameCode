package com.accountserver.entity;

import com.webcore.data.persist.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * BlackIPEntity represents the model of a blocked ltid.
 */
@Entity
@Table(name = "black_ltid")
public class BlackLtidEntity extends AbstractEntity {
	@Id
	@Column(name = "ltid", unique = true)
	private String ltid;

	public BlackLtidEntity() {
	}

	public BlackLtidEntity(String ltid) {
		this.ltid = ltid;
	}

	public String getLtid() {
		return ltid;
	}

	public void setLtid(String ltid) {
		this.ltid = ltid;
	}

	public void copy(BlackLtidEntity entity) {
		if (entity != null) {
			this.setLtid(entity.getLtid());
		}
	}

	@Override
	public String toString() {
		return "BlackLtidEntity{" +
				"ltid='" + ltid + '\'' +
				'}';
	}
}

