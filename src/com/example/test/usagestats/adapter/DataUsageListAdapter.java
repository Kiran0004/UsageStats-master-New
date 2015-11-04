package com.example.test.usagestats.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import com.example.test.usagestats.R;
import com.example.test.usagestats.bean.AppUsageDTO;
import com.example.test.usagestats.bean.ApplicationDataUsageDTO;

public class DataUsageListAdapter extends BaseAdapter {
	private LayoutInflater mInflater1;
	private Activity activity;
	
	Typeface font;
	private ArrayList<ApplicationDataUsageDTO> dataUsgBean_array;
	ApplicationDataUsageDTO dataUsgData;

	public DataUsageListAdapter(Activity a, ArrayList<ApplicationDataUsageDTO> arrayList) {
		activity = a;
		mInflater1 = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		dataUsgBean_array = arrayList;		
	}

	@Override
	public int getCount() {
		return dataUsgBean_array.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		try
		{
			if (convertView == null) {
				vi = mInflater1.inflate(R.layout.datausage_list, null);		
			}
			if(position % 2 == 0)
			{
				vi.setBackgroundColor(Color.WHITE);	
			}
			else
			{
				vi.setBackgroundColor(Color.parseColor("#F2F2F2"));
			}
			
			dataUsgData = dataUsgBean_array.get(position);
			
			TextView app_name = (TextView)vi.findViewById(R.id.app_name);
			TextView data_used = (TextView)vi.findViewById(R.id.data_used);			
			
			app_name.setText(dataUsgData.getAppname());		
			data_used.setText(dataUsgData.getDataUsed());
			
			
		}
		catch (Exception e) {
			Log.e("Account Adapter", "Message" + e);
		}

		return vi;
	}
	

}
