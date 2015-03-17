package com.healthslife.health;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.healthslife.R;
import com.healthslife.allinterface.CircleBar;
import com.healthslife.allinterface.SlidingMenu;
import com.healthslife.heartrate.XlcsActivity;
import com.healthslife.loginregister.Login;
import com.healthslife.map.MapGlobalVariable;
import com.healthslife.map.MapService;

public class HealthServiceActivity extends Activity {

	private int value[] = { 1000, 90, 35 }; // 数据收集模块传递的参数
	private int aim[] = { 5000, 200, 0 }; // 设置模块传递的用户设置目标

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.health_service);

		HealthGlobalVariable.circleBar = (CircleBar) findViewById(R.id.circle_bar);

		HealthGlobalVariable.circleBar
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (2 == HealthGlobalVariable.model) {
							value[2] = HealthGlobalVariable.newAqi;
							if (0 == HealthGlobalVariable.newAqi) {
								Toast.makeText(getApplicationContext(),
										"亲，获取空气质量失败，看看窗外吧", Toast.LENGTH_SHORT)
										.show();
							}
						}
						HealthGlobalVariable.circleBar.update(
								value[HealthGlobalVariable.model], 500,
								HealthGlobalVariable.model,
								aim[HealthGlobalVariable.model]); // 将参数传递到Circlebar中
						if (HealthGlobalVariable.model > 1) {
							HealthGlobalVariable.model = 0;
						} else {
							HealthGlobalVariable.model++;
						}
					}

				});
		HealthGlobalVariable.mLeftMenu = (SlidingMenu) findViewById(R.id.id_menu);
		/*
		 * 获取欢迎界面传来的城市名称
		 */
		// Intent getCityName = getIntent();
		// Bundle bundle = getCityName.getExtras();
		// HealthGlobalVariable.cityName = bundle.getString("cityName");
		Toast.makeText(getApplicationContext(),
				"您现在位于" + MapGlobalVariable.city, Toast.LENGTH_SHORT)
				.show();
		// 执行获取AQI的线程
		CountAirQuality airQuality = new CountAirQuality();
		airQuality.execute();

		Button map_btn = (Button) findViewById(R.id.map_btn);
		map_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mapIntent = new Intent();
				mapIntent
						.setClass(HealthServiceActivity.this, MapService.class);
				startActivity(mapIntent);
			}

		});

		/*
		 * 跳转音乐服务
		 */

		TextView music_service = (TextView) findViewById(R.id.music_service);
		music_service.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent music_intent = new Intent();
				music_intent.setClass(HealthServiceActivity.this,
						MapService.class);
				startActivity(music_intent);
			}

		});

		TextView heartTest_service = (TextView) findViewById(R.id.heartTest_service);
		heartTest_service.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent heartTest_intent = new Intent();
				heartTest_intent.setClass(HealthServiceActivity.this,
						XlcsActivity.class);
				startActivity(heartTest_intent);
			}

		});
		TextView login_service = (TextView) findViewById(R.id.login_service);
		login_service.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent heartTest_intent = new Intent();
				heartTest_intent.setClass(HealthServiceActivity.this,
						Login.class);
				startActivity(heartTest_intent);
			}

		});
	}

	/*
	 * 跳转到登录界面
	 */
	public void login_service(View v) {
		startActivity(new Intent(HealthServiceActivity.this, Login.class));
	}

	public void map_btn(View v) {

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

	private class CountAirQuality extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// 根据城市对应的区号获取AQI
			while (true) {
				if (!MapGlobalVariable.city.equals("error")) {
					String cityCode = null; // 初始化城市区号
					ArrayList<HashMap<String, Object>> list = null;
					try {
						list = Analysis(); // 解析JSON数据
					} catch (JSONException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					cityCode = resultJson(list, MapGlobalVariable.city);
					// 将查询到的区号填充到URL中
					if (cityCode != null) {
						Log.d("cityCode", cityCode);
						HealthGlobalVariable.url = "http://www.pm25.in/api/querys/pm2_5.json?city="
								+ cityCode
								+ "&token=5j1znBVAsnSf5xQyNQyq&stations=no";
					}
				}
				if (AirQuality.getAqi(HealthGlobalVariable.url) != null) {
					HealthGlobalVariable.newAqi = Integer.parseInt(AirQuality
							.getAqi(HealthGlobalVariable.url));
				}
				if (HealthGlobalVariable.newAqi != 0) {
					try {
						Thread.sleep(3600000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

	}

	/*
	 * 解析JSON数据
	 * 
	 * @see android.app.Activity#onResume()
	 */

	@SuppressLint("NewApi")
	public ArrayList<HashMap<String, Object>> Analysis() throws JSONException,
			IOException {
		String res = "";
		String fileName = "citycode.txt";
		try {

			InputStream in = getResources().getAssets().open(fileName); // 从assets下的文件中读取数据
			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			in.close();
			res = EncodingUtils.getString(buffer, "GB2312");
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONArray jsonArray = null;
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		jsonArray = new JSONArray(res);
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("city", jsonObject.getString("city"));
			map.put("areaCode", jsonObject.getString("areaCode"));
			list.add(map); // 将数据保存到list中

		}
		return list;
	}

	public String resultJson(ArrayList<HashMap<String, Object>> list,
			String cityName) {
		try {

			Iterator<HashMap<String, Object>> it = list.iterator();
			while (it.hasNext()) {
				Map<String, Object> ma = it.next();
				if (ma.get("city").equals(cityName)) {
					return ma.get("areaCode").toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		// 退出销毁
		super.onDestroy();
	}
}
