package com.healthslife.health;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "HealthSLife.db"; // 数据库名
	private final static int DATABASE_VERSION = 1; // 数据库版本
	private final static String Health_tableName = "September_TABLE";// 表名
	private final static String MajorKey = "major_key";// 主键ID
	private final static String Stepnumber = "stepnumber";// 步数
	private final static String Energy = "energy";// 能量
	private final static String Date = "date";// 日期

	// 默认构造函数
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE September_TABLE( major_key integer primary key autoincrement, stepnumber integer, energy integer, date varchar(20));";
		db.execSQL(sql);
	}

	@Override
	// 自动生成的方法，数据库更新
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + Health_tableName;
		db.execSQL(sql);
		onCreate(db);

	}
	// =============================================分割用===============================================

	/**
	 * 数据库的数据读取操作. Note:默认读取全部数据，不提供筛选功能.
	 * 
	 * @return Curse 表示数据库的全体记录对象.
	 */
	public Cursor select() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(Health_tableName, null, null, null, null,
				null, null);
		return cursor;
	}

	/**
	 * 数据库中插入一条记录. Note:主键属性xlcs_dataid为自增属性.
	 * 
	 * @param xlcs_beats
	 *            表示记录数据的xlcs_beat属性值.
	 * @param xlcs_type
	 *            表示记录数据的xlcs_type属性值.
	 * @param xlcs_time
	 *            表示记录数据的xlcs_time属性值.
	 * @return long 返回插入数据的记录ID,插入失败失败返回-1.
	 */
	public long insert(int stepnumber, int energy, String date) {
		SQLiteDatabase db = this.getWritableDatabase();
		/* ContentValues */
		ContentValues cv = new ContentValues();
		cv.put(Stepnumber, stepnumber);
		cv.put(Energy, energy);
		cv.put(Date, date);
		long row = db.insert(Health_tableName, null, cv);
		return row;
	}

	/**
	 * 数据库中删除一条记录. Note:只通过数据库ID进行删除操作，ID在界面中获取
	 * 
	 * @param id
	 *            表示删除记录的主键ID.
	 */
	public void delete(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = MajorKey + " = ?";
		String[] whereValue = { Integer.toString(id) };
		db.delete(Health_tableName, where, whereValue);
	}
}
