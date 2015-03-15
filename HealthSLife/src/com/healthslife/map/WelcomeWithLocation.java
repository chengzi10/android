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
	MapView mMapView = null; // ��ͼ����
	LocationClient mLocClient; // ��λ����
	public MyLocationListenner myListener = new MyLocationListenner();
	BitmapDescriptor mCurrentMarker; // λͼ
	BaiduMap mBaiduMap; // �ٶȵ�ͼ����
	List<LatLng> points_LatLng = new ArrayList<LatLng>(); // ��������
	LatLng ll; // �����������
	LatLng newll; // �����������
	public static String city = null; // ��ȡ��ǰ���ڳ���
	boolean isFirstLoc = true;// �Ƿ��״ζ�λ
	boolean isDisplayMap = false; // �Ƿ���ʾ��ͼ
	// UI���
	Button viewButton;
	Button drawButton;
	String res = "";
	int count = 0;
	Iterator<HashMap<String, Object>> it;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext
		// ע��÷���Ҫ��setContentView����֮ǰʵ��
		SDKInitializer.initialize(getApplicationContext());
		/*
		 * forbid lock screen
		 */
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.location_service);

		mMapView = (MapView) findViewById(R.id.bmapView_gone);
		
		mBaiduMap = mMapView.getMap();
		// ������λͼ��
		mBaiduMap.setMyLocationEnabled(true);

		// ������ͨͼ
//		mBaiduMap.setTrafficEnabled(true);
//		mBaiduMap.setMapStatus(MapStatusUpdateFactory
//				.newMapStatus(new MapStatus.Builder().zoom(17).build()));// �������ż���
		// ��λ��ʼ��
		mLocClient = new LocationClient(WelcomeWithLocation.this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setIsNeedAddress(true); // ���صĶ�λ���������ַ��Ϣ
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000); // ���÷���λ����ļ��ʱ��Ϊ1000ms
		option.setNeedDeviceDirect(true);// ���صĶ�λ��������ֻ���ͷ�ķ���
		mLocClient.setLocOption(option);
		mLocClient.start();

		if (isDisplayMap == false) {
			// ��ת������ģ��
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
	 * ��λSDK��������
	 */
	public class MyLocationListenner implements BDLocationListener {

		int i = 0;// record the LatLng's location

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
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
		// �˳�ʱ���ٶ�λ
		mLocClient.stop();
		// �رն�λͼ��
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

}
