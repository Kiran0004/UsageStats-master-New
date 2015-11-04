package com.example.test.usagestats;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.util.Log;

import com.example.test.usagestats.bean.ApplicationDataUsageDTO;
import com.example.test.usagestats.db.DataUsageDB;
import com.example.test.usagestats.pref.DataUsagePreference;
import com.example.test.usagestats.utils.Constants;
import com.example.test.usagestats.utils.GeneralUtility;
import com.example.test.usagestats.utils.Utils;

public class DataUsageStats {

	private Context context;
	private DataUsageDB database = null;
	private PackageManager pm = null;
	private final String NOT_RETRIEVABLE = "NOT RETRIEVABLE";

	public DataUsageStats(Context context) {
		this.context = context;
		pm = context.getPackageManager();
	}

	public void saveApplicationDataUsage(int triggerType, long currentTime) {
		if (database == null) {
			database = new DataUsageDB(context);
			database.openDB();
		}				
		saveApplicationDataUsage(getApplicationUsageData(),
				GeneralUtility.getStartTimeOfTheDay(currentTime));

	}
	public void bootRequestCalled(int triggerType, long currentTime) {
		if (database == null) {
			database = new DataUsageDB(context);
			database.openDB();
		}				
		Log.d("-----","Trigger Type : Boot completed.");
		saveApplicationDataUsageFromFile();

	}

	private void clearBenchMarkTable() {
		database.clearBenchMarkTable();
	}

	private void saveApplicationDataUsageFromFile() {
		List<ApplicationDataUsageDTO> dataUsageList = new ArrayList<ApplicationDataUsageDTO>();		
		String data = readSavedData();
		if (data != null) {			
			String[] dataUsage = data.split("\n");

			long date = Long.valueOf(dataUsage[0]);

			date = GeneralUtility.getStartTimeOfTheDay(date);
			int count = dataUsage.length;

			int uid;
			long bytesSent, bytesReceived;
			String temp;
			String[] tempArray;

			ApplicationDataUsageDTO dto;
			for (int i = 1; i < count; i++) {
				temp = dataUsage[i];

				tempArray = temp.split(" ");

				uid = Integer.valueOf(tempArray[0]);
				bytesReceived = Long.valueOf(tempArray[1]);
				bytesSent = Long.valueOf(tempArray[2]);

				dto = getApplicationDetails(uid);
				dto.setBytesReceived(bytesReceived);
				dto.setBytesSent(bytesSent);
				//dto.set
				dataUsageList.add(dto);

				//				Log.d("-----","FF Name : " + dto.getAppname() + " Sent : "
				//						+ dto.getBytesSent() + " Rec : "
				//						+ dto.getBytesReceived());
			}
			saveApplicationDataUsage(dataUsageList, date);

		}

		clearBenchMarkTable();

	}
	private  String getConnectionNetworkType(Context context) {

		String type = NOT_RETRIEVABLE;

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

		if (netInfo != null) {
			type = netInfo.getTypeName().toUpperCase();
		}

		return type;
	}

	private void saveApplicationDataUsage(
			List<ApplicationDataUsageDTO> currentDataUsageList,
			long day) {
		long lastSavedDate = DataUsagePreference
				.getLastSavedInsertionDate(context);
		if (lastSavedDate != 0) {
			List<ApplicationDataUsageDTO> dataUsageList = null;
			HashMap<Integer, ApplicationDataUsageDTO> benchMarkDataUsageMap = database
					.getApplicationDataUsageFromBenchMarkTable(); // get
			// benchmark
			// data.
			if (lastSavedDate == day) {


				List<ApplicationDataUsageDTO> temp = getCurrentApplicationUsageData(
						currentDataUsageList, benchMarkDataUsageMap); // subtract

				HashMap<Integer, ApplicationDataUsageDTO> existingDataUsageList = database
						.getApplicationDataUsageFor(day); // Retrieve existing
				// data for the day.

				dataUsageList = sumCurrentApplicationUsageData(temp,
						existingDataUsageList); // add current data + existing
				// data.

				database.deleteApplicationDataUsageFor(day); // clear existing
				// data from
				// table before
				// insertion.

				if (insertBenchMarkDataUsage(currentDataUsageList)) {
					DataUsagePreference.saveDataUsageInsertionDate(
							context, day);
					insertDataUsage(dataUsageList, day);
				}
			} else {



				dataUsageList = getCurrentApplicationUsageData(
						currentDataUsageList, benchMarkDataUsageMap); // subtract
				// with
				// benchmark
				// data.
				if (insertBenchMarkDataUsage(currentDataUsageList)) {
					DataUsagePreference.saveDataUsageInsertionDate(
							context, day);
					insertDataUsage(dataUsageList, day);
				}
			}
		} else // Starting Analytics firstTime. 
		{


			if (insertBenchMarkDataUsage(currentDataUsageList)) {
				DataUsagePreference.saveDataUsageInsertionDate(
						context, day);
			}
		}
	}

	private List<ApplicationDataUsageDTO> sumCurrentApplicationUsageData(
			List<ApplicationDataUsageDTO> currentDataUsageList,
			HashMap<Integer, ApplicationDataUsageDTO> benchMarkDataUsageMap) {
		List<ApplicationDataUsageDTO> dataUsageList = new ArrayList<ApplicationDataUsageDTO>();

		int size = currentDataUsageList.size();
		ApplicationDataUsageDTO currentDTO = null;
		ApplicationDataUsageDTO mapDTO = null;
		ApplicationDataUsageDTO dto = null;

		for (int i = 0; i < size; i++) {
			currentDTO = currentDataUsageList.get(i);

			mapDTO = benchMarkDataUsageMap.get(currentDTO.getUID());
			dto = null;
			if (mapDTO != null) {
				dto = new ApplicationDataUsageDTO();
				dto.setAppname(currentDTO.getAppname());
				dto.setPackagename(currentDTO.getPackagename());
				dto.setVersion(currentDTO.getVersion());
				dto.setUID(currentDTO.getUID());
				dto.setBytesSent(currentDTO.getBytesSent()
						+ mapDTO.getBytesSent());
				dto.setBytesReceived(currentDTO.getBytesReceived()
						+ mapDTO.getBytesReceived());
				dto.setSource(currentDTO.getSource());
				dto.setNetworkType(currentDTO.getNetworkType());
				dataUsageList.add(dto);
				benchMarkDataUsageMap.remove(mapDTO.getUID());

				//				Log.d("-----",dto.getAppname() + " : R " + dto.getBytesReceived()
				//						+ " : S" + dto.getBytesSent());

			} // if new application added to the list..
			else {
				dto = new ApplicationDataUsageDTO();
				dto.setAppname(currentDTO.getAppname());
				dto.setPackagename(currentDTO.getPackagename());
				dto.setVersion(currentDTO.getVersion());
				dto.setUID(currentDTO.getUID());
				dto.setSource(currentDTO.getSource());
				dto.setBytesSent(currentDTO.getBytesSent());
				dto.setBytesReceived(currentDTO.getBytesReceived());
				dto.setNetworkType(currentDTO.getNetworkType());
				dataUsageList.add(dto);

				//				Log.d("-----","Map Null: " + dto.getAppname() + " : R "
				//						+ dto.getBytesReceived() + " : S" + dto.getBytesSent());
			}

		}

		if (benchMarkDataUsageMap.size() > 1) {
			Iterator<Entry<Integer, ApplicationDataUsageDTO>> it = benchMarkDataUsageMap
					.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, ApplicationDataUsageDTO> pairs = (Map.Entry<Integer, ApplicationDataUsageDTO>) it
						.next();
				dto = benchMarkDataUsageMap.get(pairs.getKey());
				dataUsageList.add(dto);
				it.remove(); // avoids a ConcurrentModificationException
			}
		}

		return dataUsageList;
	}

	private List<ApplicationDataUsageDTO> getCurrentApplicationUsageData(
			List<ApplicationDataUsageDTO> currentDataUsageList,
			HashMap<Integer, ApplicationDataUsageDTO> benchMarkDataUsageMap) {
		List<ApplicationDataUsageDTO> dataUsageList = new ArrayList<ApplicationDataUsageDTO>();

		int size = currentDataUsageList.size();
		ApplicationDataUsageDTO currentDTO = null;
		ApplicationDataUsageDTO mapDTO = null;
		ApplicationDataUsageDTO dto = null;

		for (int i = 0; i < size; i++) {
			currentDTO = currentDataUsageList.get(i);

			mapDTO = benchMarkDataUsageMap.get(currentDTO.getUID());

			if (mapDTO != null) // subtract new bytes with old bytes.
			{
				if ((currentDTO.getBytesSent() - mapDTO.getBytesSent()) > 0
						|| (currentDTO.getBytesReceived() - mapDTO
								.getBytesReceived()) > 0) {
					dto = null;
					dto = new ApplicationDataUsageDTO();
					dto.setAppname(currentDTO.getAppname());
					dto.setPackagename(currentDTO.getPackagename());
					dto.setVersion(currentDTO.getVersion());
					dto.setUID(currentDTO.getUID());
					dto.setBytesSent(currentDTO.getBytesSent()
							- mapDTO.getBytesSent());
					dto.setBytesReceived(currentDTO.getBytesReceived()
							- mapDTO.getBytesReceived());
					dto.setSource(currentDTO.getSource());
					dto.setNetworkType(currentDTO.getNetworkType());
					dataUsageList.add(dto);					
					//					Log.d("-----",dto.getAppname() + " : R "
					//							+ dto.getBytesReceived() + " : S"
					//							+ dto.getBytesSent());
				}
			} // if new application added to the list..
			else {
				dto = null;
				dto = new ApplicationDataUsageDTO();
				dto.setAppname(currentDTO.getAppname());
				dto.setPackagename(currentDTO.getPackagename());
				dto.setVersion(currentDTO.getVersion());
				dto.setUID(currentDTO.getUID());
				dto.setSource(currentDTO.getSource());
				dto.setBytesSent(currentDTO.getBytesSent());
				dto.setBytesReceived(currentDTO.getBytesReceived());
				dto.setNetworkType(currentDTO.getNetworkType());
				dataUsageList.add(dto);			
				//				Log.d("-----","Map Null: " + dto.getAppname() + " : R "
				//						+ dto.getBytesReceived() + " : S" + dto.getBytesSent());
			}

		}

		return dataUsageList;
	}

	@SuppressLint("NewApi")
	private List<ApplicationDataUsageDTO> getApplicationUsageData() {
		List<ApplicationDataUsageDTO> dataUsageList = new ArrayList<ApplicationDataUsageDTO>();

		try {
			File dir = new File("/proc/uid_stat/");
			String[] children = dir.list();

			if (children != null) {
				int uid;
				long bytesSent, bytesReceived;
				ApplicationDataUsageDTO dto = null;
				for (int i = 0; i < children.length; i++) {
					uid = Integer.parseInt(children[i]);
					// if ((uid >= 0 && uid < 2000) || (uid >= 10000))
					{
						dto = getApplicationDetails(uid);
						bytesReceived = TrafficStats.getUidRxBytes(uid);
						bytesSent = TrafficStats.getUidTxBytes(uid);

						dto.setBytesReceived(bytesReceived);
						dto.setBytesSent(bytesSent);
						dataUsageList.add(dto);

						//						Log.d("-----","DU Name : " + dto.getAppname() + " Sent : "
						//								+ dto.getBytesSent() + " Rec : "
						//								+ dto.getBytesReceived());
					}
				}
			}
		} catch (Exception e) {
			Log.e("-----","Excpetion in : getApplicationUsageData :"
					+ e.getMessage());
		}

		return dataUsageList;
	}

	private ApplicationDataUsageDTO getApplicationDetails(int uid) {		
		ApplicationDataUsageDTO dto = new ApplicationDataUsageDTO();
		dto.setUID(uid);
		try {
			String name = Constants.NOT_RETRIEVABLE;
			String packageName = Constants.NOT_RETRIEVABLE;
			String versionName = Constants.NOT_RETRIEVABLE;
			String source = Constants.NOT_RETRIEVABLE;

			if (uid != 1000 && uid != 1013 && uid != 0) {
				PackageInfo pif = null;
				String[] packages = pm.getPackagesForUid(uid);

				if (packages != null) {
					boolean flag = true;
					if (packages.length > 1) {
						for (String pkg : packages) {
							try {
								pif = pm.getPackageInfo(pkg, 0);
								if (pif.sharedUserLabel != 0) {
									CharSequence nm = pm.getText(pkg,
											pif.sharedUserLabel,
											pif.applicationInfo);
									if (nm != null) {
										flag = false;
										name = nm.toString();
										packageName = pif.packageName != null ? pif.packageName
												: Constants.NOT_RETRIEVABLE;
										versionName = pif.versionName != null ? pif.versionName
												: Constants.NOT_RETRIEVABLE;
										versionName += "." + pif.versionCode;
										source = Utils.getSource(context, packageName);
										break;
									}
								}
							} catch (Exception e) {
								// Log.d("-----","Exception 2: getName()  ");
							}
						}

					}
					if (flag) {
						pif = pm.getPackageInfo(packages[0], 0);
						if (pif.applicationInfo != null
								&& pif.applicationInfo.loadLabel(pm) != null
								&& pif.applicationInfo.loadLabel(pm).length() > 0) {
							name = pif.applicationInfo.loadLabel(pm).toString();
						} else {
							name = pif.packageName != null ? pif.packageName
									: Constants.NOT_RETRIEVABLE;
						}
						packageName = pif.packageName != null ? pif.packageName
								: Constants.NOT_RETRIEVABLE;
						versionName = pif.versionName != null ? pif.versionName
								: Constants.NOT_RETRIEVABLE;
						versionName += "." + pif.versionCode;
						source = Utils.getSource(context, packageName);
					}
				} else {
					Log.d("-----","Packages null for uid : " + uid);
				}
			} else {
				if (uid == 1000) {
					name = "Android System";
					packageName = "Android System";
					versionName = "N/A";
					source = "System";
				} else if (uid == 1013) {
					name = "Mediaserver";
					packageName = "Mediaserver";
					versionName = "N/A";
					source = "System";
				} else if (uid == 0) {
					name = "Android OS";
					packageName = "Android OS";
					versionName = "N/A";
					source = "System";
				}
			}

			dto.setAppname(name);
			dto.setPackagename(packageName);
			dto.setVersion(versionName);
			dto.setSource(source);
			dto.setNetworkType(getConnectionNetworkType(context));
		} catch (Exception e) {
			Log.e("-----","Excpetion in : getApplicationDetails :" + e.getMessage());
		}
		return dto;
	}
	private boolean insertBenchMarkDataUsage(
			List<ApplicationDataUsageDTO> benchMarkDataUsageList) {
		return database
				.insertApplicationDataUsageInBenchMarkTable(benchMarkDataUsageList);
	}

	private boolean insertDataUsage(
			List<ApplicationDataUsageDTO> dataUsageList, long date) {
		return database.insertApplicationDataUsageRecords(date, dataUsageList);
	}

	@SuppressLint("NewApi")
	public static void saveDataUsageInFile(Context context) {
		Log.d("-----","Writing data usage to a file started:");
		try {
			File dir = new File("/proc/uid_stat/");
			String[] children = dir.list();

			if (children != null) {

				String emptySpace = " ";
				String newLine = "\n";
				int uid;
				long bytesSent, bytesReceived;
				StringBuilder sb = new StringBuilder();
				sb.append(System.currentTimeMillis() + newLine);

				for (int i = 0; i < children.length; i++) {
					uid = Integer.parseInt(children[i]);
					// if ((uid >= 0 && uid < 2000) || (uid >= 10000))
					{
						// Log.d("RDD", "UID : " + uid);
						bytesReceived = TrafficStats.getUidRxBytes(uid);
						bytesSent = TrafficStats.getUidTxBytes(uid);
						sb.append(uid + emptySpace + bytesReceived + emptySpace
								+ bytesSent + newLine);
					}
				}

				String filename = "READDATAUSAGE";
				FileOutputStream outputStream;

				outputStream = context.openFileOutput(filename,
						Context.MODE_PRIVATE);
				outputStream.write(sb.toString().getBytes());
				outputStream.close();
				Log.d("-----","Writing data usage to a file Ended.");
			}
		} catch (Exception e) {
			Log.e("-----","Exception while saving data usage in a file : "
					+ e.getMessage());
		}

	}

	private String readSavedData() {
		String data = null;
		try {
			StringBuilder fileContent = new StringBuilder();
			FileInputStream fis;
			fis = context.openFileInput("READDATAUSAGE");
			byte[] buffer = new byte[1024];
			int n;
			while ((n = fis.read(buffer)) != -1) {
				fileContent.append(new String(buffer, 0, n));
			}
			data = fileContent.toString();

			boolean flag = context.deleteFile("READDATAUSAGE");

			Log.d("-----","Datausage file deleted : " + flag);
		} catch (Exception e) {
			Log.e("-----","Exception in readSavedData (Data usage) :"
					+ e.getMessage());
		}
		return data;
	}

	public void deleteOldRecords(long date) {
		try {
			if (database == null) {
				database = new DataUsageDB(context);
				database.openDB();
			}
			database.deleteRecordsOlderThan(date);
		} catch (Exception e) {
			Log.e("-----","Exception in deleteOldRecords (READDATAUSAGE) :"
					+ e.getMessage());
		}
	}

	public void deIntialize() {
		try {
			if (database != null)
				database.closeDB();
		} catch (Exception e) {
			Log.e("-----","Exception in deIntialize (READDATAUSAGE) :"
					+ e.getMessage());
		}
	}

}
