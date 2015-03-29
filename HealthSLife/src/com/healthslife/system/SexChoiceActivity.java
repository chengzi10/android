package com.healthslife.system;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.healthslife.R;

public class SexChoiceActivity extends Activity {

	public static boolean isFinished = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//在程序启动时必须首先调用这3行代码
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        UserMessage_system.setParameters(this);
        MusicMessage_system.setValues(this);
        
		super.onCreate(savedInstanceState);
		isFinished = false;
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 竖屏幕
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.sex_choice_activity);
		//男孩图片被点击
		Button boy = (Button)findViewById(R.id.boy_button);
		boy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent itIntent  = new Intent(SexChoiceActivity.this,HeightSettingActivity.class);
				SettingActivity.sex = true;
				startActivity(itIntent);
			}
		});
		//女孩图片被点击
		Button girl = (Button)findViewById(R.id.girl_button);
		girl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent itIntent  = new Intent(SexChoiceActivity.this,HeightSettingActivity.class);
				SettingActivity.sex = false;
				startActivity(itIntent);
			}
		});
		//上一步按钮
		Button preButton = (Button)findViewById(R.id.pre_button);
		preButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SexChoiceActivity.this.finish();
			}
		});
		//返回箭头
		ImageView backImageView = (ImageView)findViewById(R.id.backimage_in_sexchoice);
		backImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SexChoiceActivity.this.finish();
			}
		});
	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(SexChoiceActivity.isFinished) {
			SexChoiceActivity.this.finish();
		}
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if(isFinished) {
			SexChoiceActivity.this.finish();
		}
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		super.onDestroy();
	}
}
