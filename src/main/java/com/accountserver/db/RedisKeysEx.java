package com.accountserver.db;

import com.accountserver.definitions.CredentialType;
import com.webcore.config.Config;
import com.webcore.data.cache.RedisKeys;
import com.webcore.util.StringUtil;

public class RedisKeysEx extends RedisKeys {
    public static final String ROLE_INFO = "role";

    public static String getPlatformKey(CredentialType platform, String platformId) {
        return generateKey(platform.type(), platformId);
    }

    public static String getRoleInfoKey(String accountId) {
        return generateKey(ROLE_INFO, accountId);
    }

    public static String getDeviceKey(String deviceId) {
        return generateKey(CredentialType.DEVICE_ID.type(), deviceId);
    }

    public static String getLtidKey(String deviceId) {
        return generateKey(CredentialType.LT_ID.type(), deviceId);
    }

    public static String getAccountKey(String accountId) {
        return generateKey(CredentialType.ACCOUNT_ID.type(), accountId);
    }

    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * A {@code null} separator is the same as an empty String ("").
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     * @param params
     * @return
     */
    public static String generateKey(String... params) {
        String[] redisKeyPrefix = {Config.getInstance().getProperty("redisKeyPrefix")};
        return StringUtil.join(concat(redisKeyPrefix, params), ":");
    }
}
