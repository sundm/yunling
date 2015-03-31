package com.zcresearch.Util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.os.Handler;
import android.os.Message;

import com.zcresearch.Util.http.AsyncHttpClient;
import com.zcresearch.Util.http.AsyncHttpResponseHandler;
import com.zcresearch.Util.http.RequestParams;

public class ZCWebService {
	private int timeOut;
	private AsyncHttpClient _asyncHttpClient = new AsyncHttpClient();
	private Handler _myHandler;
	private String _responseString;

	private static String tagString = "zcWebService";

	public int getRequestTimeoutSeconds() {
		return this.timeOut;
	}

	public void setRequestTimeoutSeconds(int time) {
		this.timeOut = time;
		_asyncHttpClient.setTimeout(this.timeOut * 1000);
		ZCLog.i(tagString,
				"set request time out seconds:" + Integer.toString(time));
	}

	public String doBasic(String paramString, boolean paramBoolean,
			List<String> paramList1, List<String> paramList2) {
		return null;
	}

	public boolean userAuth(String _userIDString, String _panParams,
			final Handler handler) {
		if (handler == null || _userIDString == null || _panParams == null) {
			return false;
		}
		_myHandler = handler;

		String getUrl = ZCWebServiceParams.BASE_URL
				+ ZCWebServiceParams.VERIFY_URL;
		getUrl = getUrl + "?authenId=" + _userIDString + "&panNo=" + _panParams;
		doGet(getUrl);

		return true;
	}

	public boolean userLoad(String _userIDString, final Handler handler) {
		if (handler == null || _userIDString == null) {
			return false;
		}
		_myHandler = handler;

		String getUrl = ZCWebServiceParams.BASE_URL
				+ ZCWebServiceParams.LOAD_URL;
		getUrl = getUrl + "?authenId=" + _userIDString;
		doGet(getUrl);

		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object getJavaObject(Class cla) {
		try {
			return new ObjectMapper().readValue(_responseString, cla);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	private void doGet(String url) {
		setRequestTimeoutSeconds(5);

		ZCLog.i(tagString, "get from url:" + url);

		_asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				_responseString = response;
				ZCLog.i(tagString, _responseString);

				Message msg = _myHandler.obtainMessage();
				msg.what = ZCWebServiceParams.HTTP_SUCCESS;
				msg.obj = _responseString;

				_myHandler.sendMessage(msg);
			}

			@Override
			public void onStart() {
				// Initiated the request
				ZCLog.i(tagString, "http get method start");
				Message msg = _myHandler.obtainMessage();
				msg.what = ZCWebServiceParams.HTTP_START;
				msg.obj = "http get method start";

				_myHandler.sendMessage(msg);
			}

			@Override
			public void onFailure(Throwable e, String response) {
				ZCLog.e(tagString, response, e);
				Message msg = _myHandler.obtainMessage();
				msg.what = ZCWebServiceParams.HTTP_FAILED;
				msg.obj = response;
				_myHandler.sendMessage(msg);

			}

			@Override
			public void onFinish() {
				ZCLog.i(tagString, "http get finish");
				Message msg = _myHandler.obtainMessage();
				msg.what = ZCWebServiceParams.HTTP_FINISH;
				msg.obj = "http get method finish";
				_myHandler.sendMessage(msg);
			}
		});

	}

	private void doPost(RequestParams params, String url) {
		setRequestTimeoutSeconds(5);

		ZCLog.i(tagString, "post from url:" + url);
		_asyncHttpClient.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				// _jsonObject = response;
				ZCLog.i(tagString, response);
				Message msg = _myHandler.obtainMessage();
				msg.what = ZCWebServiceParams.HTTP_SUCCESS;
				msg.obj = response;

				_myHandler.sendMessage(msg);

				ZCLog.i(tagString, "post success");
				ZCLog.i(tagString, response);
			}

			@Override
			public void onStart() {
				// Initiated the request
				ZCLog.i(tagString, "http post method start");
				Message msg = _myHandler.obtainMessage();
				msg.what = ZCWebServiceParams.HTTP_START;
				msg.obj = "http post method start";

				_myHandler.sendMessage(msg);
			}

			@Override
			public void onFailure(Throwable e, String response) {
				ZCLog.e(tagString, response, e);
				Message msg = _myHandler.obtainMessage();
				msg.what = ZCWebServiceParams.HTTP_FAILED;
				msg.obj = response;
				_myHandler.sendMessage(msg);

				msg.what = ZCWebServiceParams.HTTP_THROWABLE;
				msg.obj = e;
				_myHandler.sendMessage(msg);
			}

			@Override
			public void onFinish() {
				ZCLog.i(tagString, "http post finish");
				Message msg = _myHandler.obtainMessage();
				msg.what = ZCWebServiceParams.HTTP_START;
				msg.obj = "http post method finish";

				_myHandler.sendMessage(msg);

			}
		});
	}

	private String dateToString(Date _date) {
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String reportDate = df.format(_date);
		ZCLog.i(tagString, reportDate);
		return reportDate;
	}

	public byte[] doDownload(String paramString, List<String> paramList1,
			List<String> paramList2) {
		return null;
	}

	public boolean doDownloadToSave(String paramString1,
			List<String> paramList1, List<String> paramList2,
			String paramString2) {
		return true;
	}

	public String doUpload(String paramString, List<String> paramList1,
			List<String> paramList2, List<String> paramList3,
			List<String> paramList4) {
		return null;
	}

	public byte[] doUploadAndDownload(String paramString,
			List<String> paramList1, List<String> paramList2,
			List<String> paramList3, List<String> paramList4) {
		return null;
	}

	public boolean doDownloadResource(String paramString1,
			List<String> paramList1, List<String> paramList2,
			String paramString2) {
		return true;
	}

	public String doUploadResource(String paramString, List<String> paramList1,
			List<String> paramList2, List<String> paramList3,
			List<String> paramList4) {
		return null;
	}
}