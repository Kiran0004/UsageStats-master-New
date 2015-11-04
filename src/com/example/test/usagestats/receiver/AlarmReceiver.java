package com.example.test.usagestats.receiver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.util.Log;

import com.example.test.usagestats.DataUsageStats;
import com.example.test.usagestats.bean.AppUsageDTO;
import com.example.test.usagestats.db.DataUsageDatabaseHelper;
import com.example.test.usagestats.db.DatabaseHandler;
import com.example.test.usagestats.utils.Utils;

public class AlarmReceiver extends BroadcastReceiver {

	private Context context;
	private ArrayList<String> current = new ArrayList<String>();
	private ArrayList<String> temp = new ArrayList<String>();

	// Just for Background things check
	private ArrayList<String> current_bg = new ArrayList<String>();
	private ArrayList<String> temp_bg = new ArrayList<String>();
	private ArrayList<String> music_apps = new ArrayList<String>();
	private DatabaseHandler dbhelper;	
	private Cursor cursor = null;
	private SQLiteDatabase db = null;
	private PackageManager pm;
	@Override
	public void onReceive(Context context, Intent intent) {

		// For our recurring task, we'll just display a message 
		this.context = context;		
		//isAppRunning(context);
		pm = context.getPackageManager();
		dbhelper = new DatabaseHandler(context);
		new DataUsageDatabaseHelper(context);		
		getInstalledMusicApps();
		listOfRunningTasks();		
		getDataUsageDetails();
		Log.d("-----","=====Test::::");
	}
	private ArrayList<String> getInstalledMusicApps(){				
		Intent intent1 = new Intent(Intent.ACTION_MEDIA_BUTTON);
		List<ResolveInfo> appsList = pm.queryBroadcastReceivers(intent1, 0);
		for(int i=0;i<appsList.size();i++){
			music_apps.add(appsList.get(i).activityInfo.loadLabel(pm).toString());		
		}
		return music_apps;
	}
	private void getDataUsageDetails(){
		SQLiteDatabase db = null;
		try{			
			DataUsageStats dataUsage = new DataUsageStats(
					context);
			dataUsage.saveApplicationDataUsage(0, System.currentTimeMillis());
			dataUsage.deIntialize();
		}catch(Exception e){

		}finally{
			if(db!=null)
				db.close();
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

	/**
	 * This function will give currentForegroundApplicationName, saving the details in DB 
	 * 
	 */
	private void listOfRunningTasks()
	{
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);	
		//List<RunningTaskInfo> runningTaskInfo = am.getRunningTasks(Integer.MAX_VALUE); 
		//ApplicationInfo applicationInfo = null;	
		//ArrayList<AppUsageDTO> res = new ArrayList<AppUsageDTO>();
		List running_process = am.getRunningAppProcesses();
		Iterator ik = running_process.iterator();
		SQLiteDatabase db = null;
		// First time checking 
		try{		
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Calendar date = Calendar.getInstance();				
			// Below Commented code will give all listOfRunningApplications (ForeGround and Background also)
			while(ik.hasNext()) {
				ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(ik.next());
				try {					
					CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.SIGNATURE_MATCH));
					current_bg.add(c.toString());
				}catch(Exception e) {
					//Log.d("------","---Error:"+e);
					//Name Not FOund Exception
				}

			}
			RunningTaskInfo info = null;
			List<RunningTaskInfo> l = am.getRunningTasks(1000);	        
			Iterator <RunningTaskInfo> i = l.iterator();
			String packName = new String();
			while(i.hasNext()){
				info = i.next();
				packName = info.topActivity.getPackageName();
				String taskName = info.baseActivity.toShortString();	  
				int lastIndex = taskName.indexOf("/");
				if(-1 != lastIndex)
				{
					taskName = taskName.substring(1,lastIndex);
				}			
				try 
				{
				}catch(Exception e){}
				PackageInfo foregroundAppPackageInfo = pm.getPackageInfo(info.topActivity.getPackageName(), 0);
				String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo.loadLabel(pm).toString();
				if(!packName.equals("com.htc.launcher") && !packName.equals("com.android.launcher")){ 
					packName = foregroundTaskAppName;
					current.add(packName);	
					break;
				}
				info = i.next();
				packName= foregroundTaskAppName;
				break;          
			}
			getTempInfo();
			db = dbhelper.getWritableDatabase();
			for(int m=0;m<temp_bg.size();m++){
				try{
					AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);					
					if(current_bg.contains(temp_bg.get(m))){
						if(music_apps.contains(temp_bg.get(m)))
						{
							if(manager.isMusicActive())
							{								
							}else{								
								insertData("bg_start_time", "bg_end_time", "bg_app_name", temp_bg.get(m).toString(), "bg_duration", "bg_start_time", "bg_end_time"," Background");
							}
						}
					}else{
						if(music_apps.contains(temp_bg.get(m)))
						{
							if(manager.isMusicActive())
							{	

							}else{									
								insertData("bg_start_time", "bg_end_time", "bg_app_name", temp_bg.get(m).toString(), "bg_duration", "bg_start_time", "bg_end_time"," Background");
							}
						}
						else{								
							insertData("bg_start_time", "bg_end_time", "bg_app_name", temp_bg.get(m).toString(), "bg_duration", "bg_start_time", "bg_end_time"," Background");
							break;
						}


					}
				}catch(Exception e){
					Log.e(Utils.TAG," Error in Bg Apps Updation:"+e);					
				}finally{
					if(cursor!=null)
						cursor.close();
					//	if(db!=null)
					//	db.close();
				}
			}
			for(int k=0;k<current.size();k++){				
				if(temp.contains(current.get(k))){		

				}else{
					try{
						//db = dbhelper.getWritableDatabase();
						ContentValues values = new ContentValues(); 
						values.put("app_name",current.get(k).toString()); 			 
						values.put("package_name", current.get(k).toString()); 	    
						values.put("start_time", dateFormat.format(date.getTime()).toString());
						// Inserting Row 
						db.insert("app_usage_info", null, values);		
						// Newly Started APPlication need to take start time
						Log.i(Utils.TAG,"-----New APP::"+current.get(k).toString());
						//Utils.temp.add(current.get(k).toString());
						values = new ContentValues(); 
						values.put("app_name",current.get(k).toString()); 			
						db.insert("temp_info", null, values);								
						if(temp_bg.contains(current.get(k))){
							// Need to delete the app from Background because it's came foreground
							AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

							if(music_apps.contains(current.get(k)))
							{									
								if(manager.isMusicActive())
								{	
								}else{										
									insertData("bg_start_time", "bg_end_time", "bg_app_name", current.get(k).toString(), "bg_duration", "bg_start_time", "bg_end_time"," App came to Foreground change");
								}
							}
							else{								
								insertData("bg_start_time", "bg_end_time", "bg_app_name", current.get(k).toString(), "bg_duration", "bg_start_time", "bg_end_time"," App came to Foreground change");								
							}													
						}
					}catch(Exception e) {
						Log.e(Utils.TAG,"------Error here:"+e);
					}finally{
						//if(db!=null)
						//	db.close();
					}
				}
			}			
			for(int j=0;j<temp.size();j++){				
				if(current.contains(temp.get(j))){
					// App is running					
				}else{
					// App is closed updating endTime in the db and removing the app name from temp table  
					try{						
						//db = dbhelper.getWritableDatabase();
						insertData("start_time", "end_time", "app_name", temp.get(j).toString(), "duration", "start_time", "end_time"," General Case");
						ContentValues values = new ContentValues(); 
						values.put("bg_app_name",temp.get(j).toString()); 			 
						values.put("bg_package_name", temp.get(j).toString()); 	    
						values.put("bg_start_time", dateFormat.format(date.getTime()).toString());					
						db.insert("app_usage_info", null, values);							
						Log.i(Utils.TAG,"-----BackGround APP::"+temp.get(j).toString());					
						values = new ContentValues(); 
						values.put("bg_app_name",temp.get(j).toString()); 			
						db.insert("temp_info", null, values);		
					}catch(Exception e){
						Log.i(Utils.TAG," Error in app_usage_info()"+e);
					}finally{
						if(cursor!=null)
							cursor.close();
						//if(db!=null)
						//db.close();
					}
					Log.i(Utils.TAG,"----App Ended:::"+temp.get(j).toString());						
				}	
			}
		}catch(Exception e) {		
			//Name Not FOund Exception
		}finally{
			if(db!=null)
				db.close();
		}		
	}

	private void insertData(String column_name,String where_name,String app_column,String app_name,String duration_column,String start_time_column,String end_time_column,String condition){
		try{
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Calendar date = Calendar.getInstance();	
			String start_time = "",end_time="",duration="";
			String selectQuery = "SELECT "+column_name+" FROM app_usage_info where "+where_name+"  is null and "+app_column+" ='"+app_name+"'";
			db = dbhelper.getWritableDatabase();
			cursor = db.rawQuery(selectQuery, null);
			if(cursor.getCount()>0){
				if (cursor.moveToFirst()) {				
					do {						
						start_time = cursor.getString(0);
					} while (cursor.moveToNext());
				} 
			}
			end_time = dateFormat.format(date.getTime()).toString();			
			duration = Utils.getTimeDiff(start_time,end_time);			
			selectQuery = "update app_usage_info set "+duration_column+" ='"+duration+"',"+end_time_column+"='"+end_time+"' where "+end_time_column+" is null and "+app_column+"='"+app_name+"' and "+start_time_column+"='"+start_time+"'";
			db.execSQL(selectQuery);
			Log.i(Utils.TAG," Query for Background app_usage_info()"+selectQuery);
			selectQuery = " delete from temp_info where "+app_column+"='"+app_name+"'";
			db.execSQL(selectQuery);
			Log.i(Utils.TAG," Query for delete app_usage_info()"+selectQuery);
		}catch(Exception e){
			Log.e(Utils.TAG," Error in" +condition+" Updation:"+e);					
		}
		/*finally{
			if(cursor!=null)
				cursor.close();
			if(db!=null)
				db.close();
		}*/
	}
}
