package com.example.test.usagestats.db;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.test.usagestats.bean.AppUsageDTO;
import com.example.test.usagestats.utils.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper { 

	// All Static variables 
	// Database Version 
	private static final int DATABASE_VERSION = 1; 

	// Database Name 
	private static final String DATABASE_NAME = "FamilySafeguard"; 

	// App Usage table name 
	private static final String TABLE_APP_USAGE = "app_usage_info"; 
	// Temp table
	private static final String TABLE_TEMP = "temp_info";
	
	// AppUsage Table Columns names 
	private static final String APP_NAME = "app_name"; 
	private static final String PACKAGE_NAME = "package_name"; 
	private static final String START_TIME = "start_time";
	private static final String END_TIME = "end_time";
	private static final String DURATION = "duration";

	private static final String BG_APP_NAME = "bg_app_name"; 
	private static final String BG_PACKAGE_NAME = "bg_package_name"; 
	private static final String BG_START_TIME = "bg_start_time";
	private static final String BG_END_TIME = "bg_end_time";
	private static final String BG_DURATION = "bg_duration";
	
	private final String TAG = "Family POC Test";
	private Cursor cursor = null;
	private SQLiteDatabase db = null;

	public DatabaseHandler(Context context) { 
		super(context, DATABASE_NAME, null, DATABASE_VERSION); 
	} 

	/**
	 *  Creating Tables 
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			// App Usage Table Creation
			String CREATE_APP_USAGE_TABLE = "CREATE TABLE " + TABLE_APP_USAGE + "("
					+ APP_NAME + " TEXT," + PACKAGE_NAME + " TEXT,"
					+ START_TIME + " TEXT" + ","
					+ END_TIME + " TEXT" + ","
					+ DURATION + " TEXT" + ","
					+ BG_APP_NAME + " TEXT," + BG_PACKAGE_NAME + " TEXT,"
					+ BG_START_TIME + " TEXT" + ","
					+ BG_END_TIME + " TEXT" + ","
					+ BG_DURATION + " TEXT" + ")"; 
			db.execSQL(CREATE_APP_USAGE_TABLE);		
			
			// Temp Table Creation
			String CREATE_TEMP_TABLE = "CREATE TABLE " + TABLE_TEMP + "("
					+ APP_NAME + " TEXT" + ","
					+ BG_APP_NAME + " TEXT" + ")"; 
			db.execSQL(CREATE_TEMP_TABLE);	
			
		}catch(Exception e){
			Log.i(TAG, " Error in Table Creations:"+e);
		}

	} 

	/**
	 *  Upgrading database 
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
		// Drop older tables if existed 
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_APP_USAGE);
		// Create tables again 
		onCreate(db); 
	} 
	// Get Application Usage details
	public ArrayList<AppUsageDTO> getAppUsageDtl(String type) {		
		ArrayList<AppUsageDTO> appUsgList = new  ArrayList<AppUsageDTO>();
		try{			
			db = this.getReadableDatabase();			
			String selectQuery = "SELECT app_name,start_time,end_time,duration FROM app_usage_info where end_time is not null and duration not like '0%' order by "+type;
			cursor = db.rawQuery(selectQuery, null);
			//cursor = db.rawQuery(selectQuery, null);
			Log.i(Utils.TAG, " Query and result of getAppUsageDtl()"+selectQuery+"==="+cursor.getCount());
			if(cursor.getCount()>0){
				if (cursor.moveToFirst()) {				
					do {
						AppUsageDTO info = new AppUsageDTO();
						info.setApp_name(cursor.getString(0));
						info.setStart_time(cursor.getString(1));
						info.setEnd_time(cursor.getString(2));
						info.setTime_spent(cursor.getString(3));
						if(!cursor.getString(3).equals(""))
						appUsgList.add(info);
					} while (cursor.moveToNext());
				} 
			}else{
				AppUsageDTO info = new AppUsageDTO();
				info.setApp_name("No Data Found ");
				info.setStart_time("");
				info.setEnd_time("");
				info.setTime_spent("");
				appUsgList.add(info);
			}

		}catch(Exception e){
			Log.i(TAG,"Error in getSavedWifiDtl()"+e);
		}finally{
			if(cursor!=null)
				cursor.close();
			if(db!=null)
				db.close();
		}
		return appUsgList;
	} 
	///
	// Get Background Application Usage details
		public ArrayList<AppUsageDTO> getBackgroundAppUsageDtl(String type) {		
			ArrayList<AppUsageDTO> appUsgList = new  ArrayList<AppUsageDTO>();
			try{			
				db = this.getReadableDatabase();			
				String selectQuery = "SELECT bg_app_name,bg_start_time,bg_end_time,bg_duration FROM app_usage_info where bg_end_time is not null and bg_duration not like '0%' order by "+type;
				cursor = db.rawQuery(selectQuery, null);
				//cursor = db.rawQuery(selectQuery, null);
				Log.i(Utils.TAG, " Query and result of getBackgroundAppUsageDtl()"+selectQuery+"==="+cursor.getCount());
				if(cursor.getCount()>0){
					if (cursor.moveToFirst()) {				
						do {
							AppUsageDTO info = new AppUsageDTO();
							info.setApp_name(cursor.getString(0));
							info.setStart_time(cursor.getString(1));
							info.setEnd_time(cursor.getString(2));
							info.setTime_spent(cursor.getString(3));
							if(!cursor.getString(3).equals(""))
							appUsgList.add(info);
						} while (cursor.moveToNext());
					} 
				}else{
					AppUsageDTO info = new AppUsageDTO();
					info.setApp_name("No Data Found ");
					info.setStart_time("");
					info.setEnd_time("");
					info.setTime_spent("");
					appUsgList.add(info);
				}

			}catch(Exception e){
				Log.i(TAG,"Error in getBackgroundAppUsageDtl()"+e);
			}finally{
				if(cursor!=null)
					cursor.close();
				if(db!=null)
					db.close();
			}
			return appUsgList;
		} 
	

	// Get Running Applications Info
	public ArrayList<AppUsageDTO> getRunningAppDtl(String type) {		
		ArrayList<AppUsageDTO> appUsgList = new  ArrayList<AppUsageDTO>();
		try{			
			db = this.getReadableDatabase();			
			//String selectQuery = "select app_name,start_time from (select distinct app_name,start_time from app_usage_info where end_time is null) group by app_name";
			String selectQuery = "select app_name,start_time,end_time from app_usage_info group by app_name having max(start_time)";
			cursor = db.rawQuery(selectQuery, null);
			//cursor = db.rawQuery(selectQuery, null);			
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Calendar date = Calendar.getInstance();			
			String cal = dateFormat.format(date.getTime());	
			if(cursor.getCount()>0){
				if (cursor.moveToFirst()) {				
					do {
						AppUsageDTO info = new AppUsageDTO();
						info.setApp_name(cursor.getString(0));
						info.setStart_time(cursor.getString(1));
						info.setEnd_time("");
						String duration = Utils.getTimeDiff(cursor.getString(1), cal);
						String end_time = cursor.getString(2);
						//Log.d("------","---Running:::"+cursor.getString(0)+"------"+cursor.getString(1)+"-----"+cursor.getString(2));
						info.setTime_spent(duration);
						if(end_time==null)
							appUsgList.add(info);
					} while (cursor.moveToNext());
				} 
			}else{
				AppUsageDTO info = new AppUsageDTO();
				info.setApp_name("No Data Found ");
				info.setStart_time("");
				info.setEnd_time("");
				info.setTime_spent("");
				appUsgList.add(info);
			}

		}catch(Exception e){
			Log.i(TAG,"Error in getRunningAppDtl()"+e);
		}finally{
			if(cursor!=null)
				cursor.close();
			if(db!=null)
				db.close();
		}
		return appUsgList;
	} 
	// Clear AppUsage Table
	public ArrayList<AppUsageDTO> removeAppUsageInfo(){
		ArrayList<AppUsageDTO> appUsgList = new  ArrayList<AppUsageDTO>();
		try{
			db = this.getWritableDatabase();
			String selectQuery =" delete  from app_usage_info";
			db.execSQL(selectQuery);
			AppUsageDTO info = new AppUsageDTO();
			info.setApp_name("No Data Found");
			info.setStart_time("");
			info.setEnd_time("");
			info.setTime_spent("");
			appUsgList.add(info);
			Log.i(TAG," Query for removeAppUsageInfo()"+selectQuery);
		}catch(Exception e){
			Log.i(TAG,"Error in removeAppUsageInfo()"+e);
		}finally{
			if(db!=null)
				db.close();
		}
		return appUsgList;
	}	
}