package com.healthslife.sensor.activity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthslife.R;
import com.healthslife.sensor.dao.SportInfoDAO;
import com.healthslife.sensor.data.SensorData;
import com.healthslife.sensor.utilities.CalculateUtil;
import com.healthslife.share.ScreenShot;
import com.healthslife.system.SexChoiceActivity;
import com.healthslife.system.TargetSettingActivity;

public class DataAnalysisActivity extends Activity {
	private static String TAG = "ShareActivity";
	public static String imagePathString = "/sdcard/datascreen.png"; // 截取图片的保存路径
	ImageButton back;// 返回按钮------------------点击返回“个人中心”
	ImageButton share;// 分享按钮-----------------点击“分享数据”【未设置】

	ImageView face;// 头像【可变】-----------------点击“修改个人资料”
	TextView data_analysis_usename;// 显示用户名--
	TextView gender;// 性别【可变】----
	TextView height;// 身高【可变】----
	TextView weight;// 体重【可变】-----
	TextView alter_info;// 修改“个人资料”---------点击“修改个人资料”

	// TextView aim_lable;//展示“标题”
	TextView finished_aim;// 展示“完成目标的天数”【可变】
	TextView aim_stepnum;// 展示“目标步数”【可变】------------------点击“修改个人目标”
	TextView finished_percent;// 展示“完成目标百分比”【可变】
	TextView alter_aim;// 修改目标-----------------------------点击“修改个人目标”

	TextView day_most_step;// 展示“单日最多步数”【可变】----------暂无点击事件
	TextView day_most_energy;// 展示“单日最多消耗能量”【可变】-----暂无点击事件

	// TextView total_sport_state;//运动状态累计统计标签------------
	TextView total_sport_data;// 运动状态累计统计标签------------点击“展示切换累计公里或累计步数”
	TextView data_unit;// 运动状态累计统计标签

	RelativeLayout data_analysis_top;
	LinearLayout data_analysis_center;
	LinearLayout data_analysis_bottom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.sensor_data_analysis);
		back = (ImageButton) findViewById(R.id.data_back);
		face = (ImageView) findViewById(R.id.data_centre_face_image);
		data_analysis_usename = (TextView) findViewById(R.id.data_analysis_usename);
		gender = (TextView) findViewById(R.id.data_centre_gender);
		height = (TextView) findViewById(R.id.data_centre_height);
		weight = (TextView) findViewById(R.id.data_centre_weight);
		alter_info = (TextView) findViewById(R.id.alter_info);
		finished_aim = (TextView) findViewById(R.id.finished_aim);
		aim_stepnum = (TextView) findViewById(R.id.aim_stepnum);
		finished_percent = (TextView) findViewById(R.id.finished_percent);
		alter_aim = (TextView) findViewById(R.id.alter_aim);
		day_most_step = (TextView) findViewById(R.id.day_most_step);
		day_most_energy = (TextView) findViewById(R.id.day_most_energy);
		// total_sport_state=(TextView) findViewById(R.id.total_sport_state);
		total_sport_data = (TextView) findViewById(R.id.total_sport_data);
		data_unit = (TextView) findViewById(R.id.data_unit);

		back.setOnClickListener(new iback());
		/*
		 * face.setOnClickListener(new iface());
		 * alter_info.setOnClickListener(new ialter_info());
		 * aim_stepnum.setOnClickListener(new iaim_stepnum());
		 * alter_aim.setOnClickListener(new ialter_aim());
		 * total_sport_data.setOnClickListener(new itotal_sport_data());
		 */

		/* 更改个人资料 */
		data_analysis_top = (RelativeLayout) findViewById(R.id.data_analysis_top);
		data_analysis_top.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alter_info_method();
			}
		});
		/* 修改个人目标 */
		data_analysis_center = (LinearLayout) findViewById(R.id.data_analysis_center);
		data_analysis_center.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alter_aim_method();
			}
		});
		/* 展示切换累计公里或累计步数 */
		data_analysis_bottom = (LinearLayout) findViewById(R.id.data_analysis_bottom);
		data_analysis_bottom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switchoverData();
			}
		});

		initAllData();// 初始化数据!!
	}

	// 分享服务
	public void share_btn(View v) {
		ScreenShot.shoot(DataAnalysisActivity.this, imagePathString);
		shareMsg(TAG, "来自纯动的分享", "想说的话...", imagePathString);
	}

	// 系统分享
	private void shareMsg(String activityTitle, String msgTitle,
			String msgText, String imgPath) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		if (imgPath == null || imgPath.equals("")) {
			intent.setType("text/plain"); // 纯文本
		} else {
			File f = new File(imgPath);
			if (f != null && f.exists() && f.isFile()) {
				intent.setType("image/jpg");
				Uri u = Uri.fromFile(f);
				intent.putExtra(Intent.EXTRA_STREAM, u);
			}
		}
		intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
		intent.putExtra(Intent.EXTRA_TEXT, msgText);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, activityTitle));
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		initAllData();// 初始化数据!!
		super.onResume();
	}

	/* 返回 */
	class iback implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();// 销毁界面
		}
	}

	/* 未设置 */
	class ishare implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// ################################设置分享
			System.out.println("%%%%%%%%%%%%%%%%%分享");// 测试!!!!!!!

		}
	}

	/*
	 * 更改个人资料 class iface implements OnClickListener{
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub alter_info_method(); } } //更改个人资料 class ialter_info implements
	 * OnClickListener{
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub alter_info_method(); } }
	 */
	// 更改个人资料的具体方法
	void alter_info_method() {
		SensorData.setFromDataCentre(true);// 设置来自数据中心
		Intent intent = new Intent();
		intent.setClass(DataAnalysisActivity.this, SexChoiceActivity.class);// #############
		startActivity(intent);
		// finish();//销毁界面
	}

	/*
	 * 修改个人目标 class iaim_stepnum implements OnClickListener{
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub alter_aim_method(); } } 修改个人目标 class ialter_aim implements
	 * OnClickListener{
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub alter_aim_method(); } }
	 */
	/* 修改个人目标的具体方法 */
	void alter_aim_method() {
		SensorData.setFromDataCentre(true);// 设置来自数据中心
		Intent intent = new Intent();
		intent.setClass(DataAnalysisActivity.this, TargetSettingActivity.class);// #############
		startActivity(intent);
		// finish();//销毁界面
	}

	/* 展示切换累计公里或累计步数 */
	/*
	 * class itotal_sport_data implements OnClickListener{
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub switchoverData();
	 * 
	 * } }
	 */
	/* 展示切换累计公里或累计步数的具体方法 */
	void switchoverData() {
		if (SensorData.isShowStep_DataCentre()) {// 当前正在展示【步数】
			SensorData.setShowStep_DataCentre(false);// 设置“当前正在展示【运动距离】”
			total_sport_data.setText(getTotalDistance());
			data_unit.setText("公里");
		} else {
			SensorData.setShowStep_DataCentre(true);// 设置“当前正在展示【步数】”
			total_sport_data.setText(SensorData.getTotal_stepNum() + "");
			data_unit.setText("步");
		}
	}

	/* 获取累计运动距离 */// ###
	String getTotalDistance() {
		String distance = "";
		float d = 0;
		SportInfoDAO dao = new SportInfoDAO(DataAnalysisActivity.this);
		Cursor c = dao.query(
				"select user_distance from " + SensorData.getUsername(), null);// 查询完成目标的
		while (c.moveToNext()) {
			d += c.getFloat(c.getColumnIndex("user_distance"));
		}
		distance += getFormatFloat(d);// 格式化成一位小数

		dao.closeDB();
		c.close();

		return distance;
	}

	/* 初始化界面所有数据 */
	public void initAllData() {
		if (CalculateUtil.getUserFacePath() != null) {
			face.setAdjustViewBounds(true);// 大小自适应
			face.setImageBitmap(CalculateUtil.getDiskBitmap(CalculateUtil
					.getUserFacePath()));
			;
			// 头像【可变】-----------------点击“修改个人资料”
		}

		data_analysis_usename.setText(SensorData.getUsername());// 显示用户名--
		gender.setText(SensorData.getGender() == 1 ? "男" : "女");// 性别【可变】----
		height.setText(SensorData.getHeight() + "cm");// 身高【可变】----
		weight.setText(SensorData.getWeight() + "kg");// 体重【可变】-----

		int finishedAim = getFinishedAim();
		int totalDay = getTotalDay();
		finished_aim.setText(finishedAim + "天");// 展示“完成目标的天数”【可变】
		aim_stepnum.setText(SensorData.getAim_stepNum() + "");
		;// 展示“目标步数”【可变】------------------点击“修改个人目标”
		finished_percent.setText(getFinishedPercent(finishedAim, totalDay));// 展示“完成目标百分比”【可变】
		// TextView alter_aim;//修改目标-----------------------------点击“修改个人目标”

		day_most_step.setText(getDayMostStep() + "");
		;// 展示“单日最多步数”【可变】----------暂无点击事件
		day_most_energy.setText(getDayMostEnergy() + "");
		;// 展示“单日最多消耗能量”【可变】-----暂无点击事件

		// TextView total_sport_state;//运动状态累计统计标签------------
		total_sport_data.setText(SensorData.getTotal_stepNum() + "");// 运动状态累计统计标签------------点击“展示切换累计公里或累计步数”
		data_unit.setText("步");// 运动状态累计统计标签

	}

	/* 获取完成目标的天数 */
	int getFinishedAim() {
		int aim = 0;
		SportInfoDAO dao = new SportInfoDAO(DataAnalysisActivity.this);
		Cursor c = dao.query(
				"select user_date from " + SensorData.getUsername()
						+ " where user_step>=" + SensorData.getAim_stepNum(),
				null);// 查询完成目标的
		aim = c.getCount();
		dao.closeDB();
		c.close();
		return aim;
	}

	/* 获取使用的总天数:从开始注册登录使用APP，截止到现在 */
	int getTotalDay() {
		int totalDay = 0;
		SportInfoDAO dao = new SportInfoDAO(DataAnalysisActivity.this);
		Cursor c1 = dao.query(
				"select user_date from " + SensorData.getUsername()
						+ " where user_date=(select min(user_date) from "
						+ SensorData.getUsername() + ")", null);// 查询完成目标的
		Cursor c2 = dao.query(
				"select user_date from " + SensorData.getUsername()
						+ " where user_date=(select max(user_date) from "
						+ SensorData.getUsername() + ")", null);// 查询完成目标的

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long t1 = 0;
		long t2 = 0;
		while (c1.moveToNext()) {
			try {
				t1 = sdf.parse(c1.getString(c1.getColumnIndex("user_date")))
						.getTime();
			} catch (ParseException e) {
				System.out.println("%%%%%%%%%%%%%%%%%%%%获取使用总天数失败:"
						+ e.getCause());// 测试!!!!!!!!
			}
		}
		while (c2.moveToNext()) {
			try {
				t2 = sdf.parse(c2.getString(c2.getColumnIndex("user_date")))
						.getTime();
			} catch (ParseException e) {
				System.out.println("%%%%%%%%%%%%%%%%%%%%获取使用总天数失败:"
						+ e.getCause());// 测试!!!!!!!!
			}
		}
		totalDay = (int) (1 + (t2 - t1) / (1000 * 3600 * 24));// 获取使用的总天数!!

		dao.closeDB();
		c1.close();
		c2.close();
		return totalDay;
	}

	/* 获取完成百分比 */
	String getFinishedPercent(int finishedAim, int totalDay) {
		String result = "";
		float dev = 0;
		if (totalDay != 0)
			dev = (float) 100*finishedAim / totalDay;
		result += getFormatFloat(dev);
		/*
		 * if(result.contains(".")){
		 * result=result.substring(0,result.indexOf(".")+2);
		 * 
		 * }
		 */

		result += "%";

		return result;
	}

	/* 获取格式化后的float类型:格式化成一位小数 */
	public float getFormatFloat(float f) {
		String s = f + "";
		if (s.length() - s.indexOf(".") - 1 > 1) {
			s = s.substring(0, s.indexOf(".") + 2);
		}
		f = Float.valueOf(s);
		return f;
	}

	/* 获取单日最多的步数 */
	int getDayMostStep() {// user_step
		int dayMostStep = 0;
		SportInfoDAO dao = new SportInfoDAO(DataAnalysisActivity.this);
		Cursor c = dao.query(
				"select user_step from " + SensorData.getUsername()
						+ " where user_step=(select max(user_step) from "
						+ SensorData.getUsername() + ")", null);// 查询完成目标的
		while (c.moveToNext()) {
			dayMostStep = c.getInt(c.getColumnIndex("user_step"));
		}

		dao.closeDB();
		c.close();
		return dayMostStep;
	}

	/* 获取单日最多的消耗能量 */
	int getDayMostEnergy() {
		int dayMostEnergy = 0;
		SportInfoDAO dao = new SportInfoDAO(DataAnalysisActivity.this);
		Cursor c = dao.query(
				"select user_energy from " + SensorData.getUsername()
						+ " where user_energy=(select max(user_energy) from "
						+ SensorData.getUsername() + ")", null);// 查询完成目标的
		while (c.moveToNext()) {
			dayMostEnergy = c.getInt(c.getColumnIndex("user_energy"));
		}

		dao.closeDB();
		c.close();
		return dayMostEnergy;
	}

}
