package com.healthslife.server;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;

public class UpdataUserInfo extends Activity {

	public static String updataUserInfo(String str) {

		String methodName = "operation"; // login method name
		String endPoint = ServiceGlobalVariable.serverUrl
				+ "UpdateUserInfoService"; // EndPoint
		String soapAction = ServiceGlobalVariable.uploadNameSpace
				+ "/operation"; // SOAP
		// Action

		// 指定WebService的命名空间和调用的方法名
		SoapObject rpc = new SoapObject(ServiceGlobalVariable.uploadNameSpace,
				methodName);
		// 设置调用webService接口需要传入的参数
		rpc.addProperty("str", str);
		// 生成调用WebService方法的SOAP请求信息，并指定SOAP的版本
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER10);
		envelope.bodyOut = rpc;

		HttpTransportSE transport = new HttpTransportSE(endPoint, 5000);
		try {
			// 调用WebService
			transport.call(soapAction, envelope);
			ServiceGlobalVariable.updataUserInfoResult = "no_exception";
		} catch (Exception e) {
			// e.printStackTrace();
			ServiceGlobalVariable.updataUserInfoResult = "net_exception";
		}

		if (!ServiceGlobalVariable.updataUserInfoResult.equals("net_exception")) {
			// 获取返回的数据
			SoapObject object = (SoapObject) envelope.bodyIn;
			// 获取返回的结果
			if (null == object.getProperty(0)) {
				ServiceGlobalVariable.updataUserInfoResult = "false";
			} else {
				ServiceGlobalVariable.updataUserInfoResult = object
						.getProperty(0).toString();
			}
		}
		return ServiceGlobalVariable.updataUserInfoResult;
	}

}
