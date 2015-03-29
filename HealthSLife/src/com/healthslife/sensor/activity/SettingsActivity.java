package com.healthslife.sensor.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.healthslife.R;

/**
 * Activity for Pedometer settings.
 * Started when the user click Settings from the main menu.
 * @author Levente Bagi
 * 
 * 设置主菜单
 */
public class SettingsActivity extends PreferenceActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.sensor_preferences);
    }
}
