package com.accountserver.errcode;

import com.webcore.code.ErrorCode;

/**
 * Error code definitions.
 */
public enum AccountErrCode implements ErrorCode {
	/**
	 * A platform id already binded, need unbind first.
	 */
	ALREADY_BINDED(-101, "duplicated bind"),

	NO_USER_FOUND(-102, "user not found"),

	EMPTY_REQUEST(-103, "empty request"),

	SIGNATURE_CHECK_FAILURE(-104, "signature failure"),

	BAD_PARAMETER(-105, "bad params"),

	BLACK_DEVICE(-106, "black device."),

	INVALID_DEVICE_FOR_PAY(-107, "banned device for pay"),

	INVALID_DEVICE_FOR_OTHER(-108, "invalid device"),

	BUSINESS_USER_DELETED(-109, "user deleted");

	private final int code;
	private final String description;

	AccountErrCode(int code, String description) {
		this.code = code;
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		return code + ": " + description;
	}
}
