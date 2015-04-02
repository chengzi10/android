package com.healthslife.server;

import com.healthslife.R;
import com.healthslife.health.HealthServiceActivity;
import com.healthslife.loginregister.Aty_UserCenter;
import com.healthslife.loginregister.LoginRegisterGlobalVariable;
import com.healthslife.sensor.dao.SportInfoDAO;
import com.healthslife.sensor.data.SensorData;
import com.healthslife.sensor.utilities.CalculateUtil;
import com.healthslife.system.SettingActivity;

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

public class UploadServiceActivity extends Activity{
	Context context;
	private Handler handle = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Log.d("handle", "into handle");
			String result = msg.obj.toString();
			Log.d("TAG", result);
			if (result.equals("true")) {
				Toast.makeText(getApplicationContext(),
						"上传数据成功", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(UploadServiceActivity.this, HealthServiceActivity.class);
				startActivity(intent);
				finish();
			} else if (result.equals("net_exception")) {
				Toast.makeText(getApplicationContext(), "网络连接异常，请查看网络...",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(UploadServiceActivity.this, SettingActivity.class);
				startActivity(intent);
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "上传数据失败",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(UploadServiceActivity.this, SettingActivity.class);
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
		setContentView(R.layout.upload_service);
		
		ProgressDialog dialog = new ProgressDialog(this);
		// 设置进度条风格，风格为圆形，旋转的
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				String info_str=CalculateUtil.UploadInfo(UploadServiceActivity.this);
				String data_str=CalculateUtil.UploadSportData(UploadServiceActivity.this);
				String info = UpdataUserInfo.updataUserInfo(info_str);
				String data = UpdataUserData.updataUserData(SensorData.getUsername(), data_str);
				String str = null;
				if(info.equals("true") && data.equals("true")) {
					str = "true";
				}else if(info.equals("net_exception") || data.equals("net_exception")){
					str = "net_exception";
				}else {
					str = "false";
				}
				Message msg = handle.obtainMessage();
				msg.obj = str;
				handle.sendMessage(msg);
			}
			
		}.start();
	}
}
