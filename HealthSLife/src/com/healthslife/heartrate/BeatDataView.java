package com.healthslife.heartrate;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class BeatDataView extends View {

	DisplayMetrics metric = getResources().getDisplayMetrics();
	int width = metric.widthPixels; // 屏幕宽度（像素）
	int height = metric.heightPixels; // 屏幕高度（像素）

	private int XLength = width * 9 / 10;
	private int YLength = height / 2; // XY轴长度
	private int XPoint = XLength / 15;
	private int YPoint = YLength / 3; // 原点坐标

	private int XScale = width / 10;
	private int YScale = height / 190; // 刻度

	private int XSL = width / 17;

	private float textSize = width / 500;
	private float lineSize1 = textSize * 4;
	private float lineSize2 = textSize * 5;

	private int MaxDataSize = XLength / XScale;
	private String[] YLabel = new String[YLength / YScale];
	private List<Integer> data = new ArrayList<Integer>();

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0x1234) {
				BeatDataView.this.invalidate();
			}
		};
	};

	public BeatDataView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 这里获取数据到data中
		new Thread(new Runnable() {
			int beatData;

			public void run() {
				while (true) {
					try {
						Thread.sleep(700);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (data.size() >= MaxDataSize) {
						data.remove(0);
					}
					beatData = XlcsActivity.getBeatdata();
					if (beatData != 0)
						data.add(beatData - 80);// YPoint基准数值：80
					handler.sendEmptyMessage(0x1234);
				}
			}
		}).start();

	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true); // 去锯齿

		paint.setColor(Color.BLACK);
		paint.setStrokeWidth((float) lineSize1);
		canvas.drawLine(XPoint, YPoint + 5 * YScale, XPoint + XLength, YPoint
				+ 5 * YScale, paint);// 画x轴
		canvas.drawLine(XPoint, YPoint + YLength / 2, XPoint, YPoint - YLength
				/ 2, paint);// 画y轴

		paint.setColor(Color.BLACK);
		paint.setStrokeWidth((float) textSize);
		paint.setTextSize(textSize * 18);

		canvas.drawText("50", XPoint - XSL, YPoint + 30 * YScale + 10, paint);
		canvas.drawText("60", XPoint - XSL, YPoint + 20 * YScale + 10, paint);
		canvas.drawText("70", XPoint - XSL, YPoint + 10 * YScale + 10, paint);
		canvas.drawText("80", XPoint - XSL, YPoint + 0 * YScale + 10, paint);// YPoint基准数值：80
		canvas.drawText("90", XPoint - XSL, YPoint - 10 * YScale + 10, paint);
		canvas.drawText("100", XPoint - XSL, YPoint - 20 * YScale + 10, paint);
		canvas.drawText("110", XPoint - XSL, YPoint - 30 * YScale + 10, paint);
		// 图形y坐标55对应心跳75

		if (data.size() > 1) {
			for (int i = 1; i < data.size(); i++) {
				paint.setColor(Color.RED);
				paint.setStrokeWidth((float) lineSize2);
				canvas.drawLine(XPoint + (i - 1) * XScale,
						YPoint - data.get(i - 1) * YScale, XPoint + i * XScale,
						YPoint - data.get(i) * YScale, paint);

				paint.setColor(Color.BLACK);
				paint.setStrokeWidth((float) textSize);
				paint.setTextSize(textSize * 20);
				canvas.drawText(String.valueOf(data.get(i) + 80), XPoint + i
						* XScale - 10, YPoint - data.get(i) * YScale + 3, paint);

			}
		}
	}
}
