package com.healthslife.time;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.healthslife.health.DatabaseHelper;
import com.healthslife.health.HealthGlobalVariable;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.IBinder;

public class TimeManager extends Service{
	public static String str = null;
	DatabaseHelper healthDatabaseHelper = new DatabaseHelper(this);
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public static String timeManagerment() {
		SimpleDateFormat format = new SimpleDateFormat("hh:mm"); // yyyy-MM-dd																// hh:mm:ss
		Date curDate = new Date(System.currentTimeMillis()); // 获取当前时间
		str = format.format(curDate);
		return str;

	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		new Thread() {
			String time = timeManagerment();
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				healthDatabaseHelper.insert(HealthGlobalVariable.value[0], HealthGlobalVariable.value[1], time.toString());
			}
			
		}.start();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
