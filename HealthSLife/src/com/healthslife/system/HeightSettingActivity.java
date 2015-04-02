package com.healthslife.system;

import java.text.DecimalFormat;

import android.app.Activity;
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


public class HeightSettingActivity extends Activity {

	RelativeLayout bg_num_rl;
	RelativeLayout bg_item_ruler;// 血糖标尺
//	TextView tvScale;// 血糖数值
//	TextView bg_time;// 血糖数值
	TextView nTvScale;//显示数值----我写的
	HorizontalScrollView hsv_scaleplate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 竖屏幕
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.heightsetting_activity);
		nTvScale = (TextView)findViewById(R.id.bg_remal1_height);
		ImageView person = (ImageView)findViewById(R.id.girl_or_boy_height);
		if(SettingActivity.sex){
			person.setImageResource(R.drawable.person_man);
		}else{
			person.setImageResource(R.drawable.person_woman);
		}
		
//		tvScale = (TextView) findViewById(R.id.bg_remal1_height);
		hsv_scaleplate = (HorizontalScrollView)findViewById(
				R.id.hsv_scaleplate_height);
		
		scaleRuler(UserMessage_system.HEIGHT, nTvScale, hsv_scaleplate);// 刻度尺
		
		nTvScale.setText(""+UserMessage_system.HEIGHT);
		//上一步按钮
		Button preButton = (Button)findViewById(R.id.pre_button_height);
		preButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HeightSettingActivity.this.finish();
			}
		});
		
		//下一步按钮
		Button nextButton = (Button)findViewById(R.id.next_button_height);
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Toast.makeText(HeightSettingActivity.this, UserMessage_system.HEIGHT + "", Toast.LENGTH_LONG).show();
				Intent itIntent = new Intent(HeightSettingActivity.this,WeightSettingActivity.class);
				startActivity(itIntent);
			}
		});
		
		//返回箭头
		ImageView backImageView = (ImageView) findViewById(R.id.backimage_in_heightsetting);
		backImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				HeightSettingActivity.this.finish();
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
					final float a = 45.00f + (float) nHsv_scaleplate.getScrollX()
							* nDensity / 16;
					nTvScale.setText(fnum.format(a));
					SettingActivity.height = Float.parseFloat(fnum.format(a));
					return false;
				}
			});

			final int iScale = (int) ((bgvalue - 45.00f) * 16 / nDensity);
			new Handler().postDelayed((new Runnable() {
				@Override
				public void run() {
					nHsv_scaleplate.scrollTo(iScale, 0);
				}
			}), 5);
		}
		
		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			if(SexChoiceActivity.isFinished) {
				HeightSettingActivity.this.finish();
			}
			super.onResume();
		}

		
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			
			super.onDestroy();
		}
}
