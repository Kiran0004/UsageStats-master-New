package com.example.test.usagestats.receiver;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.test.usagestats.DataUsageStats;
import com.example.test.usagestats.db.DatabaseHandler;
import com.example.test.usagestats.utils.Utils;

public class DeviceBootReceiver extends BroadcastReceiver {
	private Context context;
	private boolean flag = false;
	private ArrayList<String> temp = new ArrayList<String>();
	private ArrayList<String> temp_bg = new ArrayList<String>();
	//private ArrayList<String> current = new ArrayList<String>();
	private DatabaseHandler dbhelper;
	private Cursor cursor = null;
	@Override
	public void onReceive(Context context, Intent intent) {		
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			/* Setting the alarm here */			
			Intent alarmIntent = new Intent(context, AlarmReceiver.class);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

			AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

			int interval = 55000;
			manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
			SQLiteDatabase db = null;
			try{			
				DataUsageStats dataUsage = new DataUsageStats(
						context);
				dataUsage.bootRequestCalled(0, System.currentTimeMillis());
				dataUsage.deIntialize();
			}catch(Exception e){

			}finally{
				if(db!=null)
					db.close();
			}
		}    
		else if (intent.getAction().equals("android.intent.action.ACTION_SHUTDOWN") ||intent.getAction().equals("android.intent.action.QUICKBOOT_POWEROFF")) {
			/* Setting the alarm here */

			// For our recurring task, we'll just display a message
			this.context = context;		
			dbhelper = new DatabaseHandler(context);
			//Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();	
			new Thread(new Runnable() {
				public void run() {
					getRunningAppsInfo();					
				}
			}).start();
		}    

	}
	/**
	 * Saving the NewApp names into temp_info table to check further whether the app is running or not
	 * @return appName 
	 */
	private void getTempInfo(){

		SQLiteDatabase db = null;
		try{						
			db = dbhelper.getWritableDatabase();
			String selectQuery = "SELECT app_name FROM temp_info where app_name is not null";
			cursor = db.rawQuery(selectQuery, null);						
			if(cursor.getCount()>0){
				if (cursor.moveToFirst()) {				
					do {						
						temp.add(cursor.getString(0));						
					} while (cursor.moveToNext());
				} 
			}
			cursor.close();
			selectQuery = "SELECT bg_app_name FROM temp_info where bg_app_name is not null";
			cursor = db.rawQuery(selectQuery, null);						
			if(cursor.getCount()>0){
				if (cursor.moveToFirst()) {				
					do {						
						temp_bg.add(cursor.getString(0));						
					} while (cursor.moveToNext());
				} 
			}
		}catch(Exception e){

		}finally{
			if(cursor!=null){
				cursor.close();
				if(db!=null)
					db.close();
			}
		}
	}
	private void getRunningAppsInfo(){		
		SQLiteDatabase db = null;
		// First time checking 
		try{		
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Calendar date = Calendar.getInstance();			
			String cal = dateFormat.format(date.getTime());		
			getTempInfo();			
				for(int j=0;j<temp.size();j++){
					Log.d("---------------","-------------------111");
					try{
						db = dbhelper.getWritableDatabase();
						String start_time = "",end_time="",duration="";
						String selectQuery = "SELECT start_time FROM app_usage_info where end_time is null and app_name='"+temp.get(j).toString()+"'";
						cursor = db.rawQuery(selectQuery, null);						
						if(cursor.getCount()>0){
							if (cursor.moveToFirst()) {				
								do {								
									start_time = cursor.getString(0);									
								} while (cursor.moveToNext());
							} 
						}
						//db.close();
						//db = dbhelper.getWritableDatabase();
						Log.d("---------------","-------------------222");
						end_time = dateFormat.format(date.getTime()).toString();					
						duration = Utils.getTimeDiff(start_time,end_time);
						selectQuery = "update app_usage_info set duration='"+duration+"',end_time='"+end_time+"' where end_time is null and app_name='"+temp.get(j).toString()+"' and start_time='"+start_time+"'";					
						db.execSQL(selectQuery);	
						selectQuery = " delete from temp_info where app_name='"+temp.get(j).toString()+"'";
						db.execSQL(selectQuery);
					}catch(Exception e){
						Log.i("==="," Error in app_usage_info()"+e);
					}finally{
						if(cursor!=null)
							cursor.close();
						if(db!=null)
							db.close();
					}
					Log.i(Utils.TAG,"----FG App Ended:::"+temp.get(j).toString());				
				}
				for(int k=0;k<temp_bg.size();k++){
					try{
						db = dbhelper.getWritableDatabase();
						String start_time = "",end_time="",duration="";
						String selectQuery = "SELECT bg_start_time FROM app_usage_info where bg_end_time is null and bg_app_name='"+temp_bg.get(k).toString()+"'";
						cursor = db.rawQuery(selectQuery, null);						
						if(cursor.getCount()>0){
							if (cursor.moveToFirst()) {				
								do {								
									start_time = cursor.getString(0);									
								} while (cursor.moveToNext());
							} 
						}
						Log.d("---------------","-------------------333");
						//db.close();
						//db = dbhelper.getWritableDatabase();
						end_time = dateFormat.format(date.getTime()).toString();					
						duration = Utils.getTimeDiff(start_time,end_time);
						selectQuery = "update app_usage_info set bg_duration='"+duration+"',bg_end_time='"+end_time+"' where bg_end_time is null and bg_app_name='"+temp_bg.get(k).toString()+"' and bg_start_time='"+start_time+"'";					
						db.execSQL(selectQuery);	
						selectQuery = " delete from temp_info where bg_app_name='"+temp_bg.get(k).toString()+"'";
						db.execSQL(selectQuery);
					}catch(Exception e){
						Log.i("==="," Error in app_usage_info()"+e);
					}finally{
						if(cursor!=null)
							cursor.close();
						if(db!=null)
							db.close();
					}
					Log.i(Utils.TAG,"---- BG App Ended:::"+temp_bg.get(k).toString());				
				}
		//	}
		
		}catch(Exception e) {
			Log.e(Utils.TAG,"--Error in DeviceBootReceiver  getRunningAppsInfo()"+e);
		}finally{
			if(db!=null)
				db.close();
		}
	}	
}

