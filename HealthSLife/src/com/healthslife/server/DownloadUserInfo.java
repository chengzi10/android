package com.healthslife.server;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class DownloadUserInfo {

	public static String downloadUserInfo(String user_name) {

		String methodName = "downUserInfo"; // login method name
		String endPoint = ServiceGlobalVariable.serverUrl+"DownUserInfoService"; // EndPoint
		String soapAction =ServiceGlobalVariable.downloadNameSpace+"/downUserInfo"; // SOAP
		
		String info = null;
		// Action
		// 指定WebService的命名空间和调用的方法名
		SoapObject rpc = new SoapObject(ServiceGlobalVariable.downloadNameSpace,
				methodName);
		// 设置调用webService接口需要传入的参数
		rpc.addProperty("user_name", user_name);

		// 生成调用WebService方法的SOAP请求信息，并指定SOAP的版本
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER10);
		envelope.bodyOut = rpc;

		HttpTransportSE transport = new HttpTransportSE(endPoint,5000);
		try {
			// 调用WebService
			transport.call(soapAction, envelope);
			info = "no_exception";
		} catch (Exception e) {
			//e.printStackTrace();
			info = "net_exception";
		}

		if(!info.equals("net_exception")) {
			// 获取返回的数据
			SoapObject object = (SoapObject) envelope.bodyIn;
			// 获取返回的结果

			info = object.getProperty(0).toString();
		}
		
		return info;
	}
}
