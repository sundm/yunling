package com.zcresearch.Util;

import java.io.File;
import java.util.List;

public class ZCWebServiceUtil {

	/**
	 * web service返回值。类型为String 或者 byte[]
	 * 
	 * @param url
	 * @param isDownload
	 * @param isPostMethod
	 * @param paramNames
	 * @param paramValues
	 * @return
	 */
	static Object doBasicMethod(String url, boolean isDownload,
			boolean isPostMethod, List<String> paramNames,
			List<String> paramValues) {
		return doBasicMethod(url, isDownload, isPostMethod, paramNames,
				paramValues, 0);
	}

	public static Object doBasicMethod(String url, boolean isDownload,
			boolean isPostMethod, List<String> paramNames,
			List<String> paramValues, int requestTimeoutInterval) {
		return null;// ZCHttpClientUtil.doBasicPost(isDownload, url,
		// paramNames, paramValues, null);
	}

	public static Object doMultipartMethod(String paramString,
			boolean paramBoolean, List<String> paramList1,
			List<String> paramList2, List<File> paramList3) {
		return doMultipartMethod(paramString, paramBoolean, paramList1,
				paramList2, paramList3, 0);
	}

	public static Object doMultipartMethod(String paramString,
			boolean paramBoolean, List<String> paramList1,
			List<String> paramList2, List<File> paramList3, int paramInt) {
		return null;// ZCHttpClientUtil.doMultipartPost(paramBoolean,
					// paramString,
		// paramList1, paramList2, paramList3);
	}

}