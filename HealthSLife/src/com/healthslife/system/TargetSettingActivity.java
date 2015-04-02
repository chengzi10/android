package com.healthslife.system;

import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.healthslife.R;
import com.healthslife.sensor.dao.SportInfoDAO;
import com.healthslife.sensor.data.SensorData;
import com.healthslife.sensor.utilities.DBUtil;

public class TargetSettingActivity extends Activity {

	private ImageView actor;
	private ImageView normal;
	private ImageView active;
	private ImageView crazy;
	private EditText stepsEditText;
	private EditText calEditText;
	private int steps;
	private int cal;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 竖屏幕
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.targetsetting_activity);
		actor = (ImageView)findViewById(R.id.walk_image);
		normal = (ImageView)findViewById(R.id.normal_guy);
		active = (ImageView)findViewById(R.id.activity_guy);
		crazy = (ImageView)findViewById(R.id.crazy_guy);
		
		stepsEditText = (EditText)findViewById(R.id.steps_set_edit);
		calEditText = (EditText)findViewById(R.id.cal_set_edit);
		
		normal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				actor.setImageResource(R.drawable.walk_image);
				normal.setImageResource(R.drawable.ab);
				active.setImageResource(R.drawable.ba);
				crazy.setImageResource(R.drawable.ca);
				stepsEditText.setText("7000");
				calEditText.setText("200");
				
				actor.invalidate();
				normal.invalidate();
				active.invalidate();
				crazy.invalidate();
				stepsEditText.invalidate();
				calEditText.invalidate();
			}
		});
		
		active.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				actor.setImageResource(R.drawable.basketball_boy_image);
				normal.setImageResource(R.drawable.aa);
				active.setImageResource(R.drawable.bb);
				crazy.setImageResource(R.drawable.ca);
				stepsEditText.setText("10000");
				calEditText.setText("300");
				
				actor.invalidate();
				normal.invalidate();
				active.invalidate();
				crazy.invalidate();
				stepsEditText.invalidate();
				calEditText.invalidate();
			}
		});
		
		crazy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				actor.setImageResource(R.drawable.jump_image);
				normal.setImageResource(R.drawable.aa);
				active.setImageResource(R.drawable.ba);
				crazy.setImageResource(R.drawable.cb);
				stepsEditText.setText("15000");
				calEditText.setText("400");
				
				actor.invalidate();
				normal.invalidate();
				active.invalidate();
				crazy.invalidate();
				stepsEditText.invalidate();
				calEditText.invalidate();
			}
		});
		
		//返回箭头
		ImageView backImageView = (ImageView)findViewById(R.id.backimage_in_targetsetting);
		backImageView.setOnClickListener(new OnClickListener() {
					
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TargetSettingActivity.this.finish();
			}
		});
		
		//保存按钮
		Button saveButton = (Button)findViewById(R.id.savebutton_in_target);
		saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(stepsEditText.getText().toString().equals("")||calEditText.getText().toString().equals("")){
					Toast.makeText(TargetSettingActivity.this, "不可以为空的哦！", Toast.LENGTH_LONG).show();
				}else{
					doSthing();
				}
			}
		});
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		/*steps = Integer.parseInt(stepsEditText.getText().toString());
		cal = Integer.parseInt(calEditText.getText().toString());
		
		UserMessage_system.setSteps(this, steps);
		UserMessage_system.setCalories(this, cal);
		UserMessage_system.setParameters(this);
		SensorData.setAim_stepNum(UserMessage_system.TARGET_STEPS);//设置目标步数!
		SensorData.setAim_energy(UserMessage_system.TARGET_CALORIES);//设置能量目标!
		*/
		
		super.onDestroy();
	}
	
	private void doSthing(){
		steps = Integer.parseInt(stepsEditText.getText().toString());
		cal = Integer.parseInt(calEditText.getText().toString());
		
		UserMessage_system.setSteps(TargetSettingActivity.this, steps);
		UserMessage_system.setCalories(TargetSettingActivity.this, cal);
		UserMessage_system.setParameters(TargetSettingActivity.this);
		SensorData.setAim_stepNum(UserMessage_system.TARGET_STEPS);//设置目标步数!
		SensorData.setAim_energy(UserMessage_system.TARGET_CALORIES);//设置能量目标!
		
		/*更新数据库*/
		ContentValues values=new ContentValues();
		values.put("user_aimstep", SensorData.getAim_stepNum());
		DBUtil.update(TargetSettingActivity.this, SportInfoDAO.TABLENAME_UserInfo,
				values, "user_name=?", new String[]{SensorData.getUsername()});
		
		
		TargetSettingActivity.this.finish();
	}

}
