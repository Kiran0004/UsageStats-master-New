package com.example.test.usagestats.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DataUsagePreference {

	public static final String PREFES_NAME = "AnalyticsDataUsagePrefsFile";

	public static void saveDataUsageInsertionDate(Context context, long date) {
		SharedPreferences prefs = context.getSharedPreferences(PREFES_NAME,
				Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putLong("DATAUSAGE_DAY", date);
		editor.commit();
	}

	public static long getLastSavedInsertionDate(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFES_NAME,
				Context.MODE_PRIVATE);
		long value = prefs.getLong("DATAUSAGE_DAY", 0);
		return value;
	}

}
