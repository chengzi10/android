package com.healthslife.heartrate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.healthslife.R;

public class HeartbeatView extends View {
	private static final Matrix matrix = new Matrix();// 表示一个3X3的坐标变换矩阵
	private static final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);// 绘制图像，有风格颜色信息
																		// ANTI_ALIAS_FLAG是使位图过滤的位掩码标志

	// 位图文件，分别表示红绿像素文件
	private static Bitmap greenBitmap = null;
	private static Bitmap redBitmap = null;

	// 表示宽高数据
	private static int parentWidth = 0;
	private static int parentHeight = 0;

	// =============================================分割用===============================================

	// 构造函数，从资源文件中获取到位图对象
	public HeartbeatView(Context context, AttributeSet attr) {
		super(context, attr);

		greenBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.green_icon);
		redBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.red_icon);
	}

	public HeartbeatView(Context context) {
		super(context);
		greenBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.green_icon);
		redBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.red_icon);
	}

	// =============================================分割用===============================================

	// 决定View大小
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(parentWidth, parentHeight);
	}

	// =============================================分割用===============================================

	// 绘制函数
	@Override
	protected void onDraw(Canvas canvas) {
		if (canvas == null)
			throw new NullPointerException();

		Bitmap bitmap = null;// 判断当前的TYPE并对位图bitmap进行赋值
		if (XlcsActivity.getCurrent() == XlcsActivity.TYPE.GREEN)
			bitmap = greenBitmap;
		else
			bitmap = redBitmap;

		int bitmapX = bitmap.getWidth() / 2;
		int bitmapY = bitmap.getHeight() / 2;

		int parentX = parentWidth / 2;
		int parentY = parentHeight / 2;

		int centerX = parentX - bitmapX;
		int centerY = parentY - bitmapY;

		matrix.reset();
		matrix.postTranslate(centerX, centerY);
		canvas.drawBitmap(bitmap, matrix, paint);
	}
}