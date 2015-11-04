package com.example.test.usagestats;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.test.usagestats.adapter.DataUsageListAdapter;
import com.example.test.usagestats.bean.AppUsageDTO;
import com.example.test.usagestats.db.DataUsageDatabaseHelper;
import com.example.test.usagestats.utils.Utils;


public class DataUsageListScreen extends Activity {

	private ListView maListViewPerso;

	AppUsageDTO accountData;
	DataUsageListAdapter accountListAdapter;
	DataUsageListScreen accountListActivity;	
	DataUsageDatabaseHelper db = null;
	TextView textView;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try{			
			super.onCreate(savedInstanceState);
			//Remove title bar 
			//this.requestWindowFeature(Window.FEATURE_NO_TITLE);

			//Remove notification bar
			//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.custom_view);
			textView = (TextView)findViewById(R.id.textView1);
			textView.setText("Data Usage Details");
			accountListActivity = this;

			maListViewPerso = (ListView) findViewById(R.id.listviewperso);
			db = new DataUsageDatabaseHelper(this);
			accountListAdapter = new DataUsageListAdapter(accountListActivity, db.getDataUsageDtl("sum(BytesTotal)",""));
			maListViewPerso.setAdapter(accountListAdapter);

			maListViewPerso.setOnItemClickListener(new OnItemClickListener() {
				@Override
				@SuppressWarnings("unchecked")
				public void onItemClick(AdapterView<?> a, View v, int position, long id) {
					//ent(AccountListScreen.this, AccountHome.class));
				}
			});
		}catch(Exception e){
			Log.e(Utils.TAG,"---Error in DataUsageListScreen onCreate()"+e);
		}
	}
	@Override  
	public boolean onCreateOptionsMenu(Menu menu) {  
		// Inflate the menu; this adds items to the action bar if it is present.  
		getMenuInflater().inflate(R.menu.datausage_options, menu);//Menu Resource, Menu  
		return true;  
	}  
	@Override  
	public boolean onOptionsItemSelected(MenuItem item) {  
		switch (item.getItemId()) {  
		case R.id.item1:  			
			accountListAdapter = new DataUsageListAdapter(accountListActivity, db.getDataUsageDtl("sum(BytesTotal)","WIFI"));
			maListViewPerso.setAdapter(accountListAdapter);			  
			return true;     
		case R.id.item2:
			accountListAdapter = new DataUsageListAdapter(accountListActivity, db.getDataUsageDtl("sum(BytesTotal)","MOBILE"));
			maListViewPerso.setAdapter(accountListAdapter);							  
			return true;     
		case R.id.item3:  
			
			accountListAdapter = new DataUsageListAdapter(accountListActivity, db.getDataUsageDtl("sum(BytesTotal)",""));
			maListViewPerso.setAdapter(accountListAdapter);		
			//Toast.makeText(getApplicationContext(),"Item 3 Selected",Toast.LENGTH_LONG).show();
			//test();
			return true;     
		default:  
			return super.onOptionsItemSelected(item);  
		}  

	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		finish();
	}

}
