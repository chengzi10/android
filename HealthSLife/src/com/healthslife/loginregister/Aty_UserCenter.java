package com.healthslife.loginregister;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.healthslife.R;
import com.healthslife.change.ChangeHeadPortrait;
import com.healthslife.health.HealthServiceActivity;
import com.healthslife.system.SettingActivity;
import com.isnc.facesdk.SuperID;
import com.isnc.facesdk.SuperID.IntFailCallback;
import com.isnc.facesdk.SuperID.IntSuccessCallback;
import com.isnc.facesdk.common.Cache;
import com.isnc.facesdk.common.SDKConfig;
import com.isnc.facesdk.common.Utils;
import com.isnc.facesdk.net.AsyncBitmapLoader;
import com.isnc.facesdk.net.AsyncBitmapLoader.ImageCallBack;

public class Aty_UserCenter extends Activity {
	private Context context;
	private Button btn_spbundle;
	public static ImageView icon_sp, avatarimg;
	private TextView tv_phonenum, tv_name;
	private String userinfo;
	private RelativeLayout layout;
	
	
	@SuppressLint("HandlerLeak")
	private Handler handle = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Log.d("handle", "into handle");
			String result = msg.obj.toString();
			Log.d("TAG", result);

			if (result.equals("insert") || result.equals("exist")) {
				Toast.makeText(getApplicationContext(),
						R.string.bundle_success, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), R.string.bundle_fail,
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				LoginRegisterGlobalVariable.login_model = 0;
				intent.setClass(Aty_UserCenter.this, Login.class);
				startActivity(intent);
				finish();
			}
		}

	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.aty_usercenter);
		context = this;
		avatarimg = (ImageView) findViewById(R.id.avatarimg);
		//tv_phonenum = (TextView) findViewById(R.id.tv_phonenum);
		tv_name = (TextView) findViewById(R.id.tv_name);
		layout = (RelativeLayout) findViewById(R.id.relative_face);

		// 开发者应用自己用户的信息
		userinfo = SuperID.formatInfo(this, "name", "Someone", "email", "someone@superid.me", "avatar",
				"http://spapi1.qiniudn.com/res/avatar.jpg");

		// 使用人脸登录后获取的一登用户信息
		String appinfo = Cache.getCached(context, SDKConfig.KEY_APPINFO);
		if (!appinfo.equals("") && 1 == LoginRegisterGlobalVariable.login_model) {
			try {
				JSONObject obj = new JSONObject(appinfo);
				tv_phonenum.setText(obj.getString(SDKConfig.KEY_PHONENUM));
				tv_name.setText(Utils.judgeChina(obj.getString(SDKConfig.KEY_NAME), 10));

				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Log.d("Thread", "into Thread");
						BundleService.bundleInfo(tv_phonenum.getText()
								.toString());
						Message msg = handle.obtainMessage();
						msg.obj = LoginRegisterGlobalVariable.bundle_result;
						handle.sendMessage(msg);
					}

				}).start();
				AsyncBitmapLoader asyncBitmapLoader = new AsyncBitmapLoader();
				asyncBitmapLoader.loadBitmap(this, Cache.getCached(context, SDKConfig.KEY_PHONENUM), avatarimg,
						obj.getString(SDKConfig.KEY_AVATAR), new ImageCallBack() {

							public void imageLoad(ImageView imageView, Bitmap bitmap) {
								imageView.setImageBitmap(Utils.getRoundedCornerBitmap(bitmap, 480));
							}
						});
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			// 开发者应用的用户信息
			layout.setVisibility(View.INVISIBLE);
			avatarimg.setImageResource(R.drawable.superid_avatar_img_default);
			tv_name.setText(LoginRegisterGlobalVariable.login_name);
		}

	}

	public void change_head(View v){
		Intent intent = new Intent(Aty_UserCenter.this , ChangeHeadPortrait.class);
		startActivity(intent);
	}
	
	public void btn_unbundle(View v) {
		if (!Cache.getCached(context, SDKConfig.KEY_ACCESSTOKEN).equals("")) {
			// 解除绑定
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("是否解除与一登账号的绑定？").setCancelable(false)
					.setPositiveButton("是", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {

							// 解绑
							SuperID.userCancelAuthorization(context, new IntSuccessCallback() {

								@Override
								public void onSuccess(int arg0) {
									Cache.deleCached(context, SDKConfig.KEY_ACCESSTOKEN);
									Cache.deleCached(context, "demo_phone");
									btn_spbundle.setText("点击绑定");
									btn_spbundle.setTextColor(getResources().getColor(
											R.color.s_demo_color_background_red));
									icon_sp.setImageDrawable(getResources().getDrawable(
											R.drawable.superid_demo_binding_superid_ico_disable));
								}
							}, new IntFailCallback() {

								@Override
								public void onFail(int error) {
									Toast.makeText(context, "解绑失败", Toast.LENGTH_SHORT).show();
								}
							});

						}
					}).setNegativeButton("否", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					}).show();

		} else {
			//绑定一登账号 FaceBundle(Activity activity, String uid, String userinfo)
			SuperID.faceBundle(Aty_UserCenter.this, String.valueOf(System.currentTimeMillis()), userinfo);
		}

	}

	// 有刷脸界面获取表情接口返回请查看onActivityResult，数据处理请查看Aty_AppGetFaceEmotion.class
	public void btn_facedata(View v) {
		SuperID.GetFaceEmotion(this);
	}

	// 无刷脸界面获取表情，接口返回及数据处理请查看Aty_GetFaceEmotion.class
	public void btn_sciencefacedata(View v) {

		startActivity(new Intent(this, Aty_GetFaceEmotion.class));
	}

	public void btn_health(View v) {
		Intent intent = new Intent(this, HealthServiceActivity.class);
		startActivity(intent);
		finish();
	}

	// 退出登录
	public void btn_logout(View v) {
		SuperID.faceLogout(this);
		Intent intent = new Intent(this, Login.class);
		startActivity(intent);
		finish();
	}

	// 接口返回
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode) {
		// 授权成功
		case SDKConfig.AUTH_SUCCESS:
			File file = new File(SDKConfig.TEMP_PATH + "/" + Cache.getCached(context, SDKConfig.KEY_PHONENUM) + ".JPEG");
			if (file != null && file.exists()) {
				Bitmap bm = BitmapFactory.decodeFile(SDKConfig.TEMP_PATH + "/"
						+ Cache.getCached(context, SDKConfig.KEY_PHONENUM) + ".JPEG");
				avatarimg.setImageBitmap(Utils.getRoundedCornerBitmap(bm, 480));
			}
			// 授权成功后 获取一登用户信息
			String appinfo = Cache.getCached(context, SDKConfig.KEY_APPINFO);
			if (!appinfo.equals("")) {
				try {
					JSONObject obj = new JSONObject(appinfo);
					tv_phonenum.setText(obj.getString(SDKConfig.KEY_PHONENUM));
					tv_name.setText(Utils.judgeChina(obj.getString(SDKConfig.KEY_NAME), 10));

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			btn_spbundle.setText("解除绑定");
			btn_spbundle.setTextColor(getResources().getColor(R.color.s_color_font_contant));
			icon_sp.setImageDrawable(getResources().getDrawable(R.drawable.superid_demo_binding_superid_ico_normal));
			break;

		// 网络连接失败
		case SDKConfig.NETWORKFAIL:
			break;
		// 有界面刷脸获取人脸表情成功
		case SDKConfig.GETEMOTIONRESULT:
			Intent intent = new Intent(this, Aty_AppGetFaceEmotion.class);
			intent.putExtra("facedata", data.getStringExtra(SDKConfig.FACEDATA));
			startActivity(intent);
			break;
		// 有界面刷脸获取人脸表情失败
		case SDKConfig.GETEMOTION_FAIL:
			Toast.makeText(context, "获取人脸信息失败", Toast.LENGTH_SHORT).show();
			break;
		// 取消授权
		case SDKConfig.AUTH_BACK:
			break;
		// 一登SDK版本过低
		case SDKConfig.SDKVERSIONEXPIRED:
			break;
			
		
		default:
			break;
		}

	}
	
	public void setting_center(View v) {
		Intent intent = new Intent(Aty_UserCenter.this,SettingActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}

