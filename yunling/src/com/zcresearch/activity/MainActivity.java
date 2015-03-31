package com.zcresearch.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.WindowManager;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.zcresearch.app.yiyun.R;

public class MainActivity extends SherlockPreferenceActivity {

	static private boolean appToBeEnd;

	private NfcAdapter nfcAdapter;
	private PendingIntent pendingIntent;
	public static Parcelable parcelNfc = null;

	public static String[][] TECHLISTS;
	public static IntentFilter[] FILTERS;

	static {
		try {
			TECHLISTS = new String[][] { { IsoDep.class.getName() },
					{ NfcV.class.getName() }, { NfcF.class.getName() }, };

			FILTERS = new IntentFilter[] { new IntentFilter(
					NfcAdapter.ACTION_TECH_DISCOVERED, "*/*") };
		} catch (Exception e) {

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		onNewIntent(getIntent());

		appToBeEnd = true;
		Intent intent = new Intent(this, FragmentChangeActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
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

	@Override
	protected void onNewIntent(Intent intent) {

		parcelNfc = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
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

		if (nfcAdapter != null) {
			nfcAdapter.enableForegroundDispatch(this, pendingIntent, FILTERS,
					TECHLISTS);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
		getWindow().addFlags(flags);

		if (appToBeEnd == true) {
			finish();
		}
	}

}
