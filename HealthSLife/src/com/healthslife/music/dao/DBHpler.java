package com.healthslife.music.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHpler extends SQLiteOpenHelper {

	public DBHpler(Context context) {
		super(context, DBData.DATABASE_NAME, null, DBData.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建专辑表
		db.execSQL("CREATE TABLE " + DBData.ALBUM_TABLENAME + "("
				+ DBData.ALBUM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DBData.ALBUM_NAME + " NVARCHAR(100)," + DBData.ALBUM_PICPATH
				+ " NVARCHAR(300))");
		// 创建歌手表
		db.execSQL("CREATE TABLE " + DBData.ARTIST_TABLENAME + "("
				+ DBData.ARTIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DBData.ARTIST_NAME + " NVARCHAR(100),"
				+ DBData.ARTIST_PICPATH + " NVARCHAR(300))");
		// 创建播放列表的表
		db.execSQL("CREATE TABLE " + DBData.PLAYERLIST_TABLENAME + "("
				+ DBData.PLAYERLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DBData.PLAYERLIST_NAME + " NVARCHAR(100),"
				+ DBData.PLAYERLIST_DATE + " INTEGER)");
		// 创建歌曲表
		db.execSQL("CREATE TABLE " + DBData.SONG_TABLENAME + "("
				+ DBData.SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DBData.SONG_ALBUMID + " INTEGER," 
				+ DBData.SONG_ARTISTID+ " INTEGER," 
				+ DBData.SONG_NAME + " NVARCHAR(100),"
				+ DBData.SONG_DISPLAYNAME + " NVARCHAR(100),"
				+ DBData.SONG_NETURL + " NVARCHAR(500),"
				+ DBData.SONG_DURATIONTIME + " INTEGER," 
				+ DBData.SONG_SIZE+ " INTEGER," 
				+ DBData.SONG_ISLIKE + " INTEGER,"
				+ DBData.SONG_LYRICPATH + " NVARCHAR(300),"
				+ DBData.SONG_FILEPATH + " NVARCHAR(300),"
				+ DBData.SONG_PLAYERLIST + " NVARCHAR(500)," 
				+ DBData.SONG_ISNET+ " INTEGER," 
				+ DBData.SONG_MIMETYPE + " NVARCHAR(50),"
				+ DBData.SONG_ISDOWNFINISH + " INTEGER,"
				+ DBData.SONG_LEVEL + " INTEGER)");
		
		//添加默认列表
		db.execSQL("INSERT INTO "+DBData.PLAYERLIST_TABLENAME+"("+DBData.PLAYERLIST_NAME+","+DBData.PLAYERLIST_DATE
				+") VALUES('默认列表',"+System.currentTimeMillis()+")");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 删除表
		db.execSQL("DROP TABLE IF EXISTS " + DBData.ALBUM_TABLENAME);
		db.execSQL("DROP TABLE IF EXISTS " + DBData.ARTIST_TABLENAME);
		db.execSQL("DROP TABLE IF EXISTS " + DBData.PLAYERLIST_TABLENAME);
		db.execSQL("DROP TABLE IF EXISTS " + DBData.SONG_TABLENAME);
	}
}
