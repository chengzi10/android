package com.healthslife.music.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

import com.healthslife.R;

public class BaseActivity extends Activity {
	
	public static final String BROADCASTRECEIVER_ACTION="com.healthslife.music.receiver.commonreceiver";
	private CommonReceiver commonReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 竖屏幕
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);
		// 设置背景
		this.getWindow().setBackgroundDrawableResource(R.drawable.main_bg07);
		commonReceiver=new CommonReceiver();
	}
	

	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(commonReceiver, new IntentFilter(BROADCASTRECEIVER_ACTION));
	}


	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(commonReceiver);
	}


	public class CommonReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	}



}
