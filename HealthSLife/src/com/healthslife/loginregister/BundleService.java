package com.healthslife.loginregister;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class BundleService {

	public static void bundleInfo(String info) {
		String methodName = "bundleFace"; // login method name
		String endPoint = LoginRegisterGlobalVariable.urlStr + "BundleFaceService"; // EndPoint
		String soapAction = LoginRegisterGlobalVariable.nameSpace + "/bundleFace"; // SOAP
																						// Action

		// 指定WebService的命名空间和调用的方法名
		SoapObject rpc = new SoapObject(LoginRegisterGlobalVariable.nameSpace, methodName);
		// 设置调用webService接口需要传入的参数
		rpc.addProperty("phoneNum", info);

		// 生成调用WebService方法的SOAP请求信息，并指定SOAP的版本
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER10);
		envelope.bodyOut = rpc;

		HttpTransportSE transport = new HttpTransportSE(endPoint,5000);
		try {
			// 调用WebService
			transport.call(soapAction, envelope);
			LoginRegisterGlobalVariable.bundle_result = "no_exception";
		} catch (Exception e) {
			//e.printStackTrace();
			LoginRegisterGlobalVariable.bundle_result = "net_exception";
		}

		if(!LoginRegisterGlobalVariable.bundle_result.equals("net_exception")) {
			// 获取返回的数据
			SoapObject object = (SoapObject) envelope.bodyIn;
			// 获取返回的结果
			LoginRegisterGlobalVariable.bundle_result = object.getProperty(0).toString();
		}else {
			return;
		}
	}

}
