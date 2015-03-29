package com.healthslife.sensor.listener;

import java.util.ArrayList;

import com.healthslife.sensor.utilities.PedometerSettingUtil;
import com.healthslife.sensor.utilities.VoiceUtil;

/**
 * Call all listening objects repeatedly. 
 * The interval is defined by the user settings.
 * @author Levente Bagi
 * 
 * 1、重复调用所有监听对象；
 * 2、设置语音播报时间间隔。
 * 
 */
public class SpeakingTimer implements StepListener {

    PedometerSettingUtil mSettings;
    VoiceUtil mUtils;
    boolean mShouldSpeak;
    float mInterval;
    long mLastSpeakTime;
    
    public SpeakingTimer(PedometerSettingUtil settings, VoiceUtil utils) {
        mLastSpeakTime = System.currentTimeMillis();
        mSettings = settings;
        mUtils = utils;
        reloadSettings();
    }
    public void reloadSettings() {
        mShouldSpeak = mSettings.shouldSpeak();
        mInterval = mSettings.getSpeakingInterval();
    }
    
    public void onStep() {
        long now = System.currentTimeMillis();
        long delta = now - mLastSpeakTime;
        
        if (delta / 60000.0 >= mInterval) {
            mLastSpeakTime = now;
            notifyListeners();
        }
    }
    
    public void passValue() {
        // not used
    }

    
    //-----------------------------------------------------
    // Listener
    
    public interface Listener {
        public void speak();
    }
    private ArrayList<Listener> mListeners = new ArrayList<Listener>();

    public void addListener(Listener l) {
        mListeners.add(l);
    }
    public void notifyListeners() {
        mUtils.ding();
        for (Listener listener : mListeners) {
            listener.speak();
        }
    }

    //-----------------------------------------------------
    // Speaking
    
    public boolean isSpeaking() {
        return mUtils.isSpeakingNow();
    }
}

