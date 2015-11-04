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



public class MyLocation extends Activity {

	private PendingIntent pendingIntent;
	private ArrayList<String> current = new ArrayList<String>();
	private DatabaseHandler dbhelper;	
	private Cursor cursor = null;
	private WebView webview;
	
@Override
public void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);

  setContentView(R.layout.my_location);

  webview = (WebView) findViewById(R.id.webview);
  webview.getSettings().setJavaScriptEnabled(true);
  webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
  webview.getSettings().setGeolocationEnabled(true);  


  webView.setWebChromeClient(new WebChromeClient() {
    public void onGeolocationPermissionsShowPrompt(
      String origin, 
      GeolocationPermissions.Callback callback) {
        callback.invoke(origin, true, false);
      }
  });
  webview.loadUrl(geoWebsiteURL);                    
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
		@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		finish();
	}

}

