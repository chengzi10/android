package com.healthslife.health;

import com.healthslife.allinterface.CircleBar;
import com.healthslife.allinterface.SlidingMenu;

public class HealthGlobalVariable {

	protected static CircleBar circleBar;  //计步器界面
	protected static int model = 0;       // 模式标识 
	protected static int newAqi = 0;      // 空气质量指数AQI
	protected static String url;           //获取AQI时使用的URL
	protected static SlidingMenu mLeftMenu; //左划界面
	public static int value[] = { 0, 0, 0 }; // 数据收集模块传递的参数
}
