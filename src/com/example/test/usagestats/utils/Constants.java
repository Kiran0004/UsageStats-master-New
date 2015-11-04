package com.example.test.usagestats.utils;

public class Constants {
	public static final String TRIGGER = "TRIGGER";
	public static final int TRIGGER_PLUGIN = 0;
	public static final int TRIGGER_PLUGOUT = 1;
	public static final int TRIGGER_DEVICESHUTDOWN = 2;
	public static final int TRIGGER_BOOTCOMPLETED = 3;
	public static final int TRIGGER_CONNECTIONCHANGE = 4;
	public static final int TRIGGER_MIDNIGHTALARM = 5;
	public static final int TRIGGER_MVDPULL = 6;

	public static final String CURRENTTIME = "CURRENTTIME";
	public static final String ELAPSEDREALTIME = "ELAPSEDREALTIME";
	
	public static final String NETWORKTYPE = "networkType";
	public static final String NETWORKSUBTYPE = "networkSubType";
	public static final String NETWORKSTATE = "networkState";
	
	public static String NETWORKTYPE_WIFI = "WIFI";
	
	public static final int CYCLE_TYPE_POWER = 1;
	public static final int CYCLE_TYPE_CHARGING = 2;
	
	public static final long ONE_DAY_IN_MS = 86400000L;
	
	public static final String NOT_RETRIEVABLE = "NOT_RETRIEVABLE";
	
	public static final String MIDNIGHT_ALARM_INTENT_ACTION = "com.vzw.hss.myverizon.rdd.analytics.midnight";
	
	public static final int NUM_OF_DAYS_BEFORE_COUNT = 7;
	
	public static final int NUM_OF_TOP_APPS = 20;
	

}
