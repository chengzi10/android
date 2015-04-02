package com.healthslife.map;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
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
import com.healthslife.sensor.dao.SportInfoDAO;
import com.healthslife.sensor.utilities.DBUtil;

public class MapService extends Activity {
	private static String TAG = "ShareActivity";
	public static String imagePathString = "/sdcard/screen.png"; // 截取图片的保存路径
	public MyLocationListenner myListener = new MyLocationListenner();

	private Button drawButton;

	/*
	 * private Handler handle = new Handler() {
	 * 
	 * @Override public void handleMessage(Message msg) { // TODO Auto-generated
	 * method stub super.handleMessage(msg); if
	 * (MapGlobalVariable.points_LatLng.size() >= 2) { OverlayOptions ooPolyline
	 * = new PolylineOptions().width(8) .color(Color.argb(255, 0, 102, 204))
	 * .points(MapGlobalVariable.points_LatLng);
	 * MapGlobalVariable.mBaiduMap.addOverlay(ooPolyline); } else {
	 * Toast.makeText(getApplicationContext(), R.string.remind_sports,
	 * Toast.LENGTH_SHORT).show(); return; }
	 * 
	 * }
	 * 
	 * };
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SDKInitializer.initialize(getApplicationContext()); // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		/*
		 * forbid lock screen
		 */
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.map_service);

		drawButton = (Button) findViewById(R.id.draw_btn);
		if (isNetworkAvailable(MapService.this)) {
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

			drawButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// MapGlobalVariable.Polyline = new LatLng(arg0, arg1);
					SportInfoDAO dao = new SportInfoDAO(MapService.this);
					Cursor c = dao.query("select * from "
							+ SportInfoDAO.TABLENAME_UserMap, null);
					MapGlobalVariable.points_LatLng_Polyline.clear();
					while (c.moveToNext()) {
						MapGlobalVariable.points_LatLng_Polyline.add(new LatLng(
								Double.valueOf(c.getString(c
										.getColumnIndex("latitude"))), Double
										.valueOf(c.getString(c
												.getColumnIndex("longitude")))));
					}


					if (MapGlobalVariable.points_LatLng_Polyline.size() >= 2) {
						OverlayOptions ooPolyline = new PolylineOptions()
								.width(8)
								.color(Color.argb(255, 0, 102, 204))
								.points(MapGlobalVariable.points_LatLng_Polyline);
						MapGlobalVariable.mBaiduMap.addOverlay(ooPolyline);
					} else {
						Toast.makeText(getApplicationContext(),
								R.string.remind_sports, Toast.LENGTH_SHORT)
								.show();
						return;
					}
				}

			});
		} else {
			Toast.makeText(getApplicationContext(), "网络连接异常，请查看网络设置...",
					Toast.LENGTH_SHORT).show();
		}

	}

	// 返回主界面
	public void to_health_btn(View v) {
		finish();
	}

	// 分享服务
	public void share_btn(View v) {
		if (isNetworkAvailable(MapService.this)) {
			MapGlobalVariable.mBaiduMap.snapshot(new SnapshotReadyCallback() {
				public void onSnapshotReady(Bitmap snapshot) {
					File file = new File(imagePathString);
					FileOutputStream out;
					try {
						out = new FileOutputStream(file);
						if (snapshot.compress(Bitmap.CompressFormat.PNG, 100,
								out)) {
							out.flush();
							out.close();
						}
						Toast.makeText(MapService.this,
								"屏幕截图成功，图片存在: " + file.toString(),
								Toast.LENGTH_SHORT).show();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			Toast.makeText(MapService.this, "正在截取屏幕图片...", Toast.LENGTH_SHORT)
					.show();
			// showShare(v);
			shareMsg(TAG, "来自纯动的分享", "想说的话...", imagePathString);
		} else {
			Toast.makeText(getApplicationContext(), "网络连接异常，请查看网络设置...",
					Toast.LENGTH_SHORT).show();
		}
	}

	public static boolean isNetworkAvailable(Context context) {
		if (context != null) {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = cm.getActiveNetworkInfo();

			if (null != mNetworkInfo) {
				return mNetworkInfo.isAvailable();
			}
		}

		return false;

	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

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
			if (MapGlobalVariable.isMapFirstLoc) {
				MapGlobalVariable.isMapFirstLoc = false;
				MapGlobalVariable.ll = new LatLng(location.getLatitude(),
						location.getLongitude());

				MapGlobalVariable.points_LatLng.add(MapGlobalVariable.i++,
						MapGlobalVariable.ll);
				MapStatusUpdate u = MapStatusUpdateFactory
						.newLatLng(MapGlobalVariable.ll);
				MapGlobalVariable.mBaiduMap.animateMapStatus(u);
			} else {

				MapGlobalVariable.newll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory
						.newLatLng(MapGlobalVariable.newll);
				double distance = DistanceUtil.getDistance(
						MapGlobalVariable.points_LatLng
								.get(MapGlobalVariable.i - 1),
						MapGlobalVariable.newll);
				if (distance > 5 && distance < 200) {
					// MapGlobalVariable.points_LatLng.add(MapGlobalVariable.i++,
					// MapGlobalVariable.newll);
					ContentValues values = new ContentValues();
					values.put("longitude", location.getLongitude() + "");
					values.put("latitude", location.getLatitude() + "");
					DBUtil.insert(MapService.this,
							SportInfoDAO.TABLENAME_UserMap, values);
				}
				MapGlobalVariable.mBaiduMap.animateMapStatus(u);
			}

		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	// 分享截图
	private Bitmap getbBitmap(View v) {// 获取数据的截图

		View eview = v.getRootView();
		eview.setDrawingCacheEnabled(true);
		eview.buildDrawingCache();
		Bitmap bitmap = eview.getDrawingCache();
		ByteArrayOutputStream baos = null;
		// Bitmap bitmap = null;
		final View contentView = findViewById(android.R.id.content);
		try {
			// bitmap = Bitmap.createBitmap(contentView.getWidth(),
			// contentView.getHeight(), Bitmap.Config.ARGB_4444);
			contentView.draw(new Canvas(bitmap));
			baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			savePic(bitmap, imagePathString);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				/** no need to close, actually do nothing */
				if (null != baos)
					baos.close();
				if (null != bitmap && !bitmap.isRecycled()) {
					// bitmap.recycle();
					bitmap = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	private void savePic(Bitmap b, String strFileName) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(strFileName);
			if (null != fos) {
				boolean success = b
						.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
				if (success)
					Toast.makeText(MapService.this, "截屏成功", Toast.LENGTH_SHORT)
							.show();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	/*
	 * private void showShare(View v) { //getbBitmap(v); ScreenShot.shoot(this ,
	 * imagePathString); ShareSDK.initSDK(this); OnekeyShare oks = new
	 * OnekeyShare(); // 关闭sso授权 oks.disableSSOWhenAuthorize();
	 * 
	 * // 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法 //
	 * oks.setNotification(R.drawable.ic_launcher, //
	 * getString(R.string.app_name)); // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
	 * oks.setTitle(getString(R.string.share)); // //
	 * titleUrl是标题的网络链接，仅在人人网和QQ空间使用 oks.setTitleUrl(imagePathString); //
	 * text是分享文本，所有平台都需要这个字段 oks.setText("运动轨迹"); //
	 * imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
	 * oks.setImagePath(imagePathString);// 确保SDcard下面存在此张图片 // //
	 * url仅在微信（包括好友和朋友圈）中使用 oks.setUrl(imagePathString); //
	 * comment是我对这条分享的评论，仅在人人网和QQ空间使用 oks.setComment("我是测试评论文本"); //
	 * site是分享此内容的网站名称，仅在QQ空间使用 oks.setSite(getString(R.string.app_name)); // //
	 * siteUrl是分享此内容的网站地址，仅在QQ空间使用 oks.setSiteUrl(imagePathString);
	 * 
	 * // 启动分享GUI oks.show(this); }
	 */

	@Override
	protected void onResume() {
		if (isNetworkAvailable(MapService.this)) {
			MapGlobalVariable.mMapView.onResume();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (isNetworkAvailable(MapService.this)) {
			MapGlobalVariable.mMapView.onPause();
		}
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		if (isNetworkAvailable(MapService.this)) {
			// 退出时销毁定位
			MapGlobalVariable.mLocClient.stop();
			// 关闭定位图层
			MapGlobalVariable.mBaiduMap.setMyLocationEnabled(false);
			MapGlobalVariable.mMapView.onDestroy();
			MapGlobalVariable.mMapView = null;
		}
		super.onDestroy();
	}

}