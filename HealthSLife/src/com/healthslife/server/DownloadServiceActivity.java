package com.healthslife.server;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.healthslife.R;
import com.healthslife.loginregister.Aty_UserCenter;
import com.healthslife.loginregister.Login;
import com.healthslife.loginregister.LoginRegisterGlobalVariable;
import com.healthslife.sensor.utilities.CalculateUtil;

public class DownloadServiceActivity extends Activity {

	Context context;
	private Handler handle = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Log.d("handle", "into handle");
			String result = msg.obj.toString();
			Log.d("TAG", result);
			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT)
					.show();
			if (result.equals("true")) {
				Toast.makeText(getApplicationContext(), "下载数据成功",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				LoginRegisterGlobalVariable.login_model = 0;
				intent.setClass(DownloadServiceActivity.this,
						Aty_UserCenter.class);
				startActivity(intent);
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "下载数据失败",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(DownloadServiceActivity.this,
						Login.class);
				startActivity(intent);
				finish();
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.download_service);

		ProgressDialog dialog = new ProgressDialog(this);
		// 设置进度条风格，风格为圆形，旋转的
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				String userData = DownloadUserData
						.downloadUserData(LoginRegisterGlobalVariable.login_name);
				String userInfo = DownloadUserInfo
						.downloadUserInfo(LoginRegisterGlobalVariable.login_name);
				String str = CalculateUtil.LoginMark(
						DownloadServiceActivity.this, userInfo, userData);
				Message msg = handle.obtainMessage();
				msg.obj = str;
				handle.sendMessage(msg);
			}

		}.start();
	}
}
