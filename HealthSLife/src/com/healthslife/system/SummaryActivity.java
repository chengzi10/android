package com.healthslife.system;

import com.healthslife.R;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SummaryActivity extends Activity {

	private RelativeLayout rlLayout;
	private ImageView contentImageView;
	private TextView text1;
	private TextView text2;
	private ImageView pointView1;
	private ImageView pointView2;
	private ImageView pointView3;
	private ImageView pointView4;
	private ImageView pointView5;
	private Button feelnowButton;
	private int count = 1;
	final int RIGHT = 0;  
    final int LEFT = 1;
	private GestureDetector gestureDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 竖屏幕
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.summary_activity);
		rlLayout = (RelativeLayout)findViewById(R.id.summary_parent);
		contentImageView = (ImageView)findViewById(R.id.content_imageview);
		text1 = (TextView)findViewById(R.id.unchangetext);
		text2 = (TextView)findViewById(R.id.changetext);
		pointView1 = (ImageView)findViewById(R.id.colorchangepoint1);
		pointView2 = (ImageView)findViewById(R.id.colorchangepoint2);
		pointView3 = (ImageView)findViewById(R.id.colorchangepoint3);
		pointView4 = (ImageView)findViewById(R.id.colorchangepoint4);
		pointView5 = (ImageView)findViewById(R.id.colorchangepoint5);
		feelnowButton = (Button)findViewById(R.id.feelnow_button);
		gestureDetector = new GestureDetector(SummaryActivity.this,onGestureListener); 
		
		
		contentImageView.setImageResource(R.drawable.content_image1);
		text1.setText("智能音乐");
		text2.setText("根据运动速度播放相应节奏音乐");
		pointView1.setImageResource(R.drawable.point_orange);
		pointView2.setImageResource(R.drawable.point_gray);
		pointView3.setImageResource(R.drawable.point_gray);
		pointView4.setImageResource(R.drawable.point_gray);
		pointView5.setImageResource(R.drawable.point_gray);
		
		feelnowButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SummaryActivity.this.finish();
			}
		});
		
	}
	 private GestureDetector.OnGestureListener onGestureListener =   
		        new GestureDetector.SimpleOnGestureListener() {  
		        @Override  
		        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,  
		                float velocityY) {  
		            float x = e2.getX() - e1.getX();  
		            float y = e2.getY() - e1.getY();  
		  
		            if (x > 0) {  
		                doResult(RIGHT);  
		            } else if (x < 0) {  
		                doResult(LEFT);  
		            }  
		            return true;  
		        }  
		    };  
		  
		    public boolean onTouchEvent(MotionEvent event) {  
		        return gestureDetector.onTouchEvent(event);  
		    }  
		  
		    public void doResult(int action) {  
		        switch (action) {  
		        case RIGHT:  
		        	count--;
			        if(count <= 1){
			        	count = 1;
			        }
		            break;  
		        case LEFT:  
		        	count++;
		        	if(5 <= count){
						count = 5;
					}
		            break;  
		        }  
		        
		        invalidateThisActivity();
		    }  
		    
		    private void invalidateThisActivity(){
		    	if(1 == count){
					contentImageView.setImageResource(R.drawable.content_image1);
					text1.setText("智能音乐");
					text2.setText("根据运动速度播放相应节奏音乐");
					pointView1.setImageResource(R.drawable.point_orange);
					pointView2.setImageResource(R.drawable.point_gray);
					pointView3.setImageResource(R.drawable.point_gray);
					pointView4.setImageResource(R.drawable.point_gray);
					pointView5.setImageResource(R.drawable.point_gray);
					feelnowButton.setVisibility(View.INVISIBLE);
				}
				if(2 == count){
					contentImageView.setImageResource(R.drawable.content_image2);
					text1.setText("智能记录");
					text2.setText("您的能量消耗");
					pointView1.setImageResource(R.drawable.point_gray);
					pointView2.setImageResource(R.drawable.point_orange);
					pointView3.setImageResource(R.drawable.point_gray);
					pointView4.setImageResource(R.drawable.point_gray);
					pointView5.setImageResource(R.drawable.point_gray);
					feelnowButton.setVisibility(View.INVISIBLE);
				}
				if(3 == count){
					contentImageView.setImageResource(R.drawable.content_image3);
					text1.setText("智能识别");
					text2.setText("您的运动状态");
					pointView1.setImageResource(R.drawable.point_gray);
					pointView2.setImageResource(R.drawable.point_gray);
					pointView3.setImageResource(R.drawable.point_orange);
					pointView4.setImageResource(R.drawable.point_gray);
					pointView5.setImageResource(R.drawable.point_gray);
					feelnowButton.setVisibility(View.INVISIBLE);
				}
				if(4 == count){
					contentImageView.setImageResource(R.drawable.content_image4);
					text1.setText("心率检测");
					text2.setText("时刻关注您的健康");
					pointView1.setImageResource(R.drawable.point_gray);
					pointView2.setImageResource(R.drawable.point_gray);
					pointView3.setImageResource(R.drawable.point_gray);
					pointView4.setImageResource(R.drawable.point_orange);
					pointView5.setImageResource(R.drawable.point_gray);
					feelnowButton.setVisibility(View.INVISIBLE);
				}
				if(5 == count){
					contentImageView.setImageResource(R.drawable.content_image5);
					text1.setText("智能记录");
					text2.setText("记录生活轨迹");
					pointView1.setImageResource(R.drawable.point_gray);
					pointView2.setImageResource(R.drawable.point_gray);
					pointView3.setImageResource(R.drawable.point_gray);
					pointView4.setImageResource(R.drawable.point_gray);
					pointView5.setImageResource(R.drawable.point_orange);
					feelnowButton.setVisibility(View.VISIBLE);
				}
				rlLayout.invalidate();
		    }
}
