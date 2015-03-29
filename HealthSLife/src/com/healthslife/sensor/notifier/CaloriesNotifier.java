
package com.healthslife.sensor.notifier;

import com.healthslife.sensor.data.SensorData;
import com.healthslife.sensor.listener.SpeakingTimer;
import com.healthslife.sensor.listener.StepListener;
import com.healthslife.sensor.listener.SpeakingTimer.Listener;
import com.healthslife.sensor.utilities.CalculateUtil;
import com.healthslife.sensor.utilities.PedometerSettingUtil;
import com.healthslife.sensor.utilities.VoiceUtil;


/**
 * Calculates and displays the approximate calories.  
 * @author Levente Bagi
 */
public class CaloriesNotifier implements StepListener, SpeakingTimer.Listener {

    public interface Listener {
        public void valueChanged(float value);
        public void passValue();
    }
    private Listener mListener;//监听器
    
    private static double METRIC_RUNNING_FACTOR = 1.02784823;//公制单位跑步的因子
    private static double IMPERIAL_RUNNING_FACTOR = 0.75031498;//英制单位跑步的因子

    private static double METRIC_WALKING_FACTOR = 0.708;//公制单位步行的因子
    private static double IMPERIAL_WALKING_FACTOR = 0.517;//英制单位步行的因子

    private double mCalories = 0;//消耗能量
    
    PedometerSettingUtil mSettings;//参数设置
    VoiceUtil mUtils;//管理语音
    
    boolean mIsMetric;//判断是否为公制单位
    boolean mIsRunning;//判断是否在跑步
    float mStepLength;//步长
    float mBodyWeight;//体重

    public CaloriesNotifier(Listener listener, PedometerSettingUtil settings, VoiceUtil utils) {
        mListener = listener;
        mUtils = utils;
        mSettings = settings;
        reloadSettings();
    }
    public void setCalories(float calories) {
        mCalories = calories;
        notifyListener();
    }
    
    
    //#######################################################################
    /*重新载入之前的数据*/
    public void reloadSettings() {
        mIsMetric = SensorData.isMetric;//mSettings.isMetric();
        mIsRunning = SensorData.isRunning_sport();//mSettings.isRunning();
        CalculateUtil.ComputStepLengh();//计算步长!!!!!!!!!!!!!!!!!!!!!!!!!!!
    	mStepLength=SensorData.StepLength;
//        mStepLength = SensorData.get;//mSettings.getStepLength();
        mBodyWeight = SensorData.getWeight();//获取体重
        notifyListener();
    }
    public void resetValues() {
        mCalories = 0;
    }
    
    public void isMetric(boolean isMetric) {
        mIsMetric = isMetric;
    }
    public void setStepLength(float stepLength) {
        mStepLength = stepLength;
    }
    
    public void onStep() {
        
    	CalculateUtil.ComputStepLengh();//计算步长!!!!!!!!!!!!!!!!!!!!!!!!!!!
    	mStepLength=SensorData.StepLength;
    	mIsMetric=SensorData.isMetric;
    	mBodyWeight=SensorData.getWeight();
    	mIsRunning = SensorData.isRunning_sport();//获取是否在跑步的状态!!!!!
    	
        if (mIsMetric) {
            mCalories += 
                (mBodyWeight * (mIsRunning ? METRIC_RUNNING_FACTOR : METRIC_WALKING_FACTOR))
                // Distance:
                * mStepLength // centimeters
                / 100000.0; // centimeters/kilometer
        }
        else {
            mCalories += 
                (mBodyWeight * (mIsRunning ? IMPERIAL_RUNNING_FACTOR : IMPERIAL_WALKING_FACTOR))
                // Distance:
                * mStepLength // inches
                / 63360.0; // inches/mile            
        }
        
        notifyListener();
    }
    
    private void notifyListener() {
        mListener.valueChanged((float)mCalories);
    }
    
    public void passValue() {
        
    }
	@Override
	public void speak() {
		// TODO Auto-generated method stub
		
	}
    
  //###############################################################
    /*读“能量消耗”，单位"千卡路里"*/
    /*public void speak() {
        if (mSettings.shouldTellCalories()) {
            if (mCalories > 0) {
                mUtils.say("" + (int)mCalories + "千卡路里");//" calories burned"
            }
        }
        
    }*/
    

}

