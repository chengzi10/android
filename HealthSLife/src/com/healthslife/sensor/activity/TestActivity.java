package com.healthslife.sensor.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;

import com.healthslife.sensor.dao.SportInfoDAO;
import com.healthslife.sensor.data.SensorData;
import com.healthslife.sensor.utilities.CalculateUtil;
import com.healthslife.sensor.utilities.DBUtil;

public class TestActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		System.out.println("\n===\n===\n===\n===\n===\n===============����Activity!!!");
		
		
		System.out.println("@@@@@@�˴��������֮����Ҫ���õķ���!!!");
//		String user_name;//�û���---��Ӣ����ĸ���͡��»��ߡ����
		int user_sex=1;//�Ա�
		int user_weight=70;//����
		int user_high=180;//���
		int user_aimstep=10000;//Ŀ�경����Ŀ
		int user_islogin=0;//�û���¼״̬
		ContentValues values=new ContentValues();
		values.put("user_name",SensorData.getUsername());
		values.put("user_sex",user_sex);
		values.put("user_high",user_high);
		values.put("user_weight",user_weight);
		values.put("user_aimstep",user_aimstep);
		values.put("user_islogin",user_islogin);
		/*�����ݿ����������*/
		DBUtil.insert(TestActivity.this, SportInfoDAO.TABLENAME_UserInfo, values);//��һ��Ϊ�������
		
		
		
//		SensorData.setUsername();
	 	SensorData.setGender(user_sex);
	 	SensorData.setWeight(user_weight);
	 	SensorData.setHeight(user_high);
	 	SensorData.setAim_stepNum(user_aimstep);
	 	SensorData.setLogin(user_islogin==1?true:false);//���õ�¼״̬
//	 	CalculateUtil.ComputStepLengh();//���㲽��
	 	CalculateUtil.resetInit();//��ʼ������:����ߡ����ء��Ա��û�����Ŀ�경������¼״̬֮�⣬���ó�ʼ�����в���!!!
	}
	
}
