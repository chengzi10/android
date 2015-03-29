package com.healthslife.heartrate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.healthslife.R;

public class CircleProgressBar extends View {

	private int maxProgress = 120;
	private int progress = 75;
	private int progressStrokeWidth = 60;
	private int marxArcStorkeWidth = 60;
	private String text;
	// 画圆所在的距形区域
	RectF oval;
	Paint paint;

	// 心跳显示图
	Bitmap accuracy = BitmapFactory.decodeResource(this.getResources(),
			R.drawable.xlcs_accuracy);// 资源文件
	int bmwidth = accuracy.getWidth();
	int bmheight = accuracy.getHeight() / 7;
	Bitmap part1 = Bitmap.createBitmap(accuracy, 0, bmheight * 0, bmwidth,
			bmheight);
	Bitmap part2 = Bitmap.createBitmap(accuracy, 0, bmheight * 1, bmwidth,
			bmheight);
	Bitmap part3 = Bitmap.createBitmap(accuracy, 0, bmheight * 2, bmwidth,
			bmheight);
	Bitmap part4 = Bitmap.createBitmap(accuracy, 0, bmheight * 3, bmwidth,
			bmheight);
	Bitmap part5 = Bitmap.createBitmap(accuracy, 0, bmheight * 4, bmwidth,
			bmheight);
	Bitmap part6 = Bitmap.createBitmap(accuracy, 0, bmheight * 5, bmwidth,
			bmheight);
	Bitmap part7 = Bitmap.createBitmap(accuracy, 0, bmheight * 6, bmwidth,
			bmheight);

	// 字体
	Typeface dsdig = Typeface.createFromAsset(getContext().getAssets(),
			"fonts/dsdig.ttf");

	public CircleProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		oval = new RectF();
		paint = new Paint();
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = this.getWidth();
		int height = this.getHeight();

		width = (width > height) ? height : width;
		height = (width > height) ? height : width;

		paint.setAntiAlias(true); // 设置画笔为抗锯齿
		paint.setColor(Color.WHITE); // 设置画笔颜色
		canvas.drawColor(Color.TRANSPARENT); // 白色背景
		paint.setStrokeWidth(progressStrokeWidth); // 线宽
		paint.setStyle(Style.STROKE);

		oval.left = marxArcStorkeWidth * 1 / 2; // 左上角x
		oval.top = marxArcStorkeWidth * 1 / 2; // 左上角y
		oval.right = width - marxArcStorkeWidth * 1 / 2; // 左下角x
		oval.bottom = height - marxArcStorkeWidth * 1 / 2; // 右下角y

		canvas.drawArc(oval, 0, 360, false, paint); // 绘制白色圆圈，即进度条背景
		paint.setColor(Color.RED);
		paint.setStrokeWidth(marxArcStorkeWidth);
		paint.setStrokeCap(Paint.Cap.ROUND);

		if (-1 != progress && 0 != progress)
			canvas.drawArc(oval, 0, ((float) progress / maxProgress) * 360,
					false, paint); // 绘制进度圆弧

		// 绘制文本
		paint.setColor(Color.GRAY);
		if (-1 == progress || 0 == progress)
			progress = 0;
		paint.setStrokeWidth(1);
		paint.setTypeface(dsdig);// 设置字体
		// 数字
		text = progress + " ";
		int textHeight = height / 5;
		int textWidth = (int) paint.measureText("0000", 0, text.length());
		int BMPWidth = (int) paint.measureText("00BMP", 0, text.length());
		paint.setStyle(Style.FILL);
		paint.setTextSize(textHeight);
		canvas.drawText(text, width / 2 - textWidth - BMPWidth / 2, height / 2
				+ textHeight / 2, paint);
		// 单位
		paint.setTextSize(textHeight * 2 / 3);
		canvas.drawText(" BMP ", width / 2 - BMPWidth / 2, height / 2
				+ textHeight / 2, paint);

		// 图标
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true); // 去锯齿

		if (progress <= 40) {
			canvas.drawBitmap(part1, width * 17 / 24, height / 2 - textHeight
					/ 4, paint);
		} else if (progress <= 50) {
			canvas.drawBitmap(part2, width * 17 / 24, height / 2 - textHeight
					/ 4, paint);
		} else if (progress <= 60) {
			canvas.drawBitmap(part3, width * 17 / 24, height / 2 - textHeight
					/ 4, paint);
		} else if (progress <= 70) {
			canvas.drawBitmap(part4, width * 17 / 24, height / 2 - textHeight
					/ 4, paint);
		} else if (progress <= 80) {
			canvas.drawBitmap(part5, width * 17 / 24, height / 2 - textHeight
					/ 4, paint);
		} else if (progress <= 90) {
			canvas.drawBitmap(part6, width * 17 / 24, height / 2 - textHeight
					/ 4, paint);
		} else if (progress >= 90) {
			canvas.drawBitmap(part7, width * 17 / 24, height / 2 - textHeight
					/ 4, paint);
		}

		// 光晕效果

	}

	public int getMaxProgress() {
		return maxProgress;
	}

	public void setMaxProgress(int maxProgress) {
		this.maxProgress = maxProgress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
		this.invalidate();
	}

	/**
	 * 非ＵＩ线程调用
	 */
	public void setProgressNotInUiThread(int progress) {
		this.progress = progress;
		this.postInvalidate();
	}

}