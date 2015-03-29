package com.healthslife.system;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

public class MusicMessage_system {

	public static int[] level = { 2 };// 存放音乐节奏等级的数组
	public static int[] id = { 0 };// 存放音乐id的数组
	public static String[] titles = { "none" };// 存放音乐文件的标题数组
	public static String[] duration = { "00:00" };// 存放音乐文件的时间数组
	public static String[] artists = { "none" };// 存放音乐文件的歌手标题数组
	public static String[] path = {};// 存放音乐路径的数组
	public static int count;// 音乐文件数量

	// 设置数值长度
	public static void setCount(int c) {
		level = new int[c];
		id = new int[c];
		titles = new String[c];
		duration = new String[c];
		artists = new String[c];
		path = new String[c];
	}

	// 给每个数字设值,使用前要先传入Context对象
	// 给每个数字设值,使用前要先传入Context对象
	public static void setValues(Context context) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		// 在ContentProvider中读取 外部储存器 的音乐信息
		Cursor externalCursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				// 这个字符串数组表示要查询的列
				new String[] { MediaStore.Video.Media.TITLE, // 名
						MediaStore.Audio.Media.DURATION, // 的时间
						MediaStore.Audio.Media.ARTIST, // 艺术家
						MediaStore.Audio.Media._ID, // id号
						MediaStore.Audio.Media.DATA // 音乐文件的路径
				}, null,// 查询条件，相当于sql中的where语句
				null,// 查询条件中使用到的数据
				null);// 查询结果的排序方式

		count = externalCursor.getCount();// 外部储存器中音乐文件的数量
		setCount(count);

		externalCursor.moveToFirst();
		for (int i = 0; i < count; i++) {
			id[i] = externalCursor.getInt(3);
			titles[i] = externalCursor.getString(0);
			duration[i] = toTimeForm(externalCursor.getInt(1));
			path[i] = externalCursor.getString(4);
			artists[i] = (externalCursor.getString(2).equals("<unknown>") ? "佚名"
					: externalCursor.getString(2));
			externalCursor.moveToNext();
		}

		// 在SharedPreferences中读取音乐id对应的level
		for (int i = 0; i < count; i++) {
			level[i] = sp.getInt(id[i] + "", 2);
		}
	}

	// 修改时间格式
	public static String toTimeForm(int time) {
		String stringTime;
		if ((time / 1000) % 60 >= 10) {
			stringTime = (time / 1000) / 60 + ":" + (time / 1000) % 60;
		} else {
			stringTime = (time / 1000) / 60 + ":0" + (time / 1000) % 60;
		}
		return stringTime;
	}

	// 调用此方法在名为musicMessagePreferences的Preferences文件中写入以音乐id为key的level
	// 调用前要先传入一个Context对象
	public static void setLevel(Context context, int pos, int ids, int lev) {

		SharedPreferences musicMessagePreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor;
		editor = musicMessagePreferences.edit();
		editor.putInt(ids + "", lev);
		editor.commit();
		level[pos] = lev;
	}
}
