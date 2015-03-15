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
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.healthslife.R;
import com.healthslife.allinterface.CircleBar;
import com.healthslife.allinterface.SlidingMenu;
import com.healthslife.map.MapService;

public class HealthServiceActivity extends Activity {

	private CircleBar circleBar;
	private int model = 0; // 模式标识
	private int newAqi = 0; // 空气质量指数AQI
	private int value[] = { 1000, 90, 35 }; // 传递的参数
	private int aim[] = { 5000, 200, 0 }; // 设定的目标值
	String cityName = null;
	private String url;
	private Button map_btn;
	private SlidingMenu mLeftMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.health_service);

		circleBar = (CircleBar) findViewById(R.id.circle_bar);

		circleBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (2 == model) {
					value[2] = newAqi;
					if (0 == newAqi) {
						Toast.makeText(getApplicationContext(), "网络连接错误",
								Toast.LENGTH_LONG).show();
					}
				}
				circleBar.update(value[model], 500, model, aim[model]); // 将参数传递到Circlebar中
				if (model > 1) {
					model = 0;
				} else {
					model++;
				}
			}

		});
		mLeftMenu = (SlidingMenu) findViewById(R.id.id_menu);
		// 从地图模块传递当前城市
		Intent getCityName = getIntent();
		Bundle bundle = getCityName.getExtras();
		cityName = bundle.getString("cityName");
		Toast.makeText(getApplicationContext(), "城市名" + cityName,
				Toast.LENGTH_LONG).show();
		// 执行线程
		CountAirQuality airQuality = new CountAirQuality();
		airQuality.execute();

		map_btn = (Button) findViewById(R.id.map_btn);
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
	}

	public void toggleMenu(View view) {
		mLeftMenu.toggle();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * 创建线程异步处理获取的空气质量
	 */

	private class CountAirQuality extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// 后台首先通过地图定位到所在城市然后计算AQI
			while (true) {
				if (!cityName.equals("error")) {
					String cityCode = null; // 城市编码
					ArrayList<HashMap<String, Object>> list = null;
					try {
						list = Analysis(); // 得到城市对应编码的列表
					} catch (JSONException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					cityCode = resultJson(list, cityName);
					// 获取AQI指数的URL
					if (cityCode != null) {
						Log.d("cityCode", cityCode);
						url = "http://www.pm25.in/api/querys/pm2_5.json?city="
								+ cityCode
								+ "&token=5j1znBVAsnSf5xQyNQyq&stations=no";
					}
				}
				if (AirQuality.getAqi(url) != null) {
					newAqi = Integer.parseInt(AirQuality.getAqi(url));
				}
				if (newAqi != 0) {
					try {
						Thread.sleep(3600000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				publishProgress(null);
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

	}

	/*
	 * 从文件中读取JSON数据的解析
	 * 
	 * @see android.app.Activity#onResume()
	 */

	@SuppressLint("NewApi")
	public ArrayList<HashMap<String, Object>> Analysis() throws JSONException,
			IOException {
		String res = "";
		String fileName = "citycode.txt";
		try {

			// 得到资源中的asset数据流
			InputStream in = getResources().getAssets().open(fileName);

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
			list.add(map);

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
		// 退出时销毁定位
		super.onDestroy();
	}
}
