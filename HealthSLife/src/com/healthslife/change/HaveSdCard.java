package com.healthslife.change;

import android.os.Environment;
/**
 * 检查是否存在SDCard
 * @author gyh
 *
 */
public class HaveSdCard {
	/**
	 * 
	 * @return
	 * true表示 有sdcard false表示没有sdcard
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
}
