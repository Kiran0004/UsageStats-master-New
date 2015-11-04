package com.example.test.usagestats.bean;

public class AppUsageDTO {

	public String app_name;
	public String start_time;
	public String end_time;
	public String time_spent;
	public String getApp_name() {
		return app_name;
	}
	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getTime_spent() {
		return time_spent;
	}
	public void setTime_spent(String time_spent) {
		this.time_spent = time_spent;
	}

}
