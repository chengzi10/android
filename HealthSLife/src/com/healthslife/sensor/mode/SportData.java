package com.healthslife.sensor.mode;

public class SportData {
	
	private int id;//主键
	private String user_date;//日期
	private int user_step;//步数
	private float user_distance;//运动距离
	private int user_energy;//消耗能量
	private int user_total_step;//总步数
	private int user_total_credits;//总积分
	private int user_isupload;//标识该条数据是否已上传!
	
	public int getId() {
		return id;
	}
	public String getUser_date() {
		return user_date;
	}
	public void setUser_date(String user_date) {
		this.user_date = user_date;
	}
	public int getUser_step() {
		return user_step;
	}
	public void setUser_step(int user_step) {
		this.user_step = user_step;
	}
	public float getUser_distance() {
		return user_distance;
	}
	public void setUser_distance(float user_distance) {
		this.user_distance = user_distance;
	}
	public int getUser_energy() {
		return user_energy;
	}
	public void setUser_energy(int user_energy) {
		this.user_energy = user_energy;
	}
	public int getUser_total_step() {
		return user_total_step;
	}
	public void setUser_total_step(int user_total_step) {
		this.user_total_step = user_total_step;
	}
	public int getUser_total_credits() {
		return user_total_credits;
	}
	public void setUser_total_credits(int user_total_credits) {
		this.user_total_credits = user_total_credits;
	}
	public int getUser_isupload() {
		return user_isupload;
	}
	public void setUser_isupload(int user_isupload) {
		this.user_isupload = user_isupload;
	}
	
}
