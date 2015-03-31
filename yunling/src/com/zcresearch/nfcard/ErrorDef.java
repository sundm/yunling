package com.zcresearch.nfcard;

public class ErrorDef {
	public static final int OK = 0x00000000;
	public static final int ERR_KEY_VALUE_LENGTH = 0x00000001;
	public static final int ERR_HEX2ASCII = 0x00000002;
	public static final int ERR_DATA_LENGTH_ZERO = 0x00000003;
	public static final int ERR_DATA_LENGTH_EIGHT = 0x00000004;
	public static final int ERR_KEY_VALUE_VALID = 0x00000005;
	public static final int ERR_MEMORY_OUT = 0x00000006;
	public static final int ERR_IV_LENGTH = 0x00000007;
	public static final int ERR_LENGTH_TOO_LONG = 0x00000008;
	public static final int ERR_RANDOM_LENGTH = 0x00000009;
	public static final int ERR_PARAM_LENGTH = 0x0000000A;
	public static final int ERR_OPEN_FILE_FAIL = 0x0000000B;
	public static final int ERR_SCRIPT_CMD_NOSUPPORT = 0x0000000C;
	public static final int ERR_SCRIPT_PARAM_NUM = 0x0000000D;
	public static final int ERR_SCRIPT_PARAM_WRONG = 0x0000000E;
	public static final int ERR_LOAD_KEYFILE = 0x0000000F;
	public static final int ERR_NODE_NO_FOUND = 0x00000010;
	public static final int ERR_RANDOM_LENGTH_WRONG = 0x00000011;
	public static final int ERR_SCRIPT_UPDATE_MODE = 0x00000012;
	public static final int ERR_APDU_ERROR = 0x000000FF;
	public static final int ERR_VSM = 0x0000FF00;

	public static final int ERR_Putdata = 0x00000013;
	public static final int ERR_ExternAuth = 0x00000014;
	public static final int ERR_GAC = 0x00000015;
	public static final int ERR_NeedReLoad = 0x00000818;

	public static final String BuildErr_NoMico = "01";
	public static final String BuildErr_OpenErr = "02";
	public static final String BuildErr_PowerOnErr = "03";
	public static final String BuildErr_SelectErr = "04";
	public static final String BuildErr_GPOErr = "05";
	public static final String BuildErr_GACErr_1 = "06";
	public static final String BuildErr_EuthErr = "07";
	public static final String BuildErr_GACErr_2 = "08";
	public static final String BuildErr_PutDataErr = "09";
	public static final String BuildErr_ReadDataErr = "10";
	public static final String BuildErr_TranceErr = "11";
	public static final String BuildErr_GetDataErr = "12";
	public static final String BuildErr_ConfigErr = "13";
	public static final String BuildErr_DefErr = "14";
	public static final String BuildErr_GetDateErr = "15";
	public static final String BuildErr_NotSupport = "16";
	public static final String BuildErr_BulidStrErr = "99";

	public static final String BUILD_NJ_DEFAULT = "20";
	public static final String BUILD_NJ_CONNECT = "21";
	public static final String BUILD_NJ_SELECT_STRING = "22";
	public static final String BUILD_NJ_GPO_STRING = "23";
	public static final String BUILD_NJ_GAC_STRING = "24";

	public static final String BUILD_NJ_Read_Pan = "31";
	public static final String BUILD_NJ_Read_Amount = "32";
	public static final String BUILD_NJ_READ_RECORD = "33";
	public static final String BUILD_NJ_READ_DATA = "34";
	public static final String BUILD_NJ_GAT_DATA = "35";
	public static final String BUILD_NJ_DATA_NULL = "39";

}
