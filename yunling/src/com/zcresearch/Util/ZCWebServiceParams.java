package com.zcresearch.Util;

public class ZCWebServiceParams {
	public static final int OK = 0x00000000;

	public static final int HTTP_SUCCESS = 0x90000000;
	public static final int HTTP_START = 0x90000001;
	public static final int HTTP_FINISH = 0x90000002;
	public static final int HTTP_FAILED = 0x90000003;
	public static final int HTTP_THROWABLE = 0x90000009;

	//public static final String BASE_URL = "http://192.168.2.111:8080/server";
	public static final String BASE_URL = "http://115.29.41.68:7080/authen";
	public static final String VERIFY_URL = "/verify.json";
	public static final String LOAD_URL = "/loadReq.json";
	public static final String REPORT_URL = "/report_mb.htm";

	// public static final String LOGIN_URL = BASE_URL +
	// "/data/accounts/whoami.json";
	// public static final String ACCOUNT_URL = BASE_URL +
	// "/data/accounts/cardList";
	// public static final String TRANSFER_URL = BASE_URL +
	// "/data/trade/transferTrade.json";
	// public static final String DEPOSITE_UR = BASE_URL +
	// "/data/trade/depositeTrade.json";
	// public static final String FINANCIAL_URL = BASE_URL +
	// "/data/trade/financialLoad.json";
	// public static final String TRADEQUERY_URL = BASE_URL +
	// "/data/query/tradeQuery";

}