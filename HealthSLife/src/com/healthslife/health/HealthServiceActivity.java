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
	private int model = 0; // ģʽ��ʶ
	private int newAqi = 0; // ��������ָ��AQI
	private int value[] = { 1000, 90, 35 }; // ���ݵĲ���
	private int aim[] = { 5000, 200, 0 }; // �趨��Ŀ��ֵ
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
						Toast.makeText(getApplicationContext(), "�������Ӵ���",
								Toast.LENGTH_LONG).show();
					}
				}
				circleBar.update(value[model], 500, model, aim[model]); // ���������ݵ�Circlebar��
				if (model > 1) {
					model = 0;
				} else {
					model++;
				}
			}

		});
		mLeftMenu = (SlidingMenu) findViewById(R.id.id_menu);
		// �ӵ�ͼģ�鴫�ݵ�ǰ����
		Intent getCityName = getIntent();
		Bundle bundle = getCityName.getExtras();
		cityName = bundle.getString("cityName");
		Toast.makeText(getApplicationContext(), "������" + cityName,
				Toast.LENGTH_LONG).show();
		// ִ���߳�
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
	 * �����߳��첽�����ȡ�Ŀ�������
	 */

	private class CountAirQuality extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// ��̨����ͨ����ͼ��λ�����ڳ���Ȼ�����AQI
			while (true) {
				if (!cityName.equals("error")) {
					String cityCode = null; // ���б���
					ArrayList<HashMap<String, Object>> list = null;
					try {
						list = Analysis(); // �õ����ж�Ӧ������б�
					} catch (JSONException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					cityCode = resultJson(list, cityName);
					// ��ȡAQIָ����URL
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
	 * ���ļ��ж�ȡJSON���ݵĽ���
	 * 
	 * @see android.app.Activity#onResume()
	 */

	@SuppressLint("NewApi")
	public ArrayList<HashMap<String, Object>> Analysis() throws JSONException,
			IOException {
		String res = "";
		String fileName = "citycode.txt";
		try {

			// �õ���Դ�е�asset������
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
		// �˳�ʱ���ٶ�λ
		super.onDestroy();
	}
}
