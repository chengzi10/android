package com.healthslife.map;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.healthslife.R;
import com.healthslife.health.HealthServiceActivity;

public class WelcomeWithLocation extends Activity {
	private MyLocationListenner myListener = new MyLocationListenner();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SDKInitializer.initialize(getApplicationContext()); // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		/*
		 * forbid lock screen 禁止锁屏
		 */
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.location_service);

		MapGlobalVariable.mMapView = (MapView) findViewById(R.id.bmapView_gone); // Initialize
																				// Map
																				// 初始化地图
		MapGlobalVariable.mBaiduMap = MapGlobalVariable.mMapView.getMap();
		MapGlobalVariable.mBaiduMap.setMyLocationEnabled(true); // Open location's
																// layer 开启定位图层
		MapGlobalVariable.mLocClient = new LocationClient(WelcomeWithLocation.this); // Initialize
																					// location
																					// 定位初始化
		MapGlobalVariable.mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // open gps 打开gps
		option.setIsNeedAddress(true); // The data of return contain address
		option.setCoorType("bd09ll"); // Set coordinate type 设置坐标类型
		option.setScanSpan(1000); // Location request time interval 1000ms
									// 设置发起定位请求的时间间隔为1000ms
		MapGlobalVariable.mLocClient.setLocOption(option);
		MapGlobalVariable.mLocClient.start();

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent toHealthService = new Intent();
				if (null == MapGlobalVariable.city) {
					MapGlobalVariable.city = "error";
				}
//				cityBundle.putString("cityName", MapGlobalVariable.city);
//				toHealthService.putExtras(cityBundle);
				toHealthService.setClass(WelcomeWithLocation.this,
						HealthServiceActivity.class);
				startActivity(toHealthService);
				WelcomeWithLocation.this.finish();
			}

		};
		timer.schedule(task, 3000);
	}

	/**
	 * Positioning monitor function 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不再接收新的位置
			if (location == null || MapGlobalVariable.mMapView == null)
				return;
			if (MapGlobalVariable.isFirstLoc) {
				MapGlobalVariable.isFirstLoc = false;
				MapGlobalVariable.city = location.getCity();
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	protected void onResume() {
		MapGlobalVariable.mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		MapGlobalVariable.mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		MapGlobalVariable.mLocClient.stop();
		// 关闭定位图层
		MapGlobalVariable.mBaiduMap.setMyLocationEnabled(false);
		MapGlobalVariable.mMapView.onDestroy();
		MapGlobalVariable.mMapView = null;
		super.onDestroy();
	}

}
