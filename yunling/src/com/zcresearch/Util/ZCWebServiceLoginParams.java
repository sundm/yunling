package com.zcresearch.Util;

public class ZCWebServiceLoginParams {
	private String userNameString;
	private String passWordString;

	public ZCWebServiceLoginParams() {
		init();
	}

	public void setLoginParams(String userName, String passWord) {
		userNameString = userName;
		passWordString = passWord;
	}

	public String getUserName() {
		return userNameString;
	}

	public String getPassWord() {
		return passWordString;
	}

	private void init() {
		userNameString = "";
		passWordString = "";
	}

}
