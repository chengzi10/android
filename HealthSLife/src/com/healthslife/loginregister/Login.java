package com.healthslife.loginregister;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.healthslife.R;
import com.isnc.facesdk.SuperID;
import com.isnc.facesdk.common.Cache;
import com.isnc.facesdk.common.SDKConfig;

public class Login extends Activity {
	
	private Context context;
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
				Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_SHORT)
				.show();
				Intent intent = new Intent();
				LoginRegisterGlobalVariable.login_model = 0;
				intent.setClass(Login.this, Aty_UserCenter.class);
				startActivity(intent);
				finish();
			}else {
				Toast.makeText(getApplicationContext(), R.string.login_fail, Toast.LENGTH_SHORT)
				.show();
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_login);
		SuperID.initFaceSDK(this);
		context = this;
		/*
		 * normal login style
		 */
		final EditText editText_name = (EditText) findViewById(R.id.normal_login_username);
		final EditText editText_password = (EditText) findViewById(R.id.normal_login_password);
		Button login_btn = (Button) findViewById(R.id.normal_login_btn);
		login_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LoginRegisterGlobalVariable.login_name = editText_name.getText().toString();
				LoginRegisterGlobalVariable.login_passwd = editText_password.getText().toString();
				/**
				 * 判断用户输入是否为空
				 */
				if ("".equals(LoginRegisterGlobalVariable.login_name) || "".equals(LoginRegisterGlobalVariable.login_passwd)) {
					YoYo.with(Techniques.Shake).duration(700)
							.playOn(findViewById(R.id.input_area));
					Toast.makeText(getApplicationContext(),
							"name or password is null!", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Log.d("Thread", "into Thread");
						getInfo(LoginRegisterGlobalVariable.login_name, LoginRegisterGlobalVariable.login_passwd);
						Message msg = handle.obtainMessage();
						msg.obj = LoginRegisterGlobalVariable.login_result;
						handle.sendMessage(msg);
					}

				}).start();

			}
		});
		
		
	}

	public void getInfo(String name, String passwd) {
		String nameSpace = "http://registerlogin.server.healthSLife.com"; // The namespace of login service
		String methodName = "loginCheck"; // login method name
		String endPoint = "http://10.6.12.29:8080/axis2/services/LoginService"; // EndPoint
																				// //emulator should use 10.0.2.2
		String soapAction = "http://registerlogin.server.healthSLife.com/loginCheck"; // SOAP
																						// Action

		// 指定WebService的命名空间和调用的方法名
		SoapObject rpc = new SoapObject(nameSpace, methodName);
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
		LoginRegisterGlobalVariable.login_result = object.getProperty(0).toString();

	}

	// 人脸登录
	public void btn_superidlogin(View v) {
		LoginRegisterGlobalVariable.login_model = 1;
		SuperID.faceLogin(this);
	}

	// 接口返回
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode) {
		// 授权成功
		case SDKConfig.AUTH_SUCCESS:
			System.out.println(Cache.getCached(context, SDKConfig.KEY_APPINFO));
			System.err.println("dddbb");
			Intent intent = new Intent(this, Aty_UserCenter.class);
			startActivity(intent);
			finish();
			break;
		// 取消授权
		case SDKConfig.AUTH_BACK:

			break;
		// 找不到该用户
		case SDKConfig.USER_NOTFOUND:

			break;
		// 登录成功
		case SDKConfig.LOGINSUCCESS:
			System.out.println(Cache.getCached(context, SDKConfig.KEY_APPINFO));
			Intent i = new Intent(this, Aty_UserCenter.class);
			startActivity(i);
			finish();
			break;
		// 登录失败
		case SDKConfig.LOGINFAIL:
			break;
		// 网络有误
		case SDKConfig.NETWORKFAIL:
			break;
		// 一登SDK版本过低
		case SDKConfig.SDKVERSIONEXPIRED:
			break;
		default:
			break;
		}

	}
	
	

}