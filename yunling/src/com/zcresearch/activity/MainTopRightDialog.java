package com.zcresearch.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.zcresearch.app.yiyun.R;

public class MainTopRightDialog extends Activity {
	// private MyDialog dialog;
	private final static int SCANNIN_GREQUEST_CODE = 1;
	private LinearLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_top_right_dialog);
		// dialog=new MyDialog(this);
		layout = (LinearLayout) findViewById(R.id.scan_qr_code);
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.scan_qr_code:
					Intent intent = new Intent();
					intent.setClass(MainTopRightDialog.this,
							MipcaActivityCapture.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
					finish();
					break;
				}
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

}
