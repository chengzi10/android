/*
 *  PedometerActivity - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.healthslife.sensor.notifier;

import com.healthslife.sensor.data.SensorData;
import com.healthslife.sensor.listener.StepListener;
import com.healthslife.sensor.utilities.CalculateUtil;
import com.healthslife.sensor.utilities.PedometerSettingUtil;
import com.healthslife.sensor.utilities.VoiceUtil;
import com.healthslife.sensor.listener.SpeakingTimer;

/**
 * Calculates and displays the distance walked.  
 * @author Levente Bagi
 */
public class DistanceNotifier implements StepListener, SpeakingTimer.Listener {

    public interface Listener {
        public void valueChanged(float value);
        public void passValue();
    }
    private Listener mListener;//监听器
    
    float mDistance = 0;//运动距离
    
    PedometerSettingUtil mSettings;//参数设置
    VoiceUtil mUtils;//管理语音
    
    boolean mIsMetric;//判断是否为公制单位
    float mStepLength;//步长

    public DistanceNotifier(Listener listener, PedometerSettingUtil settings, VoiceUtil utils) {
        mListener = listener;
        mUtils = utils;
        mSettings = settings;
        reloadSettings();
    }
    public void setDistance(float distance) {
        mDistance = distance;
        notifyListener();
    }
    
    //###################################
    /*重新载入数值*/
    public void reloadSettings() {
        mIsMetric = SensorData.isMetric;//mSettings.isMetric();
        CalculateUtil.ComputStepLengh();//计算步长!!!!!!!!!!!!!!!!!!!!!!!!!!!
    	mStepLength=SensorData.StepLength;
//        mStepLength = SensorData.StepLength;//mSettings.getStepLength();
        notifyListener();
    }
    
    /*计算运动距离*/
    public void onStep() {
    	CalculateUtil.ComputStepLengh();//计算步长!!!!!!!!!!!!!!!!!!!!!!!!!!!
    	mStepLength=SensorData.StepLength;
    	mIsMetric=SensorData.isMetric;
    	
        if (mIsMetric) {
            mDistance += (float)(// kilometers
                mStepLength // centimeters
                / 100000.0); // centimeters/kilometer
        }
        else {
            mDistance += (float)(// miles
                mStepLength // inches
                / 63360.0); // inches/mile
        }
        
        notifyListener();
    }
    
    private void notifyListener() {
        mListener.valueChanged(mDistance);
    }
    
    public void passValue() {
        // Callback of StepListener - Not implemented
    }

    
    //###############################################################
    /*读“运动距离”，单位"公里"*/
    public void speak() {
       /* if (mSettings.shouldTellDistance()) {
            if (mDistance >= .001f) {
                mUtils.say(("" + (mDistance + 0.000001f)).substring(0, 4) + (mIsMetric ? " 公里" : "英里"));//kilometers,miles
                // TODO: format numbers (no "." at the end)
            }
        }
        */
    }
    

}

