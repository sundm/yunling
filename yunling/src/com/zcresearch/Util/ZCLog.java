package com.zcresearch.Util;

import android.util.Log;

public class ZCLog {
	public static final int ZCLogLevelDebug = 0;
	public static final int ZCLogLevelInfo = 1;
	public static final int ZCLogLevelWarn = 2;
	public static final int ZCLogLevelError = 3;
	private static int logLevel = 0;

	public static void setZCLogLevel(int level) {
		logLevel = level;
	}

	public static int getZCLogLevel() {
		return logLevel;
	}

	public static void d(String tag, String logString) {
		if (logLevel <= 0)
			Log.d(tag, logString);
	}

	public static void d(String tag, String logString, Throwable paramThrowable) {
		if (logLevel <= 0)
			Log.d(tag, logString, paramThrowable);
	}

	public static void i(String tag, String logString) {
		if (logLevel <= 1)
			Log.i(tag, logString);
	}

	public static void i(String tag, String logString, Throwable paramThrowable) {
		if (logLevel <= 1)
			Log.i(tag, logString, paramThrowable);
	}

	public static void w(String tag, String logString) {
		if (logLevel <= 2)
			Log.w(tag, logString);
	}

	public static void w(String tag, String logString, Throwable paramThrowable) {
		if (logLevel <= 2)
			Log.w(tag, logString, paramThrowable);
	}

	public static void e(String tag, String logString) {
		Log.e(tag, logString);
	}

	public static void e(String tag, String logString, Throwable paramThrowable) {
		Log.e(tag, logString, paramThrowable);
	}
}