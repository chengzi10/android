package com.healthslife.sensor.notifier;

import com.healthslife.sensor.data.SensorData;
import com.healthslife.sensor.utilities.CalculateUtil;
import com.healthslife.sensor.utilities.PedometerSettingUtil;
import com.healthslife.sensor.utilities.VoiceUtil;
import com.healthslife.sensor.listener.SpeakingTimer;

/**
 * Calculates and displays pace (steps / minute), handles input of desired pace,
 * notifies user if he/she has to go faster or slower.
 * 
 * Uses {@link PaceNotifier}, calculates speed as product of pace and step length.
 * 
 * @author Levente Bagi
 */
public class SpeedNotifier implements PaceNotifier.Listener, SpeakingTimer.Listener {

    public interface Listener {
        public void valueChanged(float value);
        public void passValue();
    }
    private Listener mListener;//监听器
    
    int mCounter = 0;
    float mSpeed = 0;//速度
    
    boolean mIsMetric;//判断是否为公制单位
    float mStepLength;//步长

    PedometerSettingUtil mSettings;//计步器参数设置
    VoiceUtil mUtils;//语音管理

    /** Desired speed, adjusted by the user */
    float mDesiredSpeed;//预期速度
    
    /** Should we speak? */
    boolean mShouldTellFasterslower;//判断运动快慢
    boolean mShouldTellSpeed;//判断是否播报速度
    
    /** When did the TTS speak last time */
    private long mSpokenAt = 0;//上次播报语音的时间
    
    public SpeedNotifier(Listener listener, PedometerSettingUtil settings, VoiceUtil utils) {
        mListener = listener;
        mUtils = utils;
        mSettings = settings;
//        mDesiredSpeed = mSettings.getDesiredSpeed();
        reloadSettings();
    }
    public void setSpeed(float speed) {
        mSpeed = speed;
        notifyListener();
    }
    
    
    //##################################################
    /*从文件传入数值，换成从数据库传入数值*/
    public void reloadSettings() {
        mIsMetric = SensorData.isMetric;//mSettings.isMetric();
        CalculateUtil.ComputStepLengh();//计算步长!!!!!!!!!!!!!!!!!!!!!!!!!!!
    	mStepLength=SensorData.StepLength;
//        mStepLength = mSettings.getStepLength();
//        mShouldTellSpeed = mSettings.shouldTellSpeed();
/*        mShouldTellFasterslower = 
            mSettings.shouldTellFasterslower()
            && mSettings.getMaintainOption() == PedometerSettingUtil.M_SPEED;
        */
        notifyListener();
    }
    public void setDesiredSpeed(float desiredSpeed) {
        mDesiredSpeed = desiredSpeed;
    }
    
    private void notifyListener() {
        mListener.valueChanged(mSpeed);
    }
    
    /*计算运动速度*/
    public void paceChanged(int value) {
    	CalculateUtil.ComputStepLengh();//计算步长!!!!!!!!!!!!!!!!!!!!!!!!!!!
    	mStepLength=SensorData.StepLength;
    	mIsMetric=SensorData.isMetric;//最初默认值为公制单位，先不考虑英制单位!!!!!!!!!!!!!!!!
    	
        if (mIsMetric) {
            mSpeed = // kilometers / hour
                value * mStepLength // centimeters / minute
                / 100000f * 60f; // centimeters/kilometer
        }
        else {
            mSpeed = // miles / hour
                value * mStepLength // inches / minute
                / 63360f * 60f; // inches/mile 
        }
//        tellFasterSlower();
        notifyListener();
    }
    
    /**
     * Say slower/faster, if needed.
     */
    /*private void tellFasterSlower() {
        if (mShouldTellFasterslower && mUtils.isSpeakingEnabled()) {
            long now = System.currentTimeMillis();
            if (now - mSpokenAt > 3000 && !mUtils.isSpeakingNow()) {
                float little = 0.10f;
                float normal = 0.30f;
                float much = 0.50f;
                
                boolean spoken = true;
                if (mSpeed < mDesiredSpeed * (1 - much)) {
                    mUtils.say("much faster!");
                }
                else
                if (mSpeed > mDesiredSpeed * (1 + much)) {
                    mUtils.say("much slower!");
                }
                else
                if (mSpeed < mDesiredSpeed * (1 - normal)) {
                    mUtils.say("faster!");
                }
                else
                if (mSpeed > mDesiredSpeed * (1 + normal)) {
                    mUtils.say("slower!");
                }
                else
                if (mSpeed < mDesiredSpeed * (1 - little)) {
                    mUtils.say("a little faster!");
                }
                else
                if (mSpeed > mDesiredSpeed * (1 + little)) {
                    mUtils.say("a little slower!");
                }
                else {
                    spoken = false;
                }
                if (spoken) {
                    mSpokenAt = now;
                }
            }
        }
    }*/
    
    public void passValue() {
        // Not used
    }

    
    //###############################################################
    /*读“速度”，单位"公里/小时"*/
    public void speak() {
        /*if (mSettings.shouldTellSpeed()) {
            if (mSpeed >= .01f) {
                mUtils.say(("" + (mSpeed + 0.000001f)).substring(0, 4) + (mIsMetric ? "公里/小时" : "英里/小时"));
                // kilometers per hour, miles per hour
            }
        }
        */
    }

}

