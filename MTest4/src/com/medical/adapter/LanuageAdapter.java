package com.medical.adapter;

import java.util.List;

import com.zby.medical.R;



import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * <p>Description: 语言列表适配器</p>
 * @author zhujiang
 * @date 2014-9-20
 */
public class LanuageAdapter extends BaseAdapter {
	private Handler mHandler;
	private Context mContext;
	private List<String> list;
	private int index=-1;
	private LayoutInflater mLayoutInflater;// 用于加载xml

	public LanuageAdapter(Handler mHandler, List<String> list, Context mContext) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.list = list;
		//this.cmdList = cmdList;
		//this.mConnectInterface = mConnectInterface;
		mLayoutInflater = LayoutInflater.from(mContext);
	}
	
	public LanuageAdapter(Handler mHandler, List<String> list, Context mContext, int index) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.list = list;
		this.index = index;
		//this.cmdList = cmdList;
		//this.mConnectInterface = mConnectInterface;
		mLayoutInflater = LayoutInflater.from(mContext);
	}

	class Holder {
		public TextView ip_textView;
		//public TextView tv_notReady;
		public CheckBox cb_language;
	}

	private Holder mHolder = null;

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// 初始化控件
		if (convertView == null) {

			convertView = mLayoutInflater
					.inflate(R.layout.dialog_lanuage_list_item, null);
			mHolder = new Holder();
			mHolder.ip_textView = (TextView) convertView
					.findViewById(R.id.textView_ssid);
			mHolder.cb_language = (CheckBox) convertView.findViewById(R.id.checkBox_language);
			//mHolder.sendTest = (Button) convertView.findViewById(R.id.btn_send);
			convertView.setTag(mHolder);
		} else {
			mHolder = (Holder) convertView.getTag();
		}
		mHolder.ip_textView.setText(list.get(position) );
		if(index == position) {
			mHolder.ip_textView.setTextColor(mContext.getResources().getColor(R.color.text_dark));
			mHolder.cb_language.setChecked(true);
		} else {
			mHolder.cb_language.setChecked(false);
			mHolder.ip_textView.setTextColor(mContext.getResources().getColor(R.color.text_dark));
		}
//		if(position>0) {
//			mHolder.tv_notReady.setVisibility(View.VISIBLE);
//		} else {
//			mHolder.tv_notReady.setVisibility(View.INVISIBLE);
//		}
		return convertView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void updateIndex(int lang) {
		// TODO Auto-generated method stub
		this.index = lang;
		System.out.println("update"+this.index);
	}

}
