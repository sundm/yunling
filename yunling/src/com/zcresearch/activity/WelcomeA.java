package com.zcresearch.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.zcresearch.app.yiyun.R;

/** 开场欢迎动画 */
public class WelcomeA extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.strat);

		getActionBar().hide();

		// 延迟两秒后执行run方法中的页面跳转ת
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(WelcomeA.this, WhatsnewPagesA.class);
				startActivity(intent);
				WelcomeA.this.finish();
			}
		}, 2000);
	}
}
