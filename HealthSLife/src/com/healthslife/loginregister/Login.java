package com.healthslife.loginregister;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.healthslife.R;
import com.healthslife.sensor.dao.SportInfoDAO;
import com.healthslife.sensor.data.SensorData;
import com.healthslife.sensor.utilities.CalculateUtil;
import com.healthslife.sensor.utilities.DBUtil;
import com.healthslife.server.DownloadServiceActivity;
import com.isnc.facesdk.SuperID;
import com.isnc.facesdk.common.Cache;
import com.isnc.facesdk.common.SDKConfig;

public class Login extends Activity {
	
	private Context context;
	Dialog dialog;
	Intent intent = new Intent();
	
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
			
			if(result.equals("hasData")) {
				//登录成功且服务器端有数据
				//用户选择是否下载数据
				showDialog();
				
			}else if(result.equals("nullData")){
				//登录成功服务器端没有数据
				Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_SHORT)
				.show();
				//往本地数据库写 null ， null
				firstLogin();
				
				
				LoginRegisterGlobalVariable.login_model = 0;
				LoginRegisterGlobalVariable.user_login_state = true;
				intent.setClass(Login.this, Aty_UserCenter.class);
				//设置完后跳到个人中心界面
				startActivity(intent);
				finish();
			}else {
				//登录失败
				Toast.makeText(getApplicationContext(), R.string.login_fail, Toast.LENGTH_SHORT)
				.show();
			}
			
			/*if (result.equals("success")) {
				Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_SHORT)
				.show();
				Intent intent = new Intent();
				LoginRegisterGlobalVariable.login_model = 0;
				LoginRegisterGlobalVariable.user_login_state = true;
				intent.setClass(Login.this, Aty_UserCenter.class);
				startActivity(intent);
				finish();
			}else {
				
			}*/
		}

	};

	/*第一次登陆操作*/
	void firstLogin(){
		SportInfoDAO dao=new SportInfoDAO(context);
		Cursor c=dao.query("select * from "+SportInfoDAO.TABLENAME_UserInfo+" where 1=1", null);//查询
		int id=-1;
		 while(c.moveToNext()) {  
			 id=c.getInt(c.getColumnIndex("id"));
			 SensorData.setUsername(LoginRegisterGlobalVariable.login_name);
			 SensorData.setGender(c.getInt(c.getColumnIndex("user_sex")));
			 SensorData.setWeight(c.getInt(c.getColumnIndex("user_weight")));
			 SensorData.setHeight(c.getInt(c.getColumnIndex("user_high")));
			 SensorData.setAim_stepNum(c.getInt(c.getColumnIndex("user_aimstep")));
			 SensorData.setLogin(true);
	     }
		 if(id!=-1){
			 ContentValues values=new ContentValues();
			 values.put("user_name",SensorData.getUsername());
			 values.put("user_sex",SensorData.getGender());
			 values.put("user_high",SensorData.getHeight());
			 values.put("user_weight",SensorData.getWeight());
			 values.put("user_aimstep",SensorData.getAim_stepNum());
			 values.put("user_islogin",SensorData.isLogin()?1:0);
			 DBUtil.update(context, SportInfoDAO.TABLENAME_UserInfo, values, "id=?", new String[]{id+""});
			 CalculateUtil.resetInit();//初始化参数:除身高、体重、性别、用户名、目标步数、登录状态之外，重置初始化所有参数!!!
		 }
		 dao.closeDB();
		 c.close();
		 
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
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
		
		String methodName = "loginCheck"; // login method name
		String endPoint = LoginRegisterGlobalVariable.urlStr + "LoginService"; // EndPoint
		String soapAction = LoginRegisterGlobalVariable.nameSpace + "/loginCheck"; // SOAP
																						// Action

		// 指定WebService的命名空间和调用的方法名
		SoapObject rpc = new SoapObject(LoginRegisterGlobalVariable.nameSpace, methodName);
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
			Toast.makeText(getApplicationContext(), "没有连接网络，请检查网络设置", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(Login.this , Login.class); 
			startActivity(intent);
			finish();
		}

		// 获取返回的数据
		SoapObject object = (SoapObject) envelope.bodyIn;
		// 获取返回的结果
	
		if (null == object.getProperty(0)) {
			LoginRegisterGlobalVariable.login_result = "fail";
		} else {
			LoginRegisterGlobalVariable.login_result = object.getProperty(0).toString();
		}
		
	}

	public void register_btn(View v) {
		Intent intent = new Intent(Login.this , Register.class);
		startActivity(intent);
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
	
	private void showDialog() {
		View view = getLayoutInflater().inflate(R.layout.down_data_dialog,
				null);
		dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.main_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = 0;
		wl.y = getWindowManager().getDefaultDisplay().getHeight();
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

		// 设置显示位置
		dialog.onWindowAttributesChanged(wl);
		// 设置点击外围解散
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	public void on_click(View v) {
		switch (v.getId()) {
		case R.id.down_data:
			//下载数据
			
//			String userData = DownloadUserData.downloadUserData(LoginRegisterGlobalVariable.login_name);
//			String userInfo = DownloadUserInfo.downloadUserInfo(LoginRegisterGlobalVariable.login_name);
//			CalculateUtil.LoginMark(context, userInfo, userData);
			//如果下载成功跳到个人中心（进度条）
			String str = LoginRegisterGlobalVariable.login_name;
			LoginRegisterGlobalVariable.login_model = 0;
			LoginRegisterGlobalVariable.user_login_state = true;
			intent.setClass(Login.this, DownloadServiceActivity.class);
			startActivity(intent);
			finish();
			//如果下载失败由用户选择是否重新下载（弹出对话框）
			
			break;
		case R.id.not_down_data:
			//不下载数据（传参数null,null）
			
			//清空所有数据，除用户名和密码之外!!
			firstLogin();
			
			LoginRegisterGlobalVariable.login_model = 0;
			LoginRegisterGlobalVariable.user_login_state = true;
			intent.setClass(Login.this, Aty_UserCenter.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}


}