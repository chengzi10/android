package com.healthslife.system;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserMessage_system{
	public static boolean SEX = true;//性别
	public static float HEIGHT = 45.0f;//身高
	public static float WEIGHT = 35.0f;//体重
	public static int TARGET_STEPS = 7000;//目标步数
	public static int TARGET_CALORIES = 200;//目标卡路里
	public static int SEQUENCE = 10;//播报频率
	
	public static void setParameters(Context context){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		SEX = sp.getBoolean("SEX", true);
		HEIGHT = sp.getFloat("HEIGHT", 45.0f);
		WEIGHT = sp.getFloat("WEIGHT", 35.0f);
		TARGET_STEPS = sp.getInt("TARGET_STEPS", 7000);
		TARGET_CALORIES = sp.getInt("TARGET_CALORIES", 200);
		SEQUENCE = sp.getInt("SEQUENCE", 10);
	}
	
	public static void setHeight(Context context,float height){
		
		SharedPreferences Preferences =
				PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor;
		editor = Preferences.edit();
        editor.putFloat("HEIGHT", height);
        editor.commit();
	}
	
	public static void setSteps(Context context,int steps){
		
		SharedPreferences Preferences =
				PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor;
		editor = Preferences.edit();
        editor.putInt("TARGET_STEPS", steps);
        editor.commit();
	}
	
	public static void setSequence(Context context,int sequence){
		
		SharedPreferences Preferences =
				PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor;
		editor = Preferences.edit();
        editor.putInt("SEQUENCE", sequence);
        editor.commit();
	}
	
	public static void setCalories(Context context,int calories){
		
		SharedPreferences Preferences =
				PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor;
		editor = Preferences.edit();
        editor.putInt("TARGET_CALORIES", calories);
        editor.commit();
	}
	
	public static void setWeight(Context context,float weight){
		
		SharedPreferences Preferences =
				PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor;
		editor = Preferences.edit();
        editor.putFloat("WEIGHT", weight);
        editor.commit();
	}
	
	public static void setSex(Context context,boolean sex){
		
		SharedPreferences Preferences =
				PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor;
		editor = Preferences.edit();
        editor.putBoolean("SEX", sex);
        editor.commit();
	}

}
