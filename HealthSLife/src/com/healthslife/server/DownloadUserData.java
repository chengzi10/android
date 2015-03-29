package com.healthslife.server;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.healthslife.R;
import com.healthslife.loginregister.LoginRegisterGlobalVariable;

public class DownloadUserData {

	/*private Handler handle = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Log.d("handle", "into handle");
			String result = msg.obj.toString();
			Log.d("TAG", result);
			Toast.makeText(getApplicationContext(),
					result, Toast.LENGTH_SHORT).show();
			if (result.equals("true")) {
				Toast.makeText(getApplicationContext(),
						"写入数据成功", Toast.LENGTH_SHORT).show();
//				Intent intent = new Intent();
//				LoginRegisterGlobalVariable.login_model = 0;
//				intent.setClass(Register.this, Login.class);
//				startActivity(intent);
//				finish();
			} else {
				Toast.makeText(getApplicationContext(), "写入数据失败",
						Toast.LENGTH_SHORT).show();
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_service);
		
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				String userInfo = DownloadUserData.downloadUserData("chengzi");
				//String userInfo = DownloadUserInfo.downloadUserInfo(LoginRegisterGlobalVariable.login_name);
			//	String str = CalculateUtil.LoginMark(context, userInfo, userData);
				Message msg = handle.obtainMessage();
				msg.obj = userInfo;
				handle.sendMessage(msg);
			}
			
		}.start();
	}*/
	
	public static String downloadUserData(String tableName) {
		String methodName = "downUserDataStr"; // login method name
		String endPoint = ServiceGlobalVariable.serverUrl+"DownUserDataService"; // EndPoint
		String soapAction =ServiceGlobalVariable.downloadNameSpace+"/downUserDataStr"; // SOAP
																					// Action

		// 指定WebService的命名空间和调用的方法名
		SoapObject rpc = new SoapObject(ServiceGlobalVariable.downloadNameSpace,
				methodName);
		// 设置调用webService接口需要传入的参数
		rpc.addProperty("tableName", tableName);

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
		String info = object.getProperty(0).toString();
		return info;
	}
	
}