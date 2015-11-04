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

public class AppUsageListAdapter extends BaseAdapter {
	private LayoutInflater mInflater1;
	private Activity activity;
	private boolean val;
	Typeface font;
	private ArrayList<AppUsageDTO> accountBean_array;
	AppUsageDTO accountData;	

	public AppUsageListAdapter(Activity a, ArrayList<AppUsageDTO> arrayList,boolean flag) {
		activity = a;
		mInflater1 = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		accountBean_array = arrayList;		
		val = flag;
	}

	@Override
	public int getCount() {
		return accountBean_array.size();
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
				vi = mInflater1.inflate(R.layout.custom_list, null);		
			}
			if(position % 2 == 0)
			{
				vi.setBackgroundColor(Color.WHITE);	
			}
			else
			{
				vi.setBackgroundColor(Color.parseColor("#F2F2F2"));
			}
			
			accountData = accountBean_array.get(position);
			
			TextView app_name = (TextView)vi.findViewById(R.id.app_name);
			TextView time_spent = (TextView)vi.findViewById(R.id.time_spent);
			TextView start_time = (TextView)vi.findViewById(R.id.start_time);						
			app_name.setText(accountData.getApp_name());			
			time_spent.setText(accountData.getTime_spent());
			start_time.setText(accountData.getStart_time()+"  ----   "+accountData.getEnd_time());
			if(val){				
				time_spent.setVisibility(View.GONE);
				start_time.setVisibility(View.GONE);
			}else{				
				time_spent.setVisibility(View.VISIBLE);
				start_time.setVisibility(View.VISIBLE);
			}
		
			
		}
		catch (Exception e) {
			Log.e("Account Adapter", "Message" + e);
		}

		return vi;
	}
	

}
