package com.healthslife.loginregister;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.healthslife.R;

public class Register extends Activity {

	/*
	 * The thread of login
	 */
	@SuppressLint("HandlerLeak")
	private Handler handle = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Log.d("handle", "into handle");
			String result = msg.obj.toString();
			Log.d("TAG", result);

			if (result.equals("success")) {
				Toast.makeText(getApplicationContext(),
						R.string.register_success, Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				LoginRegisterGlobalVariable.login_model = 0;
				intent.setClass(Register.this, Login.class);
				startActivity(intent);
				finish();
			} else {
				Toast.makeText(getApplicationContext(), R.string.register_fail,
						Toast.LENGTH_SHORT).show();
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.register);
		/*
		 * normal login style
		 */
		final EditText editText_name = (EditText) findViewById(R.id.register_username);
		final EditText editText_password = (EditText) findViewById(R.id.register_password);
		final EditText editText_confirm_password = (EditText) findViewById(R.id.register_confirm_password);
		Button login_btn = (Button) findViewById(R.id.normal_login_btn);
		login_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LoginRegisterGlobalVariable.register_name = editText_name
						.getText().toString();
				LoginRegisterGlobalVariable.register_passwd = editText_password
						.getText().toString();
				LoginRegisterGlobalVariable.register_confirm_passwd = editText_confirm_password
						.getText().toString();
				/**
				 * 判断用户输入是否为空
				 */
				if ("".equals(LoginRegisterGlobalVariable.register_name)
						|| "".equals(LoginRegisterGlobalVariable.register_passwd)
						|| "".equals(LoginRegisterGlobalVariable.register_confirm_passwd)) {
					YoYo.with(Techniques.Shake).duration(700)
							.playOn(findViewById(R.id.input_area));
					Toast.makeText(getApplicationContext(), R.string.info_null,
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (LoginRegisterGlobalVariable.register_passwd.length() < 6) {
					YoYo.with(Techniques.Shake).duration(700)
							.playOn(findViewById(R.id.input_area));
					Toast.makeText(getApplicationContext(),
							R.string.short_passwd, Toast.LENGTH_SHORT).show();
					return;
				}
				if (!LoginRegisterGlobalVariable.register_passwd
						.equals(LoginRegisterGlobalVariable.register_confirm_passwd)) {
					YoYo.with(Techniques.Shake).duration(700)
							.playOn(findViewById(R.id.input_area));
					Toast.makeText(getApplicationContext(),
							R.string.differ_passwd, Toast.LENGTH_SHORT).show();
					return;
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Log.d("Thread", "into Thread");
						register(LoginRegisterGlobalVariable.register_name,
								LoginRegisterGlobalVariable.register_passwd);
						Message msg = handle.obtainMessage();
						msg.obj = LoginRegisterGlobalVariable.register_result;
						handle.sendMessage(msg);
					}

				}).start();
				
			}
		});

	}

	public void register(String name, String passwd) {

		String methodName = "register"; // login method name
		String endPoint = LoginRegisterGlobalVariable.urlStr
				+ "RegisterService"; // EndPoint
		String soapAction = LoginRegisterGlobalVariable.nameSpace + "/register"; // SOAP
																					// Action

		// 指定WebService的命名空间和调用的方法名
		SoapObject rpc = new SoapObject(LoginRegisterGlobalVariable.nameSpace,
				methodName);
		// 设置调用webService接口需要传入的参数
		rpc.addProperty("name", name);
		rpc.addProperty("passwd", passwd);

		// 生成调用WebService方法的SOAP请求信息，并指定SOAP的版本
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER10);
		envelope.bodyOut = rpc;

		HttpTransportSE transport = new HttpTransportSE(endPoint);
		try {
			// 调用WebService
			transport.call(soapAction, envelope);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 获取返回的数据
		SoapObject object = (SoapObject) envelope.bodyIn;
		// 获取返回的结果
		if (null == object.getProperty(0)) {
			LoginRegisterGlobalVariable.register_result = "fail";
		} else {
			LoginRegisterGlobalVariable.register_result = object.getProperty(0)
					.toString();
		}
	}
}
