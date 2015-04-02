package com.healthslife.sensor.dao;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.healthslife.sensor.data.SensorData;


/*数据库操作类
 * 1.通过DBOpenHelper获得数据库操作对象，执行SQL语句，
 * 为手机流量监控系统提供数据库保存、查询和统计等功能。
 * 
 * */
public class SportInfoDAO {
	
	public static final String DBNAME="healthslife_data";//数据库名字
//	public static final String TABLENAME_Traffic="traffic_table";//数据库表名
//	public static final String TABLENAME_Rank="rank_table";//数据库表名
//	public static final String TABLENAME_UserRegister="user_register";//数据库表名--注册表
	public static final String TABLENAME_UserInfo="user_info";//数据库表名--用户信息表
	public static final String TABLENAME_UserMap="user_map";//数据库表名--用户地图轨迹
	//public static String TABLENAME_UserSportData=SensorData.Username;//数据库表名--用户运动数据表#####不能设置为final因为表名为用户名!!!
//	public static final String TABLENAME_UserGoal="user_goal";//数据库表名--用户运动数据目标表
//	public final static int TABLETPYE_TRAFFIC=1;
//	public final static int TABLETPYE_RANK=2;
	
	private SQLiteDatabase db = null;	//数据库
	private Context contxt;				//设备上下文
	DBOpenHelper dbOpenHelper;	//定义数据库操作类对象
	
	
	/*
	public SportInfoDAO(TrafficService trafficService){
		this.trafficService=trafficService;
	}
	*/
	
	//构造方法
	public SportInfoDAO(Context context) {
		this.contxt = context;
		
//		String id=UUID.randomUUID().toString();
	}
	
	
	//通过DBOpenHelper类对象打开数据库
	/*public void openDatabase(Context context){
		if(db == null)
		{
			dbOpenHelper = new DBOpenHelper(contxt,dbName);
			db = dbOpenHelper.getWritableDatabase();			//获得数据库操作对象
		}
	}*/
	
	/*清空数据库表数据*/
	/*public boolean clear(String tableName){
		boolean isSuccess=true;
		dbOpenHelper=new DBOpenHelper(contxt,DBNAME);
		db=dbOpenHelper.getWritableDatabase();
		try{
			db.execSQL("delete from "+tableName);
			db.execSQL("update sqlite_sequence SET seq = 0 where name ='"+tableName+"'");
			//update sqlite_sequence SET seq = 0 where name ='TableName'
		}
		catch(SQLException e){
			isSuccess=false;
			System.out.println("数据库表数据清除失败的原因:"+e.getCause());
		}
		return isSuccess;
	}*/
	
	
	/*更改表名*/// ALTER TABLE 旧表名 RENAME TO 新表名
	public boolean alter(String tableName,String newTableName){
		boolean isSuccess=true;
		dbOpenHelper=new DBOpenHelper(contxt,DBNAME);
		db=dbOpenHelper.getWritableDatabase();
		dbOpenHelper.createTable(db);//先创建数据库!!!!!!!!!!!!（不存在数据表时）
		
		try{
			db.execSQL("ALTER TABLE "+tableName+" RENAME TO "+newTableName);
			//update sqlite_sequence SET seq = 0 where name ='TableName'
		}
		catch(SQLException e){
			isSuccess=false;
			System.out.println("数据库表数据清除失败的原因:"+e.getCause());
		}
		closeDB();
		return isSuccess;
	}
	
	/*删除表*/// DROP TABLE database_name.table_name;
	public boolean dropTable(String tableName){
		boolean isSuccess=true;
		dbOpenHelper=new DBOpenHelper(contxt,DBNAME);
		db=dbOpenHelper.getWritableDatabase();
		try{
			db.execSQL("DROP TABLE "+tableName);
			//db.execSQL("update sqlite_sequence SET seq = 0 where name ='TableName'");
		}
		catch(SQLException e){
			isSuccess=false;
			System.out.println("数据库表删除失败的原因:"+e.getCause());
		}
		closeDB();
		return isSuccess;
	}
	
	/*插入新数据*/
	public boolean insert(String tableName,ContentValues values){
		boolean isSuccess=false;
		dbOpenHelper=new DBOpenHelper(contxt,DBNAME);
		db=dbOpenHelper.getWritableDatabase();
		dbOpenHelper.createTable(db);//先创建数据库!!!!!!!!!!!!（不存在数据表时）
		
		long rowid=db.insert(tableName, null, values);
		if(rowid==-1){
			isSuccess=false;
			System.out.println("插入数据失败--->"+contxt.getClass());
		}
		else{
			isSuccess=true;
			System.out.println("插入数据成功，行号:【"+rowid+"】类名--->"+contxt.getClass());
		}
		closeDB();//关闭数据库
		return isSuccess;
	}
	
	/*更新原有数据*/
	public boolean update(String tableName,ContentValues values,String whereClause, String[] whereArgs){
		boolean isSuccess=false;
		dbOpenHelper=new DBOpenHelper(contxt,DBNAME);
		db=dbOpenHelper.getWritableDatabase();
		dbOpenHelper.createTable(db);//先创建数据库!!!!!!!!!!!!（不存在数据表时）
		
		int num=db.update(tableName, values, whereClause, whereArgs);
		if(num==0){
			System.out.println("更新数据失败--->"+contxt.getClass());
			isSuccess=false;
		}
		else{
			System.out.println("更新数据成功，影响行数：【"+num+"】类名--->"+contxt.getClass());
			isSuccess=true;
		}
		closeDB();//关闭数据库
		return isSuccess;
	}
	
	
	/*关闭数据库*/
	public void closeDB(){
		dbOpenHelper.close();
	}
	
	/*查找数据：双参*/
	public Cursor query(String sql,String[] args){
		dbOpenHelper=new DBOpenHelper(contxt,DBNAME);
		db=dbOpenHelper.getReadableDatabase();
		dbOpenHelper.createTable(db);//先创建数据库!!!!!!!!!!!!（不存在数据表时）
		
		Cursor cursor=db.rawQuery(sql, args);
		return cursor;		
	}
	/*查找数据：单参*/
	/*public void find(String sql){
		
	}*/
	
	
	/*关闭数据库*/
	/*public void closeDatabase(){
		 dbOpenHelper.close();
	}*/
	
	
	
	
}
