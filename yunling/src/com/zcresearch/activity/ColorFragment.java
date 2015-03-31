package com.zcresearch.activity;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.zcresearch.Util.Util;
import com.zcresearch.Util.ZCLog;
import com.zcresearch.Util.ZCWebService;
import com.zcresearch.Util.ZCWebServiceParams;
import com.zcresearch.app.yiyun.R;

@SuppressLint({ "SetJavaScriptEnabled" })
public class ColorFragment extends Fragment implements Html.ImageGetter,
		Html.TagHandler {

	private Resources res;
	private WebView board;

	private String szUserID;
	private String szCardInfo;

	private boolean isPassed = false;
	private String szAdditional = null;
	private String szBankName = null;

	private String szUserName = null;
	private String szIDNumber = null;
	private String szSite = null;
	private String szTime = null;

	private static boolean hasPassed = false;
	private String szServerStatus = null;

	private RelativeLayout v;

	private int mNfcState = 0;
	private int mPage = 0;

	private final static String TAG = "YunKeyDemo";
	private Handler authHandler;
	private Handler loadHandler;
	private Handler uiHandler;

	// 短日期格式
	public static String DATE_FORMAT = "yyyy-MM-dd";

	// 长日期格式
	public static String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private ZCWebService _wbService = new ZCWebService();

	private static final String CARDINFO = "<!doctype html><html><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0\"><link rel=\"stylesheet\" type=\"text/css\"	href=\"css/bootstrap.min.css\"></head><body>";

	public ColorFragment() {
		this(0, 0, null, null);
	}

	public ColorFragment(int nPage, int nfcState, String _userID,
			String _cardInfo) {
		mPage = nPage;
		mNfcState = nfcState;
		szUserID = _userID;
		szCardInfo = _cardInfo;
		setRetainInstance(true);
	}

	public int page() {
		return mPage;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		ZCLog.i(TAG, "onCreateView");

		if (savedInstanceState != null)
			mNfcState = savedInstanceState.getInt("mColorRes");
		v = new RelativeLayout(getActivity());
		final Resources res = getResources();
		this.res = res;

		ScrollView scrollView = new ScrollView(getActivity());

		scrollView.setScrollContainer(true);

		scrollView.setFocusable(true);

		board = new WebView(getActivity());
		board.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				boolean shouldOverride = false;
				if (url.startsWith("https://")) {
					shouldOverride = true;
				}
				return shouldOverride;
			}
		});

		board.setWebChromeClient(new WebChromeClient());

		// requires javascript
		board.getSettings().setJavaScriptEnabled(true);
		board.setBackgroundColor(0);
		// board.getSettings().setLoadWithOverviewMode(true);
		// board.getSettings().setUseWideViewPort(true);
		// board.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		board.addJavascriptInterface(this, "activity");
		// board.addJavascriptInterface(new EditJavaScriptImpl(), "edit");
		board.getSettings().setDefaultTextEncodingName("UTF-8");

		scrollView.addView(board);
		v.addView(scrollView);
		v.setBackgroundResource(R.drawable.back_defualt);

		showData();

		uiHandler = new Handler();

		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("mColorRes", mNfcState);
	}

	// protected final class EditJavaScriptImpl {
	// public void editcardinfo() {
	//
	// }
	// }

	public void onFinish() {
		ZCLog.i(TAG, "call onFinish");
		uiHandler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				showHint();
			}
		});
	}

	private void showQRSuccess(boolean isOk) {
		ZCLog.i(TAG, "showQRSuccess");

		if (mNfcState != 1 || szUserID == null || szUserID.isEmpty()) {
			showHint();
			return;
		} else if (isOk) {
			hasPassed = false;
			String szInfo_1 = new StringBuilder(
					"<style>.divcss5{text-align:center}</style>" +
					"<div class=\"well well-large\"><p><div class=\"divcss5\"><font size=\"4\" color=\"#666666\">请挥卡认证以下申请信息</font></div></p>"
							+ "<ul class=\"list-group\">"
							+ "<li class=\"list-group-item\">"
							+ "<div class=\"row\" style=\"color:#666666\">"
							+ "<div class=\"col-xs-4 text-left\">令牌序号: </div>"
							+ "<div class=\"col-xs-8 text-right\" style=\"color:#333333\">")
					.append(szUserID).append("</div></div></li>").toString();

			String szInfo_2 = new StringBuilder(
					"<li class=\"list-group-item\">"
							+ "<div class=\"row\" style=\"color:#666666\">"
							+ "<div class=\"col-xs-4 text-left\">姓名: </div>"
							+ "<div class=\"col-xs-8 text-right\" style=\"color:#333333\">")
					.append(szUserName).append("</div></div></li>").toString();

			String szInfo_3 = new StringBuilder(
					"<li class=\"list-group-item\">"
							+ "<div class=\"row\" style=\"color:#666666\">"
							+ "<div class=\"col-xs-4 text-left\">身份证: </div>"
							+ "<div class=\"col-xs-8 text-right\" style=\"color:#333333\">")
					.append(szIDNumber).append("</div></div></li>").toString();

			String szInfo_4 = new StringBuilder(
					"<li class=\"list-group-item\">"
							+ "<div class=\"row\" style=\"color:#666666\">"
							+ "<div class=\"col-xs-6 text-left\">认证申请方: </div>"
							+ "<div class=\"col-xs-6 text-right\" style=\"color:#333333\">")
					.append(szSite).append("</div></div></li>").toString();

			String szInfo_5 = new StringBuilder(
					"<li class=\"list-group-item\">"
							+ "<div class=\"row\" style=\"color:#666666\">"
							+ "<div class=\"col-xs-5 text-left\">认证申请时间: </div>"
							+ "<div class=\"col-xs-7 text-right\" style=\"color:#333333\">")
					.append(szTime).append("</div></div></li>").toString();

			String szInfo_6 = new StringBuilder(
					"<li class=\"list-group-item\">"
							+ "<div class=\"row\" style=\"color:#666666\">"
							+ "<div class=\"col-xs-4 text-left\">认证状态: </div>"
							+ "<div class=\"col-xs-8 text-right\" style=\"color:#ff6600\">")
					.append("等待挥卡认证")
					.append("</div></div></li></ul>"
							+ "<p><img src=\"img/nfc.gif\"></p></div></body></html>")
					.toString();

			final StringBuilder s = new StringBuilder();
			s.append(CARDINFO);
			s.append(szInfo_1);
			s.append(szInfo_4);
			s.append(szInfo_5);
			s.append(szInfo_2);
			s.append(szInfo_3);

			s.append(szInfo_6);

			board.getSettings().setJavaScriptEnabled(true);
			ZCLog.i(TAG, s.toString());
			board.loadDataWithBaseURL("file:///android_asset/", s.toString(),
					"text/html", "UTF-8", null);
		} else {
			String szInfo_1 = new StringBuilder(
					"<style>.divcss5{text-align:center}</style>"
							+ "<div class=\"well well-large\" style=\"margin-bottom: 5em\">"
							+ "<ul class=\"list-group\">"
							+ "<li class=\"list-group-item\"><div class=\"divcss5\">"
							+ "<img src=\"img/wrong.png\"><font size=\"4\" color=\"#666666\">"
							+ " 认证失败</font></div></li>"
							+ "<li class=\"list-group-item\">"
							+ "<div class=\"row\" style=\"color:#666666\">"
							+ "<div class=\"col-xs-4 text-left\">令牌序号: </div>"
							+ "<div class=\"col-xs-8 text-right\" style=\"color:#333333\">")
					.append(szUserID).append("</div></div></li>")
					.toString();
			
			String szInfo_2 = new StringBuilder(
					"<li class=\"list-group-item\">"
							+ "<div class=\"row\">"
							+ "<div class=\"col-xs-12 text-left\" style=\"color:#333333\">")
					.append(szServerStatus)
					.append("</div></div></li></ul>"
							+ "<div class=\"text-center\" style=\"margin-top: 6em; margin-bottom: 12em\">"
							+ "<img src=\"img/btn.png\" onclick=\"activity.onFinish();\" width=\"290\">"
							+ "<div style=\"color: white; z-index: 2; margin-top: -2.2em; font-size: 1.2em\""
							+ " class=\"text-center\" onclick=\"activity.onFinish();\">返回首页</div></div></div></body></html>")
					.toString();
			
			final StringBuilder s = new StringBuilder();
			s.append(CARDINFO);
			s.append(szInfo_1);
			s.append(szInfo_2);

			board.getSettings().setJavaScriptEnabled(true);
			ZCLog.i(TAG, s.toString());
			board.loadDataWithBaseURL("file:///android_asset/", s.toString(),
					"text/html", "UTF-8", null);
		}

		getActivity().getActionBar().setTitle(R.string.title_name_page1_home);
	}

	private void showAuthSuccess() {
		if (mNfcState != 1 || szUserID == null || szUserID.isEmpty()) {
			showHint();
			return;
		} else if (szCardInfo == null || szCardInfo.isEmpty()) {
			showHint();
			return;
		} else {
			ZCLog.i(TAG, "showAuthSuccess");

			getActivity().getActionBar().setTitle(
					R.string.title_name_page1_result);

			if (isPassed) {
				String _report_url = ZCWebServiceParams.BASE_URL
						+ ZCWebServiceParams.REPORT_URL + "?authenId="
						+ szUserID;
				ZCLog.i(TAG, _report_url);
				board.loadUrl(_report_url);
				hasPassed = true;
			} else {
				hasPassed = false;
				String szInfo_1 = new StringBuilder(
						"<style>.divcss5{text-align:center}</style>"
								+ "<div class=\"well well-large\" style=\"margin-bottom: 5em\">"
								+ "<ul class=\"list-group\">"
								+ "<li class=\"list-group-item\"><div class=\"divcss5\">"
								+ "<img src=\"img/wrong.png\"><font size=\"4\" color=\"#666666\">"
								+ " 认证失败</font></div></li>"
								+ "<li class=\"list-group-item\">"
								+ "<div class=\"row\" style=\"color:#666666\">"
								+ "<div class=\"col-xs-4 text-left\">令牌序号: </div>"
								+ "<div class=\"col-xs-8 text-right\" style=\"color:#333333\">")
						.append(szUserID).append("</div></div></li>")
						.toString();

				// String szInfo_2 = new StringBuilder(
				// "<li class=\"list-group-item\">"
				// + "<div class=\"row\" style=\"color:#666666\">"
				// + "<div class=\"col-xs-4 text-left\">认证状态: </div>"
				// +
				// "<div class=\"col-xs-8 text-right\" style=\"color:#333333\">")
				// .append("未通过").append("</div></div></li>").toString();
				String szInfo_2;

				if (szAdditional.compareTo("认证请求超时，请重新发起认证请求") != 0) {
					szInfo_2 = new StringBuilder(
							"<li class=\"list-group-item\">"
									+ "<div class=\"row\">"
									+ "<div class=\"col-xs-12 text-left\" style=\"color:#333333\">")
							.append(szAdditional)
							.append("</div></div></li></ul>"
									+ "<p><img src=\"img/nfc.gif\"></p></div></body></html>")
							.toString();

				} else {
					szInfo_2 = new StringBuilder(
							"<li class=\"list-group-item\">"
									+ "<div class=\"row\">"
									+ "<div class=\"col-xs-12 text-left\" style=\"color:#333333\">")
							.append(szAdditional)
							.append("</div></div></li></ul>"
									+ "<div class=\"text-center\" style=\"margin-top: 6em; margin-bottom: 12em\">"
									+ "<img src=\"img/btn.png\" onclick=\"activity.onFinish();\" width=\"290\">"
									+ "<div style=\"color: white; z-index: 2; margin-top: -2.2em; font-size: 1.2em\""
									+ " class=\"text-center\" onclick=\"activity.onFinish();\">返回首页</div></div></div></body></html>")
							.toString();
					
					hasPassed = true;

				}

				final StringBuilder s = new StringBuilder();
				s.append(CARDINFO);
				s.append(szInfo_1);
				s.append(szInfo_2);

				board.getSettings().setJavaScriptEnabled(true);
				ZCLog.i(TAG, s.toString());
				board.loadDataWithBaseURL("file:///android_asset/",
						s.toString(), "text/html", "UTF-8", null);
			}
		}
	}

	public void authRequest() {
		if (szUserID == null || szCardInfo == null) {
			return;
		}

		ZCLog.i(TAG, "请求网络连接userAuth");
		ZCLog.i(TAG, szUserID);
		ZCLog.i(TAG, szCardInfo);
		authHandler = new AuthHandler();

		_wbService.userAuth(szUserID, szCardInfo, authHandler);
	}

	public void loadRequest() {
		if (szUserID == null) {
			return;
		}

		ZCLog.i(TAG, "请求网络连接loadRequest");
		ZCLog.i(TAG, szUserID);
		loadHandler = new LoadHandler();

		_wbService.userLoad(szUserID, loadHandler);
	}

	private void showData() {
		ZCLog.i(TAG, "showData");
		ZCLog.i(TAG, String.valueOf(FragmentChangeActivity.canRequest));
		ZCLog.i(TAG, String.valueOf(hasPassed));
		if (mNfcState != 1 || szUserID == null || szUserID.isEmpty()) {
			showHint();
			return;
		} else if (FragmentChangeActivity.canRequest
				&& (szCardInfo == null || szCardInfo.isEmpty())) {
			loadRequest();
			return;
		} else if (FragmentChangeActivity.canRequest && !hasPassed) {
			authRequest();
			return;
		} else {
			showHint();
			return;
		}
	}

	private void showHint() {
		ZCLog.i(TAG, "showHint");
		String hint = null;

		getActivity().getActionBar().setTitle(R.string.title_name_page1_home);

		if (mNfcState == 0)
			hint = res.getString(R.string.msg_nonfc);
		else if (mNfcState == 1) {
			szUserID = null;
			szCardInfo = null;
			Toast.makeText(getActivity().getApplicationContext(),
					"先扫码获取认证令牌序号", Toast.LENGTH_SHORT).show();
			hint = "";
			// board.getSettings().setJavaScriptEnabled(true);
			// board.loadDataWithBaseURL("file:///android_asset/",
			// "home.html", "text/html", "UTF-8", null);
			// return;
		} else if (mNfcState == 2) {
			hint = res.getString(R.string.msg_nfcdisabled);
		}

		hint = "<html><head></head><body><p><font color=\"teal\">" + hint
				+ "</font></p></body></html>";
		board.getSettings().setJavaScriptEnabled(true);
		String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
		ZCLog.i(TAG, header + hint);
		board.loadDataWithBaseURL(null, header + hint, "text/html", "UTF-8",
				null);
	}

	private class LoadHandler extends Handler {
		@Override
		public void dispatchMessage(Message msg) {

			switch (msg.what) {
			case ZCWebServiceParams.HTTP_START:
				ZCLog.i(TAG, "http_start");
				ZCLog.i(TAG, msg.obj.toString());
				break;

			case ZCWebServiceParams.HTTP_FINISH:
				ZCLog.i(TAG, "http_finish");
				ZCLog.i(TAG, msg.obj.toString());
				break;

			case ZCWebServiceParams.HTTP_FAILED:
				ZCLog.i(TAG, "http_failed");
				szServerStatus = "服务器异常，请联系管理员";
				showQRSuccess(false);
				break;

			case ZCWebServiceParams.HTTP_SUCCESS:
				String _jsString = msg.obj.toString();
				ZCLog.i(TAG, _jsString);

				if (!_jsString.isEmpty()) {
					JSONTokener jsonParser = new JSONTokener(_jsString);

					try {
						JSONObject person = (JSONObject) jsonParser.nextValue();
						szUserName = person.getString("realName");
						szIDNumber = person.getString("idNumber");
						szSite = person.getString("site");
						szTime = convert2String(person.getLong("timestamp"));

						ZCLog.i(TAG, "http_success");
						ZCLog.i(TAG, szUserName);
						ZCLog.i(TAG, szIDNumber);
						ZCLog.i(TAG, szSite);
						ZCLog.i(TAG, szTime);

						showQRSuccess(true);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						szServerStatus = "过期的令牌序号，请重新扫码";
						showQRSuccess(false);
						e1.printStackTrace();
					}
				} else {
					szServerStatus = "过期的令牌序号，请重新扫码";
					showQRSuccess(false);
				}

				break;

			case ZCWebServiceParams.HTTP_THROWABLE:
				Throwable e = (Throwable) msg.obj;
				ZCLog.e(TAG, "catch thowable:", e);
				break;

			default:
				ZCLog.i(TAG, "http nothing to do");
				break;
			}
		}
	}

	public static String convert2String(long time) {
		if (time > 0l) {
			SimpleDateFormat sf = new SimpleDateFormat(TIME_FORMAT);
			Date date = new Date(time);
			return sf.format(date);
		} else {
			return "";
		}
	}

	private class AuthHandler extends Handler {
		@Override
		public void dispatchMessage(Message msg) {

			isPassed = false;
			szAdditional = null;
			szBankName = null;

			switch (msg.what) {
			case ZCWebServiceParams.HTTP_START:
				ZCLog.i(TAG, "http_start");
				ZCLog.i(TAG, msg.obj.toString());
				break;

			case ZCWebServiceParams.HTTP_FINISH:
				ZCLog.i(TAG, "http_finish");
				ZCLog.i(TAG, msg.obj.toString());
				break;

			case ZCWebServiceParams.HTTP_FAILED:

				isPassed = false;
				szAdditional = "服务器异常，请联系管理员";
				szBankName = null;

				ZCLog.i(TAG, "http_failed");

				showAuthSuccess();
				break;

			case ZCWebServiceParams.HTTP_SUCCESS:
				String _jsString = msg.obj.toString();
				JSONTokener jsonParser = new JSONTokener(_jsString);

				try {
					JSONObject person = (JSONObject) jsonParser.nextValue();
					isPassed = person.getBoolean("authenPassed");
					szAdditional = person.getString("additional");
					szBankName = person.getString("bankName");
					if (szBankName == null)
						szBankName = "未知银行";

					ZCLog.i(TAG, "http_success");
					ZCLog.i(TAG, String.valueOf(isPassed));
					ZCLog.i(TAG, szAdditional);
					ZCLog.i(TAG, szBankName);

					showAuthSuccess();

				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				ZCLog.i(TAG, _jsString);

				break;

			case ZCWebServiceParams.HTTP_THROWABLE:
				Throwable e = (Throwable) msg.obj;
				ZCLog.e(TAG, "catch thowable:", e);
				break;

			default:
				ZCLog.i(TAG, "http nothing to do");
				break;
			}
		}
	}

	@Override
	public void handleTag(boolean opening, String tag, Editable output,
			XMLReader xmlReader) {
		// TODO Auto-generated method stub

	}

	private Drawable spliter;

	@SuppressWarnings("deprecation")
	@Override
	public Drawable getDrawable(String source) {
		final Resources res = this.res;

		final Drawable ret;
		if (source.startsWith("spliter")) {
			if (spliter == null) {
				final int w = res.getDisplayMetrics().widthPixels;
				final int h = (int) (res.getDisplayMetrics().densityDpi / 72f + 0.5f);

				final int[] pix = new int[w * h];
				Arrays.fill(pix, res.getColor(R.color.bg_default));
				spliter = new BitmapDrawable(Bitmap.createBitmap(pix, w, h,
						Bitmap.Config.ARGB_8888));
				spliter.setBounds(0, 3 * h, w, 4 * h);
			}
			ret = spliter;

		} else if (source.startsWith("icon")) {
			ret = res.getDrawable(R.drawable.ic_launcher);

			final String[] params = source.split(",");
			final float f = res.getDisplayMetrics().densityDpi / 72f;
			final float w = Util.parseInt(params[1], 10, 16) * f + 0.5f;
			final float h = Util.parseInt(params[2], 10, 16) * f + 0.5f;
			ret.setBounds(0, 0, (int) w, (int) h);

		} else {
			ret = null;
		}

		return ret;
	}

}
