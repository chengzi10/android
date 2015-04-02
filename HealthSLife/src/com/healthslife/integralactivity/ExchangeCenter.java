package com.healthslife.integralactivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.healthslife.R;
import com.healthslife.sensor.data.SensorData;

public class ExchangeCenter extends Fragment {
	private GridView gridView;
	private ImageView sweepIV; 
	private TextView title_text;
	private TextView integral_text;
	public int integral = 0;
	private int QR_WIDTH = 300, QR_HEIGHT = 300;
	private int[] images = {R.drawable.icecream,R.drawable.jiandao,R.drawable.erji, R.drawable.shubiao, 
			  R.drawable.jiashiqi,R.drawable.huwan,R.drawable.yinxiang, R.drawable.jishiqi,
			R.drawable.cheng };
	private String[] text = { "冰激凌 500积分", "随身剪套 1000积分", "耳机 2000积分",
			"鼠标 3500积分", "加湿器 5300积分", "护腕 7800积分", "音响 10000积分",
			"电子计时器 12700积分", "健康秤 31700积分" };
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_exchange, container,
				false);
		integral=SensorData.getTotalCredits();//同步积分
		title_text = (TextView)view.findViewById(R.id.title_text);
		integral_text = (TextView)view.findViewById(R.id.integral_text);
		integral_text.setText(integral + " " + "分");
		this.gridView = (GridView) view.findViewById(R.id.gridView_all);
		sweepIV = (ImageView) view.findViewById(R.id.image);
		ArrayList<HashMap<String, Object>> imagelist = new ArrayList<HashMap<String, Object>>();
		// 使用HashMap将图片添加到一个数组中，注意一定要是HashMap<String,Object>类型的，因为装到map中的图片要是资源ID，而不是图片本身
		// 如果是用findViewById(R.drawable.image)这样把真正的图片取出来了，放到map中是无法正常显示的
		for (int i = 0; i < 9; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("images", images[i]);
			//map.put("text", text[i]);
			imagelist.add(map);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),
				imagelist, R.layout.activity_exchange_item, new String[] {
						"images"}, new int[] { R.id.online_iv});
		gridView.setAdapter(simpleAdapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case 0:
					exchange(integral , 500 , 0); 
					break;
				case 1:
					exchange(integral , 1000 , 1); 
					break;
				case 2:
					exchange(integral , 2000 , 2); 
					break;
				case 3:
					exchange(integral , 3500 , 3); 
					break;
				case 4:
					exchange(integral , 5300 , 4); 
					break;
				case 5:
					exchange(integral , 7800 , 5); 
					break;
				case 6:
					exchange(integral , 10000 , 6); 
					break;
				case 7:
					exchange(integral , 12700 , 7); 
					break;
				case 8:
					exchange(integral , 31700 , 8); 
					break;
				default:
					break;
				}
			}
		});
/*
		sweepIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sweepIV.setVisibility(View.INVISIBLE);
			}
		});
		*/
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		integral=SensorData.getTotalCredits();//同步积分
		super.onResume();
	}
	
	public void exchange(int integral , int standard , int i) {
		if (integral > standard) {
			String url = text[i]; 
			createQRImage(url);	
			Toast.makeText(getActivity(), R.string.exchange_explain, Toast.LENGTH_SHORT)
			.show();
		}else {
			Toast.makeText(getActivity(), R.string.integral_low, Toast.LENGTH_SHORT)
			.show();
		}
	}
	
	public void createQRImage(String url) {
		try {
			// 判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1) {
				return;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			// 显示到一个ImageView上面
			sweepIV.setImageBitmap(bitmap);
			sweepIV.setBackgroundColor(android.graphics.Color.parseColor("#F0F0F0"));
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}
	
}
