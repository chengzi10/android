package com.healthslife.music.dao;


public class DBData {
	//数据库名称
	public static final String DATABASE_NAME="healthslife.db";
	//数据库版本
	public static final int VERSION=1;
	
	//歌曲字段
	public static final String SONG_TABLENAME="song";
	public static final String SONG_ID="_id";
	public static final String SONG_ALBUMID="albumid";
	public static final String SONG_ARTISTID="artistid";
	public static final String SONG_NAME="name";
	public static final String SONG_DISPLAYNAME="displayName";
	public static final String SONG_NETURL="netUrl";
	public static final String SONG_DURATIONTIME="durationTime";
	public static final String SONG_SIZE="size";
	public static final String SONG_ISLIKE="isLike";
	public static final String SONG_LYRICPATH="lyricPath";
	public static final String SONG_FILEPATH="filePath";
	public static final String SONG_PLAYERLIST="playerList";
	public static final String SONG_ISNET="isNet";
	public static final String SONG_MIMETYPE="mimeType";
	public static final String SONG_ISDOWNFINISH="isDownFinish";
	public static final String SONG_LEVEL="level";
	
	//专辑字段
	public static final String ALBUM_TABLENAME="album";
	public static final String ALBUM_ID="_id";
	public static final String ALBUM_NAME="name";
	public static final String ALBUM_PICPATH="picPath";
	
	//歌手字段
	public static final String ARTIST_TABLENAME="artist";
	public static final String ARTIST_ID="_id";
	public static final String ARTIST_NAME="name";
	public static final String ARTIST_PICPATH="picPath";
	
	//播放列表字段
	public static final String PLAYERLIST_TABLENAME="playerList";
	public static final String PLAYERLIST_ID="_id";
	public static final String PLAYERLIST_NAME="name";
	public static final String PLAYERLIST_DATE="date";
	
	
}
