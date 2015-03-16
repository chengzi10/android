package com.healthslife.allinterface;

import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.healthslife.R;

@SuppressLint("ResourceAsColor")
public class CircleBar extends View {
	private RectF mColorWheelRectangle = new RectF(); // draw rectangle
	private Paint mDefaultWheelPaint;
	private Paint mColorWheelPaint;
	private Paint mColorWheelPaintCentre;
	private Paint mTextP, mTextnum, mTextch;
	private float circleStrokeWidth;
	private float mSweepAnglePer; // progress bar's angle
	private float mPercent;
	private String completionRate; // Target finish reliability
	private int stepnumber, stepnumbernow;
	private float pressExtraStrokeWidth;
	private BarAnimation anim;
	private int setAim = 0; // the default target
	private float mPercent_y, stepnumber_y, Text_y;
	private DecimalFormat fnum = new DecimalFormat("#.0"); // keep one decimal
															// place
	public int allModel; // The definition of global variables 0:walk  1:energy
							// 2:aqi
	private String modelText, modelTextView = getResources().getString(R.string.stepnumber);
	public int level; // set the level of air quality

	/*
	 * default and user-defined form
	 */

	public CircleBar(Context context) {
		super(context);
		init(null, 0);
	}

	public CircleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public CircleBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle) {

		mColorWheelPaint = new Paint();
		mColorWheelPaint.setColor(getResources().getColor(R.color.lightoranger));
		mColorWheelPaint.setStyle(Paint.Style.STROKE);    
		mColorWheelPaint.setStrokeCap(Paint.Cap.ROUND);   
		mColorWheelPaint.setAntiAlias(true);    // 进度条角度

		mColorWheelPaintCentre = new Paint();
		mColorWheelPaintCentre.setColor(getResources().getColor(R.color.snow));
		mColorWheelPaintCentre.setStyle(Paint.Style.STROKE);
		mColorWheelPaintCentre.setStrokeCap(Paint.Cap.ROUND);
		mColorWheelPaintCentre.setAntiAlias(true);

		mDefaultWheelPaint = new Paint();
		mDefaultWheelPaint.setColor(getResources().getColor(R.color.grav));
		mDefaultWheelPaint.setStyle(Paint.Style.STROKE);
		mDefaultWheelPaint.setStrokeCap(Paint.Cap.ROUND);
		mDefaultWheelPaint.setAntiAlias(true);

		mTextP = new Paint();
		mTextP.setAntiAlias(true);

		mTextnum = new Paint();
		mTextnum.setAntiAlias(true);
		mTextnum.setColor(Color.BLACK);

		mTextch = new Paint();
		mTextch.setAntiAlias(true);
		mTextch.setColor(Color.BLACK);

		anim = new BarAnimation();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawArc(mColorWheelRectangle, 0, 359, false, mDefaultWheelPaint);
		canvas.drawArc(mColorWheelRectangle, 0, 359, false,
				mColorWheelPaintCentre);
		canvas.drawArc(mColorWheelRectangle, 90, mSweepAnglePer, false,
				mColorWheelPaint);
		/*
		 * set different model's view
		 */
		if (0 == allModel) {
			completionRate = mPercent + "%";
		} else if (2 == allModel) {
			completionRate = setAirLevel();
		}else if (1 == allModel) {
			completionRate = setAim + "kCal";
		}
		canvas.drawText(
				completionRate,
				mColorWheelRectangle.centerX()
						- (mTextP.measureText(String.valueOf(completionRate)
								) / 2), mPercent_y, mTextP);
		canvas.drawText(stepnumbernow + "", mColorWheelRectangle.centerX()
				- (mTextnum.measureText(String.valueOf(stepnumbernow)) / 2),
				stepnumber_y, mTextnum);

		canvas.drawText(modelTextView, mColorWheelRectangle.centerX()
				- (mTextch.measureText(String.valueOf(modelTextView)) / 2),
				Text_y, mTextch);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = getDefaultSize(getSuggestedMinimumHeight(),
				heightMeasureSpec);
		int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		int min = Math.min(width, height);// get View's length of the shortest
											// edge
		setMeasuredDimension(min, min); // compel the view become a square which
										// the shortest 
		circleStrokeWidth = Textscale(30, min);   
		pressExtraStrokeWidth = Textscale(2, min);
		mColorWheelRectangle.set(circleStrokeWidth + pressExtraStrokeWidth,
				circleStrokeWidth + pressExtraStrokeWidth, min
						- circleStrokeWidth - pressExtraStrokeWidth, min
						- circleStrokeWidth - pressExtraStrokeWidth);
		mTextP.setTextSize(Textscale(80, min));
		mTextnum.setTextSize(Textscale(150, min));
		mTextch.setTextSize(Textscale(50, min));
		mPercent_y = Textscale(190, min);
		stepnumber_y = Textscale(330, min);
		Text_y = Textscale(400, min);
		mColorWheelPaint.setStrokeWidth(circleStrokeWidth);
		mColorWheelPaintCentre.setStrokeWidth(circleStrokeWidth);
		mDefaultWheelPaint
				.setStrokeWidth(circleStrokeWidth - Textscale(2, min));
		mDefaultWheelPaint.setShadowLayer(Textscale(10, min), 0, 0,
				getResources().getColor(R.color.grav));  
	}

	/**
	 * ����������
	 * 
	 * @author Administrator
	 * 
	 */
	public class BarAnimation extends Animation {
		public BarAnimation() {

		}

		/**
		 * ÿ��ϵͳ�����������ʱ�� �ı�mSweepAnglePer��mPercent��stepnumbernow��ֵ��
		 * Ȼ�����postInvalidate()��ͣ�Ļ���view��
		 */
		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			// ֻ�в���ģʽ��Ҫ����ٷֱ�
			if (interpolatedTime < 1.0f && 2 != allModel) {
				mPercent = Float.parseFloat(fnum.format(interpolatedTime
						* stepnumber * 100f / setAim));// ������ֵ�������뱣��һλС��
				mSweepAnglePer = interpolatedTime * stepnumber * 360
						/ setAim;        //���ý�����
				stepnumbernow = (int) (interpolatedTime * stepnumber);
				modelTextView = modelText;
			} else if(2 != allModel){
				mPercent = Float.parseFloat(fnum.format(stepnumber * 100f
						/ setAim));// ������ֵ�������뱣��һλС��
				mSweepAnglePer = stepnumber * 360 / setAim;  //���ý�����
				stepnumbernow = stepnumber;
				modelTextView = modelText;
			} else if(2 == allModel) {
				mSweepAnglePer = 360;    //���������Ľ���������ȫ��
				stepnumbernow = stepnumber;
				modelTextView = modelText;
			}

			postInvalidate();
		}
	}

	/**
	 * ���ݿؼ��Ĵ�С�ı����λ�õı���
	 * 
	 * @param n
	 * @param m
	 * @return
	 */
	public float Textscale(float n, float m) {
		return n / 500 * m;
	}

	/*
	 * 更改显示模式：步行、能量消耗、空气质量 传递不同模式的参数：步行：实时步数+设置的目标步数 能量消耗：实时能量消耗+设置的目标
	 * 空气质量：实时空气质量参数+空气质量
	 */
	public void update(int stepnumber, int time, int model , int aim) {
		this.stepnumber = stepnumber;
		this.allModel = model;
		this.setAim = aim;
		switch (model) {
		case 0:
			this.modelText = getResources().getString(R.string.stepnumber);//"步数";
			setColor(getResources().getColor(R.color.lime));       
			mTextP.setColor(getResources().getColor(R.color.lime));
			break;
		case 1:
			this.modelText  = getResources().getString(R.string.energy);  //能量消耗
			setColor(getResources().getColor(R.color.steelblue));    
			mTextP.setColor(getResources().getColor(R.color.steelblue));
			break;
		case 2:
			this.modelText = getResources().getString(R.string.aqi);  //空气质量指数
			break;
		}
		anim.setDuration(time);
		this.startAnimation(anim);
	}

	/**
	 * 设置进度条颜色
	 * 
	 * @param red
	 * @param green
	 * @param blue
	 */
	public void setColor(int rgb) {
		mColorWheelPaint.setColor(rgb);
	}

	/**
	 * 设置进度条动画时间
	 * 
	 * @param time
	 */
	public void setAnimationTime(int time) {
		anim.setDuration(time * stepnumber / setAim);// 按照比例设置动画执行时间
	}
	
	/*
	 * 设置空气质量级别
	 */
	public String setAirLevel() {
		level = stepnumber / 50;
		String airQuality;
		switch (level) {
		case 0:
			airQuality = getResources().getString(R.string.excellent);  //优
			setColor(getResources().getColor(R.color.lightgreen));
			mTextP.setColor(getResources().getColor(R.color.lightgreen));
			break;
		case 1:
			airQuality = getResources().getString(R.string.good);   //良好
			setColor(getResources().getColor(R.color.yellow));
			mTextP.setColor(getResources().getColor(R.color.yellow));
			break;
		case 2:
			airQuality = getResources().getString(R.string.mild_pollution);  //轻度污染
			setColor(getResources().getColor(R.color.lightoranger));
			mTextP.setColor(getResources().getColor(R.color.lightoranger));
			break;
		case 3:
			airQuality = getResources().getString(R.string.mild_pollution);  //轻度污染
			setColor(getResources().getColor(R.color.lightoranger));
			mTextP.setColor(getResources().getColor(R.color.lightoranger));
			break;
		case 4:
			airQuality = getResources().getString(R.string.moderate_pollution);  //中度污染
			setColor(getResources().getColor(R.color.red));
			mTextP.setColor(getResources().getColor(R.color.red));
			break;
		case 5:
			airQuality = getResources().getString(R.string.moderate_pollution);  //中度污染
			setColor(getResources().getColor(R.color.red));
			mTextP.setColor(getResources().getColor(R.color.red));
			break;
		default:
			airQuality = getResources().getString(R.string.heavily_polluted);  //重度污染
			setColor(getResources().getColor(R.color.purple));
			mTextP.setColor(getResources().getColor(R.color.purple));
		}
		return airQuality;
		
	}

}