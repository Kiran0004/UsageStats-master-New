package com.example.test.usagestats;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;

import com.example.test.usagestats.db.DataUsageDatabaseHelper;
import com.example.test.usagestats.db.DatabaseHandler;
import com.example.test.usagestats.receiver.AlarmReceiver;

public class SplashScreenActivity extends Activity {
	String name;
	//LoginDataBaseAdapter logindatabase;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// remove title 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
		
		/* Retrieve a PendingIntent that will perform a broadcast */
		Intent alarmIntent = new Intent(SplashScreenActivity.this, AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(SplashScreenActivity.this, 0, alarmIntent, 0);
		//new DatabaseHandler(this);
		DatabaseHandler dbhelper = new DatabaseHandler(this);
		new DataUsageDatabaseHelper(this);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SplashScreenActivity.this);
		 name = preferences.getString("check", "");
//		 
		
		if(name!=null && name!=""){
			
			//public void start() {
				AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				//int interval = 55000;
				int interval = 2500;

				manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
				//Toast.makeText(this, "Alarm Started", Toast.LENGTH_LONG).show();
			//}

		}
		

		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				
				
					 startActivity(new Intent(SplashScreenActivity.this,
								MyActivity.class));
					 
					 finish();
			}
		}, 5000);
	}

}
