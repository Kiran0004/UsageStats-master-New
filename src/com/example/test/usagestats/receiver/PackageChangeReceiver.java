package com.example.test.usagestats.receiver;

import com.example.test.usagestats.utils.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class PackageChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Uri data = intent.getData();
		Log.d(Utils.TAG, "Action: " + intent.getAction());
		Log.d(Utils.TAG, "The DATA: " + data);
	}
}
