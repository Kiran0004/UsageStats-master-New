package com.example.test.usagestats.db;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.test.usagestats.bean.ApplicationDataUsageDTO;
import com.example.test.usagestats.utils.ResetFiles;
import com.example.test.usagestats.utils.Utils;

public class DataUsageDatabaseHelper extends SQLiteOpenHelper {

	static final int DATABASE_VERSION = 2;

	public static final String DATABASE_NAME = "DATA_USAGE_INFO";

	private static DataUsageDatabaseHelper sInstance;
	private static int sDatabaseOpenCount = 0;
	private static SQLiteDatabase sSqliteDatabase = null;

	// Table Names	
	public static final String TABLE_APP_USING_DATAUSAGE_BENCHAMRK = "TableAppUsingDataUsageBenchmark";
	public static final String TABLE_APP_USING_DATAUSAGE = "TableAppUsingDataUsage";
	
	Cursor cursor = null;
	static Context appContext;
	
	public static synchronized SQLiteDatabase openDatabase(Context context) 
	{
		appContext = context;
		if (sDatabaseOpenCount <= 0) 
		{
			try 
			{
					if (sSqliteDatabase != null) //this should not happen.. just in case..
						sSqliteDatabase.close();
					if (sInstance != null) //this should not happen.. just in case..
						sInstance.close();
					sSqliteDatabase = null;
					sInstance = null;
			} 
			catch (Exception e) {
				sSqliteDatabase = null;
				sInstance = null;
				sDatabaseOpenCount = 0;
				Log.e("-----","Exception in openDatabase :" + e.getMessage());
			}
			
			// Opening new database
			sInstance = new DataUsageDatabaseHelper(context);
			sSqliteDatabase = sInstance.getWritableDatabase();
		}
		sDatabaseOpenCount++;
		return sSqliteDatabase;
	}

	public static synchronized void closeDatabase() {
		try {
			if (sDatabaseOpenCount == 1) {
				if (sSqliteDatabase != null)
					sSqliteDatabase.close();
				if (sInstance != null)
					sInstance.close();
				sSqliteDatabase = null;
				sInstance = null;
				sDatabaseOpenCount = 0;
			} else {
				sDatabaseOpenCount--;
			}
		} catch (Exception e) {
			sSqliteDatabase = null;
			sInstance = null;
			sDatabaseOpenCount = 0;
			Log.e("-----","Exception in closeDatabase :" + e.getMessage());
		}
	}

	public DataUsageDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("-----","Creating DB.");
		createDB(db);
	}
	
	private void createDB(SQLiteDatabase db)
	{
		try
		{
			 db.beginTransaction();
			// creating required tables		
			db.execSQL(CREATE_TABLE_DATAUSAGE);
			db.execSQL(CREATE_TABLE_DATAUSAGE_BENCHAMRK);
			
			db.setTransactionSuccessful();
			db.endTransaction();
			
			Log.d("-----","Creating DB succesful.");
		}
		catch(Exception e)
		{
			Log.e("-----","Exception in createDB : " + e.getMessage());
		}
	}
	
	private void clearDB(SQLiteDatabase db)
	{
		try
		{
			ResetFiles.clearPrefernces(appContext);
			 db.beginTransaction();
			//DROP TABLE IF EXISTS tablename			
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_APP_USING_DATAUSAGE);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_APP_USING_DATAUSAGE_BENCHAMRK);
			
			Log.d("-----","clearing DB successful.");
			db.setTransactionSuccessful();
			db.endTransaction();
		}
		catch(Exception e)
		{
			Log.e("-----","Exception in clearDB : " + e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		
	 		Log.d(Utils.TAG,"Database Upgrade failed. Clearing the existing db and creating it agian.");
	    	clearDB(db);
	    	onCreate(db);
	    
	
	}
	
	// Data usage - column names
	static final String KEY_UID = "Uid";	
	static final String KEY_BYTESSENT = "BytesSent";
	static final String KEY_BYTESRECEIVED = "BytesReceived";
	static final String KEY_BYTESTOTAL = "BytesTotal";
	static final String KEY_APP_NAME = "AppName";
	static final String KEY_PKG_NAME = "PkgName";
	static final String KEY_APP_VERSION = "AppVersion";
	static final String KEY_FORMATTED_TIME = "FormattedTime";
	static final String KEY_TIME = "Date";
	static final String KEY_APP_INSTALL_SOURCE = "AppInstallSource";
	static final String KEY_CONNECTION_TYPE = "ConnectionType";
	// Data usage Benchmark table create statement 
	private static final String CREATE_TABLE_DATAUSAGE_BENCHAMRK = "CREATE TABLE "
			+ TABLE_APP_USING_DATAUSAGE_BENCHAMRK
			+ "("
			+ KEY_UID
			+ " INTEGER,"
			+ KEY_APP_NAME
			+ " TEXT,"
			+ KEY_PKG_NAME
			+ " TEXT,"
			+ KEY_APP_VERSION
			+ " TEXT,"
			+ KEY_BYTESSENT
			+ " INTEGER,"
			+ KEY_BYTESRECEIVED + " INTEGER" + ","
			+ KEY_CONNECTION_TYPE + " TEXT" + ")";

	// Data usage table create statement
	private static final String CREATE_TABLE_DATAUSAGE = "CREATE TABLE "
			+ TABLE_APP_USING_DATAUSAGE + "(" + KEY_TIME + " INTEGER,"
			+ KEY_FORMATTED_TIME + " TEXT," + KEY_UID + " INTEGER,"
			+ KEY_APP_NAME + " TEXT," + KEY_PKG_NAME + " TEXT,"
			+ KEY_APP_VERSION + " TEXT," + KEY_APP_INSTALL_SOURCE + " TEXT,"
			+ KEY_BYTESSENT + " INTEGER," + KEY_BYTESRECEIVED + " INTEGER,"
			+ KEY_BYTESTOTAL + " INTEGER" + ","
			+ KEY_CONNECTION_TYPE + " TEXT" + ")";
	private String readableFileSize(long size) {
		if(size <= 0) return "0";
		final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}  
	public ArrayList<ApplicationDataUsageDTO> getDataUsageDtl(String type,String conType) {		
		ArrayList<ApplicationDataUsageDTO> appUsgList = new  ArrayList<ApplicationDataUsageDTO>();
		try{			
			sSqliteDatabase = this.getReadableDatabase();			
			//String selectQuery = "SELECT AppName,BytesSent,BytesReceived,(BytesSent+BytesReceived) AS USER_KARMA  FROM TableAppUsingDataUsageBenchmark order by USER_KARMA desc ";
			//String selectQuery = "SELECT AppName,BytesTotal  FROM TableAppUsingDataUsage group by BytesTotal";
			String selectQuery = "";
			if(conType.equals("WIFI") || conType.equals("MOBILE")){
				selectQuery = "select AppName,sum(BytesTotal) as test,ConnectionType from TableAppUsingDataUsage  where ConnectionType='"+conType+"' and BytesTotal is not null group by AppName order by "+type+" desc";
			}else{
				selectQuery = "select AppName,sum(BytesTotal) as test,ConnectionType from TableAppUsingDataUsage  where BytesTotal is not null group by AppName order by "+type+" desc";
			}
			
			cursor = sSqliteDatabase.rawQuery(selectQuery, null);
			//cursor = db.rawQuery(selectQuery, null);			
			if(cursor.getCount()>0){
				if (cursor.moveToFirst()) {				
					do {
						ApplicationDataUsageDTO info = new ApplicationDataUsageDTO();
						info.setAppname(cursor.getString(0));
						info.setDataUsed(readableFileSize(cursor.getLong(1)));						
						appUsgList.add(info);
					} while (cursor.moveToNext());
				} 
			}else{
				ApplicationDataUsageDTO info = new ApplicationDataUsageDTO();
				info.setAppname("No Data Found");
				info.setDataUsed("");
				appUsgList.add(info);
			}

		}catch(Exception e){
			Log.i("--------------------","Error in getDataUsageDtl()"+e);
		}finally{
			if(cursor!=null)
				cursor.close();
			if(sSqliteDatabase!=null)
				sSqliteDatabase.close();
		}
		return appUsgList;
	} 
}
