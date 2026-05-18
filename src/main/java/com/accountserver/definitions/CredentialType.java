package com.accountserver.definitions;

/**
 * CredentialType represents the kind of credential.
 */
public enum CredentialType {
	/**
	 * Device Id as credential.
	 */
	LT_ID("ltid"),
	/**
	 * Device Id as credential.
	 */
	DEVICE_ID("device_id"),

	/**
	 * Account Id as credential.
	 */
	ACCOUNT_ID("account_id"),

	/**
	 * Facebook token as credential.
	 */
	FACEBOOK_TOKEN("facebook_token"),

	/**
	 * Facebook id  as credential.
	 */
	FACEBOOK_ID("facebook_id"),

	/**
	 * apple appstore ID
	 */
	APP_STORE_ID("app_store_id"),

	/**
	 * googleplay ID
	 */
	GOOGLE_PLAY_ID("google_play_id");

	private final String type;

	CredentialType(String type) {
		this.type = type;
	}

	public final String type() {
		return this.type;
	}
}
