package com.healthslife.sensor.mode;

public class User_info {
	private int id;//主键
	private String user_name;//用户名---“英文字母”和“下划线”组成
	private int user_sex;//性别
	private int user_weight;//体重
	private int user_high;//身高
	private int user_aimstep;//目标步行数目
	private int user_islogin;//用户登录状态
	
	public int getId() {
		return id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public int getUser_sex() {
		return user_sex;
	}
	public void setUser_sex(int user_sex) {
		this.user_sex = user_sex;
	}
	public int getUser_weight() {
		return user_weight;
	}
	public void setUser_weight(int user_weight) {
		this.user_weight = user_weight;
	}
	public int getUser_high() {
		return user_high;
	}
	public void setUser_high(int user_high) {
		this.user_high = user_high;
	}
	public int getUser_aimstep() {
		return user_aimstep;
	}
	public void setUser_aimstep(int user_aimstep) {
		this.user_aimstep = user_aimstep;
	}
	public int getUser_islogin() {
		return user_islogin;
	}
	public void setUser_islogin(int user_islogin) {
		this.user_islogin = user_islogin;
	}
	
	
}
