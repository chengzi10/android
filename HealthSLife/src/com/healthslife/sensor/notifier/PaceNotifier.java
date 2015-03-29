package com.healthslife.sensor.notifier;

import java.util.ArrayList;

import com.healthslife.sensor.listener.StepListener;
import com.healthslife.sensor.utilities.PedometerSettingUtil;
import com.healthslife.sensor.utilities.VoiceUtil;
import com.healthslife.sensor.listener.SpeakingTimer;
/**
 * Calculates and displays pace (steps / minute), handles input of desired pace,
 * notifies user if he/she has to go faster or slower.  
 * @author Levente Bagi
 */
public class PaceNotifier implements StepListener, SpeakingTimer.Listener {

    public interface Listener {
        public void paceChanged(int value);
        public void passValue();
    }
    private ArrayList<Listener> mListeners = new ArrayList<Listener>();//监听器
    
    int mCounter = 0;//
    
    private long mLastStepTime = 0;//上一次运动时间
    private long[] mLastStepDeltas = {-1, -1, -1, -1};//存储最近四次的运动时间
    private int mLastStepDeltasIndex = 0;//标记上一次运动时间在数组对应的下标
    private long mPace = 0;//步频
    
    PedometerSettingUtil mSettings;//计步器参数设置
    VoiceUtil mUtils;//语音管理

    /** Desired pace, adjusted by the user */
    int mDesiredPace;//预计步频

    /** Should we speak? */
    boolean mShouldTellFasterslower;//判断当前运动快慢

    /** When did the TTS speak last time */
    private long mSpokenAt = 0;//上次播报语音的时间

    public PaceNotifier(PedometerSettingUtil settings, VoiceUtil utils) {
        mUtils = utils;
        mSettings = settings;
//        mDesiredPace = mSettings.getDesiredPace();
        reloadSettings();
    }
    
    //##############################################################
    /*传入步数*/
    public void setPace(int pace) {
        mPace = pace;
        int avg = (int)(60*1000.0 / mPace);
        for (int i = 0; i < mLastStepDeltas.length; i++) {
            mLastStepDeltas[i] = avg;
        }
        notifyListener();
    }
    public void reloadSettings() {
        /*mShouldTellFasterslower = 
            mSettings.shouldTellFasterslower()
            && mSettings.getMaintainOption() == PedometerSettingUtil.M_PACE;
        */
        notifyListener();
    }
    
    public void addListener(Listener l) {
        mListeners.add(l);
    }

    public void setDesiredPace(int desiredPace) {
        mDesiredPace = desiredPace;
    }

    /*计算运动步频*/
    public void onStep() {
        long thisStepTime = System.currentTimeMillis();
        mCounter ++;
        
        // CalculateUtil pace based on last x steps
        if (mLastStepTime > 0) {
            long delta = thisStepTime - mLastStepTime;
            
            mLastStepDeltas[mLastStepDeltasIndex] = delta;
            mLastStepDeltasIndex = (mLastStepDeltasIndex + 1) % mLastStepDeltas.length;
            
            long sum = 0;
            boolean isMeaningfull = true;
            for (int i = 0; i < mLastStepDeltas.length; i++) {
                if (mLastStepDeltas[i] < 0) {
                    isMeaningfull = false;
                    break;
                }
                sum += mLastStepDeltas[i];
            }
            if (isMeaningfull && sum > 0) {
                long avg = sum / mLastStepDeltas.length;
                mPace = 60*1000 / avg;
                
                // TODO: remove duplication. This also exists in SpeedNotifier
                /*if (mShouldTellFasterslower && !mUtils.isSpeakingEnabled()) {
                    if (thisStepTime - mSpokenAt > 3000 && !mUtils.isSpeakingNow()) {
                        float little = 0.10f;
                        float normal = 0.30f;
                        float much = 0.50f;
                        
                        boolean spoken = true;
                        if (mPace < mDesiredPace * (1 - much)) {
                            mUtils.say("再快一些！");//much faster!
                        }
                        else
                        if (mPace > mDesiredPace * (1 + much)) {
                            mUtils.say("再慢一些！");//much slower!
                        }
                        else
                        if (mPace < mDesiredPace * (1 - normal)) {
                            mUtils.say("快一点儿！");//faster!
                        }
                        else
                        if (mPace > mDesiredPace * (1 + normal)) {
                            mUtils.say("慢一点儿！");//slower!
                        }
                        else
                        if (mPace < mDesiredPace * (1 - little)) {
                            mUtils.say("稍微快一点儿！");//a little faster!
                        }
                        else
                        if (mPace > mDesiredPace * (1 + little)) {
                            mUtils.say("稍微慢一点儿！");//a little slower!
                        }
                        else {
                            spoken = false;
                        }
                        if (spoken) {
                            mSpokenAt = thisStepTime;
                        }
                    }
                }*/
            }
            else {
                mPace = -1;
            }
        }
        mLastStepTime = thisStepTime;
        notifyListener();
    }
    
    private void notifyListener() {
        for (Listener listener : mListeners) {
            listener.paceChanged((int)mPace);
        }
    }
    
    public void passValue() {
        // Not used
    }

    //-----------------------------------------------------
    // Speaking
    
    //###############################################################
    /*读“步频”，单位"步/分钟"*/
    public void speak() {
    	//Context con;
        /*if (mSettings.shouldTellPace()) {
            if (mPace > 0) {
                mUtils.say(mPace + "步/分钟");//"步/分钟"// steps per minute
            }
        }
        */
    }
    

}

