package com.example.test.usagestats;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.test.usagestats.db.DataUsageDatabaseHelper;
import com.example.test.usagestats.db.DatabaseHandler;
import com.example.test.usagestats.receiver.AlarmReceiver;
import com.example.test.usagestats.utils.Utils;



public class MyActivity extends Activity {

	private PendingIntent pendingIntent;
	private ArrayList<String> current = new ArrayList<String>();
	private DatabaseHandler dbhelper;	
	private Cursor cursor = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			// remove title 
	      //  requestWindowFeature(Window.FEATURE_NO_TITLE);
	        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	          //  WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.activity_main);

			/* Retrieve a PendingIntent that will perform a broadcast */
			Intent alarmIntent = new Intent(MyActivity.this, AlarmReceiver.class);
			pendingIntent = PendingIntent.getBroadcast(MyActivity.this, 0, alarmIntent, 0);
			//new DatabaseHandler(this);
			dbhelper = new DatabaseHandler(this);
			new DataUsageDatabaseHelper(this);		
			findViewById(R.id.startAlarm).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					start();								
				}
			});

findViewById(R.id.trackLocation)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//start();								Intent loc = new Intent(MyActivity.this,MyLocation.class);
startActivity(loc);
				}
			});

			findViewById(R.id.stopAlarm).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					cancel();
					findViewById(R.id.startAlarm).setEnabled(true);
				}
			});

			findViewById(R.id.appUsage).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Display appUsage things				
					Intent i = new Intent(MyActivity.this,AppUsageListScreen.class);
					i.putExtra("key", "appUsage");
					startActivity(i);
				}
			});
			findViewById(R.id.bgApps).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Display appUsage things				
					Intent i = new Intent(MyActivity.this,AppUsageListScreen.class);
					i.putExtra("key", "bgApps");
					startActivity(i);
				}
			});
			findViewById(R.id.runningApps).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {					
					Utils.val = "app";
					Intent i = new Intent(MyActivity.this,AppUsageListScreen.class);
					i.putExtra("key", "runningApps");
					startActivity(i);
				}
			});
			findViewById(R.id.dataUsage).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//Display dataUsage things
					Utils.val = "data";
					Intent i = new Intent(MyActivity.this,DataUsageListScreen.class);
					startActivity(i);
				}
			});
		}catch(Exception e){
			Log.e(Utils.TAG,"-------MyActivity onCreate()"+e);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();		
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();		
	}	
	@SuppressLint("NewApi")
	public void start() {
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		//int interval = 55000;
		int interval = 2500;

		manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
		Toast.makeText(this, "Alarm Started", Toast.LENGTH_LONG).show();
	}

	public void cancel() {
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		manager.cancel(pendingIntent);
		Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
	}

	public void startAt10() {
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		//  int interval = 1000 * 60 * 20;

		/* Set the alarm to start at 10:30 AM */
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 12);
		calendar.set(Calendar.MINUTE, 38);

		/* Repeating on every 2 minutes interval */
		manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				60000, pendingIntent);

	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		finish();
	}

}

