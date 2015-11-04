package com.example.test.usagestats.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class Utils {

	private static final String NOT_RETRIEVABLE = "NOT RETRIEVABLE";
	public final static String TAG = "UsageStats";
	public final static String SOURCE_SYSTEM = "System";
	public final static String SOURCE_PRELOADED = "Preloaded";
	public final static String SOURCE_PRELOADED_UPDATE = "Preloaded Update";
	public final static String SOURCE_GOOGLE_PLAYSTORE = "Google Play Store";
	public final static String SOURCE_AMAZON_APPSTORE = "Amazon AppStore";
	public final static String SOURCE_SIDELOADED = "Sideloaded";
	public final static String SOURCE_UNKNOWN = "Unknown";
	//public static ArrayList<String> temp =  new ArrayList<String>();	
	public static String val = "";
	public static String getSource(Context context, String packageName) {

		String source = SOURCE_UNKNOWN;

		PackageManager pm = context.getPackageManager();

		ApplicationInfo ai;

		try {
			ai = pm.getApplicationInfo(packageName, 0);

			if (isPreloaded(ai)) {
				source = SOURCE_PRELOADED;
			} else if (isPreloadedUpdate(ai)) {
				source = SOURCE_PRELOADED_UPDATE;
			} else if (isGooglePlay(context, packageName)) {
				source = SOURCE_GOOGLE_PLAYSTORE;
			} else if (isAmazonStore(context, packageName)) {
				source = SOURCE_AMAZON_APPSTORE;
			} else {
				source = SOURCE_SIDELOADED;
			}

		} catch (Exception e) {
			Log.d("-----","Exception, set source to Unknown");
			source = SOURCE_UNKNOWN;
		}

		return source;
	}

	public static boolean isAmazonStore(Context context, String pkgName) {
		PackageManager pm = context.getPackageManager();

		String ipn = pm.getInstallerPackageName(pkgName);

		if ("com.amazon.venezia".equalsIgnoreCase(ipn)) {
			return true;
		}

		return false;
	}

	public static boolean isGooglePlay(Context context, String pkgName) {
		PackageManager pm = context.getPackageManager();

		String ipn = pm.getInstallerPackageName(pkgName);

		if ("com.android.vending".equalsIgnoreCase(ipn)
				|| "com.google.android.feedback".equalsIgnoreCase(ipn)) {
			return true;
		}

		return false;
	}

	public static boolean isPreloaded(ApplicationInfo info) {
		return info != null && (info.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
	}

	public static boolean isPreloadedUpdate(ApplicationInfo info) {
		return info != null
				&& (info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
	}
	public static String getDateTimeFromMilliSeconds(long time) {

		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

		String result = sdf.format(new Date(time));

		return result;
	}
	
	public static String getTimeDiff(String start,String end){		
		String duration = "";
		//HH converts hour in 24 hours format (0-23), day calculation
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

		Date d1 = null;
		Date d2 = null;

		try {
			d1 = format.parse(start);
			d2 = format.parse(end);

			//in milliseconds
			long diff = d2.getTime() - d1.getTime();

			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);

			if(diffDays>0){
				duration = diffDays + " days ";
			}if(diffHours>0){
				duration = duration + diffHours + " hours ";
			}if(diffMinutes>0){
			duration = duration + diffMinutes + " min ";
			}
			Log.d(Utils.TAG,"-----------------Total Duration:::"+duration);
			//duration  = diffMinutes+"";			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return duration;

	}
}
