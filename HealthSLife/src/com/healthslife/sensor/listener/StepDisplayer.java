/*
 *  Pedometer - Android App
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

package com.healthslife.sensor.listener;

import java.util.ArrayList;

import com.healthslife.sensor.utilities.PedometerSettingUtil;
import com.healthslife.sensor.utilities.VoiceUtil;

/**
 * Counts steps provided by StepDetector and passes the current
 * step count to the activity.
 * 
 * 通过StepDetector计算步数，并把当前的步数传送到Activity中。
 * ####读“步数”
 * 
 */
public class StepDisplayer implements StepListener, SpeakingTimer.Listener {

    private int mCount = 0;
    PedometerSettingUtil mSettings;
    VoiceUtil mUtils;

    public StepDisplayer(PedometerSettingUtil settings, VoiceUtil utils) {
        mUtils = utils;
        mSettings = settings;
        notifyListener();
    }
    public void setUtils(VoiceUtil utils) {
        mUtils = utils;
    }

    public void setSteps(int steps) {
        mCount = steps;
        notifyListener();
    }
    public void onStep() {
        mCount ++;
        notifyListener();
    }
    public void reloadSettings() {
        notifyListener();
    }
    public void passValue() {
    }
    
    

    //-----------------------------------------------------
    // Listener
    
    public interface Listener {
        public void stepsChanged(int value);
        public void passValue();
    }
    private ArrayList<Listener> mListeners = new ArrayList<Listener>();

    public void addListener(Listener l) {
        mListeners.add(l);
    }
    public void notifyListener() {
        for (Listener listener : mListeners) {
            listener.stepsChanged((int)mCount);
        }
    }
    
    //-----------------------------------------------------
    // Speaking
    
    //###############################################################
    /*读“步数”，单位"步"*/
    public void speak() {
        /*if (mSettings.shouldTellSteps()) { 
            if (mCount > 0) {
                mUtils.say("" + mCount + "步");// steps
            }
        }
        */
    }
    
    
}
