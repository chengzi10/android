package com.healthslife.system;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.healthslife.R;
import com.healthslife.health.HealthServiceActivity;
import com.healthslife.loginregister.LoginRegisterGlobalVariable;
import com.healthslife.sensor.dao.SportInfoDAO;
import com.healthslife.sensor.data.SensorData;
import com.healthslife.sensor.utilities.CalculateUtil;
import com.healthslife.server.UploadServiceActivity;
import com.healthslife.system.SlideSwitch.SlideListener;


public class SettingActivity extends Activity implements SlideListener{

	public static boolean sex = true;
	public static float height;
	public static float weight;
	//退出登录按钮
	private Button quit_btn;
	Dialog dialog;
	//获取播报频率的textview  @+id/backimage_in_settingactivity
	private TextView sequencesetting;
	private LinearLayout btn_layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.setting_activity_layout);
		//获取参数
		UserMessage_system.setParameters(this);
		//获取播报频率的textview
		sequencesetting = (TextView)findViewById(R.id.sequence_textview);
		btn_layout = (LinearLayout)findViewById(R.id.btn_layout);
		//给开关设监听器
		SlideSwitch slide = (SlideSwitch)findViewById(R.id.sequence_swit);
		slide.setSlideListener(this);
		//设置开关初始状态
		slide.moveToDest(UserMessage_system.ISSPEAKEROPEN);
		//点击跳出简介界面
		TextView summaryTextView = (TextView)findViewById(R.id.summary_textview);
		summaryTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(SettingActivity.this,SummaryActivity.class);
				startActivity(it);
			}
		});
		//点击跳出音乐设置界面
		TextView musicsettingTextView = (TextView)findViewById(R.id.music_setting_textview);
		musicsettingTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(SettingActivity.this,Music_Setting_Activity_system.class);
				startActivity(it);
			}
		});
		
		//点击跳出播报设置的界面
		sequencesetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Toast.makeText(SettingActivity.this, "可以", Toast.LENGTH_LONG).show();
				Intent it = new Intent(SettingActivity.this,SetStepsActivity.class);
				startActivity(it);
			}
		});
		
		//个人信息设置
		TextView usermsg = (TextView)findViewById(R.id.user_msg_textview);
		usermsg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(SettingActivity.this,SexChoiceActivity.class);
				startActivity(it);
			}
		});
		//关于页面
		TextView aboutTextView = (TextView)findViewById(R.id.about_text_view);
		aboutTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(SettingActivity.this,AboutActivity.class);
				startActivity(it);
			}
		});
		//目标设置页面
		TextView targetTextView = (TextView)findViewById(R.id.target_textview);
		targetTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(SettingActivity.this,TargetSettingActivity.class);
				startActivity(it);
			}
		});
		//TextView 版本更新
		TextView hasnew = (TextView)findViewById(R.id.hasnew);
		hasnew.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(SettingActivity.this, "你使用的纯动是最新版本！", Toast.LENGTH_LONG).show();;
			}
		});
		ImageView backImageView = (ImageView)findViewById(R.id.backimage_in_settingactivity);
		backImageView.setOnClickListener(new OnClickListener() {
					
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SettingActivity.this.finish();
			}
		});
		
		/*
		 * 是否处于登录状态  为登录状态时是True
		 */
		if(!SensorData.isLogin()) {
			btn_layout.setVisibility(View.INVISIBLE);
		}
		
	}

	@Override
	public void open() {
		// TODO Auto-generated method stub
		UserMessage_system.setIsspeakeropen(this, true);
		UserMessage_system.setParameters(SettingActivity.this);
		sequencesetting.setClickable(true);
		sequencesetting.setTextColor(Color.rgb(0, 0, 0));
		sequencesetting.invalidate();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		UserMessage_system.setIsspeakeropen(this, false);
		UserMessage_system.setParameters(SettingActivity.this);
		sequencesetting.setClickable(false);
		sequencesetting.setTextColor(Color.rgb(239, 239, 244));
		sequencesetting.invalidate();
	}

	
	public void quit_btn(View v) {
		showDialog();
	}
	
	private void showDialog() {
		View view = getLayoutInflater().inflate(R.layout.upload_data_dialog,
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
		case R.id.upload_data:
			//上传数据
			//如果上传成功跳到？（进度条）
			LoginRegisterGlobalVariable.user_login_state = false;
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, UploadServiceActivity.class);
			startActivity(intent);
			finish();
			//如果下载失败由用户选择是否重新下载（弹出对话框）
			
			break;
		case R.id.not_upload_data:
			//不上传数据（传参数null,null）
			
			//清空所有数据，除用户名和密码之外!!
			loginoutDeleteData();
			
			LoginRegisterGlobalVariable.login_model = 0;
			LoginRegisterGlobalVariable.user_login_state = true;
			Intent return_intent = new Intent();
			return_intent.setClass(SettingActivity.this, HealthServiceActivity.class);
			startActivity(return_intent);
			finish();
			break;
		case R.id.cancel://##########################################################
			dialog.cancel();
			break;
		default:
			break;
		}
	}
	
	/*退出不保存数据*/
	void loginoutDeleteData(){
		SportInfoDAO dao=new SportInfoDAO(SettingActivity.this);
		ContentValues values= new ContentValues();
		values.put("user_islogin", 0);
		SensorData.setLogin(false);//未登录状态!!!
		dao.update(SportInfoDAO.TABLENAME_UserInfo, values, "user_name=?", new String[]{SensorData.getUsername()});
		dao.dropTable(SensorData.getUsername());
		CalculateUtil.resetInit();
	}
	
}
