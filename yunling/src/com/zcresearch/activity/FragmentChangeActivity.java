package com.zcresearch.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.zcresearch.Util.ZCLog;
import com.zcresearch.app.yiyun.R;
import com.zcresearch.nfcard.NFCCardManager;

public class FragmentChangeActivity extends CustomZoomAnimation {

	private Resources res;

	private NfcAdapter nfcAdapter;
	private PendingIntent pendingIntent;
	private Parcelable parcelNfc = null;
	private int mPage = 0;
	private int nNfcState = 0;

	public String szAuthenIdString = null;
	public String szICCardInfo = null;

	public static boolean canRequest = false;

	private Fragment mContent;

	private final static int SCANNIN_GREQUEST_CODE = 1;

	public FragmentChangeActivity() {
		super();// (R.string.app_name);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set the Above View

		final Resources res = getResources();
		this.res = res;

		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		mContent = new ColorFragment(mPage, nNfcState, szAuthenIdString,
				szICCardInfo);

		// set the Above View
		setContentView(R.layout.content_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();

		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new ColorMenuFragment()).commit();

		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		parcelNfc = MainActivity.parcelNfc;

		onNewIntent(getIntent());
	}

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {

			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onNewIntent(Intent intent) {

		ZCLog.i("Activity", "onNewIntent");

		Parcelable parcelNfc1;
		parcelNfc1 = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		if (parcelNfc1 != null) {
			parcelNfc = parcelNfc1;
			MainActivity.parcelNfc = parcelNfc;
		}

		mPage = ((ColorFragment) mContent).page();

		switch (mPage) {
		case 0:
			szICCardInfo = (parcelNfc != null) ? NFCCardManager
					.BuildDDA(parcelNfc) : null;
			// (parcelNfc != null) ? NFCCardManager.GetCardInfo(parcelNfc) :
			// null;
			break;
		case 1:
			break;
		}

		if (szICCardInfo != null)
			canRequest = true;
		else
			canRequest = false;

		refreshStatus();
	}

	private void refreshStatus() {
		final Resources r = this.res;
		final String tip;

		if (nfcAdapter == null) {
			tip = r.getString(R.string.tip_nfc_notfound);
			nNfcState = 0;
			szAuthenIdString = null;
			szICCardInfo = null;

		} else if (nfcAdapter.isEnabled()) {
			tip = r.getString(R.string.tip_nfc_enabled);
			nNfcState = 1;
		} else {
			tip = r.getString(R.string.tip_nfc_disabled);
			nNfcState = 2;
			szAuthenIdString = null;
			szICCardInfo = null;
		}

		if (szICCardInfo != null) {
			if (szICCardInfo.length() == 2) {
				szICCardInfo = null;
			} else {
				szICCardInfo = NFCCardManager.getPan();
				canRequest = true;
			}
		}

		switch (mPage) {
		case 0:
			mContent = new ColorFragment(mPage, nNfcState, szAuthenIdString,
					szICCardInfo);
			break;
		}

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();
		getSlidingMenu().showContent();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (nfcAdapter != null) {
			nfcAdapter.disableForegroundDispatch(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		ZCLog.i("Activity", "onResume");

		if (nfcAdapter != null) {
			nfcAdapter.enableForegroundDispatch(this, pendingIntent,
					MainActivity.FILTERS, MainActivity.TECHLISTS);
		}

		canRequest = false;
		// refreshStatus();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		// return true;
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_menu,
				(com.actionbarsherlock.view.Menu) menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivityForResult(new Intent(
					android.provider.Settings.ACTION_WIRELESS_SETTINGS), 0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void switchContent(Fragment fragment) {
		ZCLog.i("Activity", "switchContent");
		canRequest = false;
		szAuthenIdString = null;
		szICCardInfo = null;

		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		getSlidingMenu().showContent();
	}

	public int NfcState() {
		return nNfcState;
	}

	public void btnmainright(MenuItem item) {
		Intent intent = new Intent();
		intent.setClass(FragmentChangeActivity.this, MipcaActivityCapture.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		szAuthenIdString = null;
		szICCardInfo = null;

		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if (resultCode == RESULT_OK) {
				canRequest = false;
				Bundle bundle = data.getExtras();
				szAuthenIdString = bundle.getString("result").trim();
				ZCLog.i("qrCode", szAuthenIdString);

				if (szAuthenIdString.startsWith("#")
						&& szAuthenIdString.endsWith("#")) {
					szAuthenIdString = szAuthenIdString.replace("#", "");
					ZCLog.i("qrCode", szAuthenIdString);
					canRequest = true;
					refreshStatus();
				} else {
					Toast.makeText(getApplicationContext(), "二维码格式错误，请重试",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getApplicationContext(), "二维码扫描失败，请重试",
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

}
