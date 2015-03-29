package com.healthslife.map;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import com.healthslife.sensor.activity.TestActivity;
import com.healthslife.sensor.dao.SportInfoDAO;
import com.healthslife.sensor.data.SensorData;
import com.healthslife.sensor.utilities.CalculateUtil;
import com.healthslife.system.SexChoiceActivity;

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
		MapGlobalVariable.mBaiduMap.setMyLocationEnabled(true); // Open
																// location's
																// layer 开启定位图层
		MapGlobalVariable.mLocClient = new LocationClient(
				WelcomeWithLocation.this); // Initialize
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
				
				if (null == MapGlobalVariable.city) {
					MapGlobalVariable.city = "error";
				}
//				if (SettingGlobalVariable.height != 0
////						&& SettingGlobalVariable.weight != 0) {
//				Intent toNextService = new Intent();
//					toNextService.setClass(WelcomeWithLocation.this,
//							HealthServiceActivity.class);
//					startActivity(toNextService);
//				}else {
//					toNextService.setClass(WelcomeWithLocation.this,
//							SetWeight.class);
//				}
				decideJump(WelcomeWithLocation.this);
				
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

	
	
	public void decideJump(Context context){
    	//User_info user_info=new User_info();
    	SportInfoDAO dao=new SportInfoDAO(context);
    	String sql="select * from "+SportInfoDAO.TABLENAME_UserInfo+" where 1=1";
    	Cursor c=dao.query(sql, null);//查询
    	//int info_id=-1;//id默认值，-1表示不存在!!
		 while(c.moveToNext()) {
			/* private String user_name;//用户名---“英文字母”和“下划线”组成
				private int user_sex;//性别
				private int user_weight;//体重
				private int user_high;//身高
				private int user_aimstep;//目标步行数目
				private int user_islogin;//用户登录状态
				*/
			 	SensorData.setInfo_id(c.getInt(c.getColumnIndex("id")));//获取userinfo中的id!!
			 	SensorData.setUsername(c.getString(c.getColumnIndex("user_name")));
			 	SensorData.setGender(c.getInt(c.getColumnIndex("user_sex")));
			 	SensorData.setWeight(c.getInt(c.getColumnIndex("user_weight")));
			 	SensorData.setHeight(c.getInt(c.getColumnIndex("user_high")));
			 	SensorData.setAim_stepNum(c.getInt(c.getColumnIndex("user_aimstep")));
			 	SensorData.setLogin(c.getInt(c.getColumnIndex("user_islogin"))==1?true:false);
	     
			 	System.out.println("在引导页查询info表结果："+SensorData.getUsername()
			 			+"@"+SensorData.getGender()
			 			+"@"+SensorData.getWeight()
			 			+"@"+SensorData.getHeight()
			 			+"@"+SensorData.getAim_stepNum()
			 			+"@"+SensorData.isLogin());//@@@@@@@@@@测试!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		 }
		 
		if(c.getCount()==0){//1、(空表)第一次使用该APP
			System.out.println("@@@@@@请打开“设置身高、体重和目标”!!!");//测试!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			SensorData.isFirstUse = true;
			SensorData.setLogin(false);//设置未登录!!
			Intent intent=new Intent();
			intent.setClass(this, SexChoiceActivity.class);   //跳到设置身高体重的页面
			startActivity(intent);//模拟页面，设置之后，再跳到主界面!!!!!
			
		}
		else if(!SensorData.isLogin()){//2、未登录//不保存除“用户信息表的数据”外的数据!
			SensorData.isFirstUse = false;
			System.out.println("%%%%%%%未登录!!!");//测试!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			CalculateUtil.resetInit();//初始化参数:除身高、体重、性别、用户名、目标步数、登录状态之外，重置初始化所有参数!!!
			//#####先初始化重置数据，然后再跳至“主界面”!!!
			Intent toNextService = new Intent();
			toNextService.setClass(WelcomeWithLocation.this,HealthServiceActivity.class);
			startActivity(toNextService);
			
		}
		else{//3、已登录
			SensorData.isFirstUse = false;
			System.out.println("*********已登录!!!");//测试!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			CalculateUtil.initALL(WelcomeWithLocation.this);//初始化当前所有数据:从数据库读取!!!
			
			//#####先初始化所有数据，然后再跳至“主界面”!!!
			Intent toNextService = new Intent();
			toNextService.setClass(WelcomeWithLocation.this,HealthServiceActivity.class);
			startActivity(toNextService);
		}
		
		c.close();
		dao.closeDB();//关闭数据库!
    	
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
