package com.healthslife.health;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.baidu.mapapi.SDKInitializer;
import com.healthslife.R;
import com.healthslife.allinterface.CircleBar;
import com.healthslife.allinterface.SlidingMenu;
import com.healthslife.heartrate.XlcsActivity;
import com.healthslife.loginregister.Aty_UserCenter;
import com.healthslife.loginregister.Login;
import com.healthslife.loginregister.LoginRegisterGlobalVariable;
import com.healthslife.map.MapGlobalVariable;
import com.healthslife.map.MapService;
import com.healthslife.music.activity.ListMainActivity;
import com.healthslife.sensor.data.SensorData;
import com.healthslife.sensor.service.StepService;
import com.healthslife.sensor.utilities.CalculateUtil;
import com.healthslife.sensor.utilities.PedometerSettingUtil;
import com.healthslife.sensor.utilities.VoiceUtil;

public class HealthServiceActivity extends Activity {

	private static boolean isExit = false;  // 设置退出判断参数
	private SharedPreferences mSettings;// 参数设置
	private PedometerSettingUtil mPedometerSettings;// 参数设置
	private VoiceUtil voiceUtil;// 语音工具设置

	private int mStepValue;// 步数
	private int mPaceValue;// 步频
	private float mDistanceValue;// 运动距离
	private float mSpeedValue;// 运动速度
	private int mCaloriesValue;// 消耗能量
	private float mDesiredPaceOrSpeed;// 预期步频或速度
	private int mMaintain;// 记录参照对比的值。
	private boolean mQuitting = false; // 退出设置
	// Set when user selected Quit from menu, can be used by onPause, onStop,
	// onDestroy

	private boolean mIsMetric;// 判断是否为公制距离
	private float mMaintainInc;
	/**
	 * True, when service is running.
	 */
	private boolean mIsRunning;// 判断监听步数的service是否还在运行

	private int aim[] = { 5000, 200, 0 }; // 设置模块传递的用户设置目标
	DatabaseHelper healthDatabaseHelper = new DatabaseHelper(this);

	ImageButton stepnumber_btn;
	ImageButton energy_btn;
	ImageButton aqi_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.health_service);

		JPushInterface.setDebugMode(false);
		JPushInterface.init(this);

		/*
		 * sener
		 */
		mStepValue = 0;
		mPaceValue = 0;

		/* 监听空气质量指数 */
		MonitoringFechAQI fechAQI = new MonitoringFechAQI();
		Thread fechAQI_t = new Thread(fechAQI);

		/* 语音播报 */
		voiceUtil = VoiceUtil.getInstance();
		// MonitoringSpeak speak=new MonitoringSpeak();
		// Thread speak_t=new Thread(speak);
		/* 启动监听功能步数变化 */
		MonitoringAVGSpeed moving = new MonitoringAVGSpeed();
		Thread moving_t = new Thread(moving);
		/* 监听跑步时间 */
		MonitoringRunningTime runningTime = new MonitoringRunningTime();
		Thread runningTime_t = new Thread(runningTime);
		/* 判断是否在语音播报：当语音播报时，暂停音乐播放，语音播报结束，再继续播放!!! */
		// MonitoringIsSpeaking isSpeaking=new MonitoringIsSpeaking();
		// Thread isSpeaking_t=new Thread(isSpeaking);

		/* 每隔一定时间(0.5s)，把该条数据更新到本地数据库！ */
		MonitoringRefreshTable refreshTable = new MonitoringRefreshTable();
		Thread refreshTable_t = new Thread(refreshTable);

		fechAQI_t.start();
		runningTime_t.start();
		moving_t.start();
		// speak_t.start();
		// isSpeaking_t.start();
		refreshTable_t.start();

		stepnumber_btn = (ImageButton) findViewById(R.id.stepnumber_btn);
		energy_btn = (ImageButton) findViewById(R.id.energy_btn);
		aqi_btn = (ImageButton) findViewById(R.id.aqi_btn);

		HealthGlobalVariable.circleBar = (CircleBar) findViewById(R.id.circle_bar);

		HealthGlobalVariable.circleBar
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// if (2 == HealthGlobalVariable.model) {
						// HealthGlobalVariable.value[2] =
						// HealthGlobalVariable.newAqi;
						// if (0 == HealthGlobalVariable.newAqi) {
						// Toast.makeText(getApplicationContext(),
						// "亲，获取空气质量失败，看看窗外吧", Toast.LENGTH_SHORT)
						// .show();
						// }
						// }

						if (HealthGlobalVariable.model > 1) {
							HealthGlobalVariable.model = 0;
						} else {
							HealthGlobalVariable.model++;
						}
						HealthGlobalVariable.circleBar
								.update(HealthGlobalVariable.value[HealthGlobalVariable.model],
										200, HealthGlobalVariable.model,
										aim[HealthGlobalVariable.model]); // 将参数传递到Circlebar中
						if (0 == HealthGlobalVariable.model) {
							stepnumber_btn.setImageDrawable(getResources()
									.getDrawable(R.drawable.walking_press));
							energy_btn.setImageDrawable(getResources()
									.getDrawable(R.drawable.kaluli_normal2));
							aqi_btn.setImageDrawable(getResources()
									.getDrawable(R.drawable.quality_normal));
						} else if (1 == HealthGlobalVariable.model) {
							stepnumber_btn.setImageDrawable(getResources()
									.getDrawable(R.drawable.walking_normal));
							energy_btn.setImageDrawable(getResources()
									.getDrawable(R.drawable.kaluli_press));
							aqi_btn.setImageDrawable(getResources()
									.getDrawable(R.drawable.quality_normal));
						} else if (2 == HealthGlobalVariable.model) {
							stepnumber_btn.setImageDrawable(getResources()
									.getDrawable(R.drawable.walking_normal));
							energy_btn.setImageDrawable(getResources()
									.getDrawable(R.drawable.kaluli_normal2));
							aqi_btn.setImageDrawable(getResources()
									.getDrawable(R.drawable.quality_press));
						}
					}

				});

		HealthGlobalVariable.mLeftMenu = (SlidingMenu) findViewById(R.id.id_menu);
		/*
		 * 获取欢迎界面传来的城市名称
		 */
		Toast.makeText(getApplicationContext(),
				"您现在位于" + MapGlobalVariable.city, Toast.LENGTH_SHORT).show();
		// 执行获取AQI的线程
		/*
		 * CountAirQuality airQuality = new CountAirQuality();
		 * airQuality.execute();
		 */

		Button map_btn = (Button) findViewById(R.id.map_btn);
		map_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mapIntent = new Intent();
				mapIntent
						.setClass(HealthServiceActivity.this, MapService.class);
				startActivity(mapIntent);
			}

		});

	}

	/*
	 * 跳转到心率测试
	 */
	public void heart_test(View v) {
		startActivity(new Intent(HealthServiceActivity.this, XlcsActivity.class));
	}

	/*
	 * 跳转到音乐服务
	 */
	public void music_service(View v) {
		startActivity(new Intent(HealthServiceActivity.this,
				ListMainActivity.class));
	}

	/*
	 * 跳转到数据中心
	 */
	public void data_service(View v) {
		if (false == LoginRegisterGlobalVariable.user_login_state) {
			startActivity(new Intent(HealthServiceActivity.this, Login.class));
		} else {
			startActivity(new Intent(HealthServiceActivity.this, Login.class));
		}
	}

	/*
	 * 跳转到登录界面
	 */
	public void login_register(View v) {
		if (SensorData.isLogin()) {
			startActivity(new Intent(HealthServiceActivity.this, Login.class));
		} else {
			startActivity(new Intent(HealthServiceActivity.this,
					Aty_UserCenter.class));
		}

	}

	/*
	 * 跳转到设置界面
	 */
	public void setting_service(View v) {
		startActivity(new Intent(HealthServiceActivity.this,
				com.healthslife.system.SettingActivity.class));
	}

	public void stepnumber_btn(View v) {
		stepnumber_btn.setImageDrawable(getResources().getDrawable(
				R.drawable.walking_press));
		energy_btn.setImageDrawable(getResources().getDrawable(
				R.drawable.kaluli_normal2));
		aqi_btn.setImageDrawable(getResources().getDrawable(
				R.drawable.quality_normal));
		HealthGlobalVariable.circleBar.update(HealthGlobalVariable.value[0],
				200, 0, aim[0]); // 将参数传递到Circlebar中
	}

	public void energy_btn(View v) {
		stepnumber_btn.setImageDrawable(getResources().getDrawable(
				R.drawable.walking_normal));
		energy_btn.setImageDrawable(getResources().getDrawable(
				R.drawable.kaluli_press));
		aqi_btn.setImageDrawable(getResources().getDrawable(
				R.drawable.quality_normal));
		HealthGlobalVariable.circleBar.update(HealthGlobalVariable.value[1],
				200, 1, aim[1]); // 将参数传递到Circlebar中
	}

	public void aqi_btn(View v) {
		stepnumber_btn.setImageDrawable(getResources().getDrawable(
				R.drawable.walking_normal));
		energy_btn.setImageDrawable(getResources().getDrawable(
				R.drawable.kaluli_normal2));
		aqi_btn.setImageDrawable(getResources().getDrawable(
				R.drawable.quality_press));
		HealthGlobalVariable.circleBar.update(HealthGlobalVariable.value[2],
				200, 2, aim[2]); // 将参数传递到Circlebar中
	}

	public void to_navige_btn(View v) {
		HealthGlobalVariable.mLeftMenu.toggle();
	}

	public void toggleMenu(View view) {
		HealthGlobalVariable.mLeftMenu.toggle();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * 获取空气质量AQI的线程
	 */
	public class MonitoringFechAQI implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			float second = 3;// 秒钟数!!!
			long time = (long) (1000 * second);
			while (true) {
				try {
					Thread.sleep(time);
					int AQI = fechAQI();
					if (AQI != 0) {
						SensorData.setAQI(AQI);
					}
					time = 1000 * 600;// 从第二次以后，10分钟每获取一次!!

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.println(e.getCause());
				}
			}
		}
	}

	public int fechAQI() {
		String AQI = "0";
		String url = "http://www.baidu.com/s?wd=AQI";
		Document doc = null;
		try {
			doc = Jsoup.connect(url).userAgent("Mozilla").timeout(8000).get();
		} catch (IOException e) {
			System.out.println("获取空气质量失败:" + e.getCause());

		}
		Elements elems = doc.select("[class=op_pm25_graexp]");// class="op_pm25_graexp"
		if (elems.size() == 0) {
			System.out.println("###获取空气质量失败！");
		} else {
			AQI = elems.get(0).text();
		}
		return Integer.valueOf(AQI);
	}

	/*
	 * private class CountAirQuality extends AsyncTask<Void, Integer, Void> {
	 * 
	 * @Override protected Void doInBackground(Void... params) { //
	 * 根据城市对应的区号获取AQI
	 * 
	 * if (HealthGlobalVariable.newAqi != 0) { try { Thread.sleep(3600000); }
	 * catch (InterruptedException e) { e.printStackTrace(); } } else { if
	 * (!MapGlobalVariable.city.equals("error")) { String cityCode = null; //
	 * 初始化城市区号 ArrayList<HashMap<String, Object>> list = null; try { list =
	 * Analysis(); // 解析JSON数据 } catch (JSONException | IOException e) {
	 * e.printStackTrace(); } cityCode = resultJson(list,
	 * MapGlobalVariable.city); // 将查询到的区号填充到URL中 if (cityCode != null) {
	 * Log.d("cityCode", cityCode); HealthGlobalVariable.url =
	 * "http://www.pm25.in/api/querys/pm2_5.json?city=" + cityCode +
	 * "&token=5j1znBVAsnSf5xQyNQyq&stations=no"; } }else {
	 * HealthGlobalVariable.newAqi = 0; } if
	 * (AirQuality.getAqi(HealthGlobalVariable.url) != null) {
	 * HealthGlobalVariable.newAqi = Integer.parseInt(AirQuality
	 * .getAqi(HealthGlobalVariable.url)); } } return null; }
	 * 
	 * @Override protected void onPreExecute() { super.onPreExecute(); }
	 * 
	 * }
	 */
	/*
	 * 解析JSON数据
	 * 
	 * @see android.app.Activity#onResume()
	 */

	/*
	 * @SuppressLint("NewApi") public ArrayList<HashMap<String, Object>>
	 * Analysis() throws JSONException, IOException { String res = ""; String
	 * fileName = "citycode.txt"; try {
	 * 
	 * InputStream in = getResources().getAssets().open(fileName); //
	 * 从assets下的文件中读取数据 int length = in.available(); byte[] buffer = new
	 * byte[length]; in.read(buffer); in.close(); res =
	 * EncodingUtils.getString(buffer, "UTF-8"); } catch (Exception e) {
	 * e.printStackTrace(); } JSONArray jsonArray = null;
	 * ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,
	 * Object>>(); jsonArray = new JSONArray(res); for (int i = 0; i <
	 * jsonArray.length(); i++) { JSONObject jsonObject =
	 * jsonArray.getJSONObject(i); HashMap<String, Object> map = new
	 * HashMap<String, Object>(); map.put("city", jsonObject.getString("city"));
	 * map.put("areaCode", jsonObject.getString("areaCode")); list.add(map); //
	 * 将数据保存到list中
	 * 
	 * } return list; }
	 * 
	 * public String resultJson(ArrayList<HashMap<String, Object>> list, String
	 * cityName) { try {
	 * 
	 * Iterator<HashMap<String, Object>> it = list.iterator(); while
	 * (it.hasNext()) { Map<String, Object> ma = it.next(); if
	 * (ma.get("city").equals(cityName)) { return ma.get("areaCode").toString();
	 * } } } catch (Exception e) { e.printStackTrace(); } return null; }
	 */
	@Override
	protected void onResume() {
		super.onResume();

		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		mPedometerSettings = new PedometerSettingUtil(mSettings);

		voiceUtil.setSpeak(mSettings.getBoolean("speak",
				SensorData.isOpenedVoice));

		// Read from preferences if the service was running on the last onPause
		mIsRunning = mPedometerSettings.isServiceRunning();

		// Start the service if this is considered to be an application start
		// (last onPause was long ago)
		if (!mIsRunning && mPedometerSettings.isNewStart()) {
			startStepService();
			bindStepService();
		} else if (mIsRunning) {
			bindStepService();
		}

		mPedometerSettings.clearServiceRunning();

		mIsMetric = SensorData.isMetric;

	}

	private StepService mService;

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = ((StepService.StepBinder) service).getService();

			mService.registerCallback(mCallback);
			mService.reloadSettings();

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}
	};

	private void startStepService() {
		if (!mIsRunning) {
			mIsRunning = true;
			startService(new Intent(HealthServiceActivity.this,
					StepService.class));
		}
	}

	private void bindStepService() {
		bindService(new Intent(HealthServiceActivity.this, StepService.class),
				mConnection, Context.BIND_AUTO_CREATE
						+ Context.BIND_DEBUG_UNBIND);
	}

	// private void unbindStepService() {
	// Log.i(TAG, "[SERVICE] Unbind");
	// unbindService(mConnection);
	// }

	// private void stopStepService() {
	// Log.i(TAG, "[SERVICE] Stop");
	// if (mService != null) {
	// Log.i(TAG, "[SERVICE] stopService");
	// stopService(new Intent(HealthServiceActivity.this,
	// StepService.class));
	// }
	// mIsRunning = false;
	// }

	// private void resetValues(boolean updateDisplay) {
	// if (mService != null && mIsRunning) {
	// mService.resetValues();
	// } else {
	// SharedPreferences state = getSharedPreferences("state", 0);
	// SharedPreferences.Editor stateEditor = state.edit();
	// if (updateDisplay) {
	// stateEditor.putInt("steps", 0);
	// stateEditor.putInt("pace", 0);
	// stateEditor.putFloat("distance", 0);
	// stateEditor.putFloat("speed", 0);
	// stateEditor.putFloat("calories", 0);
	// stateEditor.commit();
	// }
	// }
	// }

	private StepService.ICallback mCallback = new StepService.ICallback() {
		public void stepsChanged(int value) {
			mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
		}

		public void paceChanged(int value) {
			mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
		}

		public void distanceChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG,
					(int) (value * 1000), 0));
		}

		public void speedChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG,
					(int) (value * 1000), 0));
		}

		public void caloriesChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG,
					(int) (value), 0));
		}
	};

	private static final int STEPS_MSG = 1;
	private static final int PACE_MSG = 2;
	private static final int DISTANCE_MSG = 3;
	private static final int SPEED_MSG = 4;
	private static final int CALORIES_MSG = 5;

	/* 监听数据库更新及清零工作 */// MonitoringUpdata
	public class MonitoringRefreshTable implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			float second = (float) 0.5;// 秒钟数!!!
			long time = (long) (1000 * second);
			while (true) {
				try {
					Thread.sleep(time);
					if (SensorData.isLogin()
							&& SensorData.getStepNum_lastRefresh() != SensorData
									.getStepNum()) {
						CalculateUtil.RefreshTable(HealthServiceActivity.this);
						SensorData.setStepNum_lastRefresh(SensorData
								.getStepNum());// 同步数据!!!
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	// #################################################
	/* 监听当前系统是否被语音播报所占用 */
	public class MonitoringIsSpeaking implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			float second = (float) 0.1;// 秒钟数!!!
			long time = (long) (1000 * second);
			while (true) {
				try {
					Thread.sleep(time);

					if (voiceUtil.isSpeakingNow()) {
						System.out
								.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@----正在语音播报，暂停歌曲！！");
					} else {
						System.out.println("&&&&&&=====语音播报结束，恢复唱歌歌曲！！");
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	// #################################################
	/* 监听跑步时间 */
	public class MonitoringRunningTime implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			float second = (float) 0.08;// 秒钟数!!!
			long time = (long) (1000 * second);
			while (true) {
				try {
					Thread.sleep(time);
					/* 运动强度在二级及其以上时，定义为“跑步” */
					if (SensorData.getMoveHZ() >= SensorData.LEVEL_DOWN) {
						SensorData.setRunning_sport(true);// 跑步
						SensorData.setRunning_Time(SensorData.getRunning_Time()
								+ time);// 累加跑步时间!!!
					} else {
						SensorData.setRunning_sport(false);// 步行
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public class MonitoringSpeak implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			float second = (float) 0.08;// 秒钟数!!!
			while (true) {
				try {
					Thread.sleep((long) (1000 * second));

					if (SensorData.isFirst) {
						Thread.sleep(3000);
						// SensorData.setSpeaking(true);//开始语音播报!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
						voiceUtil.say(getString(R.string.welcome));// 获取String.xml中的数据!!

						// SensorData.setSpeaking(false);//结束语音播报!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
						SensorData.isFirst = false;
					}

					if (SensorData.getStepNum_lastSpeak() != SensorData
							.getStepNum()) {// 如果步数没有发生变化，就判断当前没有运动!!!!!!!!!
						/* 语音播报频率 */
						if (SensorData.getStepNum()
								% SensorData.getSpeakHZ_StepNum() == 0) {
							// voiceUtil.say("您已经走了"+SensorData.stepNum+"步");//测试!!!!
							String message = "您已经走了" + SensorData.stepNum
									+ "步，相当于" + SensorData.distance + "公里，"
									+ "消耗了" + SensorData.energy + "卡路里！";
							System.out.println(message);
							// SensorData.setSpeaking(true);//开始语音播报!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
							voiceUtil.say(message);// !!!~!!!!!!!!!!!!!!!!!!!

							String levelMessage = "";
							if (SensorData.getMoveHZ() < SensorData.LEVEL_DOWN) {// 步频90以下，运动强度等级为LEVEL_ONE
								levelMessage = getString(R.string.level_message_one);// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
							} else if (SensorData.getMoveHZ() >= SensorData.LEVEL_DOWN
									&& SensorData.getMoveHZ() <= SensorData.LEVEL_UP) {// 步频在90和240之间，运动强度等级为LEVEL_TWO
								levelMessage = getString(R.string.level_message_two);// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
							} else {// 步频在240之上，运动强度等级为LEVEL_THREE
								levelMessage = getString(R.string.level_message_three);// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
							}
							voiceUtil.say("平均每分钟运动" + SensorData.getMoveHZ()
									+ "步," + levelMessage);// !!!!!!!!!!!!!!!!!!!!!
							voiceUtil.say(CalculateUtil
									.GetRunningTimeByString(SensorData
											.getRunning_Time()));// !!!!!!!!!!
							System.out.println("---------------跑步时间："
									+ SensorData.getRunning_Time());// 测试!!!!!!!!!!!!!!!
							// SensorData.setSpeaking(false);//结束语音播报!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
						}

						SensorData
								.setStepNum_lastSpeak(SensorData.getStepNum());
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	// ##########################################################
	/* 监测用户是否在运动 */
	public class MonitoringAVGSpeed implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			long second = 5;// 秒钟数!!!
			SensorData.setMonitor_LastTime(System.currentTimeMillis());
			while (true) {
				try {
					// voiceUtil.say("休眠前：平均速度");//测试!!!!!!!!!!!!!!!!!!!!!
					Thread.sleep(1000 * second);
					// voiceUtil.say("休眠后：平均速度");//测试!!!!!!!!!!!!!!!!!!!!!

					// long
					// distance_temp=getIntegerPart(SensorData.getDistance());

					if (SensorData.getStepNum_old() == SensorData.getStepNum()) {// 如果步数没有发生变化，就判断当前没有运动!!!!!!!!!
						SensorData.isMoving = false;
						SensorData.setSpeed(0);
						SensorData.setMoveHZ(0);
						SensorData.setLevel(SensorData.LEVEL_ONE);
						System.out.println("--------------运动速度们为零!!!!!!");
						// voiceUtil.say("步数相等："+SensorData.getStepNum_old());//测试！！！！！！！！！！！！！！！1
					} else {
						SensorData.isMoving = true;
						String levelMessage = "";

						/* 平均监测时间间隔 */
						long currentTime = System.currentTimeMillis();
						long interval = currentTime
								- SensorData.getMonitor_LastTime();

						if (interval >= SensorData.AVERAGE_SPEED_MONITOR_TIME) {
							/*
							 * voiceUtil.say("步数 不相等，以前步数："+SensorData.
							 * getStepNum_old()
							 * +"现在步数："+SensorData.getStepNum())
							 * ;//测试！！！！！！！！！！！！！！！1
							 * voiceUtil.say("时间间隔："+interval
							 * );//测试！！！！！！！！！！！！！！！1
							 */
							SensorData.setMonitor_LastTime(currentTime);
							// voiceUtil.say("您已经走了"+SensorData.stepNum+"步，休息一下吧！");//测试!!!!!!!!!!!!!!!!
							// voiceUtil.say("您已经走了"+SensorData.distance+"公里，休息一下吧！");//测试!!!!!!!!!!!!!!!!!!!
							SensorData
									.setSpeed((SensorData.getDistance() - SensorData
											.getDistance_old())
											* 3600
											/ (interval / 1000));// 已修改改进!!!
							SensorData.setSpeed(getFormatFloat(SensorData
									.getSpeed()));
							SensorData
									.setMoveHZ((SensorData.getStepNum() - SensorData
											.getStepNum_old())
											* 60
											/ (int) (interval / 1000));// 已修改改进!!!

							if (SensorData.getMoveHZ() < SensorData.LEVEL_DOWN) {// 步频90以下，运动强度等级为LEVEL_ONE
								SensorData.setLevel(SensorData.LEVEL_ONE);
								levelMessage = getString(R.string.level_message_one);// 测试!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
							} else if (SensorData.getMoveHZ() >= SensorData.LEVEL_DOWN
									&& SensorData.getMoveHZ() <= SensorData.LEVEL_UP) {// 步频在90和240之间，运动强度等级为LEVEL_TWO
								SensorData.setLevel(SensorData.LEVEL_TWO);
								levelMessage = getString(R.string.level_message_two);// 测试!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
							} else {// 步频在240之上，运动强度等级为LEVEL_THREE
								SensorData.setLevel(SensorData.LEVEL_THREE);
								levelMessage = getString(R.string.level_message_three);// 测试!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
							}

							SensorData.setStepNum_old(SensorData.getStepNum());
							SensorData
									.setDistance_old(SensorData.getDistance());

							System.out.println(levelMessage);// 测试!!!!!!!!!!!!!!!!!!!!!

							System.out.println("--------------步数："
									+ SensorData.getStepNum());
							System.out.println("-------------#修改速度："
									+ SensorData.getSpeed());
							System.out.println("-------------#修改步频："
									+ SensorData.getMoveHZ());
							System.out.println("--------------距离："
									+ SensorData.getDistance());
							System.out.println("--------------能量："
									+ SensorData.getEnergy());
							System.out.println("==============运动强度等级："
									+ SensorData.getLevel());
						}
					}

					// voiceUtil.say("当前运动距离一公里！");//测试!!!!

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	long getIntegerPart(double d) {
		String s = (d + "");
		s = s.substring(0, s.indexOf("."));
		return Long.valueOf(s);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case STEPS_MSG:
				mStepValue = (int) msg.arg1;
				if (SensorData.getStepNum() == mStepValue) {// 如果步数没有发生变化，就判断当前没有运动!!!!!!!!!
					SensorData.isMoving = false;
					SensorData.setSpeed(0);
					SensorData.setMoveHZ(0);
				} else {
					SensorData.setStepNum(mStepValue);
					SensorData.isMoving = true;
				}
				if (0 == HealthGlobalVariable.model) {
					HealthGlobalVariable.value[0] = (int) SensorData
							.getStepNum();
					HealthGlobalVariable.circleBar.update(
							HealthGlobalVariable.value[0], 0, 0, aim[0]); // 将参数传递到Circlebar中
				}
				if (2 == HealthGlobalVariable.model) {
					HealthGlobalVariable.value[2] = (int) SensorData.getAQI();
					HealthGlobalVariable.circleBar.update(
							HealthGlobalVariable.value[2], 0, 2, aim[2]); // 将参数传递到Circlebar中
				}

				System.out.println("######步数:" + SensorData.getStepNum());// 测试!!!!!!
				break;
			case PACE_MSG:
				mPaceValue = msg.arg1;
				if (mPaceValue <= 0) {
					SensorData.setMoveHZ(0);
					System.out.println("######步频:" + SensorData.getMoveHZ());// 测试!!!!!!
				} else {
					SensorData.setMoveHZ(mPaceValue);
					System.out.println("######步频:" + SensorData.getMoveHZ());// 测试!!!!!!
				}
				break;
			case DISTANCE_MSG:
				mDistanceValue = ((int) msg.arg1) / 1000f;
				if (mDistanceValue <= 0) {
					SensorData.setDistance(0);
					System.out.println("######距离:" + SensorData.getDistance());// 测试!!!!!!
				} else {
					float temp = getFormatFloat(mDistanceValue);
					SensorData.setDistance(temp);
					System.out.println("######距离:" + SensorData.getDistance());// 测试!!!!!!
				}
				break;
			case SPEED_MSG:
				mSpeedValue = ((int) msg.arg1) / 1000f;
				if (mSpeedValue <= 0) {
					SensorData.setSpeed(0);
					System.out.println("######速度:" + SensorData.getSpeed());// 测试!!!!!!
				} else {
					float temp = getFormatFloat(mSpeedValue);
					SensorData.setSpeed(temp);
					System.out.println("######速度:" + SensorData.getSpeed());// 测试!!!!!!
				}
				break;
			case CALORIES_MSG:
				mCaloriesValue = msg.arg1;
				if (mCaloriesValue <= 0) {
					SensorData.setEnergy(0);
					System.out.println("######kCal:" + SensorData.getEnergy());// 测试!!!!!!
				} else {
					SensorData.setEnergy(mCaloriesValue);
					System.out.println("######kCal:" + SensorData.getEnergy());// 测试!!!!!!
				}
				HealthGlobalVariable.value[1] = (int) SensorData.getEnergy();
				break;
			default:
				super.handleMessage(msg);
			}

			mService.showNotification();// ##############################################################################

		}

	};

	/* 获取格式化后的float类型 */
	public float getFormatFloat(float f) {
		String s = f + "";
		if (s.length() - s.indexOf(".") - 1 > 3) {
			s = s.substring(0, s.indexOf(".") + 4);
		}
		f = Float.valueOf(s);
		return f;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
		}
		return false;
	}

	private void exit() {
        if(!isExit) {
            isExit = true;
            Toast.makeText(this, "在按一次退出程序", Toast.LENGTH_SHORT).show();
            new Timer().schedule(new TimerTask() {
                
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            finish();
        }
    }
	
	

}
