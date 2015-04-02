package com.healthslife.server;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.healthslife.loginregister.LoginRegisterGlobalVariable;

import android.app.Activity;

public class UpdataUserData extends Activity {
	public static String updataUserData(String tableName, String str) {

		String methodName = "operation"; // login method name
		String endPoint = ServiceGlobalVariable.serverUrl
				+ "UpdateUserDataService"; // EndPoint
		String soapAction = ServiceGlobalVariable.uploadNameSpace
				+ "/operation"; // SOAP
		// Action

		// 指定WebService的命名空间和调用的方法名
		SoapObject rpc = new SoapObject(ServiceGlobalVariable.uploadNameSpace,
				methodName);
		// 设置调用webService接口需要传入的参数
		rpc.addProperty("tableName", tableName);
		rpc.addProperty("str", str);
		// 生成调用WebService方法的SOAP请求信息，并指定SOAP的版本
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER10);
		envelope.bodyOut = rpc;

		HttpTransportSE transport = new HttpTransportSE(endPoint, 5000);
		try {
			// 调用WebService
			transport.call(soapAction, envelope);
			ServiceGlobalVariable.updataUserDataResult = "no_exception";
		} catch (Exception e) {
			//e.printStackTrace();
			ServiceGlobalVariable.updataUserDataResult = "net_exception";
		}

		if(!ServiceGlobalVariable.updataUserDataResult.equals("net_exception")) {
			// 获取返回的数据
			SoapObject object = (SoapObject) envelope.bodyIn;
			// 获取返回的结果
			if (null == object.getProperty(0)) {
				ServiceGlobalVariable.updataUserDataResult = "false";
			} else {
				ServiceGlobalVariable.updataUserDataResult = object.getProperty(0)
						.toString();
			}
		}
		
		return ServiceGlobalVariable.updataUserInfoResult;
	}

}