package com.healthslife.map;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
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

public class MapService extends Activity {

	public MyLocationListenner myListener = new MyLocationListenner();
	protected static List<LatLng> points_LatLng = new ArrayList<LatLng>();;   //The list of LatLng's points 坐标点的集合
	private static boolean isFirstLoc = true;  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SDKInitializer.initialize(getApplicationContext()); // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		/*
		 * forbid lock screen
		 */
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.map_service);

		Button viewButton = (Button) findViewById(R.id.button1);
		Button drawButton = (Button) findViewById(R.id.button2);
		/*
		 * 地图初始化
		 */
		MapGlobalVariable.mMapView = (MapView) findViewById(R.id.bmapView);
		MapGlobalVariable.mBaiduMap = MapGlobalVariable.mMapView.getMap();

		MapGlobalVariable.mBaiduMap.setMyLocationEnabled(true);// 开启定位图层

		MapGlobalVariable.mBaiduMap.setTrafficEnabled(true);// 开启交通图
		MapGlobalVariable.mBaiduMap.setMapStatus(MapStatusUpdateFactory
				.newMapStatus(new MapStatus.Builder().zoom(17).build()));// 设置缩放级别
		/*
		 * 定位初始化
		 */
		MapGlobalVariable.mLocClient = new LocationClient(this);
		MapGlobalVariable.mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000); // 设置发起定位请求的间隔时间为1000ms
		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		MapGlobalVariable.mLocClient.setLocOption(option);
		MapGlobalVariable.mLocClient.start();

		viewButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 显示经纬度
				Toast.makeText(getApplicationContext(),
						points_LatLng.toString(),
						Toast.LENGTH_SHORT).show();

			}

		});

		drawButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 绘制轨迹
				if (points_LatLng.size() >= 2) {
					OverlayOptions ooPolyline = new PolylineOptions().width(8)
							.color(Color.argb(255, 0, 102, 204))
							.points(points_LatLng);
					MapGlobalVariable.mBaiduMap.addOverlay(ooPolyline);
				} else {
					Toast.makeText(getApplicationContext(),
							R.string.remind_sports, Toast.LENGTH_SHORT).show();
					return;
				}

			}

		});

	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		int i = 0;// record the LatLng's location
		 
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || MapGlobalVariable.mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			MapGlobalVariable.mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				MapGlobalVariable.ll = new LatLng(location.getLatitude(),
						location.getLongitude());

				points_LatLng.add(i++, MapGlobalVariable.ll);
				MapStatusUpdate u = MapStatusUpdateFactory
						.newLatLng(MapGlobalVariable.ll);
				MapGlobalVariable.mBaiduMap.animateMapStatus(u);
			} else {

				MapGlobalVariable.newll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory
						.newLatLng(MapGlobalVariable.newll);
				double distance = DistanceUtil.getDistance(
						points_LatLng.get(i - 1),
						MapGlobalVariable.newll);
				if (distance > 1 && distance < 10) {
					points_LatLng.add(i++,
							MapGlobalVariable.newll);
				}
				MapGlobalVariable.mBaiduMap.animateMapStatus(u);
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