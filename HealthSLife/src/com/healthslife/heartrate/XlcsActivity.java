package com.healthslife.heartrate;

import java.util.concurrent.atomic.AtomicBoolean;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.healthslife.R;

public class XlcsActivity extends Activity {

	// =============================================分割用===============================================

	// Activity标签
	private static final String TAG = "心率测试模块";

	// 控制用
	private static final AtomicBoolean processing = new AtomicBoolean(false);

	// 定义全部组件
	private static SurfaceView preview = null;
	private static SurfaceHolder previewHolder = null;
	private static Camera camera = null;
	private static View image = null;
	// private static TextView text = null;
	private static ImageView background_cover = null;
	private static ImageView xlcs_helpImageView = null;
 
	private ImageView xlcs_back_imageview;
	// 屏幕禁止休眠功能用
	private static WakeLock wakeLock = null;

	// 数据处理用
	private static int averageIndex = 0;
	private static final int averageArraySize = 4;
	private static final int[] averageArray = new int[averageArraySize];

	public static enum TYPE {
		GREEN, RED
	};

	private static TYPE currentType = TYPE.GREEN;

	public static TYPE getCurrent() {
		return currentType;
	}

	private static int beatsIndex = 0;
	private static final int beatsArraySize = 3;
	private static final int[] beatsArray = new int[beatsArraySize];
	private static double beats = 0;
	private static long startTime = 0;

	// 进度条
	private static CircleProgressBar mBar;

	// =============================================分割用===============================================

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.hearttest_service);

		// 获取组件SurfaceView
		preview = (SurfaceView) findViewById(R.id.xlcs_preview);
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// 获取组件Image，Text和list
		image = findViewById(R.id.xlcs_image);
		// text = (TextView) findViewById(R.id.xlcs_text);
		background_cover = (ImageView) findViewById(R.id.background);
		xlcs_back_imageview = (ImageView) findViewById(R.id.xlcs_back_imageview);
		xlcs_back_imageview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		// 电源管理工具
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm
				.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

		// 进度条
		mBar = (CircleProgressBar) findViewById(R.id.xlcs_circle);

		// help图片
		xlcs_helpImageView = (ImageView) findViewById(R.id.xlcs_help);

	}

	// =============================================分割用===============================================

	@SuppressLint("Wakelock")
	public void onResume() {
		super.onResume();

		// 获取WakeLock锁
		wakeLock.acquire();

		// 打开摄像头
		camera = Camera.open();

		// 获取系统时间
		startTime = System.currentTimeMillis();
	}

	// =============================================分割用===============================================

	public void onPause() {
		super.onPause();

		// 释放wakeLock锁
		wakeLock.release();

		// 摄像头处理
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	// =============================================分割用===============================================

	// 此处定义的是Camera的内部对象，作用是在界面上实时显示摄像头锁获取到的图像数据
	private static int beatsAvg;// 平均心率数据
	private static PreviewCallback previewCallback = new PreviewCallback() {
		// View的回调函数对象用于实时预览Camera的图像数据

		/**
		 * 图像数据的处理函数.
		 * 
		 * @param data
		 *            表示为图数据的字节数组
		 * @param cam
		 *            摄像头对象
		 */
		public void onPreviewFrame(byte[] data, Camera cam) {
			if (data == null)
				throw new NullPointerException();
			Camera.Size size = cam.getParameters().getPreviewSize();
			if (size == null)
				throw new NullPointerException();

			if (!processing.compareAndSet(false, true))
				return;

			int width = size.width;
			int height = size.height;

			int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(),
					height, width);// 获取到图像数据中的红色像素的单位平均值
			Log.i(TAG, "imgAvg=" + imgAvg);
			if (imgAvg <= 120 || imgAvg == 255) {// 图像异常判定
			// text.setText("摄像头没有捕获到正确的图像\n请确定将手指覆盖摄像头！");
			// mBar.setProgress(-1);
				try {
					Thread.currentThread().sleep(1000);// 覆盖图形延迟
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				background_cover.setVisibility(View.VISIBLE);
				processing.set(false);
				return;
			}// 错误数据处理
			background_cover.setVisibility(View.GONE);

			int averageArrayAvg = 0;
			int averageArrayCnt = 0;
			for (int i = 0; i < averageArray.length; i++) {
				if (averageArray[i] > 0) {
					averageArrayAvg += averageArray[i];
					averageArrayCnt++;
				}
			}

			int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt)
					: 0;
			TYPE newType = currentType;
			if (imgAvg < rollingAverage) {
				newType = TYPE.RED;
				if (newType != currentType) {
					beats++;
					Log.e(TAG, "BEAT!! beats=" + beats);
				}
			} else if (imgAvg > rollingAverage) {
				newType = TYPE.GREEN;
			}

			if (averageIndex == averageArraySize)
				averageIndex = 0;
			averageArray[averageIndex] = imgAvg;
			averageIndex++;

			// 转换状态，从一个转换到另外一个相同的
			if (newType != currentType) {
				currentType = newType;

				image.postInvalidate();
			}

			// 采样时间
			long endTime = System.currentTimeMillis();
			double totalTimeInSecs = (endTime - startTime) / 1000d;
			if (totalTimeInSecs >= 0.7) {// 采样时间
			// boolean changed = true;//数据改变判定
				double bps = (beats / totalTimeInSecs);
				int dpm = (int) (bps * 60d);// 时间使用
				if (dpm < 40 || dpm > 140) {
					startTime = System.currentTimeMillis();
					beats = 0;
					// changed = false;
					processing.set(false);
					return;
				}

				Log.e(TAG, "totalTimeInSecs=" + totalTimeInSecs + " beats="
						+ beats);

				// 心跳计数
				if (beatsIndex == beatsArraySize)
					beatsIndex = 0;
				beatsArray[beatsIndex] = dpm;
				beatsIndex++;

				int beatsArrayAvg = 0;
				int beatsArrayCnt = 0;
				for (int i = 0; i < beatsArray.length; i++) {
					if (beatsArray[i] > 0) {
						beatsArrayAvg += beatsArray[i];
						beatsArrayCnt++;
					}
				}

				// 数据显示
				// if(changed) {
				beatsAvg = (beatsArrayAvg / beatsArrayCnt);
				// text.setText("当前心率约为："+String.valueOf(beatsAvg));
				mBar.setProgress(beatsAvg);

				// }
				// else {
				// beatsAvg= 0;
				// text.setText("您的心率数值有错\n请确定将手指覆盖摄像头！");
				//
				// }
				// // //连续三次数据相同提示用户
				// if (warnItem == beatsAvg) {
				// warnCount++;
				// }else if (warnItem != beatsAvg) {
				// warnCount = 0;
				// }
				// warnItem = beatsAvg;
				// if(warnCount >= 2)
				// text.setText("心跳数值出现错误\n请确定将手指覆盖摄像头！");
				// //=======================================
				startTime = System.currentTimeMillis();
				beats = 0;
			}
			processing.set(false);
		}
	};
	// private static int warnCount = 0;
	// private static int warnItem = 0;

	// =============================================分割用===============================================

	// =============================================分割用===============================================

	// 将数据显现到界面上
	private static SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera.setPreviewDisplay(previewHolder);
				camera.setPreviewCallback(previewCallback);
			} catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Camera.Parameters parameters = camera.getParameters();
			parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			Camera.Size size = getSmallestPreviewSize(width, height, parameters);
			if (size != null) {
				parameters.setPreviewSize(size.width, size.height);
				Log.d(TAG, "Using width=" + size.width + " height="
						+ size.height);
			}
			camera.setParameters(parameters);
			camera.startPreview();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
		}
	};

	// =============================================分割用===============================================

	// 获取摄像头的最小预览大小
	private static Camera.Size getSmallestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea < resultArea)
						result = size;
				}
			}
		}
		return result;
	}

	// =============================================分割用===============================================

	public class ListAdapter extends BaseAdapter {

		private Context mContext;
		private Cursor mCursor;

		public ListAdapter(Context context, Cursor cursor) {

			mContext = context;
			mCursor = cursor;
		}

		@Override
		public int getCount() {
			return mCursor.getCount();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		// 将数据库数据显示到界面上
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView mTextView = new TextView(mContext);
			mCursor.moveToPosition(position);
			mTextView.setText(mCursor.getString(3));
			mTextView.setTextSize(25);
			return mTextView;
		}

	}


	public void xlcs_help(View view) {

		xlcs_helpImageView.setVisibility(View.INVISIBLE);

	}

	// =============================================分割用===============================================
	public static int getBeatdata() {
		return beatsAvg;
	}

}