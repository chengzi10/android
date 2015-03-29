package com.healthslife.sensor.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.healthslife.sensor.dao.SportInfoDAO;
import com.healthslife.sensor.mode.SportData;
import com.healthslife.sensor.mode.User_info;




public class DBUtil {
	
	/*插入数据*/
	/*public static boolean insert(Context context,String tableName,String fieldName,String fieldValue){
		boolean isSuccess=false;
		SportInfoDAO dao=new SportInfoDAO(context);
		ContentValues values=new ContentValues();
		values.put(fieldName, fieldValue);
		isSuccess=dao.insert(tableName, values);
		return isSuccess;
	}*/
	
	/*插入数据(集合)*/
	public static boolean insert(Context context,String tableName,ContentValues values){
		boolean isSuccess=false;
		SportInfoDAO dao=new SportInfoDAO(context);
		isSuccess=dao.insert(tableName, values);
		return isSuccess;
	}
	
	/*更新数据*/
	/*public static boolean update(Context context,String tableName,String fieldName,String fieldValue,
			String whereClause, String[] whereArgs){
		boolean isSuccess=false;
		SportInfoDAO dao=new SportInfoDAO(context);
		ContentValues values=new ContentValues();
		values.put(fieldName, fieldValue);
		isSuccess=dao.update(tableName, values, whereClause, whereArgs);
		return isSuccess;
	}*/
	
	/*更新数据(集合)*/
	public static boolean update(Context context,String tableName,ContentValues values,
			String whereClause, String[] whereArgs){
		boolean isSuccess=false;
		SportInfoDAO dao=new SportInfoDAO(context);
		isSuccess=dao.update(tableName, values, whereClause, whereArgs);
		return isSuccess;
	}
	
	/*清空数据库表数据*/
	/*public static boolean clear(Context context,String tableName){
		boolean isSuccess=false;
		SportInfoDAO dao=new SportInfoDAO(context);
		isSuccess=dao.clear(tableName);
		return isSuccess;
	}*/
	
	/*更改表名*/
	// ALTER TABLE 旧表名 RENAME TO 新表名
	public static boolean alter(Context context,String tableName,String newTableName){
		boolean isSuccess=false;
		SportInfoDAO dao=new SportInfoDAO(context);
		isSuccess=dao.alter(tableName,newTableName);
		return isSuccess;
	}
	
	/*删除表*/
	// ALTER TABLE 旧表名 RENAME TO 新表名
	public static boolean dropTable(Context context,String tableName){
		boolean isSuccess=false;
		SportInfoDAO dao=new SportInfoDAO(context);
		isSuccess=dao.dropTable(tableName);
		return isSuccess;
	}
	
	/*查询User_info表*/
	/*public static User_info query(Context context,String sql,String args[]){
		User_info info=new User_info();
		SportInfoDAO dao=new SportInfoDAO(context);
		Cursor c=dao.query(sql, args);//查询
		 while(c.moveToNext()) {  
			 sportData.setUser_date(c.getString(c.getColumnIndex("user_date")));
			 sportData.setUser_step(c.getInt(c.getColumnIndex("user_step")));
			 sportData.setUser_distance(c.getFloat(c.getColumnIndex("user_distance")));
			 sportData.setUser_energy(c.getInt(c.getColumnIndex("user_energy")));
			 sportData.setUser_total_step(c.getInt(c.getColumnIndex("user_total_step")));
			 sportData.setUser_total_credits(c.getInt(c.getColumnIndex("user_total_credits")));
			 sportData.setUser_isupload(c.getInt(c.getColumnIndex("user_isupload")));
	     }  
		if(c.getCount()==0)sportData.setUser_date(null);//如果数据库中没有数据，则将"日期"置为null!!
		c.close();
		dao.closeDB();//关闭数据库!
		
		return sportData;
	}*/
	
	
	/*查询SportData表*/
	public static SportData query(Context context,String sql,String args[]){
		SportData sportData=new SportData();
		SportInfoDAO dao=new SportInfoDAO(context);
		Cursor c=dao.query(sql, args);//查询
		 while(c.moveToNext()) {  
			 sportData.setUser_date(c.getString(c.getColumnIndex("user_date")));
			 sportData.setUser_step(c.getInt(c.getColumnIndex("user_step")));
			 sportData.setUser_distance(c.getFloat(c.getColumnIndex("user_distance")));
			 sportData.setUser_energy(c.getInt(c.getColumnIndex("user_energy")));
			 sportData.setUser_total_step(c.getInt(c.getColumnIndex("user_total_step")));
			 sportData.setUser_total_credits(c.getInt(c.getColumnIndex("user_total_credits")));
			 sportData.setUser_isupload(c.getInt(c.getColumnIndex("user_isupload")));
	     }  
		if(c.getCount()==0)sportData.setUser_date(null);//如果数据库中没有数据，则将"日期"置为null!!
		c.close();
		dao.closeDB();//关闭数据库!
		
		return sportData;
	}
	
	
	
}
