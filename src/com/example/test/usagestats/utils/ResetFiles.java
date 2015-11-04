package com.example.test.usagestats.utils;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.example.test.usagestats.db.DataUsageDatabaseHelper;
import com.example.test.usagestats.pref.DataUsagePreference;

public class ResetFiles {

	public static void clearDatabaseAndPreference(Context context) 
	{
		Log.d("-----","Clearing Database and Preferences. All data will be resetted.");
		clearPrefernces(context);
		clearDatabase(context);
	}

	public static void clearPrefernces(Context context) {
		String[] filename = new String[] {				
				DataUsagePreference.PREFES_NAME
				};

		SharedPreferences pref;

		for (int i = 0; i < filename.length; i++) {
			try {
				pref = context.getSharedPreferences(filename[i],
						Context.MODE_PRIVATE);
				pref.edit().clear().commit();
				
				Log.d("-----","Cleared preference : " + filename[i]);
				
			} catch (Exception e) {
				Log.e("-----","Exception in clearPrefernces [" + filename[i]
						+ "] : " + e.getMessage());
			}
		}
	}

	private static void clearDatabase(Context context) {
//		String[] filename = new String[] {
//				RDDAnalyticsDatabaseHelper.TABLE_BATTERY_HISTORY,
//				RDDAnalyticsDatabaseHelper.TABLE_WIFI,
//				RDDAnalyticsDatabaseHelper.TABLE_HANDOVER,
//				RDDAnalyticsDatabaseHelper.TABLE_DISPLAYBRIGHTNESS,
//				RDDAnalyticsDatabaseHelper.TABLE_DISPLAYTIMEOUT,
//				RDDAnalyticsDatabaseHelper.TABLE_WALLPAPERSTATUS,
//				RDDAnalyticsDatabaseHelper.TABLE_CYCLE,
//				RDDAnalyticsDatabaseHelper.TABLE_APPLICATION_BATTERY_DRAIN,
//				RDDAnalyticsDatabaseHelper.TABLE_APPLICATION_VERSION_DRAIN,
//				RDDAnalyticsDatabaseHelper.TABLE_APP_USING_DATAUSAGE_BENCHAMRK,
//				RDDAnalyticsDatabaseHelper.TABLE_APP_USING_DATAUSAGE,
//				RDDAnalyticsDatabaseHelper.TABLE_APPS_USING_GPS_WAKELOCKS_BENCHMARK,
//				RDDAnalyticsDatabaseHelper.TABLE_APPS_USING_GPS_WAKELOCKS, };

		SQLiteDatabase database;

		database = DataUsageDatabaseHelper.openDatabase(context);
		
		ArrayList< String> tableNames = getAllTableNames(database);

		for (int i = 0; i < tableNames.size(); i++) 
		{
			try 
			{
				database.delete(tableNames.get(i), null, null);
				Log.d("-----","Cleared database : " + tableNames.get(i));
			} 
			catch (Exception e) 
			{
				Log.e("-----","Exception in clearDatabase [" + tableNames.get(i) + "] : " + e.getMessage());
			}
		}

		DataUsageDatabaseHelper.closeDatabase();
		database = null;

	}
	
	private static ArrayList<String> getAllTableNames(SQLiteDatabase database)
	{
		ArrayList<String> tableNames = new ArrayList<String>();
		
		try
		{
		Cursor c = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
		if(c != null)
		{
			if (c.moveToFirst())
		    {
				String tableName = null;
				do {
					tableName =c.getString( c.getColumnIndex("name") );
					if(!TextUtils.isEmpty(tableName)  &&  !tableName.equals("android_metadata")){
						tableNames.add(tableName);
					}
				}while (c.moveToNext());
		    }
			c.close();
		}
		}
		catch(Exception e)
		{
			Log.d("-----","Exception in getAllTableNames: " + e.getMessage());
		}

		return tableNames;
	} 

}
