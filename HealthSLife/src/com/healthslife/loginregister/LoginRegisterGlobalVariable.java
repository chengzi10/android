package com.healthslife.loginregister;

import com.healthslife.server.ServiceGlobalVariable;

public class LoginRegisterGlobalVariable {
	public static String login_name = null;  //登录用户名
	public static String login_passwd = null;  //登录密码
	public static String register_name = null;  //登录用户名
	public static String register_passwd = null;  //登录密码
	public static String register_confirm_passwd = null;  //登录密码
	public static boolean user_login_state = false;  //用户的登录状态 
	protected static String login_result = null; // the data of return
	protected static String register_result = null; // the data of return
	public static int login_model = 0;   //记录用户登录方式： 0：普通登录  1：一登登录 
	protected static String bundle_result = null;
	protected static String nameSpace = "http://registerlogin.server.healthSLife.com"; // The namespace of login service
	protected static String urlStr = ServiceGlobalVariable.serverUrl;
}
