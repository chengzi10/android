package com.healthslife.sensor.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.healthslife.loginregister.LoginRegisterGlobalVariable;
import com.healthslife.sensor.dao.SportInfoDAO;
import com.healthslife.sensor.data.SensorData;
import com.healthslife.sensor.mode.SportData;

public class CalculateUtil {
	
	
	/*上传info信息*///需要更新整个界面的数据!!!!!!
	public static String UploadInfo(Context context){
		String data="";//数据
		SportInfoDAO dao=new SportInfoDAO(context);
		Cursor c=dao.query("select * from "+SportInfoDAO.TABLENAME_UserInfo+" where 1=1", null);//查询
		while(c.moveToNext()) {  
			SensorData.setUsername(c.getString(c.getColumnIndex("user_name")));
			data+=SensorData.getUsername();
			data+="@"+c.getInt(c.getColumnIndex("user_sex"));
			data+="@"+c.getInt(c.getColumnIndex("user_weight"));
			data+="@"+c.getInt(c.getColumnIndex("user_high"));
			data+="@"+c.getInt(c.getColumnIndex("user_aimstep"));
	     } 
		 ContentValues values=new ContentValues();
		 values.put("user_islogin", 0);
		 SensorData.setLogin(false);//未登录状态!!!
		 dao.update(SportInfoDAO.TABLENAME_UserInfo, values, "user_name=?", 
				 new String[]{SensorData.getUsername()});
		 //(SportInfoDAO.TABLENAME_UserInfo, values);
		if(c.getCount()==0)System.out.println("上传$$$$$$$$$$$$$$错误!!!");;//如果数据库中没有数据，则将"日期"置为null!!
		c.close();
		dao.closeDB();//关闭数据库!
		return data;
	}
	
	/*上传sportData信息*///需要更新整个界面的数据!!!!!!
	public static String UploadSportData(Context context){
		String data="";//数据
		SportInfoDAO dao=new SportInfoDAO(context);
		SportInfoDAO.TABLENAME_UserSportData=SensorData.getUsername();
		Cursor c=dao.query("select * from "+SportInfoDAO.TABLENAME_UserSportData+" where user_isupload=0", null);//查询未上传的数据//user_isupload=0
		while(c.moveToNext()) {  
			 data+=c.getString(c.getColumnIndex("user_date"));
			 data+="@"+c.getInt(c.getColumnIndex("user_step"));
			 data+="@"+c.getFloat(c.getColumnIndex("user_distance"));
			 data+="@"+c.getInt(c.getColumnIndex("user_energy"));
			 data+="@"+c.getInt(c.getColumnIndex("user_total_step"));
			 data+="@"+c.getInt(c.getColumnIndex("user_total_credits"))+"#";
	    } 
		boolean isDelete=DBUtil.dropTable(context, SportInfoDAO.TABLENAME_UserSportData);//删除表
		if(!isDelete){
			System.out.println("-------------------删除数据库表【失败】!!");//测试!!!!!!!!!!!!!!!!!!
		}else{
			System.out.println("-------------------删除数据库表【成功】!!");//测试!!!!!!!!!!!!!!!!!!
		}
		if(c.getCount()==0)System.out.println("上传SportData$$$$$$$$$$$$$$错误!!!");;//如果数据库中没有数据，则将"日期"置为null!!
		c.close();
		dao.closeDB();//关闭数据库!
		return data;
	}
	
	/*登录时，更改信息标志*///需要更新整个界面的数据!!!!!!
	public static String LoginMark(Context context,String info,String data){
		boolean isSuccess1=true;
		boolean isSuccess2=true;
		boolean isSuccess3=true;
		String info_words[]=info.split("@");
		String user_name=info_words[0];
		int user_sex=Integer.valueOf(info_words[1]);
		int user_weight=Integer.valueOf(info_words[2]);
		int user_high=Integer.valueOf(info_words[3]);
		int user_aimstep=Integer.valueOf(info_words[4]);
		int user_islogin=1;
		SensorData.setUsername(user_name);
		SensorData.setGender(user_sex);
		SensorData.setWeight(user_weight);
		SensorData.setHeight(user_high);
		SensorData.setAim_stepNum(user_aimstep);
		SensorData.setLogin(user_islogin==1?true:false);
		//DBUtil.dropTable(context, SportInfoDAO.TABLENAME_UserInfo);//删除表
		
		SportInfoDAO dao=new SportInfoDAO(context);
		Cursor c=dao.query("select * from "+SportInfoDAO.TABLENAME_UserInfo+" where 1=1", null);//查询
		int id=-1;
		 while(c.moveToNext()) {  
			 id=c.getInt(c.getColumnIndex("id"));
	     }
		 if(id!=-1){
			 ContentValues values=new ContentValues();
			 values.put("user_name", user_name);
			 values.put("user_sex", user_sex);
			 values.put("user_weight", user_weight);
			 values.put("user_high", user_high);
			 values.put("user_aimstep", user_aimstep);
			 values.put("user_islogin", user_islogin);
			 isSuccess1=DBUtil.update(context, SportInfoDAO.TABLENAME_UserInfo, values, "id=?", new String[]{id+""});
		 }else{
			 ContentValues values=new ContentValues();
			 values.put("user_name", user_name);
			 values.put("user_sex", user_sex);
			 values.put("user_weight", user_weight);
			 values.put("user_high", user_high);
			 values.put("user_aimstep", user_aimstep);
			 values.put("user_islogin", user_islogin);
			 isSuccess2=DBUtil.insert(context, SportInfoDAO.TABLENAME_UserInfo, values);
		 }
		 dao.closeDB();
		 c.close();
		
		//DBUtil.insert(context, SportInfoDAO.TABLENAME_UserInfo, values);
		//DBUtil.update(context, SportInfoDAO.TABLENAME_UserInfo, , "user_name=?", new String[]{SensorData.getUsername()});
		
		String data_rows[]=data.split("#");
		for(int i=0;i<data_rows.length;i++){
			String data_words[]=data_rows[i].split("@");
			System.out.println("第"+(i+1)+"行:");
			String user_date=data_words[0];
			int user_step=Integer.valueOf(data_words[1]);
			float user_distance=Float.valueOf(data_words[2]);
			int user_energy=Integer.valueOf(data_words[3]);
			int user_total_step=Integer.valueOf(data_words[4]);
			int user_total_credits=Integer.valueOf(data_words[5]);
			int user_isupload=1;//从服务器端下载下来的数据，都已上传!
			ContentValues values=new ContentValues();
			values.put("user_date", user_date);
			values.put("user_step", user_step);
			values.put("user_distance", user_distance);
			values.put("user_energy", user_energy);
			values.put("user_total_step", user_total_step);
			values.put("user_total_credits", user_total_credits);
			values.put("user_isupload", user_isupload);
			boolean isExist=DBUtil.update(context, SportInfoDAO.TABLENAME_UserSportData, 
					values, "user_date=?", new String[]{user_date});
			if(!isExist){
				isSuccess3&=DBUtil.insert(context, SportInfoDAO.TABLENAME_UserSportData, values);
			}
			
			
		}
		String message="true";
		if(!(isSuccess1&isSuccess2&isSuccess3)){
			message="false";
		}
		
		return message;
	}
	
	//#######################################################
	/*修改个人信息*///######注意做完修改操作时，一定要更新界面!!!
	public static void AlterInfo(Context context,ContentValues values){
		
		DBUtil.update(context, SportInfoDAO.TABLENAME_UserInfo, values, "user_name=?", new String[]{SensorData.getUsername()});
		Renew(context);
	}
	
	/*更新个人信息*/
	public static void Renew(Context context){
		SportInfoDAO dao=new SportInfoDAO(context);
		Cursor c=dao.query("select * from "+SportInfoDAO.TABLENAME_UserInfo+" where 1=1", null);//查询
		 while(c.moveToNext()) {  
			 SensorData.setUsername(c.getString(c.getColumnIndex("user_name")));
			 SensorData.setGender(c.getInt(c.getColumnIndex("user_sex")));
			 SensorData.setWeight(c.getInt(c.getColumnIndex("user_weight")));
			 SensorData.setHeight(c.getInt(c.getColumnIndex("user_high")));
			 SensorData.setAim_stepNum(c.getInt(c.getColumnIndex("user_aimstep")));
			 SensorData.setLogin(c.getInt(c.getColumnIndex("user_islogin"))==1?true:false);
	     }  
		if(c.getCount()==0)System.out.println("$$$$$$$$$$$$$$错误!!!");;//如果数据库中没有数据，则将"日期"置为null!!
		c.close();
		dao.closeDB();//关闭数据库!
	}
	
	
	/*定时刷新数据库:包括每天零点当天数据清零*/
	public static void RefreshTable(Context context){
		String nowTime=GetNowTime();//获取系统当前日期!!
		SportInfoDAO.TABLENAME_UserSportData=SensorData.getUsername();//用户名即表名!
		SportData sportData=DBUtil.query(context, "select * from "+SportInfoDAO.TABLENAME_UserSportData
				+" where user_date='"+nowTime+"'",null);//搜索当天数据，如果没有数据，则重置当天数据；如果有，则读取当前数据!!
		if(sportData.getUser_date()!=null){//当天有数据:更新操作!!
			ContentValues values=new ContentValues();
			values.put("user_date", nowTime);
			values.put("user_step", SensorData.getStepNum());
			values.put("user_distance", SensorData.getDistance());
			values.put("user_energy", SensorData.getEnergy());
			values.put("user_total_step", SensorData.getTotal_stepNum());
			values.put("user_total_credits", SensorData.getTotalCredits());
			values.put("user_isupload", 0);//默认设置为“0”，表示未上传!
			DBUtil.update(context, SportInfoDAO.TABLENAME_UserSportData, values, "user_date=?", new String[]{nowTime});
		}else{//当天无数据:插入操作（将今天数据清空并插入一条清零数据）!!!
			ContentValues values=new ContentValues();
			values.put("user_date", nowTime);
			values.put("user_step", 0);
			values.put("user_distance", 0);
			values.put("user_energy", 0);
			values.put("user_total_step", SensorData.getTotal_stepNum()-SensorData.getStepNum());
			values.put("user_total_credits", SensorData.getTotalCredits());
			values.put("user_isupload", 0);//默认设置为“0”，表示未上传!
			DBUtil.insert(context, SportInfoDAO.TABLENAME_UserSportData, values);
			
			/*清零*/
			SensorData.setStepNum(0);
			SensorData.setStepNum_lastRefresh(0);//上次刷新步数
			SensorData.setStepNum_lastSpeak(0);//上次播报步数
			SensorData.setStepNum_old(0);//上次步数
			SensorData.setDistance(0);//距离
			SensorData.setDistance_old(0);//上次距离
			SensorData.setEnergy(0);//能量
		}
		
		
	}
	
	
	/*获取当前系统时间*/
	public static String GetNowTime(){
		Date d=new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		return format.format(d);//系统当前时间
	}
	
	/*初始化所有参数:从数据库读取*/
	public static void initALL(Context context){
		String nowTime=GetNowTime();//获取系统当前日期!!
		SportInfoDAO.TABLENAME_UserSportData=SensorData.getUsername();//用户名即表名!
		SportData sportData=DBUtil.query(context, "select * from "+SportInfoDAO.TABLENAME_UserSportData
				+" where user_date='"+nowTime+"'",null);//搜索当天数据，如果没有数据，则重置当天数据；如果有，则读取当前数据!!
		if(sportData.getUser_date()!=null){//有数据:读取当前数据!!
			SensorData.setStepNum(sportData.getUser_step());//步数
			SensorData.setStepNum_lastRefresh(sportData.getUser_step());//上次刷新步数
			SensorData.setStepNum_lastSpeak(sportData.getUser_step());//上次播报步数
			SensorData.setStepNum_old(sportData.getUser_step());//上次步数
			ComputStepLengh();//计算步长
			SensorData.setDistance(sportData.getUser_distance());//距离
			SensorData.setDistance_old(sportData.getUser_distance());//上次距离
			SensorData.setEnergy(sportData.getUser_energy());//消耗能量
			SensorData.setTotal_stepNum(sportData.getUser_total_step());//累计总运动步数
			SensorData.setTotalCredits(sportData.getUser_total_credits());//累计总积分
		}else{//无数据:只重置当天数据!!
			SensorData.setStepNum(0);
			ComputStepLengh();//计算步长
			SensorData.setStepNum_lastRefresh(0);//上次刷新步数
			SensorData.setStepNum_lastSpeak(0);//上次播报步数
			SensorData.setStepNum_old(0);//上次步数
			SensorData.setDistance(0);//距离
			SensorData.setDistance_old(0);//上次距离
			SensorData.setEnergy(0);//能量
		}
	}
	
	/*重置初始化所有参数（包括“累计数据”）*/
	//初始化所有参数:除身高、体重、性别、用户名、目标步数、登录状态之外，重置初始化所有参数!!!
	public static void resetInit(){
		SensorData.setStepNum(0);
		ComputStepLengh();//计算步长
		SensorData.setStepNum_lastRefresh(0);//上次刷新步数
		SensorData.setStepNum_lastSpeak(0);//上次播报步数
		SensorData.setStepNum_old(0);//上次步数
		SensorData.setDistance(0);//距离
		SensorData.setDistance_old(0);//上次距离
		SensorData.setEnergy(0);//能量
		SensorData.setTotalCredits(0);//累计总积分
		SensorData.setTotal_stepNum(0);//累计总步数
	}
	
	/*将跑步时间,转换成合理的String类型*/
	public static String GetRunningTimeByString(long time){
		String result="累计跑步";
		time/=60000;//转换成“分钟”
		long hour=time/60;//获取跑步的小时数!
		long minute=time%60;//获取跑步的分钟数！
		if(hour!=0){
			result+=hour+"小时";
		}
		if(minute!=0){
			result+=minute+"分钟";
		}
		if(result.equals("累计跑步"))result="";
		return result;
	}
	
	
	/*计算步长*/
	public static void ComputStepLengh(){
		if(SensorData.getHeight()<SensorData.HEIGHT_DOWN){
			SensorData.setStepLength(SensorData.SMALL_HS_RATIO*SensorData.height);
		}
		else if(SensorData.height>=SensorData.HEIGHT_DOWN&&SensorData.height<=SensorData.HEIGHT_UP){
			SensorData.setStepLength(SensorData.MEDIUM_HS_RATIO*SensorData.height);
		}else{
			SensorData.setStepLength(SensorData.BIG_HS_RATIO*SensorData.height);
		}
		
	}
	
	/*计算跑步步长*/
	/*public static void ComputRunningStepLengh(){
		SensorData.StepLength=SensorData.RW_RATIO*SensorData.StepLength;
		
	}*/
	
	
	/*public static long GetEnergy(int stepNum){
    	double energy=0;
    	if(SensorData.gender==1){//男性能量消耗计算公式
    		SensorData.height=SensorData.height_M;
    		SensorData.weight=SensorData.weight_M;
    		energy=0.53*SensorData.height+0.58*SensorData.weight+0.37*SensorData.moveHZ
    				+1.51*SensorData.moveTime-145.03;
    	}else{//女性能量消耗计算公式
    		SensorData.height=SensorData.height_F;
    		SensorData.weight=SensorData.weight_F;
    		energy=0.003*SensorData.height+0.45*SensorData.weight+0.16*SensorData.moveHZ
    				+0.39*SensorData.moveTime-12.93;
    	}
    	long result=RoundDouble(energy);
    	return result;
    }*/
	
	/*一天消耗总能量*/
	/* public static long GetTotalEnergy(int stepNum){
	    	double energy=0;
	    	double bodySurfaceArea=0.0061*SensorData.height+0.0128*SensorData.weight-0.1529;//体表面积
	    	if(SensorData.gender==1){//男性能量消耗计算公式
	    		energy=0.07*stepNum+1900.95*bodySurfaceArea-1646.11;
	    	}else{//女性能量消耗计算公式
	    		energy=0.03*stepNum+2269.25*bodySurfaceArea-1776.08;
	    	}
	    	long result=RoundDouble(energy);
	    	return result;
	    }*/
	 
	 /*简易的四舍五入方法---Long类型范围*/
	 public static long RoundDouble(double d){//对double类型取整数!
			long result=0;
			String str=String.valueOf(d);
			int index=str.indexOf(".");//获取小数点位置
			result=Long.parseLong(str.substring(0, index));//获取整数部分
			char num=str.charAt(index+1);
			if(num>='5'){//四舍五入
				result+=1;
			}
			return result;
		}
	
}
