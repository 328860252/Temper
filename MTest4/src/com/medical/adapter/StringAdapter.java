package com.medical.adapter;

import java.util.List;

import com.zby.medical.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StringAdapter extends BaseAdapter {

	private List<String> list;
	private Context mContext;
	
	public StringAdapter(Context context ,List<String> list) {
		this.list = list;
		this.mContext = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		String address = list.get(arg0);
		System.out.println("found adapter refre " + list.size() + " " + arg0);
		if(arg1==null) {
			arg1 = LayoutInflater.from(mContext).inflate(R.layout.string_list_item, null);
			mHolder = new Holder();
			mHolder.tv_name = (TextView) arg1.findViewById(R.id.textView_name);
			arg1.setTag(mHolder);
		} else {
			mHolder =  (Holder) arg1.getTag();
		}
		mHolder.tv_name.setText(address);
		return arg1;
	}
	
	private Holder mHolder;
	private class Holder {
		private TextView tv_name;
	}

}
