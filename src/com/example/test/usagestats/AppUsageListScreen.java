package com.example.test.usagestats;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.test.usagestats.adapter.AppUsageListAdapter;
import com.example.test.usagestats.bean.AppUsageDTO;
import com.example.test.usagestats.db.DatabaseHandler;
import com.example.test.usagestats.utils.Utils;


public class AppUsageListScreen extends Activity {

	private ListView maListViewPerso;

	AppUsageDTO accountData;
	AppUsageListAdapter accountListAdapter;
	AppUsageListScreen accountListActivity;	
	DatabaseHandler db = null;
	String key = "";
	TextView textView1;
	private boolean flag = false;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try{	
			super.onCreate(savedInstanceState);
			//Remove title bar 
			//this.requestWindowFeature(Window.FEATURE_NO_TITLE);

			//Remove notification bar
			//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			Bundle extras = getIntent().getExtras();
			if (extras!= null) {
				key = extras.get("key").toString();
			}
			setContentView(R.layout.custom_view);
			textView1 = (TextView)findViewById(R.id.textView1);
			accountListActivity = this;

			maListViewPerso = (ListView) findViewById(R.id.listviewperso);
			db = new DatabaseHandler(this);
			if(key.equals("appUsage")){
				flag = false;
				textView1.setText("App Usage Info");
				accountListAdapter = new AppUsageListAdapter(accountListActivity, db.getAppUsageDtl("app_name"),false);	
			}else if(key.equals("runningApps")){
				flag = true;
				textView1.setText("Running Apps Info");
				accountListAdapter = new AppUsageListAdapter(accountListActivity,getInstalledApps(false),true);
			}else if(key.equals("bgApps")){
				flag = false;
				textView1.setText("Background App Usage Info");
				accountListAdapter = new AppUsageListAdapter(accountListActivity, db.getBackgroundAppUsageDtl("app_name"),false);	
			}

			maListViewPerso.setAdapter(accountListAdapter);

			maListViewPerso.setOnItemClickListener(new OnItemClickListener() {
				@Override
				@SuppressWarnings("unchecked")
				public void onItemClick(AdapterView<?> a, View v, int position, long id) {
					//ent(AccountListScreen.this, AccountHome.class));
				}
			});
		}catch(Exception e){
			Log.e(Utils.TAG,"---AppUsageListScreen oncreate()"+e);
		}

	}
	/**
	 * Function to get allInstalledApplications in the device
	 * @param getSysPackages
	 * @return
	 */
	private ArrayList<AppUsageDTO> getInstalledApps(boolean getSysPackages) {

		ArrayList<AppUsageDTO> res = new ArrayList<AppUsageDTO>();        
		try{


			List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
			for(int i=0;i<packs.size();i++) {
				PackageInfo p = packs.get(i);
				if ((!getSysPackages) && (p.versionName == null)) {
					continue ;
				}

				AppUsageDTO newInfo = new AppUsageDTO();	     
				newInfo.app_name = p.applicationInfo.loadLabel(getPackageManager()).toString();	  
				newInfo.setStart_time("");
				newInfo.setEnd_time("");
				res.add(newInfo);
			}
		}catch(Exception e){

		}
		return res; 
	}
	/**
	 * This function will give list of RunningProcessInfo both Cached and Running process 
	 */
	
	private ArrayList<AppUsageDTO> getRunningProcessInfo(){
		
		final PackageManager pm = this.getPackageManager();	
		ArrayList<AppUsageDTO> res = new ArrayList<AppUsageDTO>();        
		ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
		List l = am.getRunningAppProcesses();
		Iterator i = l.iterator();
		SQLiteDatabase db = null;

		try{		
			while(i.hasNext()) {
				ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
				try {
					CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.SIGNATURE_MATCH));						
					AppUsageDTO newInfo = new AppUsageDTO();	
					newInfo.app_name = c.toString();	
					newInfo.setStart_time("");
					newInfo.setEnd_time("");
					res.add(newInfo);
					Log.d(Utils.TAG,"------ app name from getRunningProcessInfo()"+c.toString());					
				}catch(Exception e) {
					Log.e(Utils.TAG," Error at getRunningProcessInfo()" +e);
				}finally{
					if(db!=null)
						db.close();
				}

			}	

		}catch(Exception e) {
			//Name Not FOund Exception
		}finally{
			if(db!=null)
				db.close();
		}
		return res; 
	}

	private String formatData(long data) {
		Date d = new Date(data);
		DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = format2.format(d);
		return str;
	}

	/**
	 * This will give results of allRunningServicesInfo
	 * @param context
	 * @return 
	 */
	private ArrayList<AppUsageDTO> getRunningServicesInfo(Context context) {
		ArrayList<AppUsageDTO> res = new ArrayList<AppUsageDTO>(); 
		StringBuffer serviceInfo = new StringBuffer();
		final PackageManager pm = this.getPackageManager();	
		try{
			ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
	           List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(50);
	           String message = null;

	        for (int i=0; i<rs.size(); i++) {
	              ActivityManager.RunningServiceInfo
	              rsi = rs.get(i);
	              AppUsageDTO newInfo = new AppUsageDTO();	
					newInfo.app_name = rsi.service.getClassName();
					newInfo.setStart_time("");
					newInfo.setEnd_time("");
					res.add(newInfo);
	            //  Log.i("Service", "Process " + rsi.process + " with component " + rsi.service.getClassName());
	     //message =message+rsi.process ;
	            }

			/*List<RunningServiceInfo> services = activityManager
					.getRunningServices(100);

			Iterator<RunningServiceInfo> l = services.iterator();
			while (l.hasNext()) {
				RunningServiceInfo si = (RunningServiceInfo) l.next();
				CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(si.process, PackageManager.GET_META_DATA));
				AppUsageDTO newInfo = new AppUsageDTO();	
				newInfo.app_name = si.process;
				newInfo.setStart_time("");
				newInfo.setEnd_time("");
				res.add(newInfo);
				serviceInfo.append("pid: ").append(si.pid);
				serviceInfo.append("\nprocess: ").append(si.process);
				serviceInfo.append("\nservice: ").append(si.service);
				serviceInfo.append("\ncrashCount: ").append(si.crashCount);
				serviceInfo.append("\nclientCount: ").append(si.clientCount);
				serviceInfo.append("\nactiveSince: ").append(
						formatData(si.activeSince));
				serviceInfo.append("\nlastActivityTime: ").append(
						formatData(si.lastActivityTime));
				serviceInfo.append("\n\n");
				Log.d("----","-----:"+si.toString());
			}
*/
		}catch(Exception e){

		}

		return res;
	}

	@Override  
	public boolean onCreateOptionsMenu(Menu menu) {  
		// Inflate the menu; this adds items to the action bar if it is present.  
		if(flag){
			getMenuInflater().inflate(R.menu.app_types, menu);//Menu Resource, Menu  
		}else{
			getMenuInflater().inflate(R.menu.main, menu);//Menu Resource, Menu  
		}

		return true;  
	}  

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		return super.onPrepareOptionsMenu(menu);
	}
	@Override  
	public boolean onOptionsItemSelected(MenuItem item) {  
		switch (item.getItemId()) {  
		case R.id.item1:  
			if(flag){
				accountListAdapter = new AppUsageListAdapter(accountListActivity, getInstalledApps(false),true);
				maListViewPerso.setAdapter(accountListAdapter);			  
			}else{
				accountListAdapter = new AppUsageListAdapter(accountListActivity, db.getAppUsageDtl("app_name"),false);
				maListViewPerso.setAdapter(accountListAdapter);			  
			}

			return true;     
		case R.id.item2: 
			if(flag){
				accountListAdapter = new AppUsageListAdapter(accountListActivity,getRunningProcessInfo() ,true);
				maListViewPerso.setAdapter(accountListAdapter);			  
			}else{
				accountListAdapter = new AppUsageListAdapter(accountListActivity, db.getAppUsageDtl("start_time"),false);
				maListViewPerso.setAdapter(accountListAdapter); 		  
			}

			return true;     
		case R.id.item3:
			if(flag){
				accountListAdapter = new AppUsageListAdapter(accountListActivity,getRunningServicesInfo(this),true);
				maListViewPerso.setAdapter(accountListAdapter);			  
			}else{
				displayDialogue();  
			}
			
			return true;     
		default:  
			return super.onOptionsItemSelected(item);  
		} 

	}
	/**
	 * Yes or No confirmation dialogue to delete the database or not
	 */
	private void displayDialogue(){

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Confirm");
		builder.setMessage("Are you sure you want to clear data?");

		builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// Do nothing but close the dialog
				accountListAdapter = new AppUsageListAdapter(accountListActivity, db.removeAppUsageInfo(),false);
				maListViewPerso.setAdapter(accountListAdapter);
				dialog.dismiss();
			}

		});

		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do nothing
				dialog.dismiss();
			}
		});
		builder.show();
	}
}
