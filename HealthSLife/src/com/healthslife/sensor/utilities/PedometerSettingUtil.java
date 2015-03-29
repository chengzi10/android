package com.healthslife.sensor.utilities;

import android.content.SharedPreferences;

/**
 * Wrapper for {@link SharedPreferences}, handles preferences-related tasks.
 * 
 * @author Levente Bagi
 * 
 *         计步器参数设置！！！ 从XML文件中获取数据!!!!!
 */
public class PedometerSettingUtil {

	SharedPreferences mSettings;

	public static int M_NONE = 1;
	public static int M_PACE = 2;
	public static int M_SPEED = 3;

	public PedometerSettingUtil(SharedPreferences settings) {
		mSettings = settings;
	}

	/* 获取单位标准 */
	/*
	 * public boolean isMetric() { return mSettings.getString("units",
	 * "imperial").equals("metric"); }
	 */

	/* 获取步长 */
	/*
	 * public float getStepLength() { try { return
	 * Float.valueOf(mSettings.getString("step_length", "60").trim());//默认值为60 }
	 * catch (NumberFormatException e) { // TODO: reset value, & notify user
	 * somehow return 0f; } }
	 */

	/* 获取体重 */
	/*
	 * public float getBodyWeight() { try { return
	 * Float.valueOf(mSettings.getString("body_weight", "50").trim()); } catch
	 * (NumberFormatException e) { // TODO: reset value, & notify user somehow
	 * return 0f; } }
	 */

	/* 获取运动状态 */
	/*
	 * public boolean isRunning() { return mSettings.getString("exercise_type",
	 * "running").equals("running"); }
	 */

	/* 设置参照——无用 */
	/*
	 * public int getMaintainOption() { String p =
	 * mSettings.getString("maintain", "none"); return p.equals("none") ? M_NONE
	 * : ( p.equals("pace") ? M_PACE : ( p.equals("speed") ? M_SPEED : ( 0))); }
	 */

	// -------------------------------------------------------------------
	// Desired pace & speed:
	// these can not be set in the preference activity, only on the main
	// screen if "maintain" is set to "pace" or "speed"

	/* 获取参照：期望步频（目前无用） */
	/*
	 * public int getDesiredPace() { return mSettings.getInt("desired_pace",
	 * 180); // steps/minute }
	 */
	/* 获取参照：期望速度（目前无用） */
	/*
	 * public float getDesiredSpeed() { return
	 * mSettings.getFloat("desired_speed", 4f); // km/h or mph }
	 */
	/* 保存参照数值：期望步频或速度（目前无用） */
	/*
	 * public void savePaceOrSpeedSetting(int maintain, float
	 * desiredPaceOrSpeed) { SharedPreferences.Editor editor =
	 * mSettings.edit();//获取Editor对象，对存储XML文件进行操作！ if (maintain == M_PACE) {
	 * editor.putInt("desired_pace", (int)desiredPaceOrSpeed); } else if
	 * (maintain == M_SPEED) { editor.putFloat("desired_speed",
	 * desiredPaceOrSpeed); } editor.commit(); }
	 */

	// -------------------------------------------------------------------
	// Speaking:

	/* 获取是否可以播报 */
	public boolean shouldSpeak() {
		return mSettings.getBoolean("speak", false);
	}

	/* 获取播报时间间隔（无用） */
	public float getSpeakingInterval() {
		try {
			return Float.valueOf(mSettings.getString("speaking_interval", "1"));
		} catch (NumberFormatException e) {
			// This could not happen as the value is selected from a list.
			return 1;
		}
	}

	/* 获取：对步数的播报权限（无用） */
	/*
	 * public boolean shouldTellSteps() { return mSettings.getBoolean("speak",
	 * false) && mSettings.getBoolean("tell_steps", false); }
	 */
	/* 获取：对步频的播报权限（无用） */
	/*
	 * public boolean shouldTellPace() { return mSettings.getBoolean("speak",
	 * false) && mSettings.getBoolean("tell_pace", false); }
	 */
	/* 获取：对距离的播报权限（无用） */
	/*
	 * public boolean shouldTellDistance() { return
	 * mSettings.getBoolean("speak", false) &&
	 * mSettings.getBoolean("tell_distance", false); }
	 */
	/* 获取：对速度的播报权限（无用） */
	/*
	 * public boolean shouldTellSpeed() { return mSettings.getBoolean("speak",
	 * false) && mSettings.getBoolean("tell_speed", false); }
	 */
	/* 获取：对能量消耗的播报权限（无用） */
	/*
	 * public boolean shouldTellCalories() { return
	 * mSettings.getBoolean("speak", false) &&
	 * mSettings.getBoolean("tell_calories", false); }
	 */
	/* 获取：对“快慢判断”的播报权限（无用） */
	/*
	 * public boolean shouldTellFasterslower() { return
	 * mSettings.getBoolean("speak", false) &&
	 * mSettings.getBoolean("tell_fasterslower", false); }
	 */

	/* 获取：对“屏幕唤醒的判断”的播报权限（无用） */
	public boolean wakeAggressively() {
		return mSettings.getString("operation_level", "run_in_background")
				.equals("wake_up");
	}

	/* 获取：对“屏幕常亮的判断”的播报权限（无用） */
	public boolean keepScreenOn() {
		return mSettings.getString("operation_level", "run_in_background")
				.equals("keep_screen_on");
	}

	//
	// Internal

	/* 保存service运行，带时间戳（无用） */
	public void saveServiceRunningWithTimestamp(boolean running) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean("service_running", running);
		editor.putLong("last_seen", VoiceUtil.currentTimeInMillis());
		editor.commit();
	}

	/* 保存service运行，无时间戳（无用） */
	public void saveServiceRunningWithNullTimestamp(boolean running) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean("service_running", running);
		editor.putLong("last_seen", 0);
		editor.commit();
	}

	/* 清理service运行（无用） */
	public void clearServiceRunning() {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean("service_running", false);
		editor.putLong("last_seen", 0);
		editor.commit();
	}

	/* 获取service运行状态 */
	public boolean isServiceRunning() {
		return mSettings.getBoolean("service_running", false);
	}

	/* 获取是否是一个新的开始（ 无用） */
	public boolean isNewStart() {
		// activity last paused more than 10 minutes ago
		return mSettings.getLong("last_seen", 0) < VoiceUtil
				.currentTimeInMillis() - 1000 * 60 * 10;
	}

}
