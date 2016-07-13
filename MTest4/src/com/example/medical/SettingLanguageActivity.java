package com.example.medical;

import java.util.ArrayList;
import java.util.List;

import com.medical.adapter.LanuageAdapter;
import com.medical.constants.AppString;
import com.medical.help.LanguageHelp;
import com.medical.help.SetupData;
import com.zby.medical.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * ѡ������
 * @author Administrator
 *
 */
public class SettingLanguageActivity extends Activity {
	
	private TextView tv_title, tv_save;
	
	private SetupData mSetupData;
	
	
	private List<String> list;
	private LanuageAdapter wAdapter;
	private ListView listView;
	
	private int language_item;
	
	private final static int handler_refresh =11;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_language);
		init();
		
	}
	
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case handler_refresh:
//				tv_title.setText(R.string.setting);
//				tv_save.setText(R.string.save);
				wAdapter.notifyDataSetChanged();
				break;
			}
		}
	};
	
	private void init() {
		listView = (ListView) findViewById(R.id.list_view);
		mSetupData = SetupData.getSetupData(this);
		language_item = mSetupData.readInt(AppString.language, AppString.Language_zh);
		if(list ==null) {
			list= new ArrayList<String>();
			list.add(getString(R.string.language_zh));
			list.add(getString(R.string.language_en));
			list.add(getString(R.string.language_ar));
		} 
		if(wAdapter==null) {
			wAdapter = new LanuageAdapter(mHandler, list, this, language_item);
		} else {
			wAdapter.updateIndex(language_item);
		}
		listView.setAdapter(wAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				language_item = arg2;
				wAdapter.updateIndex(language_item);
				mHandler.sendEmptyMessage(handler_refresh);
			}
		});
	}
	
	public void btn_back(View v){
		finish();
	}
	
	public void btn_confirm(View v){
		mSetupData.saveInt(AppString.language, language_item);
		LanguageHelp.switchLanguage(this, language_item);
		mHandler.sendEmptyMessage(handler_refresh);
		setResult(Activity.RESULT_OK);
		finish();
	}


}
