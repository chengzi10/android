package com.healthslife.loginregister;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.healthslife.R;

public class IdentifyCode extends Activity{
	private TextView textView;
	private EditText editText_name;
	private EditText editText_password;
	String name = null;
	String passwd = null;
	String result; // the data of return

	/*
	 * ���߳�
	 */
	private Handler handle = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Log.d("handle", "into handle");
			String result = msg.obj.toString();
			Log.d("TAG", result);
			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG)
					.show();
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		editText_name = (EditText) findViewById(R.id.username);
		editText_password = (EditText) findViewById(R.id.password);
		Button login_btn = (Button) findViewById(R.id.login_btn);
		textView = (TextView) findViewById(R.id.notice);

		login_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				name = editText_name.getText().toString();
				passwd = editText_password.getText().toString();
				if ("".equals(name) || "".equals(passwd)) {
					YoYo.with(Techniques.Shake).duration(700).playOn(
							findViewById(R.id.edit_area));
					Toast.makeText(getApplicationContext(),
							"name or password is null!", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				Log.d("Thread", "out of Thread");
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Log.d("Thread", "into Thread");
						getInfo(name, passwd);
						Message msg = handle.obtainMessage();
						msg.obj = result;
						handle.sendMessage(msg);
					}

				}).start();

			}
		});
	}

	public void getInfo(String name, String passwd) {
		String nameSpace = "http://registerlogin.server.healthSLife.com"; // ���������ռ�
		String methodName = "loginCheck"; // ���õķ�������
		String endPoint = "http://10.6.11.17:8080/axis2/services/LoginService"; // EndPoint
																				// //ģ������10.0.2.2������ǵ��Ե�IP��ַ
		String soapAction = "http://registerlogin.server.healthSLife.com/loginCheck"; // SOAP
																						// Action

		// ָ��WebService�������ռ�͵��õķ�����
		SoapObject rpc = new SoapObject(nameSpace, methodName);
		// ���õ���webService�ӿ���Ҫ����Ĳ���
		rpc.addProperty("name", name);
		rpc.addProperty("passwd", passwd);

		// ���ɵ���WebService������SOAP������Ϣ����ָ��SOAP�İ汾
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER10);
		envelope.bodyOut = rpc;

		HttpTransportSE transport = new HttpTransportSE(endPoint);
		try {
			// ����WebService
			transport.call(soapAction, envelope);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// ��ȡ���ص�����
		SoapObject object = (SoapObject) envelope.bodyIn;
		// ��ȡ���صĽ��
		result = object.getProperty(0).toString();

	}

}