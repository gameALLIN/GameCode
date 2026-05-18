package com.accountserver.entity;

import com.webcore.data.persist.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * RoleEntity represents the model of a role in one game server.
 */
@Entity
@Table(name = "role")
public class RoleEntity extends AbstractEntity {
	/**
	 * The id of role, generates by game server.
	 */
	@Id
	@Column(name = "id", unique = true)
	private long id;

	@Column(name = "account_id", nullable = false)
	private String accountId;

	@Column(name = "ltid", nullable = false)
	private String ltid;

	public RoleEntity() {
	}

	public RoleEntity(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getLtid() {
		return ltid;
	}

	public void setLtid(String ltid) {
		this.ltid = ltid;
	}

	public void copy(RoleEntity entity) {
		if (entity != null) {
			this.setAccountId(entity.getAccountId());
			this.setLtid(entity.getLtid());
		}
	}

	@Override
	public String toString() {
		return "Role{" +
				", roleId='" + this.getId() + '\'' +
				", accountId='" + accountId + '\'' +
				", ltid='" + ltid + '\'' +
				", createAt=" + createAt +
				", updateAt=" + updateAt +
				'}';
	}
}

