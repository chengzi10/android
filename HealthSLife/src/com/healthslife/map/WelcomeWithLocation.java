package com.healthslife.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.healthslife.R;
import com.healthslife.health.HealthServiceActivity;

public class WelcomeWithLocation extends Activity {
	MapView mMapView = null; // 地图对象
	LocationClient mLocClient; // 定位对象
	public MyLocationListenner myListener = new MyLocationListenner();
	BitmapDescriptor mCurrentMarker; // 位图
	BaiduMap mBaiduMap; // 百度地图对象
	List<LatLng> points_LatLng = new ArrayList<LatLng>(); // 坐标数组
	LatLng ll; // 定义坐标对象
	LatLng newll; // 定义坐标对象
	public static String city = null; // 获取当前所在城市
	boolean isFirstLoc = true;// 是否首次定位
	boolean isDisplayMap = false; // 是否显示地图
	// UI相关
	Button viewButton;
	Button drawButton;
	String res = "";
	int count = 0;
	Iterator<HashMap<String, Object>> it;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		/*
		 * forbid lock screen
		 */
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.location_service);

		mMapView = (MapView) findViewById(R.id.bmapView_gone);
		
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);

		// 开启交通图
//		mBaiduMap.setTrafficEnabled(true);
//		mBaiduMap.setMapStatus(MapStatusUpdateFactory
//				.newMapStatus(new MapStatus.Builder().zoom(17).build()));// 设置缩放级别
		// 定位初始化
		mLocClient = new LocationClient(WelcomeWithLocation.this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000); // 设置发起定位请求的间隔时间为1000ms
		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		mLocClient.setLocOption(option);
		mLocClient.start();

		if (isDisplayMap == false) {
			// 跳转至健康模块
			Timer timer = new Timer();
			TimerTask task = new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Intent toHealthService = new Intent();
					Bundle cityBundle = new Bundle();
					if (city == null) {
						city = "error";
					} 
					cityBundle.putString("cityName", city);
					toHealthService.putExtras(cityBundle);
					toHealthService.setClass(WelcomeWithLocation.this,
							HealthServiceActivity.class);
					startActivity(toHealthService);
					WelcomeWithLocation.this.finish();
				}

			};
			timer.schedule(task, 3000);
		}
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		int i = 0;// record the LatLng's location

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			// city = location.getCity();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				ll = new LatLng(location.getLatitude(), location.getLongitude());
				city = location.getCity();
				points_LatLng.add(i++, ll);
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			} else {

				newll = new LatLng(location.getLatitude(),
						location.getLongitude());
				city = location.getCity();
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(newll);
				double distance = DistanceUtil.getDistance(
						points_LatLng.get(i - 1), newll);
				if (distance > 1 && distance < 10) {
					points_LatLng.add(i++, newll);
					Log.d("Sava_Data", "location is:" + newll);
				} else {
					Log.d("Don't Sava_Data", "location is in the form" + newll);
				}

				mBaiduMap.animateMapStatus(u);
			}

		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

}
