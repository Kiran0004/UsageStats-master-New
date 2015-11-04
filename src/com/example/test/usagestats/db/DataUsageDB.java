package com.example.test.usagestats.db;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.test.usagestats.bean.ApplicationDataUsageDTO;
import com.example.test.usagestats.utils.Utils;

public class DataUsageDB {

	private SQLiteDatabase database;
	private Context context;
	
	public DataUsageDB(Context context) {
		this.context = context;
	}

	public void openDB() {
		database = DataUsageDatabaseHelper.openDatabase(context);
	}

	public void closeDB() {
		DataUsageDatabaseHelper.closeDatabase();
	}

	public boolean insertApplicationDataUsageInBenchMarkTable(
			List<ApplicationDataUsageDTO> dtos) {
		clearBenchMarkTable();

		boolean flag = false;
		try {

			String sql = "Insert into "
					+ DataUsageDatabaseHelper.TABLE_APP_USING_DATAUSAGE_BENCHAMRK
					+ " (" +

					DataUsageDatabaseHelper.KEY_UID + ","
					+ DataUsageDatabaseHelper.KEY_APP_NAME + ","
					+ DataUsageDatabaseHelper.KEY_PKG_NAME + ","
					+ DataUsageDatabaseHelper.KEY_APP_VERSION + ","
					+ DataUsageDatabaseHelper.KEY_BYTESSENT + ","
					+ DataUsageDatabaseHelper.KEY_BYTESRECEIVED + ","
					+ DataUsageDatabaseHelper.KEY_CONNECTION_TYPE + ") "
					+ "values(?,?,?,?,?,?,?)";

			SQLiteStatement insert = database.compileStatement(sql);

			ApplicationDataUsageDTO dto;
			int size = dtos.size();

			Log.d("-----","Adding : " + size + " Records");

			database.beginTransaction();
			for (int i = 0; i < size; i++) {
				dto = dtos.get(i);

				insert.bindLong(1, dto.getUID());
				insert.bindString(2, dto.getAppname());
				insert.bindString(3, dto.getPackagename());
				insert.bindString(4, dto.getVersion());
				insert.bindLong(5, dto.getBytesSent());
				insert.bindLong(6, dto.getBytesReceived());
				insert.bindString(7, dto.getNetworkType());				
				insert.execute();
			}

			database.setTransactionSuccessful();
			database.endTransaction();
			Log.d("-----","Added DataUsageInBenchMarkTable : " + size + " Records");
			flag = true;

		} catch (Exception e) {
			flag = false;
			Log.e("-----","Exception in insertApplicationDataUsageInBenchMarkTable :"
					+ e.getMessage());
		}

		return flag;
	}

	public void clearBenchMarkTable() {
		int count = database.delete(
				DataUsageDatabaseHelper.TABLE_APP_USING_DATAUSAGE_BENCHAMRK,
				null, null);

		Log.d("-----","Deleted : " + count + " records from Bench Mark Table");
	}

	public void deleteApplicationDataUsageFor(long date) {
		int count = database.delete(
				DataUsageDatabaseHelper.TABLE_APP_USING_DATAUSAGE,
				DataUsageDatabaseHelper.KEY_TIME + " = " + date, null);

		Log.d("-----","Deleted : " + count + " records from DataUsage Table");
	}

	public HashMap<Integer, ApplicationDataUsageDTO> getApplicationDataUsageFor(
			long date) {

		HashMap<Integer, ApplicationDataUsageDTO> map = new HashMap<Integer, ApplicationDataUsageDTO>();

		try {
			String sql = "Select * from "
					+ DataUsageDatabaseHelper.TABLE_APP_USING_DATAUSAGE
					+ " Where " + DataUsageDatabaseHelper.KEY_TIME + "="
					+ date;

			Cursor cursor = database.rawQuery(sql, null);

			if (cursor != null) {

				if (cursor.moveToFirst()) {
					ApplicationDataUsageDTO dto = null;
					do {
						dto = new ApplicationDataUsageDTO();
						dto.setUID(cursor.getInt(2));
						dto.setAppname(cursor.getString(3));
						dto.setPackagename(cursor.getString(4));
						dto.setVersion(cursor.getString(5));
						dto.setSource(cursor.getString(6));
						dto.setBytesSent(cursor.getLong(7));
						dto.setBytesReceived(cursor.getLong(8));
						dto.setNetworkType(cursor.getString(10));						
						map.put(dto.getUID(), dto);

					} while (cursor.moveToNext());

				}
			}
		} catch (Exception e) {
			Log.e("-----","Exception in getApplicationDataUsageFromBenchMarkTable :"
					+ e.getMessage());

		}

		return map;
	}

	public HashMap<Integer, ApplicationDataUsageDTO> getApplicationDataUsageFromBenchMarkTable() {

		HashMap<Integer, ApplicationDataUsageDTO> map = new HashMap<Integer, ApplicationDataUsageDTO>();

		try {
			String sql = "Select * from "
					+ DataUsageDatabaseHelper.TABLE_APP_USING_DATAUSAGE_BENCHAMRK;

			Cursor cursor = database.rawQuery(sql, null);

			if (cursor != null) {

				if (cursor.moveToFirst()) {
					ApplicationDataUsageDTO dto = null;
					do {
						dto = new ApplicationDataUsageDTO();
						dto.setUID(cursor.getInt(0));
						dto.setAppname(cursor.getString(1));
						dto.setPackagename(cursor.getString(2));
						dto.setVersion(cursor.getString(3));
						dto.setBytesSent(cursor.getLong(4));
						dto.setBytesReceived(cursor.getLong(5));
						dto.setNetworkType(cursor.getString(6));						
						map.put(dto.getUID(), dto);

//						Log.d("-----","BM Name : " + dto.getAppname() + " Sent : "
//								+ dto.getBytesSent() + " Rec : "
//								+ dto.getBytesReceived());
						//Log.d("-------","--DU::"+dto.getAppname()+"---"+readableFileSize(dto.getBytesSent()+dto.getBytesReceived()));
					} while (cursor.moveToNext());

				}
			}
		} catch (Exception e) {
			Log.e("-----","Exception in getApplicationDataUsageFromBenchMarkTable :"
					+ e.getMessage());

		}

		return map;
	}
	
	public boolean insertApplicationDataUsageRecords(long date,
			List<ApplicationDataUsageDTO> dtos) {
		boolean flag = false;
		try {

			String sql = "Insert into "
					+ DataUsageDatabaseHelper.TABLE_APP_USING_DATAUSAGE
					+ " ("
					+ DataUsageDatabaseHelper.KEY_TIME
					+ ","
					+ DataUsageDatabaseHelper.KEY_FORMATTED_TIME
					+ ","
					+ // for testing purpose..this column will be removed.
					DataUsageDatabaseHelper.KEY_UID + ","
					+ DataUsageDatabaseHelper.KEY_APP_NAME + ","
					+ DataUsageDatabaseHelper.KEY_PKG_NAME + ","
					+ DataUsageDatabaseHelper.KEY_APP_VERSION + ","
					+ DataUsageDatabaseHelper.KEY_APP_INSTALL_SOURCE + "," 
					+ DataUsageDatabaseHelper.KEY_CONNECTION_TYPE +","
					+ DataUsageDatabaseHelper.KEY_BYTESSENT + ","
					+ DataUsageDatabaseHelper.KEY_BYTESRECEIVED + ","
					+ DataUsageDatabaseHelper.KEY_BYTESTOTAL + ") "
					+ "values(?,?,?,?,?,?,?,?,?,?,?)";

			SQLiteStatement insert = database.compileStatement(sql);

			ApplicationDataUsageDTO dto;
			int size = dtos.size();

			Log.d("-----","Adding : " + size + " Records");

			database.beginTransaction();
			for (int i = 0; i < size; i++) {
				dto = dtos.get(i);				
				insert.bindLong(1, date);
				insert.bindString(2, Utils.getDateTimeFromMilliSeconds(date));
				insert.bindLong(3, dto.getUID());
				insert.bindString(4, dto.getAppname());
				insert.bindString(5, dto.getPackagename());
				insert.bindString(6, dto.getVersion());
				insert.bindString(7, dto.getSource());
				insert.bindString(8, dto.getNetworkType());				
				insert.bindLong(9, dto.getBytesSent());
				insert.bindLong(10, dto.getBytesReceived());
				insert.bindLong(11, dto.getBytesSent() + dto.getBytesReceived());
			
				insert.execute();
			}

			database.setTransactionSuccessful();
			database.endTransaction();
			Log.d("-----","Added DataUsageRecords : " + size + " Records");
			flag = true;

		} catch (Exception e) {
			flag = false;
			Log.e("-----","Exception in insertApplicationDataUsageRecords :"
					+ e.getMessage());
		}

		return flag;
	}

	/*public HashMap<Long, List<ApplicationDataUsageDTO>> getTopApplicationDataUsageList(
			long startDay, long endDay, int numOfTopAppsCount) {
		HashMap<Long, List<ApplicationDataUsageDTO>> hashmap = new HashMap<Long, List<ApplicationDataUsageDTO>>();
		try {
			List<ApplicationDataUsageDTO> listDto = null;
			ArrayList<Long> daysDuring = GeneralUtility.getDaysArrayList(
					startDay, endDay);
			ApplicationDataUsageDTO dto = null;
			int counter = 0;
			String sql = "";
			Cursor cursor = null;
			for (int i = 0; i < daysDuring.size(); i++) {
				startDay = GeneralUtility.getStartTimeOfTheDay(daysDuring
						.get(i));
				endDay = startDay + (Constants.ONE_DAY_IN_MS - 1);
				counter = 0;
				sql = "SELECT *  FROM "
						+ DataUsageDatabaseHelper.TABLE_APP_USING_DATAUSAGE
						+ " where " + DataUsageDatabaseHelper.KEY_TIME
						+ " >=?  and  " + DataUsageDatabaseHelper.KEY_TIME 
						+ " <= ? ORDER BY "
						+ DataUsageDatabaseHelper.KEY_BYTESTOTAL + " DESC";
				cursor = database.rawQuery(sql, new String[] { "" + startDay,""+endDay });
				if (cursor != null) {
					if (cursor.moveToFirst()) {
						listDto = new ArrayList<ApplicationDataUsageDTO>();

						do {

							dto = new ApplicationDataUsageDTO();
							// objRDDAnalyticsApplicationDataUsageDTO.setKeyTime(cursor.getLong(0));
							dto.setUID(cursor.getInt(2));
							dto.setAppname(cursor.getString(3));
							dto.setPackagename(cursor.getString(4));
							dto.setVersion(cursor.getString(5));
							dto.setSource(cursor.getString(6));
							dto.setBytesSent(cursor.getLong(7));
							dto.setBytesReceived(cursor.getLong(8));

							listDto.add(dto);

							counter++;
							if (counter >= numOfTopAppsCount) {
								break;
							}
						} while (cursor.moveToNext());
						hashmap.put(GeneralUtility
								.getStartTimeOfTheDay(daysDuring.get(i)),
								listDto);
					} else {
						Log.d("-----","No Record for   : "
								+ Utils.getDateTimeFromMilliSeconds(startDay));
					}
				}
			}
		} catch (Exception e) {
			Log.e("-----","Exception in getTopApplicationDataUsageList : "
					+ e.getMessage());
			hashmap = null;
		}
		return hashmap;
	}*/

	public int deleteRecordsOlderThan(long date) {
		return database.delete(
				DataUsageDatabaseHelper.TABLE_APP_USING_DATAUSAGE,
				DataUsageDatabaseHelper.KEY_TIME + " <= " + date, null);
	}

}
