package com.accountserver.entity;

import com.accountserver.db.RedisKeysEx;
import com.accountserver.definitions.CredentialType;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.webcore.config.Config;
import com.webcore.data.cache.RedisClientPool;
import com.webcore.data.persist.AbstractEntity;
import com.webcore.data.persist.DbManager;
import com.webcore.util.StringUtil;
import redis.clients.jedis.Jedis;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Account represents the model of a user's all kinds of credentials.
 */
@Entity
@Table(name = "account")
public class AccountEntity extends AbstractEntity {
	/**
	 * The account id of user,generates by client or server.
	 * Client generates an account id if it can't connect the account server,
	 * otherwise, the account server will generate it.
	 */
	@Id
	@Column(name = "account_id", nullable = false)
	private String accountId;

	/**
	 * The id of device which provides by client.
	 */
	@Column(name = "device_id", unique = false, nullable = false)
	private String deviceId;

	/**
	 * The facebook id of user in this game.
	 */
	@Column(name = "facebook_id", nullable = true)
	private String facebookId;

	@Column(name = "role_info", nullable = true)
	private String roleInfoStr;

	@Column(name = "ltid", nullable = false)
	private String ltid;

	@Column(name = "agree_privacy_policy", nullable = true)
	private Boolean agreePrivacyPolicy;

	@Column(name = "privacy_policy_version", nullable = true)
	private String privacyPolicyVersion;

	@Column(name = "is_tester", nullable = true)
	private Boolean isTester;

	@Column(name = "register_ip", nullable = true)
	private String registerIp;

	@Transient
	private boolean hasDataInDb;

	@Transient
	private Map<Integer, RoleInfo> roleInfoMap = new HashMap<>();

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public void setRoleInfo(String roleInfo) {
		this.roleInfoStr = roleInfo;
	}

	public Boolean getAgreePrivacyPolicy() {
		return agreePrivacyPolicy;
	}

	public void setAgreePrivacyPolicy(Boolean agreePrivacyPolicy) {
		this.agreePrivacyPolicy = agreePrivacyPolicy;
	}

	public String getPrivacyPolicyVersion() {
		return privacyPolicyVersion;
	}

	public void setPrivacyPolicyVersion(String privacyPolicyVersion) {
		this.privacyPolicyVersion = privacyPolicyVersion;
	}

	public String getRegisterIp() {
		return registerIp;
	}

	public void setRegisterIp(String registerIp) {
		this.registerIp = registerIp;
	}

	public String getLtid() {
		return ltid;
	}

	public void setLtid(String ltid) {
		this.ltid = ltid;
	}

	public Boolean isTester() {
		return isTester;
	}

	public void setTester(boolean tester) {
		isTester = tester;
	}

	@Override
	public boolean create() {
		if (super.create()) {
			this.updateCache();
			return true;
		}
		return false;
	}

	@Override
	public boolean update() {
		if (super.update()) {
			this.updateCache();
			return true;
		}

		return false;
	}

	private boolean useCache(Jedis client) {
		return client != null && Boolean.parseBoolean(Config.getInstance().getProperty("useCache"));
	}

	@Override
	public void fill() {
		Map<String, String> result = null;

		try (Jedis client = RedisClientPool.getInstance().getClient()) {
			if (StringUtil.isNotEmpty(this.getAccountId())) {
				if (useCache(client)) {
					result = client.hgetAll(this.getAccountId());
				}

				if (result == null || result.size() == 0) {
					AccountEntity entity = DbManager.getInstance().fetch("from AccountEntity where account_id=?", this.getAccountId());
					this.copy(entity);
				}

			} else if (StringUtil.isNotEmpty(this.getLtid())) {
				if (useCache(client)) {
					String accountId = client.get(RedisKeysEx.getLtidKey(this.getLtid()));
					if (accountId != null) {
						result = client.hgetAll(RedisKeysEx.getAccountKey(this.getAccountId()));
					}
				}

				if (result == null || result.size() == 0) {
					AccountEntity entity = DbManager.getInstance().fetch("from AccountEntity where ltid=?", this.getLtid());
					this.copy(entity);
				}
			} else if (StringUtil.isNotEmpty(this.getDeviceId())) {
				if (useCache(client)) {
					String accountId = client.get(RedisKeysEx.getDeviceKey(this.getDeviceId()));
					if (accountId != null) {
						result = client.hgetAll(RedisKeysEx.getAccountKey(this.getAccountId()));
					}
				}

				if (result == null || result.size() == 0) {
					AccountEntity entity = DbManager.getInstance().fetch("from AccountEntity where device_id=? and (ltid is NULL or ltid='')", this.getDeviceId());
					this.copy(entity);
				}

			} else if (StringUtil.isNotEmpty(this.getFacebookId())) {
				if (useCache(client)) {
					String accountId = client.get(RedisKeysEx.getPlatformKey(CredentialType.FACEBOOK_ID, this.getFacebookId()));

					if (accountId != null) {
						result = client.hgetAll(this.getAccountId());
					}
				}

				if (result == null || result.size() == 0) {
					AccountEntity entity = DbManager.getInstance().fetch("from AccountEntity where device_id=?", this.getDeviceId());
					this.copy(entity);
				}
			}

			if (result != null && result.size() > 0) {
				this.setAccountId(result.get(CredentialType.ACCOUNT_ID.type()));
				this.setDeviceId(result.get(CredentialType.DEVICE_ID.type()));
				this.setFacebookId(result.get(CredentialType.FACEBOOK_ID.type()));
				this.setRoleInfo(result.get(RedisKeysEx.ROLE_INFO));

				this.hasDataInDb = true;
			} else if (this.hasDataInDb) {
				updateCache();
			}

			this.parse();
		}
	}

	private void parse() {
		if (StringUtil.isNotEmpty(this.roleInfoStr)) {
			this.roleInfoMap = JSON.parseObject(
					this.roleInfoStr, new TypeReference<Map<Integer, RoleInfo>>() {
					});
		}
	}

	public String getRoleInfo() {
		return this.roleInfoStr;
	}

	public void updateRoleInfo(RoleInfo roleInfo) {
		this.roleInfoMap.put(roleInfo.serverId, roleInfo);
		this.roleInfoStr = JSON.toJSONString(this.roleInfoMap);

		this.update();
	}

	private void updateCache() {
		try (Jedis client = RedisClientPool.getInstance().getClient()) {
			if (useCache(client)) {
				if (StringUtil.isNotEmpty(this.getAccountId()) && StringUtil.isNotEmpty(this.getDeviceId())) {

					client.hset(RedisKeysEx.getAccountKey(this.getAccountId()), CredentialType.DEVICE_ID.type(), this.getDeviceId());

					if (StringUtil.isNotEmpty(this.getFacebookId())) {
						client.hset(RedisKeysEx.getAccountKey(this.getAccountId()), CredentialType.FACEBOOK_ID.type(), this.getFacebookId());
						client.set(RedisKeysEx.getPlatformKey(CredentialType.FACEBOOK_ID, this.getFacebookId()), this.getAccountId());
					}

					if (StringUtil.isNotEmpty(this.getRoleInfo())) {
						client.set(RedisKeysEx.getRoleInfoKey(this.getAccountId()), this.getRoleInfo());
					}
				}
			}

		}
	}

	public void copy(AccountEntity entity) {
		if (entity != null) {
			this.hasDataInDb = true;
			this.setAccountId(entity.getAccountId());
			this.setDeviceId(entity.getDeviceId());
			this.setFacebookId(entity.getFacebookId());
			this.setRoleInfo(entity.getRoleInfo());
			this.setAgreePrivacyPolicy(entity.getAgreePrivacyPolicy());
			this.setPrivacyPolicyVersion(entity.getPrivacyPolicyVersion());
			this.setLtid(entity.getLtid());
			this.setTester(entity.isTester());
			this.setRegisterIp(entity.getRegisterIp());
		}
	}

	public boolean isHasDataInDb() {
		return hasDataInDb;
	}

	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("accountId", this.getAccountId());
		jsonObject.put("deviceId", this.getDeviceId());
		jsonObject.put("facebookId", this.getFacebookId());
		jsonObject.put("role", this.roleInfoMap.values());
		jsonObject.put("ltid", this.getLtid());
		jsonObject.put("agreePrivacyPolicy", true);
		if (this.isTester() != null && this.isTester()) {
			jsonObject.put("isTester", this.isTester());
		}
		if (StringUtil.isNotEmpty(this.getPrivacyPolicyVersion())) {
			jsonObject.put("privacyPolicyVersion", this.getPrivacyPolicyVersion());
		}

		return jsonObject;
	}

	@Override
	public String toString() {
		return "Account{" +
				", deviceId='" + deviceId + '\'' +
				", accountId='" + accountId + '\'' +
				", facebookId='" + facebookId + '\'' +
				", ltid='" + ltid + '\'' +
				", createAt=" + createAt +
				", updateAt=" + updateAt +
				'}';
	}

	public static class RoleInfo {
		@JSONField(name = "roleId")
		int roleId;

		@JSONField(name = "level")
		int level;

		@JSONField(name = "serverId")
		int serverId;

		@JSONField(name = "updatedAt")
		long updatedAt;

		public int getRoleId() {
			return roleId;
		}

		public void setRoleId(int roleId) {
			this.roleId = roleId;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public int getServerId() {
			return serverId;
		}

		public void setServerId(int serverId) {
			this.serverId = serverId;
		}

		public long getUpdatedAt() {
			return updatedAt;
		}

		public void setUpdatedAt(long updatedAt) {
			this.updatedAt = updatedAt;
		}
	}
}

