package com.healthslife.system;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.healthslife.R;

public class HandView extends View {

	public float currentX = 0;
	public float currentY = 50;
	Bitmap handimage;
	
	public HandView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		handimage = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.hand_steps);
		setFocusable(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		Paint hand = new Paint();
		canvas.drawBitmap(handimage, currentX, currentY, hand);
	}

}
