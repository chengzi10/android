package com.healthslife.system;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.healthslife.R;
import com.healthslife.sensor.data.SensorData;

public class SetStepsActivity extends Activity {

	private EditText showsteps;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 竖屏幕
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.setsteps_sequence);
		
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		WindowManager wm = getWindowManager();
		Display display = wm.getDefaultDisplay();
		final DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		showsteps = (EditText)findViewById(R.id.show_steps);
		//获取标尺背景
		LinearLayout rule = (LinearLayout)findViewById(R.id.rule_image);
		//实例一个手图形
		final HandView hand = new HandView(this);
		hand.setMinimumHeight(40);
		hand.setMinimumWidth(40);
		//设定手的初始位置
		int s = UserMessage_system.SEQUENCE;
		showsteps.setText(UserMessage_system.SEQUENCE + "");
		
		if(s >= 20000){
			hand.currentY = (50000 - s)/2000*((1660 - 152)/35) + 152;
		}else if((s <= 20000)&&(s > 5000)){
			hand.currentY = (float)((20000 - s)/1000*((1660 - 152)/35) + 798.285714);
		}else if((s <= 5000)&&(s >= 1000)){
			hand.currentY = (float)((20000 - s)/800*((1660 - 152)/35) + 1444.57143);
		}else{
			hand.currentY = 1660;
		}
//		hand.currentY = UserMessage_system.SEQUENCE;
		
		
		//把手加到尺子上
		rule.addView(hand);
		//显示文字  -- 步数
		
		//点击保存按钮
		Button saveButton = (Button)findViewById(R.id.savesequence_button);
		saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(showsteps.getText().toString().equals("")){
					Toast.makeText(SetStepsActivity.this, "不可以为空的哦！", Toast.LENGTH_LONG).show();
				}else{
					UserMessage_system.setSequence(SetStepsActivity.this, Integer.parseInt(showsteps.getText().toString()));
					UserMessage_system.setParameters(SetStepsActivity.this);
					SensorData.setSpeakHZ_StepNum(UserMessage_system.SEQUENCE);//获取播报步数频率
					SetStepsActivity.this.finish();
				}
			}
		});
		
		//监听手势，让手滑动
		hand.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				hand.currentX = 0;
				int steps = 1000;
				if(event.getY() <= 152){
					hand.currentY = 152;
				}else if(event.getY() >= 1660){
					hand.currentY = 1660;
				}else{
					hand.currentY = event.getY();
					}
				hand.invalidate();
				
				//设定步数值
				if(hand.currentY >= 152 && hand.currentY <= 798.285714)/*(1640 - 132)/35*4)*/{
					steps = (int)(50000 - (hand.currentY - 152)/((1660 - 152)/35)*2000);
				}else if(hand.currentY >= 798.285714 && hand.currentY <= 1444.57143){
					steps = (int)(20000 - (hand.currentY - 798.285714)/((1660 - 152)/35)*1000);
				}else{
					steps = (int)(5000 - (hand.currentY - 1444.57143)/((1660 - 152)/35)*800);
				}
				if(steps < 1000){
					steps = 10;
				}
				showsteps.setText(steps+"");
				showsteps.invalidate();
				return true;
			}
		});
		
		//返回按钮
		ImageView backImageView = (ImageView)findViewById(R.id.backimage_in_setsteps);
		backImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SetStepsActivity.this.finish();
			}
		});
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		super.onDestroy();
	}

}
