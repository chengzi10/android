package com.healthslife.sensor.dao;

import com.healthslife.sensor.data.SensorData;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/*1.数据库操作的帮助类，通过该类的方法，打开和关闭数据库。
 * openDatabase()打开数据库；
 * closeDatabase()关闭数据库。
 * 2.通过构造方法来指定具体操作的数据库名，并提供创建数据库和
 * 更新数据库的方法。
 * 
 * */
public class DBOpenHelper extends SQLiteOpenHelper {
	// private String DB_VERSION;//数据库版本号
	// private String DB_NAME;//数据库文件名
	// private String CREATE_TABLE_TRAFFICINFO;//创建表的SQL脚本
	// private String DROP_TABLE_TRAFFICINFO;//删除表的SQL脚本
	private final static int VERSION = 1;// 数据库版本号

	// 创建一个包含所有实体类数据的表的sql语句

	/* 用户信息表：user_info */
	public static final String CREATE_INFO_TABLE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ SportInfoDAO.TABLENAME_UserInfo
			+ "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "user_name varchar(20),user_sex int,user_weight int,user_high int,user_aimstep int,"
			+ "user_islogin int)";
	/* 用户健康数据信息表：（用户名为表名）====用户退出该APP时，需要删除该表 */
	public static String CREATE_SPROTDATA_TABLE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ SportInfoDAO.TABLENAME_UserSportData
			+ "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "user_date varchar(20),user_step int,user_distance float,user_energy int, user_total_step int,"
			+ "user_total_credits int,user_isupload int)";

	/*
	 * int uid; //APP的uid private boolean flag; //ture表示“系统APP”，false表示“用户APP”.
	 * private String appName; //APP的名字 private long receive;
	 * //接收总字节数【每月1号0点至当前时刻的累计值，每月1号0点之后置零】 private long transfer; private
	 * String date;
	 */

	public DBOpenHelper(Context context, String name, CursorFactory factory,
			int version) {// 四个参数的构造方法
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DBOpenHelper(Context context, String name, int version) {// 三个参数的构造方法
		this(context, name, null, version);// 调用四个参数的构造方法
	}

	public DBOpenHelper(Context context, String name) {// 两个参数的构造方法
		this(context, name, VERSION);// 调用三个参数的构造方法
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			createTable(db);//创建表
		} catch (SQLException e) {
			System.out.println("创建数据库表时，出错!!!!!");
			e.printStackTrace();// 产生错误!!!
		}
		System.out.println("成功创建了一个数据库以及table!!");
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		createTable(db);//创建表
		super.onOpen(db);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	void createTable(SQLiteDatabase db){
		SportInfoDAO.TABLENAME_UserSportData=SensorData.getUsername();//创建表之前，获取用户名!!
		db.execSQL(CREATE_INFO_TABLE_SQL);// 用户信息表：user_info
		db.execSQL(CREATE_SPROTDATA_TABLE_SQL);// 用户健康数据信息表：（用户名为表名）
	}

}
