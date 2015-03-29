package com.healthslife.system;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthslife.R;
import com.healthslife.health.HealthServiceActivity;
import com.healthslife.sensor.dao.SportInfoDAO;
import com.healthslife.sensor.data.SensorData;
import com.healthslife.sensor.utilities.CalculateUtil;
import com.healthslife.sensor.utilities.DBUtil;

public class WeightSettingActivity extends Activity {
	
	private boolean iskilledbyfinishbutton = false;
	RelativeLayout bg_num_rl;
	RelativeLayout bg_item_ruler;// 标尺
//	TextView tvScale;// 血糖数值
//	TextView bg_time;// 血糖数值
	TextView ntvScale;//显示数值----我写的
	HorizontalScrollView hsv_scaleplate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 竖屏幕
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.weightsetting_activity);
		ImageView person = (ImageView)findViewById(R.id.girl_or_boy_weight);
		
		if(SettingActivity.sex){
			person.setImageResource(R.drawable.person_man);
		}else{
			person.setImageResource(R.drawable.person_woman);
		}
		
//		tvScale = (TextView)findViewById(R.id.bg_remal1_weight);
		ntvScale = (TextView) findViewById(R.id.bg_remal1_weight);
		DecimalFormat fnum = new DecimalFormat("##0.0");
		ntvScale.setText("" + UserMessage_system.WEIGHT);
		
		
		hsv_scaleplate = (HorizontalScrollView)findViewById(
				R.id.hsv_scaleplate_weight);
		scaleRuler(UserMessage_system.WEIGHT, ntvScale, hsv_scaleplate);// 刻度尺
		
		Button preButton = (Button)findViewById(R.id.pre_button_weight);
		preButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WeightSettingActivity.this.finish();
			}
		});
		
		Button finishButton = (Button)findViewById(R.id.finish_button_weight);
		finishButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(SensorData.isFirstUse) {
					int user_sex=SettingActivity.sex?1:0;//性别
					int user_high=(int) SettingActivity.height;//体重
					int user_weight=(int) SettingActivity.weight;//身高
					int user_aimstep=7000;//默认目标步行数目
					int user_islogin=0;//用户登录状态
					ContentValues values=new ContentValues();
					values.put("user_name",SensorData.getUsername());
					values.put("user_sex",user_sex);
					values.put("user_high",user_high);
					values.put("user_weight",user_weight);
					values.put("user_aimstep",user_aimstep);
					values.put("user_islogin",user_islogin);
					/*向数据库中添加数据*/
					DBUtil.insert(WeightSettingActivity.this, SportInfoDAO.TABLENAME_UserInfo, values);//第一条为插入操作
//					SensorData.setUsername();
				 	SensorData.setGender(user_sex);
				 	SensorData.setWeight(user_weight);
				 	SensorData.setHeight(user_high);
				 	SensorData.setAim_stepNum(user_aimstep);
				 	SensorData.setLogin(user_islogin==1?true:false);//设置登录状态
//				 	CalculateUtil.ComputStepLengh();//计算步长
				 	CalculateUtil.resetInit();//初始化参数:除身高、体重、性别、用户名、目标步数、登录状态之外，重置初始化所有参数!!!
				 	
				 	SexChoiceActivity.isFinished = true;
				 	iskilledbyfinishbutton = true;
				 	
				 	SensorData.isFirstUse=false;//点击完成按钮之后，不再是第一次使用该APP了!!
				 	
				 	Intent intent = new Intent(WeightSettingActivity.this , HealthServiceActivity.class);
				 	startActivity(intent);
				 	WeightSettingActivity.this.finish();
				}else{
					iskilledbyfinishbutton = true;
					SexChoiceActivity.isFinished = true;
					WeightSettingActivity.this.finish();
					
					//#########################################需要加数据!!!
					int user_sex=SettingActivity.sex?1:0;//性别
					int user_weight=(int) SettingActivity.height;//体重
					int user_high=(int) SettingActivity.weight;//身高
					SensorData.setGender(user_sex);
				 	SensorData.setWeight(user_weight);
				 	SensorData.setHeight(user_high);
				}
			}
		});
		
		//返回箭头
		ImageView backImageView = (ImageView)findViewById(R.id.backimage_in_weightsetting);
		backImageView.setOnClickListener(new OnClickListener() {
					
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WeightSettingActivity.this.finish();
			}
		});
	}
	// 刻度尺
		private void scaleRuler(float bgvalue, final TextView nTvScale,
				final HorizontalScrollView nHsv_scaleplate) {
			DisplayMetrics mDisplayMetrics = new DisplayMetrics();
			this.getWindowManager().getDefaultDisplay()
					.getMetrics(mDisplayMetrics);
			final float nDensity = (float) 1 / mDisplayMetrics.density;

			nHsv_scaleplate.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					DecimalFormat fnum = new DecimalFormat("##0.0");
					final float a = 35.00f + (float) nHsv_scaleplate.getScrollX()
							* nDensity / 16;
					nTvScale.setText(fnum.format(a));
					SettingActivity.weight = Float.parseFloat(fnum.format(a));
					return false;
				}
			});

			final int iScale = (int) ((bgvalue - 35.00f) * 16 / nDensity);
			new Handler().postDelayed((new Runnable() {
				@Override
				public void run() {
					nHsv_scaleplate.scrollTo(iScale, 0);
				}
			}), 5);
		}
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			if(iskilledbyfinishbutton){
				UserMessage_system.setSex(this, SettingActivity.sex);
				UserMessage_system.setHeight(this, SettingActivity.height);
				UserMessage_system.setWeight(this, SettingActivity.weight);
				UserMessage_system.setParameters(this);
			}
			super.onDestroy();
		}
}
