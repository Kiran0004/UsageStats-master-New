package com.example.test.usagestats.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.util.Log;

public class GeneralUtility {
	public static long getPastDate(int numOfDaysBefore) 
	{
		Calendar from = Calendar.getInstance();
		from.add(Calendar.DATE, -numOfDaysBefore);
		from.set(Calendar.AM_PM, Calendar.AM);
		from.set(Calendar.HOUR, 0);
		from.set(Calendar.MINUTE, 0);
		from.set(Calendar.SECOND, 0);
		from.set(Calendar.MILLISECOND,0);

		return GeneralUtility.getStartTimeOfTheDay(from.getTimeInMillis());
	}

	public static long getFutureDate(Long timeinmillis, int numOfDaysAfter) 
	{
		Calendar from = Calendar.getInstance();
		from.setTimeInMillis(timeinmillis);
		from.add(Calendar.DATE, +numOfDaysAfter);
		from.set(Calendar.AM_PM, Calendar.AM);
		from.set(Calendar.HOUR, 0);
		from.set(Calendar.MINUTE, 0);
		from.set(Calendar.SECOND, 0);

		return getStartTimeOfTheDay(from.getTimeInMillis());
	}



	public static boolean isWifiConnected(Context context)
	{
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if(wifiInfo!= null && wifiInfo.isConnected()) {
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}


	public static boolean isWifiEnalbed(Context context)
	{
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if(wifiManager!= null && wifiManager.isWifiEnabled()){
			return true;
		}

		return false;
	}

	public static boolean isCellularOn(Context context)
	{

		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

			if(mobileInfo!= null && mobileInfo.isConnected()) {
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isDeviceCharging(Context context)
	{
		try
		{
			Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
			return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
		}
		catch(Exception e)
		{

		}
		return false;
	}


	public static ArrayList<Long>  getDaysArrayList(long fromMillis, long toMillis)
	{
		ArrayList<Long> days = new ArrayList<Long>();

		Calendar from = Calendar.getInstance();
		from.setTimeInMillis(GeneralUtility.getStartTimeOfTheDay(fromMillis));

		Calendar to = Calendar.getInstance();
		to.setTimeInMillis(GeneralUtility.getStartTimeOfTheDay(toMillis)+Constants.ONE_DAY_IN_MS);

		for (Calendar c = from ; c.before(to) ; c.add(Calendar.DATE, +1))
		{
			days.add(c.getTimeInMillis());
		}

		return days;     
	}


	public static JSONArray getDates(ArrayList<Long> days)
	{
		JSONArray datesArray = new JSONArray();

		for(int i = 0;i<days.size();i++)
		{
			datesArray.put(days.get(i));
		}

		return datesArray;
	}

	
	public static long getStartTimeOfTheDay(long time)
	{
		long currentDate = -1;
		Calendar now = Calendar.getInstance();
		try 
		{
			Date d = new Date(time);
			now.setTime(d);
			now.set(Calendar.AM_PM, Calendar.AM);
			now.set(Calendar.HOUR, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.MILLISECOND,0);
			currentDate = now.getTimeInMillis();
		} 
		catch (Exception e) 
		{
			currentDate = -2;
			Log.e("-----","Exception in getTodayStartTime " + e.getMessage());
		}
		return currentDate;

	}

	public static long getTimestampForNextAlarm() {
		Calendar midnight = Calendar.getInstance(); //(TimeZone.getDefault(), Locale.US);
		midnight.setLenient(true);
		midnight.set(Calendar.HOUR_OF_DAY, 23);
		midnight.set(Calendar.MINUTE, 59);
		midnight.set(Calendar.SECOND, 0);
		midnight.set(Calendar.MILLISECOND, 0);
		if (midnight.before(Calendar.getInstance())) {
			midnight.add(Calendar.DAY_OF_MONTH, 1);
		}
		return midnight.getTimeInMillis();
	}


}
